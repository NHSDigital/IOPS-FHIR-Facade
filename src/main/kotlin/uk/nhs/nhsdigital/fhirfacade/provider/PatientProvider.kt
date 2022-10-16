package uk.nhs.nhsdigital.fhirfacade.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.param.DateRangeParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.StructureMap
import org.springframework.stereotype.Component

@Component
class PatientProvider : IResourceProvider {
    override fun getResourceType(): Class<Patient> {
        return Patient::class.java
    }

    @Read
    fun read(@IdParam internalId: IdType): Patient? {
        return Patient()
    }

    @Search
    fun search(
        @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) addressPostcode : StringParam,
        @OptionalParam(name= Patient.SP_BIRTHDATE) birthDate : DateRangeParam,
        @OptionalParam(name= Patient.SP_EMAIL) email : StringParam,
        @OptionalParam(name = Patient.SP_FAMILY) familyName : StringParam,
        @OptionalParam(name= Patient.SP_GENDER) gender :StringParam,
        @OptionalParam(name= Patient.SP_GIVEN) givenName :StringParam,
        @OptionalParam(name = Patient.SP_IDENTIFIER) identifier :TokenParam,
        @OptionalParam(name= Patient.SP_NAME) name :StringParam,
        @OptionalParam(name= Patient.SP_PHONE) phone : StringParam
    ): List<StructureMap> {
        val patients = mutableListOf<StructureMap>()

        return patients
    }
}
