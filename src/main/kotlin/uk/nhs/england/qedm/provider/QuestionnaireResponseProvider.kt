package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSQuestionnaireResponse
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class QuestionnaireResponseProvider(
    var cognitoAuthInterceptor: CognitoAuthInterceptor,
    var awsQuestionnaireResponse: AWSQuestionnaireResponse
)  {

    @Read(type=QuestionnaireResponse::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): QuestionnaireResponse? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,null)
        return if (resource is QuestionnaireResponse) resource else null
    }
    @Search(type=QuestionnaireResponse::class)
    fun search(httpRequest : HttpServletRequest,
               @RequiredParam(name = QuestionnaireResponse.SP_PATIENT) patient: ReferenceParam?,
             //.  @OptionalParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE) questionnaire : ReferenceParam?,
               @OptionalParam(name= QuestionnaireResponse.SP_STATUS) status : TokenParam?,
               @OptionalParam(name = "_getpages")  pages : StringParam?,
               @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"QuestionnaireResponse")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }

}
