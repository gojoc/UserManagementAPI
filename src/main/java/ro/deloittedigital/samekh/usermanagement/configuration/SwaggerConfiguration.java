package ro.deloittedigital.samekh.usermanagement.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
public class SwaggerConfiguration {
    private static final String AUTHORIZATION = "Authorization";

    private List<SecurityReference> securityReferences() {
        AuthorizationScope[] authorizationScopes = {new AuthorizationScope("global",
                "accessEverything")};
        return List.of(new SecurityReference(AUTHORIZATION, authorizationScopes));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(securityReferences())
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey(AUTHORIZATION, AUTHORIZATION, "header");
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()))
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }
}
