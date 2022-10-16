package uk.nhs.nhsdigital.fhirfacade.provider

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.OptionalParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.StructureMap
import org.springframework.stereotype.Component

@Component
class EncounterProvider : IResourceProvider {
    override fun getResourceType(): Class<Encounter> {
        return Encounter::class.java
    }

    @Read
    fun read(@IdParam internalId: IdType): Encounter? {
        return Encounter()
    }

    @Search
    fun search(

        @OptionalParam(name = Encounter.SP_PATIENT) patient : ReferenceParam,
        @OptionalParam(name = Encounter.SP_DATE)  date : DateRangeParam,
        @OptionalParam(name = Encounter.SP_IDENTIFIER)  identifier :TokenParam,
        @OptionalParam(name = Encounter.SP_RES_ID)  resid : StringParam
    ): List<StructureMap> {
        val patients = mutableListOf<StructureMap>()

        return patients
    }
}
