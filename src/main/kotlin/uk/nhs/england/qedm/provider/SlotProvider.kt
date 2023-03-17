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
import javax.servlet.http.HttpServletRequest

@Component
class SlotProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor, val awsPatient: AWSPatient)  {


    @Read(type = Slot::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Slot? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null, null)
        return if (resource is Slot) resource else null
    }

    @Search(type = Slot::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Slot.SP_START)  date : DateRangeParam?,
        @OptionalParam(name = Slot.SP_SPECIALTY)  specialty :TokenOrListParam?,
        @OptionalParam(name = Slot.SP_STATUS)  active :TokenParam?,
        @OptionalParam(name = Slot.SP_SCHEDULE) schedule :ReferenceParam?,
        @OptionalParam(name = Slot.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Slot.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,null)

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"Slot")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
