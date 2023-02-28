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
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.configuration.FHIRServerProperties
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class ObservationProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor, var fhirServerProperties: FHIRServerProperties) : IResourceProvider {
    override fun getResourceType(): Class<Observation> {
        return Observation::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Observation? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is Observation) resource else null
    }

    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Observation.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = Observation.SP_DATE)  date : DateRangeParam?,
        @OptionalParam(name = Observation.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Observation.SP_CODE)  status :TokenParam?,
        @OptionalParam(name = Observation.SP_CATEGORY)  category: TokenParam?,
        @OptionalParam(name = Observation.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Resource? {
        val observations = mutableListOf<Observation>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString)
        if (resource != null && resource is Bundle) {
            for (entry in resource.entry) {
                entry.fullUrl = fhirServerProperties.server.baseUrl + "/FHIR/R4/"+entry.resource.javaClass.simpleName + "/"+entry.resource.idElement.idPart
            }
            return resource
        }

        return null
    }
}
