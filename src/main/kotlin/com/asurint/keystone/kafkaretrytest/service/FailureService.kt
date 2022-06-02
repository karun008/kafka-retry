package com.asurint.keystone.kafkaretrytest.service

import com.asurint.keystone.kafkaretrytest.entity.FailureRecord
import com.asurint.keystone.kafkaretrytest.entity.FailureRecordRepository
import com.course.avro.data.GetClient
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class FailureService() {
    @Autowired
    lateinit var failureRecordRepository: FailureRecordRepository

    @Transactional
    fun saveFailedRecord(consumerRecord: ConsumerRecord<String?, GetClient>, e: Exception, status: String?) {
        val failureRecord = FailureRecord(
            null,
            consumerRecord.topic(),
            consumerRecord.key(),
            consumerRecord.value().toString(),
            consumerRecord.partition(),
            consumerRecord.offset(),
            e.cause!!.message,
            status
        )
        failureRecordRepository.save(failureRecord)
    }
}