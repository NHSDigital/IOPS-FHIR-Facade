package uk.nhs.england.qedm.awsProvider

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.rest.gclient.ICriterion
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.param.UriParam
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.r4.model.*

import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import uk.nhs.england.qedm.configuration.MessageProperties
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class AWSQuestionnaireResponse (val messageProperties: MessageProperties, val awsClient: IGenericClient,
               //sqs: AmazonSQS?,
                                @Qualifier("R4") val ctx: FhirContext,
                                val awsQuestionnaire: AWSQuestionnaire,
                               ) {


    private val log = LoggerFactory.getLogger("FHIRAudit")

    fun update(questionnaireResponse: QuestionnaireResponse, internalId: IdType?): MethodOutcome? {
        var response: MethodOutcome? = null

        var retry = 3
        while (retry > 0) {
            try {
                response = awsClient!!.update().resource(questionnaireResponse).withId(internalId).execute()
                log.info("AWS QuestionnaireResponse updated " + response.resource.idElement.value)
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
    fun create(newQuestionnaireResponse: QuestionnaireResponse): MethodOutcome? {
        val awsBundle: Bundle? = null
        var response: MethodOutcome? = null

        var retry = 3
        while (retry > 0) {
            try {
                response = awsClient
                    .create()
                    .resource(newQuestionnaireResponse)
                    .execute()
                val questionnaireResponse = response.resource as QuestionnaireResponse
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

    fun search(
       patient: ReferenceParam?,
       questionnaire : ReferenceParam?,
       status : TokenParam?
    ): List<QuestionnaireResponse>? {
        var awsBundle: Bundle? = null
        val list = mutableListOf<QuestionnaireResponse>()

        // if (uriParam == null || uriParam.value == null) throw UnprocessableEntityException("url parameter is mandatory")
        var retry = 3
        while (retry > 0) {
            try {

                var criteria1 :ICriterion<ReferenceClientParam>? = null
                var criteria2 :ICriterion<ReferenceClientParam>? = null

                if (questionnaire != null) {
                    // Need to search registry for Questionnaire id

                    val listQ = awsQuestionnaire.search(UriParam().setValue(questionnaire.value))
                    if (listQ == null || listQ.size==0) return list
                    criteria1 = QuestionnaireResponse.QUESTIONNAIRE.hasId("Questionnaire/"+listQ[0].idElement.idPart)
                }

                if (patient != null) {
                    val criteriaPat = QuestionnaireResponse.PATIENT.hasId(java.net.URLDecoder.decode(patient.value, StandardCharsets.UTF_8.name()))
                    if (criteria1 == null) {
                        criteria1 = criteriaPat
                    } else {
                        criteria2 = criteriaPat
                    }
                }

                if (criteria1 == null) {
                    awsBundle = awsClient!!.search<IBaseBundle>().forResource(QuestionnaireResponse::class.java)
                        .returnBundle(Bundle::class.java)
                        .execute()
                } else {
                    if (criteria2 != null) {
                        awsBundle = awsClient!!.search<IBaseBundle>().forResource(QuestionnaireResponse::class.java)
                            .where(
                                criteria1
                            )
                            .and(criteria2)
                            .returnBundle(Bundle::class.java)
                            .execute()
                    } else {
                        awsBundle = awsClient!!.search<IBaseBundle>().forResource(QuestionnaireResponse::class.java)
                            .where(
                                criteria1
                            )
                            .returnBundle(Bundle::class.java)
                            .execute()
                    }
                }
                break
            } catch (ex: Exception) {
                // do nothing
                log.error(ex.message)
                retry--
                if (retry == 0) throw ex
            }
        }
        if (awsBundle != null) {
            if (awsBundle.hasEntry() ) {
                for (entry in awsBundle.entry) {
                    if (entry.hasResource() && entry.resource is QuestionnaireResponse) {
                        list.add(entry.resource as QuestionnaireResponse)
                    }
                }
            }
        }
        return list
    }
   


}
