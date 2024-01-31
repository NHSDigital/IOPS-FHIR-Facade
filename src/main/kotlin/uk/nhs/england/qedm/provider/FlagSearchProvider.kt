package uk.nhs.england.qedm.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.api.server.RequestDetails
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringAndListParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenOrListParam
import ca.uhn.fhir.rest.param.TokenParam
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.qedm.awsProvider.AWSFlag
import uk.nhs.england.qedm.awsProvider.AWSPatient
import uk.nhs.england.qedm.configuration.FHIRServerProperties
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest


@Component
class FlagSearchProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor, var awsPatient: AWSPatient, var awsFlag: AWSFlag)  {

    @Create
    fun create(
        theRequest: HttpServletRequest,
        @ResourceParam flag: Flag,
    ): MethodOutcome? {
        return awsFlag.create(flag)
    }

    @Update
    fun update(
        theRequest: HttpServletRequest,
        @ResourceParam flag: Flag,
        @IdParam theId: IdType?,
        @ConditionalUrlParam theConditional : String?,
        theRequestDetails: RequestDetails?
    ): MethodOutcome? {

        return awsFlag.update(flag, theId)

    }

    @Read(type=Flag::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Flag? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null, null)
        return if (resource is Flag) resource else null
    }
    @Search(type=Flag::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Flag.SP_PATIENT) patient : ReferenceParam?,
        @OptionalParam(name = "patient:identifier") nhsNumber : TokenParam?,
        @OptionalParam(name = Flag.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Flag.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_include")  pages : StringAndListParam?,
        @OptionalParam(name = "_getpages")  include : StringParam?,
        @OptionalParam(name = "_sort")  sort : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle? {
        val queryString = awsPatient.processQueryString(httpRequest.queryString,nhsNumber)
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, queryString, "Flag")
        if (resource != null && resource is Bundle) {
            return resource
        }

        return null
    }
}
