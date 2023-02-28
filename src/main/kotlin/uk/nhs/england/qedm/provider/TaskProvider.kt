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
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class TaskProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor) {

    @Read(type=Task::class)
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): Task? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null,"Task")
        return if (resource is Task) resource else null
    }

    @Search(type=Task::class)
    fun search(
        httpRequest : HttpServletRequest,
        @OptionalParam(name = Task.SP_PATIENT) task : ReferenceParam?,
        @OptionalParam(name = Task.SP_AUTHORED_ON)  date : DateRangeParam?,
        @OptionalParam(name = Task.SP_CODE)  code :TokenParam?,
        @OptionalParam(name = Task.SP_IDENTIFIER)  identifier :TokenParam?,
        @OptionalParam(name = Task.SP_STATUS)  status :TokenParam?,
        @OptionalParam(name = Task.SP_RES_ID)  resid : StringParam?,
        @OptionalParam(name = "_getpages")  pages : StringParam?,
        @OptionalParam(name = "_count")  count : StringParam?
    ): Bundle?{
        val tasks = mutableListOf<Task>()
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, httpRequest.queryString,"Task")
        if (resource != null && resource is Bundle) {
            return resource
        }
        return null
    }
}
