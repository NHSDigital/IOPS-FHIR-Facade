package uk.nhs.nhsdigital.qedm.interceptor

import ca.uhn.fhir.interceptor.api.Hook
import ca.uhn.fhir.interceptor.api.Interceptor
import ca.uhn.fhir.interceptor.api.Pointcut
import org.hl7.fhir.instance.model.api.IBaseConformance
import org.hl7.fhir.r4.model.*
import uk.nhs.nhsdigital.qedm.configuration.FHIRServerProperties
import uk.nhs.nhsdigital.qedm.configuration.MessageProperties

@Interceptor
class CapabilityStatementInterceptor(
    val fhirServerProperties: FHIRServerProperties,
    val messageProperties: MessageProperties
) {


    @Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
    fun customize(theCapabilityStatement: IBaseConformance) {

        // Cast to the appropriate version
        val cs: CapabilityStatement = theCapabilityStatement as CapabilityStatement

        cs.implementation.url = messageProperties.getFhirServerBaseUrl()
        cs.implementation.description = "NHS Digital UKCore API Reference Implementation"
    }

    fun getResourceComponent(type : String, cs : CapabilityStatement ) : CapabilityStatement.CapabilityStatementRestResourceComponent? {
        for (rest in cs.rest) {
            for (resource in rest.resource) {
                // println(type + " - " +resource.type)
                if (resource.type.equals(type))
                    return resource
            }
        }
        return null
    }

}
