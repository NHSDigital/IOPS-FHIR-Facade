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
                  val fhirServerProperties: FHIRServerProperties) {


    private val log = LoggerFactory.getLogger("FHIRAudit")

    fun processQueryString(httpString: String, nhsNumber : TokenParam? ) : String? {
        var queryString = httpString
        if (queryString != null && nhsNumber != null) {
            val params: List<String> = queryString.split("&")
            val newParams = mutableListOf<String>()
            if (nhsNumber.value == null || nhsNumber.system == null) throw UnprocessableEntityException("Malformed patient identifier parameter both system and value are required.")
            val patient = get(Identifier().setSystem(nhsNumber.system).setValue(nhsNumber.value))
            if (patient != null) {
                for (param in params) {
                    val name: String = param.split("=").get(0)
                    if (java.net.URLDecoder.decode(name, StandardCharsets.UTF_8.name()).equals("patient:identifier")) {
                        newParams.add( "patient=" + patient.idElement.idPart)
                    } else {
                        newParams.add(param)
                    }
                }
                queryString = newParams.joinToString("&")
            }
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
