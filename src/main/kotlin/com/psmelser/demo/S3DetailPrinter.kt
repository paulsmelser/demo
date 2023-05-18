package com.psmelser.demo

import com.psmelser.demo.color.green
import com.psmelser.demo.color.red
import org.springframework.stereotype.Component

@Component
class S3DetailPrinter {
    fun print(s3: S3Details) {
        println()
        println(
            """
                ----------------------------------------------------------------------------------------        
                S3 Details Collected for ${s3.region}
                ----------------------------------------------------------------------------------------
            """.trimIndent().red()
        )

        s3.buckets.forEach {

            println("""
                ----------------------------------------------------------------------------------------        
                Bucket: ${it.name}
                ----------------------------------------------------------------------------------------
            """.trimIndent().green())
            print("LastModified: ".green())
            println(it.lastModified)
            print("Cost: ".green())
            println(it.cost)
            print("Total Size: ".green())
            println(it.totalSize)
            print("Number of Files: ".green())
            println(it.numberOfFiles)
            print("Date Created: ".green())
            println(it.creationDate)
        }
    }
}