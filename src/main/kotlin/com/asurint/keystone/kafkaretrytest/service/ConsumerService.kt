package com.asurint.keystone.kafkaretrytest.service

import com.course.avro.data.GetClient
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.RecoverableDataAccessException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConsumerService {

    private val logger: Logger = LoggerFactory.getLogger(ConsumerService::class.java)

    @Autowired
    private lateinit var producerService: ProducerService

    @KafkaListener(topics = ["local.accounts"])
    @Transactional
    fun listenRetry(record: ConsumerRecord<String, GetClient>) {
        logger.info("received 'local.accounts' record! (value: ${record.value()})")
        var client = GetClient.newBuilder().setFullName("testname").setActive(true).setMaritalStatus("Married").build()
        producerService.sendMessage(client, "local.accounts.topic1")
        producerService.sendMessage(client, "local.accounts.topic2")
        //throw RecoverableDataAccessException("Temporary Network Issue")
        // logger.info("correlation id : ${record.value().metadata.correlationId}");
        /* val libraryEventOptional: Optional<LibraryEvent> =
             libraryEventsRepository.findById(libraryEvent.getLibraryEventId())
         require(libraryEventOptional.isPresent()) { "Not a valid library Event" }
         log.info("Validation is successful for the library Event : {} ", libraryEventOptional.get())
         *///exceptionService.withRecord(record).iFailButWillRecover()
    }

}