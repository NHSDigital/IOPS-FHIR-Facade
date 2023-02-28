package uk.nhs.england.qedm

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.server.RestfulServer
import org.springframework.beans.factory.annotation.Qualifier
import uk.nhs.england.qedm.interceptor.AWSAuditEventLoggingInterceptor
import uk.nhs.england.qedm.interceptor.CapabilityStatementInterceptor
import uk.nhs.england.qedm.provider.*
import java.util.*
import javax.servlet.annotation.WebServlet


@WebServlet("/FHIR/R4/*", loadOnStartup = 1, displayName = "FHIR Facade")
class FHIRR4RestfulServer(
    @Qualifier("R4") fhirContext: FhirContext,
    val fhirServerProperties: uk.nhs.england.qedm.configuration.FHIRServerProperties,
    val messageProperties: uk.nhs.england.qedm.configuration.MessageProperties,
    public val encounterProvider: EncounterProvider,
    val medicationDispenseProvider: MedicationDispenseProvider,
    val medicationRequestProvider: MedicationRequestProvider,
    val medicationStatementProvider: MedicationStatementProvider,

    val serviceRequestProvider: ServiceRequestProvider,
    val taskProvider: TaskProvider,

    val allergyIntoleranceProvider: AllergyIntoleranceProvider,
    val conditionProvider: ConditionProvider,
    val immunisationProvider: ImmunisationProvider,

    val observationSearchProvider: ObservationSearchProvider,
    val procedureProvider: ProcedureProvider,
    val diagnosticReportProvider: DiagnosticReportProvider,

    val patientSearchProvider: PatientSearchProvider,
    val documentReferenceProvider: DocumentReferenceProvider,
    val binaryProvider: BinaryProvider,
    val specimenProvider: SpecimenProvider,
    val consentProvider: ConsentProvider,
    val questionnaireResponseProvider: QuestionnaireResponseProvider

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

        registerProvider(procedureProvider)
        registerProvider(diagnosticReportProvider)

        registerProvider(binaryProvider)
        registerProvider(documentReferenceProvider)
        registerProvider(specimenProvider)
        registerProvider(consentProvider)

        registerProvider(questionnaireResponseProvider)

        registerProvider(observationSearchProvider)
        registerProvider(patientSearchProvider)

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
