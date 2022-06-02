package com.asurint.keystone.kafkaretrytest.configuration


import com.asurint.keystone.kafkaretrytest.service.ProducerService
import com.course.avro.data.GetClient
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.RecoverableDataAccessException
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.listener.ConsumerRecordRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.RetryListener
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries
import org.springframework.kafka.transaction.KafkaTransactionManager
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import org.springframework.util.backoff.FixedBackOff


const val BACK_OFF_PERIOD: Long = 1000L // should not be longer than max.poll.interval.ms
const val MAX_ATTEMPTS: Int = 1

@Configuration
@EnableKafka
@EnableJpaRepositories(basePackages = arrayOf("com.asurint.keystone.kafkaretrytest.entity"))
class KafkaConsumerConfiguration {
    private val logger: Logger = LoggerFactory.getLogger(KafkaConsumerConfiguration::class.java)

    @Autowired
    private lateinit var producerService: ProducerService

    @Autowired
    private lateinit var kafkaProducerConfiguration: KafkaProducerConfiguration

    @Value("\${topics.retry}")
    private val retryTopic: String? = "local.accounts.retry"

    @Value("\${topics.dlt}")
    private val deadLetterTopic: String? = "local.accounts.dlq"

    var consumerRecordRecoverer = ConsumerRecordRecoverer { consumerRecord: ConsumerRecord<*, *>, e: Exception ->
        logger.info("Exception in consumerRecordRecoverer : {} ", e.message, e)
        val record = consumerRecord as ConsumerRecord<String?, GetClient>
        var topic = deadLetterTopic
        if (e.cause is RecoverableDataAccessException) {
            //recovery logic
            logger.info("Inside Recovery")
            topic = retryTopic
        } else {
            // non-recovery logic
            logger.info("Inside Non-Recovery - Sending to dead letter topic")
        }
        producerService.sendFailureToKafka(record, e, topic)
    }

    fun errorHandler(kafkaProperties: KafkaProperties): DefaultErrorHandler? {
        /* val exceptionsToIgnoreList: Unit = List.of(
            IllegalArgumentException::class.java
        )
        val exceptionsToRetryList: Unit = List.of(
            RecoverableDataAccessException::class.java
        )*/
        val fixedBackOff = FixedBackOff(1000L, 1)
        val expBackOff = ExponentialBackOffWithMaxRetries(MAX_ATTEMPTS)
        expBackOff.initialInterval = 1000L
        expBackOff.multiplier = 4.0
        expBackOff.maxInterval = 40000L

        var defaultKafkaProducerFactory:DefaultKafkaProducerFactory<String, String>
            = DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())

        //val errorHandler1 = DefaultErrorHandler(DeadLetterPublishingRecoverer( kafkaTemplate()), expBackOff)
        val errorHandler1 = DefaultErrorHandler(
            consumerRecordRecoverer,
            expBackOff
        )
        //exceptionsToIgnoreList.forEach(errorHandler::addNotRetryableExceptions);
        //exceptionsToRetryList.forEach(errorHandler::addRetryableExceptions)
        errorHandler1
            .setRetryListeners(RetryListener { record: ConsumerRecord<*, *>?, ex: Exception, deliveryAttempt: Int ->
                logger.info(
                    "Failed Record in Retry Listener, Exception : {} , deliveryAttempt : {} ",
                    ex.message,
                    deliveryAttempt
                )
            })
        return errorHandler1
    }

    @Bean
    fun consumerFactory(properties: KafkaProperties): ConsumerFactory<String?, GetClient?> =
        DefaultKafkaConsumerFactory(properties.buildConsumerProperties())

    @Bean
    fun kafkaListenerContainerFactory(properties: KafkaProperties): ConcurrentKafkaListenerContainerFactory<String?, GetClient?> {
        val factory = ConcurrentKafkaListenerContainerFactory<String?, GetClient?>()
        factory.consumerFactory = consumerFactory(properties)
        factory.setCommonErrorHandler(errorHandler(properties)!!)
        factory.containerProperties.transactionManager = kafkaProducerConfiguration.kafkaTransactionManager()
        return factory
    }
}