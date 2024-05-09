package com.tripsnap.api.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;


@DisplayName("swagger 설정 테스트")
class SwaggerConfigTest {
    @DisplayName("url pattern 테스트")
    @Test
    void urlPatternTest() {
        Pattern pattern = Pattern.compile("^(?!/v3|/swagger-ui).*");
        Assertions.assertAll(
                () -> Assertions.assertTrue(pattern.matcher("/join").matches()),
                () -> Assertions.assertTrue(pattern.matcher("/group/1").matches()),
                () -> Assertions.assertFalse(pattern.matcher("/v3/api-docs").matches()),
                () -> Assertions.assertFalse(pattern.matcher("/swagger-ui/index.html").matches()),
                () -> Assertions.assertFalse(pattern.matcher("/swagger-ui/swagger-initializer.js").matches()),
                () -> Assertions.assertFalse(pattern.matcher("/v3/api-docs/swagger-config").matches()),
                () -> Assertions.assertFalse(pattern.matcher("/v3").matches())
        );

    }
}