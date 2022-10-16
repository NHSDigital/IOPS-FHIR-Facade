package uk.nhs.nhsdigital.fhirfacade;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {

    private static SpringBootLambdaContainerHandler handler;

    static {

        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(FHIRFacade.class);
        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
      // Need to review this, not sorting with server.servlet.context-path  handler.stripBasePath("/NHSD");
    }

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {

        handler.proxyStream(inputStream, outputStream, context);
    }
}
