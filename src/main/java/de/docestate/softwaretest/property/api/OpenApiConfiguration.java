package de.docestate.softwaretest.property.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI propertyApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Immobilien REST API")
                        .description("REST-Schnittstelle zur Verwaltung von Immobilien.")
                        .version("v1")
                        .contact(new Contact().name("DocEstate")));
    }
}
