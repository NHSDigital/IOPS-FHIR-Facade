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
import io.swagger.v3.oas.annotations.tags.Tag
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSPatient
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import jakarta.servlet.http.HttpServletRequest

@Component
@Tag(name="Medications")
class MedicationDispenseProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor,
    val awsPatient: AWSPatient)  {


    @Read(type=MedicationDispense::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): MedicationDispense? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"MedicationDispense")
        return if (resource is MedicationDispense) resource else null
    }

    @Search(type=MedicationDispense::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = MedicationDispense.SP_PATIENT) medicationDispense : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = MedicationDispense.SP_WHENHANDEDOVER)  date : DateRangeParam?,
        @OptionalParam(name = MedicationDispense.SP_PRESCRIPTION)  prescription: ReferenceParam?,
        @OptionalParam(name = MedicationDispense.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = MedicationDispense.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"MedicationDispense")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
