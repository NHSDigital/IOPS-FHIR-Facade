package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenOrListParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSPatient
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import jakarta.servlet.http.HttpServletRequest

@Component
class ScheduleProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor, val awsPatient: AWSPatient)  {


    @Read(type = Schedule::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Schedule? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null, null)
        return if (resource is Schedule) resource else null
    }

    @Search(type = Schedule::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Schedule.SP_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Schedule.SP_SPECIALTY)  specialty :TokenOrListParam?,
        @OptionalParam(name = Schedule.SP_ACTIVE)  active :TokenParam?,
        @OptionalParam(name = Schedule.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Schedule.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,null)

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"Schedule")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
