package com.asurint.keystone.kafkaretrytest.consumer

import com.course.avro.data.GetClient
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.RecoverableDataAccessException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer() {

    private val logger: Logger = LoggerFactory.getLogger(KafkaConsumer::class.java)

    @KafkaListener(topics = ["local.accounts"])
    fun listenRetry(record: ConsumerRecord<String, GetClient>) {
        logger.info("received 'test-retry' record! (value: ${record.value()})")
        throw RecoverableDataAccessException("Temporary Network Issue")

    }

}