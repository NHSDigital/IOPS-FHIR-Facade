package uk.nhs.nhsdigital.qedm.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.nhsdigital.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class ConditionProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) : IResourceProvider {
    override fun getResourceType(): Class<Condition> {
        return Condition::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Condition? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is Condition) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Condition.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = Condition.SP_RECORDED_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Condition.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Condition.SP_CLINICAL_STATUS)  status :TokenParam?,
        @OptionalParam(name = Condition.SP_RES_ID)  resid : StringParam?
    ): List<Condition> {
        val conditions = mutableListOf<Condition>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                if (entry.hasResource() && entry.resource is Condition) conditions.add(entry.resource as Condition)
            }
        }

        return conditions
    }
}
