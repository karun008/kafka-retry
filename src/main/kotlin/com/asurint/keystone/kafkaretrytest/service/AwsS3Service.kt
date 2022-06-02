package com.asurint.keystone.kafkaretrytest.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class AwsS3Service {
    private var amazonS3: AmazonS3? = null
    @Autowired
    fun AwsS3ServiceImpl(amazonS3: AmazonS3?) {
        this.amazonS3 = amazonS3
    }

    fun generatePreSignedUrl(
        filePath: String?,
        bucketName: String?,
        httpMethod: HttpMethod?
    ): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.MINUTE, 10) //validity of 10 minutes
        return amazonS3!!.generatePresignedUrl(bucketName, filePath, calendar.time, httpMethod).toString()
    }
}