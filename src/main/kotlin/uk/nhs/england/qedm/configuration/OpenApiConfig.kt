package uk.nhs.england.qedm.configuration


import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType

import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration



@Configuration
open class OpenApiConfig {
    var QEDM = ""

    val CLINICAL = "Clinical"
    val DIAGNOSTICS = "Diagnostics"
    val MEDICATION = "Medication"
    val ADMINISTRATION = "Administration"
    var MHD = "Documents"

    var PDQ = "Patient Demographic Queries"
    @Bean
    open fun customOpenAPI(
        fhirServerProperties: uk.nhs.england.qedm.configuration.FHIRServerProperties
    ): OpenAPI? {

        val oas = OpenAPI()
            .info(
                Info()
                    .title(fhirServerProperties.server.name)
                    .version(fhirServerProperties.server.version)
                    .description(""
                        /*
                                "\n\n For Patient Document Queries and Document Notifications, see [Access to Health Documents](http://lb-fhir-mhd-1617422145.eu-west-2.elb.amazonaws.com/)."
                                + "\n\n To add patient data, demographic queries and FHIR Subscription interactions, see [Events and Subscriptions](http://lb-hl7-tie-1794188809.eu-west-2.elb.amazonaws.com/)"
                                + "\n\n For Care Diretory Queries, see [Care Services Directory](http://lb-fhir-mcsd-1736981144.eu-west-2.elb.amazonaws.com/). This OAS also includes **Care Teams Management**"
*/
                              //  "## FHIR Implementation Guides"
                              //  + "\n\n [UK Core Implementation Guide (0.5.1)](https://simplifier.net/guide/ukcoreimplementationguide0.5.0-stu1/home?version=current)"
                              //  + "\n\n [NHS Digital Implementation Guide (2.6.0)](https://simplifier.net/guide/nhsdigital?version=2.6.0)"

                    )
                    .termsOfService("http://swagger.io/terms/")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
        oas.addServersItem(
            Server().description(fhirServerProperties.server.name).url(fhirServerProperties.server.baseUrl)
        )

        // Tags
        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(PDQ)
                .description("[HL7 FHIR Foundation Module](https://hl7.org/fhir/foundation-module.html) \n"
                        + " [IHE Patient Demographics Query for mobile (PDQm)](https://profiles.ihe.net/ITI/PDQm/index.html)")
        )
        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(QEDM + ADMINISTRATION)
                .description("[HL7 FHIR Administration Module](https://www.hl7.org/fhir/R4/administration-module.html) \n"

                        + " [IHE Mobile Query Existing Data PCC-44](https://www.ihe.net/uploadedFiles/Documents/PCC/IHE_PCC_Suppl_QEDm.pdf)")
        )
        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(QEDM +  CLINICAL)
                .description("[HL7 FHIR Clinical Module](https://www.hl7.org/fhir/R4/clinicalsummary-module.html) \n"

                + " [IHE Mobile Query Existing Data PCC-44](https://www.ihe.net/uploadedFiles/Documents/PCC/IHE_PCC_Suppl_QEDm.pdf)")
        )
        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(QEDM +  DIAGNOSTICS)
                .description(
                         "[HL7 FHIR Diagnostics Module](https://www.hl7.org/fhir/R4/clinicalsummary-module.html) \n"

                        + " [IHE Mobile Query Existing Data PCC-44](https://www.ihe.net/uploadedFiles/Documents/PCC/IHE_PCC_Suppl_QEDm.pdf)")
        )
        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(QEDM +  MEDICATION)
                .description(
                        "[HL7 FHIR Medications Module](https://www.hl7.org/fhir/R4/medications-module.html) \n"
                        + " [IHE Mobile Query Existing Data PCC-44](https://profiles.ihe.net/ITI/mCSD/ITI-90.html)")
        )

        // Administrative
        var patientItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(PDQ)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/Patient/{id}",patientItem)

        patientItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(PDQ)
                    .summary("Patient Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("_id")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("active")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Whether the patient record is active")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("family")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A portion of the family name of the patient")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("given")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A portion of the given name of the patient")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A patient identifier")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number|9876543210")
                    )
                    .addParametersItem(Parameter()
                        .name("telecom")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The value in any kind of telecom details of the patient")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("birthdate")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The patient's date of birth")
                        .schema(StringSchema())
                    )

                    .addParametersItem(Parameter()
                        .name("address-postalcode")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A postalCode specified in an address")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("gender")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Gender of the patient")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/Patient",patientItem)
        // Clinical

        // Endpoint
        var allegyIntoleranceItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + CLINICAL)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/AllergyIntolerance/{id}",allegyIntoleranceItem)

        allegyIntoleranceItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM + CLINICAL)
                    .summary("Allergies and Intolerances Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who the sensitivity is for")
                        .schema(StringSchema())
                    )

                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Date first version of the resource instance was recorded")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("External ids for this item")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("clinical-status")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("active | inactive | resolved")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/AllergyIntolerance",allegyIntoleranceItem)


        // Condition

        var conditionItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + CLINICAL)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )

        oas.path("/FHIR/R4/Condition/{id}",conditionItem)
        conditionItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + CLINICAL)
                    .summary("Allergies and Intolerances Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who has the condition?")
                        .schema(StringSchema())
                    )

                    .addParametersItem(Parameter()
                        .name("record-date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Date record was first recorded")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("identifier")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A unique identifier of the condition record")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("clinical-status")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The clinical status of the condition")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Condition",conditionItem)

        // DiagnosticReport
        var diagnosticReportItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM + DIAGNOSTICS)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/DiagnosticReport/{id}",diagnosticReportItem)

        diagnosticReportItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + DIAGNOSTICS)
                    .summary("Diagnostic Reports Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The subject of the report if a patient")
                        .schema(StringSchema())
                    )

                    .addParametersItem(Parameter()
                        .name("category")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Which diagnostic discipline/department created the report")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("code")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The code for the report, as opposed to codes for the atomic results, which are the names on the observation resource referred to from the result")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The clinically relevant time of the report")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/DiagnosticReport",diagnosticReportItem)

        // Observation
        var observationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + DIAGNOSTICS)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Observation/{id}",observationItem)

        observationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + DIAGNOSTICS)
                    .summary("Observation Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The subject that the observation is about (if patient)")
                        .schema(StringSchema())
                    )

                    .addParametersItem(Parameter()
                        .name("category")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The classification of the type of observation")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("code")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The code of the observation type")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Obtained date/time. If the obtained element is a period, a date that falls in the period")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Observation",observationItem)
// Procedure
        var procedureItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + CLINICAL)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Procedure/{id}",procedureItem)

        procedureItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + CLINICAL)
                    .summary("Procedure Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Search by subject - a patient")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("When the procedure was performed")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Procedure",procedureItem)

        // Encounter
        var encounterItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + ADMINISTRATION)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Encounter/{id}",encounterItem)

        encounterItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + ADMINISTRATION)
                    .summary("Encounter Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The patient or group present at the encounter")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("A date within the period the Encounter lasted")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Encounter",encounterItem)


        // MedicationRequest
        var medicationRequestItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + MEDICATION)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/MedicationRequest/{id}",medicationRequestItem)

        medicationRequestItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + MEDICATION)
                    .summary("MedicationRequest Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Returns prescriptions for a specific patient")
                        .schema(StringSchema())
                    )
                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Returns medication request to be administered on a specific date")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/MedicationRequest",medicationRequestItem)

        // MedicationStatement
        var medicationStatementItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + MEDICATION)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/MedicationStatement/{id}",medicationStatementItem)

        medicationStatementItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM + MEDICATION)
                    .summary("MedicationStatement Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Returns statements for a specific patient.")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/MedicationStatement",medicationStatementItem)
        // MedicationStatement
        var immunizationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + MEDICATION)
                    .summary("Read Endpoint")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                    )
            )
        oas.path("/FHIR/R4/Immunization/{id}",immunizationItem)

        immunizationItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM  + MEDICATION)
                    .summary("Immunization Option Search Parameters")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The patient for the vaccination record")
                        .schema(StringSchema())
                    )

            )
        oas.path("/FHIR/R4/Immunization",immunizationItem)

        var documentReferenceItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MHD)
                    .summary("Search DocumentReference")
                    .description("The Health Document Supplier shall support the following search parameters on the DocumentReference resource")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the document")
                        .schema(StringSchema())
                        .example("073eef49-81ee-4c2e-893b-bc2e4efd2630")
                    )
                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("When this document reference was created")
                        .schema(StringSchema())
                    )


            )
        oas.path("/FHIR/R4/DocumentReference",documentReferenceItem)

        var binaryItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MHD)
                    .summary("Any url can be used for retrieval of a raw document. See [document binary](https://care-connect-documents-api.netlify.app/api_documents_binary.html)")
                    .responses(getApiResponsesBinary())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                        .example("3074093f-183b-47d1-a16c-ea5c101b5451")
                    )
            )
        oas.path("/FHIR/R4/Binary/{id}",binaryItem)

        return oas
    }



    fun getApiResponses() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content().addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }

    fun getApiResponsesMarkdown() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content().addMediaType("text/markdown", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
    fun getApiResponsesXMLJSON() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
            .addMediaType("application/fhir+xml", MediaType().schema(StringSchema()._default("<>")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }

    fun getApiResponsesRAWJSON() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("application/json", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
    fun getPathItem(tag :String, name : String,fullName : String, param : String, example : String, description : String ) : PathItem {
        val pathItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(tag)
                    .summary("search-type")
                    .description(description)
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name(param)
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The uri that identifies the "+fullName)
                        .schema(StringSchema().format("token"))
                        .example(example)))
        return pathItem
    }
    fun getApiResponsesBinary() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("*/*", MediaType().schema(StringSchema()._default("{}")))
            .addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
            .addMediaType("application/fhir+xml", MediaType().schema(StringSchema()._default("<>")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
}
