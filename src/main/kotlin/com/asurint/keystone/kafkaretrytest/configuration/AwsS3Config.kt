package com.asurint.keystone.kafkaretrytest.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config {
    @Value("\${amazon.aws.access-key}")
    private val accessKeyId: String? = null

    @Value("\${amazon.aws.secret-key}")
    private val accessKeySecret: String? = null

    @Value("\${amazon.aws.region}")
    private val s3RegionName: String? = null

    // Get Amazon S3 client and return the S3 client object
    @Bean
    fun getAmazonS3Client(): AmazonS3 {
        val basicAwsCredentials = BasicAWSCredentials(accessKeyId, accessKeySecret)
        // Get Amazon S3 client and return the S3 client object
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(basicAwsCredentials))
            .withRegion(s3RegionName)
            .build()
    }
}