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
class ServiceRequestProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor,
    val awsPatient: AWSPatient)  {


    @Read(type=ServiceRequest::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): ServiceRequest? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"ServiceRequest")
        return if (resource is ServiceRequest) resource else null
    }

    @Search(type=ServiceRequest::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = ServiceRequest.SP_PATIENT) serviceRequest : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = ServiceRequest.SP_AUTHORED)  date : DateRangeParam?,
        @OptionalParam(name = ServiceRequest.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = ServiceRequest.SP_STATUS)  status: TokenOrListParam?,
        @OptionalParam(name = ServiceRequest.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"ServiceRequest")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
