package com.ecommerce.config;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

@TestConfiguration
public class RestDocsConfigration {

  @Value("${server.scheme}")
  private String scheme;

  @Value("${server.host}")
  private String host;

  @Value("${server.port}")
  private int port;

  @Bean
  public RestDocumentationResultHandler write() {
    return MockMvcRestDocumentation.document(
        "{method-name}",
        preprocessRequest(
            prettyPrint(),
            modifyUris()
                .scheme(scheme)
                .host(host)
                .port(port)
        ),
        preprocessResponse(prettyPrint()
        )
    );
  }
}
