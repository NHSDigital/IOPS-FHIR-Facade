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
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest


@Component
class MedicationStatementProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<MedicationStatement> {
        return MedicationStatement::class.java
    }

    @Read
    fun read(httpStatement : HttpServletRequest, @IdParam internalId: IdType): MedicationStatement? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpStatement.pathInfo, null)
        return if (resource is MedicationStatement) resource else null
    }

    @Search
    fun search(
        httpStatement : HttpServletRequest,
        @OptionalParam(name = MedicationStatement.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = MedicationStatement.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = MedicationStatement.SP_STATUS)  status :TokenParam?,
        @OptionalParam(name = MedicationStatement.SP_RES_ID)  resid : StringParam?
    ): List<MedicationStatement> {
        val medicationStatements = mutableListOf<MedicationStatement>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpStatement.pathInfo, httpStatement.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is MedicationStatement) medicationStatements.add(entry.resource as MedicationStatement)
            }
        }

        return medicationStatements
    }
}
