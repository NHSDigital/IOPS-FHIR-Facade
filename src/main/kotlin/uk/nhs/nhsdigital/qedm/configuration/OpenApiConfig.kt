package uk.nhs.nhsdigital.qedm.configuration


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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration



@Configuration
open class OpenApiConfig {
    var QEDM = "Query for Existing Patient Data"
    val CLINICAL = "Clinical"
    val DIAGNOSTICS = "Diagnostics"
    val MEDICATION = "Medication"


    @Bean
    open fun customOpenAPI(
        fhirServerProperties: FHIRServerProperties
    ): OpenAPI? {

        val oas = OpenAPI()
            .info(
                Info()
                    .title(fhirServerProperties.server.name)
                    .version(fhirServerProperties.server.version)
                    .description(
                        fhirServerProperties.server.name
                                + "\n "
                                + "\n [UK Core Implementation Guide (0.5.1)](https://simplifier.net/guide/ukcoreimplementationguide0.5.0-stu1/home?version=current)"
                                + "\n\n [NHS Digital Implementation Guide (2.6.0)](https://simplifier.net/guide/nhsdigital?version=2.6.0)"
                    )
                    .termsOfService("http://swagger.io/terms/")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )


        // Tags

        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(QEDM + " - " + CLINICAL)
                .description("[HL7 FHIR Clinical Module](https://www.hl7.org/fhir/R4/clinicalsummary-module.html) \n"

                + " [IHE Mobile Query Existing Data PCC-44](https://profiles.ihe.net/ITI/mCSD/ITI-90.html)")
        )
        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(QEDM + " - " + DIAGNOSTICS)
                .description(
                         "[HL7 FHIR Diagnostics Module](https://www.hl7.org/fhir/R4/clinicalsummary-module.html) \n"

                        + " [IHE Mobile Query Existing Data PCC-44](https://profiles.ihe.net/ITI/mCSD/ITI-90.html)")
        )
        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(QEDM + " - " + MEDICATION)
                .description(
                        "[HL7 FHIR Medications Module](https://www.hl7.org/fhir/R4/medications-module.html) \n"
                        + " [IHE Mobile Query Existing Data PCC-44](https://profiles.ihe.net/ITI/mCSD/ITI-90.html)")
        )

        // Clinical

        // Endpoint
        var allegyIntoleranceItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM + " - " + CLINICAL)
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

        oas.path("/FHIR/R4/AllegyIntolerance/{id}",allegyIntoleranceItem)

        allegyIntoleranceItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM + " - " + CLINICAL)
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
        oas.path("/FHIR/R4/AllegyIntolerance",allegyIntoleranceItem)


        // Condition

        var conditionItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(QEDM + " - " + CLINICAL)
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
                    .addTagsItem(QEDM + " - " + CLINICAL)
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
                    .addTagsItem(QEDM + " - " + DIAGNOSTICS)
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
                    .addTagsItem(QEDM + " - " + DIAGNOSTICS)
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
}
