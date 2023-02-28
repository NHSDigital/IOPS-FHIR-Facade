package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class ConditionProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor)  {


    @Read(type=Condition::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Condition? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Condition")
        return if (resource is Condition) resource else null
    }

    @Search(type=Condition::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Condition.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = Condition.SP_RECORDED_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Condition.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Condition.SP_CLINICAL_STATUS)  status :TokenParam?,
        @OptionalParam(name = Condition.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val conditions = mutableListOf<Condition>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"Condition")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
