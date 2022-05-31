package com.asurint.keystone.kafkaretrytest.configuration

import com.course.avro.data.GetClient
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import java.util.*
import kotlin.collections.HashMap

@Configuration
@EnableKafka
class KafkaProducerConfiguration {

    //@Bean
    fun producerFactory(): DefaultKafkaProducerFactory<String, GenericRecord> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
        configProps[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8085"
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String?, GenericRecord> {
        return KafkaTemplate(producerFactory())
    }

    fun buildProducerRecord(key: String?, value: GetClient, topic: String): ProducerRecord<String?, GenericRecord> {
        return ProducerRecord(topic, null, key, value, null)
    }

    fun sendMessage(client: GetClient, topic: String) {
        kafkaTemplate().send(buildProducerRecord(UUID.randomUUID().toString(), client, topic))
    }
}