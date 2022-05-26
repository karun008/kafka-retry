package com.asurint.keystone.kafkaretrytest.entity

//import org.springframework.data.repository.CrudRepository

//import javax.persistence.GeneratedValue
//
//@Service
//class FailureService(private val failureRecordRepository: FailureRecordRepository) {
//    fun saveFailedRecord(consumerRecord: ConsumerRecord<String?, String>, e: Exception, status: String?) {
//        val failureRecord = FailureRecord(
//            null,
//            consumerRecord.topic(),
//            consumerRecord.key(),
//            consumerRecord.value(),
//            consumerRecord.partition(),
//            consumerRecord.offset(),
//            e.cause!!.message,
//            status
//        )
//        failureRecordRepository.save(failureRecord)
//    }
//}
//
//interface FailureRecordRepository : CrudRepository<FailureRecord?, Int?> {
//    fun findAllByStatus(retry: String?): List<FailureRecord?>?
//}
//
//
//class FailureRecord (
//    @javax.persistence.Id
//    @GeneratedValue
//     val id: Int? = null,
//     val topic: String? = null,
//     val key: String? = null,
//     val errorRecord: String? = null,
//     val partition: Int? = null,
//     val offset_value: Long? = null,
//     val exception: String? = null,
//     val status: String? = null
//)
