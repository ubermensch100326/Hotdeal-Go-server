package com.budge.hotdeal_go.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

    // Swagger-UI 3.x 확인
	// http://{public IPv4}/swagger-ui/index.html

    private final ServerProperties serverProperties;

    public SwaggerConfiguration(ServerProperties serverProperties) {
        super();
        this.serverProperties = serverProperties;
    }

    @Bean
    public Docket allApi() {
        final ApiInfo apiInfo = new ApiInfoBuilder()
                .title("HotDeal Go!")
                // .description("<h3>핫딜에 </h3>")
                .version("1.0")
                .build();

        return new Docket(DocumentationType.OAS_30)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .apiInfo(apiInfo) // 문서에 대한 정보를 설정한다.
                .select() // ApiSelectorBuilder를 반환하며 상세한 설정 처리
                .apis(RequestHandlerSelectors.basePackage("com.budge.hotdeal_go.controller"))// 대상으로하는 api 설정
                .paths(PathSelectors.ant("/**")) // controller에서 swagger를 지정할 대상 path 설정
                .build()
                .useDefaultResponseMessages(false);
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }
}