package com.asurint.keystone.kafkaretrytest.service

import com.amazonaws.HttpMethod
import com.asurint.keystone.kafkaretrytest.configuration.KafkaProducerConfiguration
import com.course.avro.data.GetClient
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProducerService {

    @Autowired
    private lateinit var awsS3Service: AwsS3Service

    @Autowired
    private lateinit var failureService: FailureService

    @Autowired
    private lateinit var kafkaProducerConfig: KafkaProducerConfiguration

    fun sendMessage(client: GetClient, topic: String) {
        kafkaProducerConfig.sendMessage(client, topic)
    }

    fun sendFailureToKafka(
        record: ConsumerRecord<String?, GetClient>,
        e: Exception, topic: String?
    ) {
        var producerRecord =
            record?.let { kafkaProducerConfig.buildProducerRecord(it.key(), it.value(), topic!!) }
        if (producerRecord != null) {
            kafkaProducerConfig.kafkaTemplate().send(producerRecord)
        }
        failureService.saveFailedRecord(record, e, topic)
    }
}