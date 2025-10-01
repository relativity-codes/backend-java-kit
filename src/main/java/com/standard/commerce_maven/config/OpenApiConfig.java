package com.standard.commerce_maven.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Configuration
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Standard API", version = "1.0", description = "REST API Documentation for Standard Platform", contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Ukweh C. Everest", email = "ukweheverest@gmail.com", url = "https://relativity-codes.vercel.app"), license = @io.swagger.v3.oas.annotations.info.License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT Authorization header using Bearer scheme. Example: 'Bearer {token}'")
public class OpenApiConfig {

    @Bean
    @Primary 
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .openapi("3.1.0")
                .info(new Info()
                        .title("Standard API")
                        .version("1.0")
                        .description("REST API Documentation for Standard Platform")
                        .contact(new Contact()
                                .name("Ukweh C. Everest")
                                .email("ukweheverest@gmail.com")
                                .url("https://relativity-codes.vercel.app"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addResponses("UnauthorizedError",
                                new ApiResponse().description("Authentication information is missing or invalid"))
                        .addResponses("ForbiddenError",
                                new ApiResponse().description("You don't have permission to access this resource"))
                        .addResponses("NotFoundError",
                                new ApiResponse().description("The specified resource was not found"))
                        .addResponses("ValidationError",
                                new ApiResponse().description("Validation error on the request"))
                        .addResponses("ServerError", new ApiResponse().description("Internal server error"))
                        .addSchemas("PageResponse", new Schema<>()
                                .name("PageResponse")
                                .description("Standard paginated response structure")
                                .addProperty("content", new Schema<>().type("array"))
                                .addProperty("totalElements", new Schema<>().type("integer"))
                                .addProperty("totalPages", new Schema<>().type("integer"))
                                .addProperty("size", new Schema<>().type("integer"))
                                .addProperty("number", new Schema<>().type("integer"))));
    }

    @Bean
    public CommandLineRunner checkMapperModules(ObjectMapper mapper) {
        return args -> {
            System.out.println("=== REGISTERED JACKSON MODULES ===");
            mapper.getRegisteredModuleIds().forEach(System.out::println);
            System.out.println("=================================");
        };
    }

    @Bean
    public Map<String, Object> dateFormatMapping() {
        return Map.of(
                "org.springdoc.core.SpringDocDateFormat", "yyyy-MM-dd",
                "org.springdoc.core.SpringDocTimeFormat", "HH:mm:ss",
                "org.springdoc.core.SpringDocDateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }
}