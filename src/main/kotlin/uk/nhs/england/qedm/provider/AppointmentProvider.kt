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
class AppointmentProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor, val awsPatient: AWSPatient)  {


    @Read(type = Appointment::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Appointment? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null, null)
        return if (resource is Appointment) resource else null
    }

    @Search(type = Appointment::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Appointment.SP_PATIENT) appointment : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = Appointment.SP_STATUS) status : TokenOrListParam?,
        @OptionalParam(name = Appointment.SP_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Appointment.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Appointment.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"Appointment")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
