package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.*
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSPatient
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class ConditionProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor, var awsPatient: AWSPatient)  {


    @Read(type=Condition::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Condition? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Condition")
        return if (resource is Condition) resource else null
    }

    @Search(type=Condition::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Condition.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = Condition.SP_RECORDED_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Condition.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Condition.SP_CLINICAL_STATUS)  status: TokenOrListParam?,
        @OptionalParam(name = Condition.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"Condition")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
