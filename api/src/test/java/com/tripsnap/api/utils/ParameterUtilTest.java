package com.tripsnap.api.utils;

import com.google.gson.Gson;
import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import com.tripsnap.api.domain.dto.GroupAlbumParamDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@DisplayName("ParameterUtil 테스트")
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@Import({ParameterUtilTest.TestConfiguration.class, ParameterUtilTest.TestController.class})
class ParameterUtilTest {

    @EnableWebMvc
    static class TestConfiguration {

    }

    @RestController
    @RequestMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
    static class TestController {
        @PostMapping("/validation-and-convert/remove-photo-list")
        ResponseEntity<?> validationAndConvertRemovePhotoList(@RequestBody Map<String, Object> param) {
            ParameterUtil.validation(param, ValidationType.Collection.RemovePhotoList._class);
            List<Long> convert = (List<Long>) ParameterUtil.convert(param.get(ValidationType.Collection.RemovePhotoList.property), ValidationType.Collection.RemovePhotoList.type);
            return ResponseEntity.ok(convert);
        }
        @PostMapping("/validation-and-convert/new-photo-list")
        ResponseEntity<?> validationAndConvertNewPhotoList(@RequestBody Map<String, Object> param) {
            ParameterUtil.validation(param, ValidationType.Collection.NewPhotoList._class);
            List<AlbumPhotoInsDTO> convert = (List<AlbumPhotoInsDTO>) ParameterUtil.convert(param.get(ValidationType.Collection.NewPhotoList.property), ValidationType.Collection.NewPhotoList.type);
            return ResponseEntity.ok(convert);
        }
    }

    private MockMvc mvc;
    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void setup() {
        this.mvc  = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("[CollectionType] NewPhotoList validationAndConvert 테스트")
    void validationAndConvertTest_NewPhotoList() throws Exception {
        this.mvc.perform(
                post("/test/validation-and-convert/new-photo-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(Map.of(ValidationType.Collection.NewPhotoList.property,List.of(new AlbumPhotoInsDTO("testPhoto1"),new AlbumPhotoInsDTO("testPhoto2")))))
        );
    }

    @Test
    @DisplayName("[CollectionType] RemovePhotoList validationAndConvert 테스트")
    void validationAndConvertTest_RemovePhotoList() throws Exception {
        this.mvc.perform(
                post("/test/validation-and-convert/remove-photo-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(Map.of(ValidationType.Collection.RemovePhotoList.property,List.of(1,2,3))))
        );
    }

    @Test
    @DisplayName("PrimitiveWrapperType validationAndConvert 테스트")
    void validationAndConvertTest_PrimitiveWrapperType() {
        Map<String, Object> map = Map.of("testLongValue", 1, "testStringValue", "email@naver.com");
        Assertions.assertDoesNotThrow(() -> {
            Long testValue = ParameterUtil.validationAndConvert(map.get("testLongValue"), ValidationType.PrimitiveWrapper.EntityId, Long.class);
            Assertions.assertInstanceOf(Long.class, testValue);
        });

        Assertions.assertDoesNotThrow(() -> {
            String testValue = ParameterUtil.validationAndConvert(map.get("testStringValue"), ValidationType.PrimitiveWrapper.Email);
            Assertions.assertInstanceOf(String.class, testValue);
        });
    }

    @Test
    @DisplayName("validationAndConvert class 테스트")
    void validationAndConvert_class() {
        Map<String, Object> validValue = Map.of("page", 1, "pagePerCnt", 2);
        Assertions.assertDoesNotThrow(() -> {
            PageDTO pageDTO = ParameterUtil.validationAndConvert(validValue, PageDTO.class);
            Assertions.assertEquals(pageDTO.page(), validValue.get("page"));
            Assertions.assertEquals(pageDTO.pagePerCnt(), validValue.get("pagePerCnt"));
        });

        Map<String, Object> wrongValue = Map.of("page", 1, "pagePerCnt", "gb4390");
        Assertions.assertThrows(ServiceException.class, () -> {
            PageDTO pageDTO = ParameterUtil.validationAndConvert(wrongValue, PageDTO.class);
        });

        Map<String, Object> validValue2 = Map.of("groupId", 1L, "albumId", 2L);
        Assertions.assertDoesNotThrow(() -> {
            GroupAlbumParamDTO paramDTO = ParameterUtil.validationAndConvert(validValue2, GroupAlbumParamDTO.class);
            Assertions.assertEquals(paramDTO.getAlbumId(), validValue2.get("albumId"));
            Assertions.assertEquals(paramDTO.getGroupId(), validValue2.get("groupId"));
        });
    }

    @Test
    @DisplayName("validationAndConvert 테스트")
    void validationAndConvert() {
        Map<String, Object> value = Map.of("email", "email@emial.test", "id", 196L);
        Assertions.assertDoesNotThrow(() -> {
            String email = ParameterUtil.validationAndConvert(value.get("email"), ValidationType.PrimitiveWrapper.Email);
            Assertions.assertInstanceOf(String.class, email);

        });
        Assertions.assertDoesNotThrow(() -> {
            Long id = ParameterUtil.validationAndConvert(value.get("id"), ValidationType.PrimitiveWrapper.EntityId, Long.class);
            Assertions.assertInstanceOf(Long.class, id);

        });
    }
}