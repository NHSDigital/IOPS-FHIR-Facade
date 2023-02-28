package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.api.server.RequestDetails
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class PatientSearchProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) {

    @Read(type=Patient::class)
    fun read( httpRequest : HttpServletRequest,@IdParam internalId: IdType): Patient? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo,  null, null)
        return if (resource is Patient) resource else null
    }
    @Search(type = Patient::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) addressPostcode : StringParam?,
        @OptionalParam(name= Patient.SP_BIRTHDATE) birthDate : DateRangeParam?,
        @OptionalParam(name= Patient.SP_EMAIL) email : StringParam?,
        @OptionalParam(name = Patient.SP_FAMILY) familyName : StringParam?,
        @OptionalParam(name= Patient.SP_GENDER) gender : StringParam?,
        @OptionalParam(name= Patient.SP_GIVEN) givenName : StringParam?,
        @OptionalParam(name = Patient.SP_IDENTIFIER) identifier : TokenParam?,
        @OptionalParam(name= Patient.SP_NAME) name : StringParam?,
        @OptionalParam(name= Patient.SP_TELECOM) phone : StringParam?
    ): Bundle? {
        val patients = mutableListOf<Patient>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"Patient")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }

}
