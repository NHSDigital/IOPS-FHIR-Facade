package uk.nhs.england.qedm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import uk.nhs.england.qedm.configuration.FHIRServerProperties


@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties(uk.nhs.england.qedm.configuration.FHIRServerProperties::class)
open class FHIRFacade

fun main(args: Array<String>) {
    runApplication<uk.nhs.england.qedm.FHIRFacade>(*args)
}
