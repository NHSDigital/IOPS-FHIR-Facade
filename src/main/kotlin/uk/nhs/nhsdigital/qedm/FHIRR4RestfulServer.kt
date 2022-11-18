package uk.nhs.nhsdigital.qedm

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.server.RestfulServer
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.cors.CorsConfiguration
import uk.nhs.nhsdigital.qedm.configuration.FHIRServerProperties
import uk.nhs.nhsdigital.qedm.configuration.MessageProperties
import uk.nhs.nhsdigital.qedm.interceptor.AWSAuditEventLoggingInterceptor
import uk.nhs.nhsdigital.qedm.interceptor.CapabilityStatementInterceptor
import uk.nhs.nhsdigital.qedm.provider.*
import java.util.*
import javax.servlet.annotation.WebServlet

@WebServlet("/FHIR/R4/*", loadOnStartup = 1, displayName = "FHIR Facade")
class FHIRR4RestfulServer(
    @Qualifier("R4") fhirContext: FhirContext,
    val fhirServerProperties: FHIRServerProperties,
    val messageProperties: MessageProperties,
    public val encounterProvider: EncounterProvider,
    val medicationDispenseProvider: MedicationDispenseProvider,
    val medicationRequestProvider: MedicationRequestProvider,
    val medicationStatementProvider: MedicationStatementProvider,

    val serviceRequestProvider: ServiceRequestProvider,
    val taskProvider: TaskProvider,

    val allergyIntoleranceProvider: AllergyIntoleranceProvider,
    val conditionProvider: ConditionProvider,
    val immunisationProvider: ImmunisationProvider,
    val observationProvider: ObservationProvider,
    val procedureProvider: ProcedureProvider,
    val diagnosticReportProvider: DiagnosticReportProvider
) : RestfulServer(fhirContext) {

    override fun initialize() {
        super.initialize()

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        registerProvider(encounterProvider)

        registerProvider(medicationDispenseProvider)
        registerProvider(medicationRequestProvider)
        registerProvider(medicationStatementProvider)

        registerProvider(taskProvider)
        registerProvider(serviceRequestProvider)

        registerProvider(allergyIntoleranceProvider)
        registerProvider(conditionProvider)
        registerProvider(immunisationProvider)
        registerProvider(observationProvider)
        registerProvider(procedureProvider)
        registerProvider(diagnosticReportProvider)

        val awsAuditEventLoggingInterceptor =
            AWSAuditEventLoggingInterceptor(
                this.fhirContext,
                fhirServerProperties
            )
        interceptorService.registerInterceptor(awsAuditEventLoggingInterceptor)
        registerInterceptor(CapabilityStatementInterceptor(fhirServerProperties, messageProperties))


        isDefaultPrettyPrint = true
        defaultResponseEncoding = EncodingEnum.JSON
    }
}
