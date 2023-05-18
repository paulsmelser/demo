package com.psmelser.demo

import aws.smithy.kotlin.runtime.time.Instant

data class BucketContentDetails(var name: String, var creationDate: Instant, var numberOfFiles: Int, var totalSize: Long, var lastModified: Instant, var cost: Double)
