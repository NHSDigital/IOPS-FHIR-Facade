package uk.nhs.england.qedm.provider

import ca.uhn.fhir.context.FhirContext

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import mu.KLogging
import org.hl7.fhir.r4.model.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSPatient
import uk.nhs.england.qedm.configuration.MessageProperties
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class ValueSetProvider (
                        private val cognitoAuthInterceptor: CognitoAuthInterceptor,
    private val messageProperties: MessageProperties
) : IResourceProvider {
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    override fun getResourceType(): Class<ValueSet> {
        return ValueSet::class.java
    }

    companion object : KLogging()


    @Operation(name = "\$expand", idempotent = true)
    fun expand(
        httpRequest : HttpServletRequest,
        @ResourceParam valueSet: ValueSet?,
        @OperationParam(name = ValueSet.SP_URL) url: TokenParam?,
        @OperationParam(name = "filter") filter: StringParam?): ValueSet? {

        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(messageProperties.getValidationFhirServer()+"/FHIR/R4",httpRequest.pathInfo, httpRequest.queryString,"ValueSet")
        if (resource != null && resource is ValueSet) {
            return resource
        }
        return null
    }
}
