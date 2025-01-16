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
class AllergyIntoleranceProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor,
    var awsPatient : AWSPatient)  {


    @Read(type =AllergyIntolerance::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): AllergyIntolerance? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,null)
        return if (resource is AllergyIntolerance) resource else null
    }

    @Search(type=AllergyIntolerance::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = AllergyIntolerance.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = AllergyIntolerance.SP_DATE)  date : DateRangeParam?,
        @OptionalParam(name = AllergyIntolerance.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = AllergyIntolerance.SP_CLINICAL_STATUS)  status: TokenOrListParam?,
        @OptionalParam(name = AllergyIntolerance.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"AllergyIntolerance")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }
}
