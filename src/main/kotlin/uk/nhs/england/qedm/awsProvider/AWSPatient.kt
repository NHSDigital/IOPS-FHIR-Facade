package uk.nhs.england.qedm.awsProvider

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.r4.model.Organization
import org.hl7.fhir.r4.model.Patient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.configuration.FHIRServerProperties
import uk.nhs.england.qedm.configuration.MessageProperties
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class AWSPatient (val messageProperties: MessageProperties, val awsClient: IGenericClient,
               //sqs: AmazonSQS?,
                  @Qualifier("R4") val ctx: FhirContext,
                  val awsOrganization: AWSOrganization,
                  val fhirServerProperties: FHIRServerProperties) {


    private val log = LoggerFactory.getLogger("FHIRAudit")

    fun update(patient: Patient, internalId: IdType?): MethodOutcome? {
        var response: MethodOutcome? = null

        var retry = 3
        while (retry > 0) {
            try {
                response = awsClient!!.update().resource(patient).withId(internalId).execute()
                log.info("AWS Patient updated " + response.resource.idElement.value)
                break
            } catch (ex: Exception) {
                // do nothing
                log.error(ex.message)
                retry--
                if (retry == 0) throw ex
            }
        }
        return response

    }
    fun create(newPatient: Patient): MethodOutcome? {
        val awsBundle: Bundle? = null
        var response: MethodOutcome? = null

        var retry = 3
        while (retry > 0) {
            try {
                response = awsClient
                    .create()
                    .resource(newPatient)
                    .execute()
                val patient = response.resource as Patient
                break
            } catch (ex: Exception) {
                // do nothing
                log.error(ex.message)
                retry--
                if (retry == 0) throw ex
            }
        }
        return response
    }

    fun processQueryString(httpString: String?, nhsNumber : TokenParam? ) : String? {
        var queryString: String? = httpString
        if (queryString != null) {
            val params: List<String> = queryString.split("&")
            val newParams = mutableListOf<String>()
            var patient : Patient? = null
            if (nhsNumber != null) {
                if (nhsNumber.value == null || nhsNumber.system == null) throw UnprocessableEntityException("Malformed patient identifier parameter both system and value are required.")
                patient = get(Identifier().setSystem(nhsNumber.system).setValue(nhsNumber.value))
            }

            for (param in params) {
                val name: String = java.net.URLDecoder.decode(param.split("=").get(0), StandardCharsets.UTF_8.name())
                val value: String = param.split("=").get(1)
                val newvalue: String = java.net.URLDecoder.decode(param.split("=").get(1), StandardCharsets.UTF_8.name())
                if (name.equals("patient:identifier")) {
                    if (patient != null) {
                        newParams.add("patient=" + patient.idElement.idPart)
                    } else {
                        newParams.add("patient=PATIENT_NOT_FOUND_NHS_NUMBER")
                    }
                }
                else if (name.equals("_content")) {
                    newParams.add("title=$value")
                } else if (name.equals("_total")) {
                    //newParams.add("title=$value")
                } else if (name.equals("custodian:identifier") && newvalue.split("|").size>1) {
                    val ids = newvalue.split("|")
                    val org = awsOrganization.get(Identifier().setSystem(ids[0]).setValue(ids[1]))
                    if (org != null) {
                        newParams.add( "custodian=" + org.idElement.idPart)
                    } else {
                        newParams.add( "custodian=" + value)
                    }
                }
                else {
                    newParams.add(param)
                }
            }
            queryString = newParams.joinToString("&")
        }
        return queryString
    }

    public fun get(identifier: Identifier): Patient? {
        var bundle: Bundle? = null
        var retry = 3
        while (retry > 0) {
            try {
                bundle = awsClient
                    .search<IBaseBundle>()
                    .forResource(Patient::class.java)
                    .where(
                        Patient.IDENTIFIER.exactly()
                            .systemAndCode(identifier.system, identifier.value)
                    )
                    .returnBundle(Bundle::class.java)
                    .execute()
                break
            } catch (ex: Exception) {
                // do nothing
                log.error(ex.message)
                retry--
                if (retry == 0) throw ex
            }
        }
        if (bundle == null || !bundle.hasEntry()) return null
        return bundle.entryFirstRep.resource as Patient
    }
}
