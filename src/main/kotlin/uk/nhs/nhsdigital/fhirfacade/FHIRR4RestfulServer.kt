package uk.nhs.nhsdigital.fhirfacade

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor
import ca.uhn.fhir.rest.server.RestfulServer
import org.springframework.beans.factory.annotation.Qualifier
import uk.nhs.nhsdigital.fhirfacade.configuration.FHIRServerProperties
import uk.nhs.nhsdigital.fhirfacade.interceptor.AWSAuditEventLoggingInterceptor
import uk.nhs.nhsdigital.fhirfacade.provider.*
import java.util.*
import javax.servlet.annotation.WebServlet

@WebServlet("/FHIR/R4/*", loadOnStartup = 1, displayName = "FHIR Facade")
class FHIRR4RestfulServer(
    @Qualifier("R4") fhirContext: FhirContext,
    public val fhirServerProperties: FHIRServerProperties,
    public val encounterProvider: EncounterProvider,
    public val patientProvider: PatientProvider
) : RestfulServer(fhirContext) {

    override fun initialize() {
        super.initialize()

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        registerProvider(patientProvider)
        registerProvider(encounterProvider)

        val awsAuditEventLoggingInterceptor =
            AWSAuditEventLoggingInterceptor(
                this.fhirContext,
                fhirServerProperties
            )
        interceptorService.registerInterceptor(awsAuditEventLoggingInterceptor)

        // Now register the interceptor
        // Now register the interceptor
        val openApiInterceptor = OpenApiInterceptor()
        registerInterceptor(openApiInterceptor)

        isDefaultPrettyPrint = true
        defaultResponseEncoding = EncodingEnum.JSON
    }
}
