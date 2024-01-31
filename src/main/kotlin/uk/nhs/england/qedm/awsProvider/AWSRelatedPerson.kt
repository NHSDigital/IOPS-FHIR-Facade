package uk.nhs.england.qedm.awsProvider

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.client.api.IGenericClient
import org.hl7.fhir.r4.model.*

import org.hl7.fhir.r4.model.RelatedPerson
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import uk.nhs.england.qedm.configuration.MessageProperties

@Component
class AWSRelatedPerson (val messageProperties: MessageProperties, val awsClient: IGenericClient,
               //sqs: AmazonSQS?,
                        @Qualifier("R4") val ctx: FhirContext
                               ) {


    private val log = LoggerFactory.getLogger("FHIRAudit")

    fun update(relatedPerson: RelatedPerson, internalId: IdType?): MethodOutcome? {
        var response: MethodOutcome? = null

        var retry = 3
        while (retry > 0) {
            try {
                response = awsClient!!.update().resource(relatedPerson).withId(internalId).execute()
                log.info("AWS RelatedPerson updated " + response.resource.idElement.value)
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
    fun create(newRelatedPerson: RelatedPerson): MethodOutcome? {
        val awsBundle: Bundle? = null
        var response: MethodOutcome? = null

        var retry = 3
        while (retry > 0) {
            try {
                response = awsClient
                    .create()
                    .resource(newRelatedPerson)
                    .execute()
                val relatedPerson = response.resource as RelatedPerson
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
 
   


}
