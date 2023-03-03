package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSPatient
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest


@Component
class MedicationStatementProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor,
    val awsPatient: AWSPatient)  {


    @Read(type=MedicationStatement::class)
    fun read(httpStatement : HttpServletRequest, @IdParam internalId: IdType): MedicationStatement? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpStatement.pathInfo, null,null)
        return if (resource is MedicationStatement) resource else null
    }

    @Search(type=MedicationStatement::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = MedicationStatement.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = MedicationStatement.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = MedicationStatement.SP_STATUS)  status :TokenParam?,
        @OptionalParam(name = MedicationStatement.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString,"MedicationStatement")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
