package com.asurint.keystone.kafkaretrytest.entity

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

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