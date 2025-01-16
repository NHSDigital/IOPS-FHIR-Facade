package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.Transaction
import ca.uhn.fhir.rest.annotation.TransactionParam
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.api.server.RequestDetails
import ca.uhn.fhir.rest.server.RestfulServerUtils
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.configuration.MessageProperties
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import jakarta.servlet.http.HttpServletRequest

@Component
class TransactionProvider(private val cognitoAuthInterceptor: CognitoAuthInterceptor,
                          private val messageProperties: MessageProperties
   ) {


    @Transaction
    fun transaction(httpRequest : HttpServletRequest, requestDetails: RequestDetails?, @TransactionParam bundle:Bundle,
    ): Bundle {
        var encoding = RestfulServerUtils.determineRequestEncodingNoDefault(requestDetails)
        if (encoding == null) encoding = EncodingEnum.JSON
        val response = cognitoAuthInterceptor.postResource(encoding, bundle)
        return response.resource as Bundle
    }

}
