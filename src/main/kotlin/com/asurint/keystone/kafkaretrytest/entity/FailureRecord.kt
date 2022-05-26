package com.asurint.keystone.kafkaretrytest.entity

import com.course.avro.data.GetClient
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import javax.persistence.*
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

@Repository
interface FailureRecordRepository : CrudRepository<FailureRecord?, Int?> {
    fun findAllByStatus(retry: String?): List<FailureRecord?>?
}

@Entity
@Table(name = "FAILURERECORD")
data class FailureRecord (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,
    val topic: String? = null,
    val topickey: String? = null,
    val errorRecord: String? = null,
    val partition: Int? = null,
    val offset_value: Long? = null,
    val exception: String? = null,
    val status: String? = null
)