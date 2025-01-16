package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.api.server.RequestDetails
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSPatient
import uk.nhs.england.qedm.awsProvider.AWSQuestionnaireResponse
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import jakarta.servlet.http.HttpServletRequest

@Component
class QuestionnaireResponseProvider(
    var cognitoAuthInterceptor: CognitoAuthInterceptor,
    var awsQuestionnaireResponse: AWSQuestionnaireResponse,
    val awsPatient: AWSPatient
)  {

    @Create
    fun create(
        theRequest: HttpServletRequest,
        @ResourceParam questionnaireResponse: QuestionnaireResponse
    ): MethodOutcome? {
        return awsQuestionnaireResponse.create(questionnaireResponse)
    }

    @Update
    fun update(
        theRequest: HttpServletRequest,
        @ResourceParam questionnaireResponse: QuestionnaireResponse,
        @IdParam theId: IdType?,
        @ConditionalUrlParam theConditional : String?,
        theRequestDetails: RequestDetails?
    ): MethodOutcome? {

        return awsQuestionnaireResponse.update(questionnaireResponse, theId)

    }

    @Read(type=QuestionnaireResponse::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): QuestionnaireResponse? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,null)
        return if (resource is QuestionnaireResponse) resource else null
    }
    @Search(type=QuestionnaireResponse::class)
    fun search(httpRequest : HttpServletRequest,
               @OptionalParam(name = QuestionnaireResponse.SP_PATIENT) patient: ReferenceParam?,
               @OptionalParam(name = QuestionnaireResponse.SP_SUBJECT) subject: ReferenceParam?,
               @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
             //.  @OptionalParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE) questionnaire : ReferenceParam?,
               @OptionalParam(name= QuestionnaireResponse.SP_STATUS) status : TokenParam?,
               @OptionalParam(name = "_getpages")  pages : StringParam?,
               @OptionalParam(name = "_count")  count : StringParam?,
               @OptionalParam(name = "_include")  include : StringParam?,
                @OptionalParam(name = "_revinclude") revinclude : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"QuestionnaireResponse")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }

}
