package com.psmelser.demo


import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.listBuckets
import aws.sdk.kotlin.services.s3.listObjects
import aws.sdk.kotlin.services.s3.model.Bucket
import aws.smithy.kotlin.runtime.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class Crawler {
    fun crawlBucket(s3Bucket: String, awsRegion: String): S3Details {
        return runBlocking {
            S3Client
                .fromEnvironment {
                    region = awsRegion
                    credentialsProvider = EnvironmentCredentialsProvider()
                }
                .use { s3 ->
                    s3.listBuckets { }.buckets?.first { it.name == s3Bucket }?.let {
                        collectBucketDetails(it, s3)
                    } ?: BucketContentDetails("", Instant.fromEpochSeconds("1"), 0, 0, Instant.fromEpochSeconds("1"), 0.0)
                }
        }.let { S3Details(awsRegion, listOf(it)) }
    }

    fun crawlAllBuckets(awsRegion: String): S3Details {
        return runBlocking {
            S3Client
                .fromEnvironment {
                    region = awsRegion
                    credentialsProvider = EnvironmentCredentialsProvider()
                }
                .use { s3 ->
                    s3.listBuckets { }.buckets?.map { async { collectBucketDetails(it, s3)} }?.awaitAll()
                        ?: listOf(BucketContentDetails("", Instant.now(), 0, 0, Instant.fromEpochSeconds("1"), 0.0))
                }
        }.let { S3Details(awsRegion, it) }
    }

    suspend fun collectBucketDetails(s3Bucket: Bucket, s3: S3Client): BucketContentDetails {
        return runBlocking { s3.listObjects { bucket = s3Bucket.name }
            .contents?.fold(BucketContentDetails(s3Bucket.name!!, s3Bucket.creationDate!!, 0, 0, Instant.fromEpochSeconds("1"), 0.0)) {
                    acc, current ->
                async {
                acc.cost = acc.cost + calculateBucketCost(current.size)
                acc.numberOfFiles++
                acc.totalSize = acc.totalSize + current.size
                acc.lastModified = current.lastModified?.let { if (it > acc.lastModified) it else acc.lastModified } ?: acc.lastModified
                acc
                }.await()
            } ?: BucketContentDetails("", Instant.now(), 0, 0, Instant.fromEpochSeconds("1"), 0.0) }
    }

    suspend fun calculateBucketCost(size: Long) : Double {
        return (0.023 * (size / 1024 / 1024 / 1024)).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }
}