package com.swifre.trade_fx_maven;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Trade FX API", version = "1.0", description = "Documentation for Trade FX API", contact = @Contact(name = "Ukweh C. Everest", email = "ukweheverest@gmail.com", url = "https://swifre.com"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
public class OpenApiConfig {

    /**
     * This class is used to configure OpenAPI documentation for the application.
     * It can be extended to customize the OpenAPI configuration.
     * Currently, it does not contain any specific configurations.
     */
    // Add OpenAPI configuration methods here if needed in the future.

}
