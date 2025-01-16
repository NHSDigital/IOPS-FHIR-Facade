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
import jakarta.servlet.http.HttpServletRequest

@Component
class ImmunisationProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor,
    val awsPatient: AWSPatient)  {

    @Read(type = Immunization::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Immunization? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Immunization")
        return if (resource is Immunization) resource else null
    }

    @Search(type =  Immunization::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Immunization.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = Immunization.SP_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Immunization.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Immunization.SP_STATUS)  status: TokenOrListParam?,
        @OptionalParam(name = Immunization.SP_RES_ID)  resid : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"Immunization")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
