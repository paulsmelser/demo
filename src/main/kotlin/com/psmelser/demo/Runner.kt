package com.psmelser.demo

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Runner(val crawler: Crawler, val printer: S3DetailPrinter) : CommandLineRunner {
    val region2 = "us-east-1"
    val bucket2 = "coveochal"
    override fun run(vararg args: String?) {
        println("Welcome to the S3 Detail Crawler")
        print("Please Enter the region: ")
        val region = readln()
        print("Please Enter the bucket name or empty if you would like to read all buckets: ")
        val bucket = readln()

        (if (bucket.isNotBlank()) {
            crawler.crawlBucket(bucket, region2)
        } else crawler.crawlAllBuckets("us-east-1")).also { printer.print(it) }
    }
}

