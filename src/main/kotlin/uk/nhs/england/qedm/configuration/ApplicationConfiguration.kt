package uk.nhs.england.qedm.configuration

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.StrictErrorHandler
import ca.uhn.fhir.rest.client.api.IGenericClient
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.AmazonSQSException
import com.amazonaws.services.sqs.model.CreateQueueRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import uk.nhs.england.qedm.interceptor.CognitoAuthInterceptor
import uk.nhs.england.qedm.provider.ValueSetProvider.Companion.logger
import uk.nhs.england.qedm.util.CorsFilter
import jakarta.servlet.Filter

@Configuration
open class ApplicationConfiguration(
    val messageProperties: MessageProperties
) {

    private val logger = LoggerFactory.getLogger(MessageProperties::class.java)
    @Bean("R4")
    open fun fhirR4Context(): FhirContext {
        val fhirContext = FhirContext.forR4()
        fhirContext.setParserErrorHandler(StrictErrorHandler())
        return fhirContext
    }

    @Bean
    open fun restTemplate(): RestTemplate {
        return RestTemplate()
    }



    @Bean
    fun getAWSclient(cognitoIdpInterceptor: CognitoAuthInterceptor?, mmessageProperties: uk.nhs.england.qedm.configuration.MessageProperties, @Qualifier("R4") ctx : FhirContext): IGenericClient? {
        val client: IGenericClient = ctx.newRestfulGenericClient(mmessageProperties.getCdrFhirServer())
        client.registerInterceptor(cognitoIdpInterceptor)
        return client
    }


    @Bean
    fun getSQS(): AmazonSQS {
        var sqs: AmazonSQS = AmazonSQSClientBuilder.defaultClient();
        logger.info("AWS SQS Queue "+ messageProperties.getAwsQueueName() + " configuration");
        // Don't connect if not enabled
        if (!messageProperties.getAWSQueueEnabled()) return sqs;

        val create_request : CreateQueueRequest = CreateQueueRequest(messageProperties.getAwsQueueName())
            .addAttributesEntry("DelaySeconds", "60")
            .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqs.createQueue(create_request);
        } catch (e : AmazonSQSException) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
            logger.info("AWS SQS Queue "+ messageProperties.getAwsQueueName() + " already exists");
        }
        return sqs;
    }


}
