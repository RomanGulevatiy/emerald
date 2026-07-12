package me.romangulevatiy.emerald.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = @Server(
                url = "http://localhost:8080",
                description = "Local server"
        ),
        info = @Info(
                title = "Emerald API",
                version = "1.0",
                summary = "API documentation for Emerald application",
                description = "Emerald is a RESTful web service based on Spring Boot." +
                        " The project is developed as a learning initiative to improve architecture skills" +
                        " and use best practices in backend development.",
                contact = @Contact(
                        name = "Roman Gulevatiy",
                        url = "https://github.com/RomanGulevatiy"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://github.com/RomanGulevatiy/emerald/blob/main/LICENSE"
                )
        ),
        security = {@SecurityRequirement(name = "bearerToken")}
)
@SecuritySchemes({
        @SecurityScheme(
                name = "bearerToken",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT"
        )
})
class SwaggerConfig {
}
