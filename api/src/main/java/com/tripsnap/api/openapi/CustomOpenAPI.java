package com.tripsnap.api.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomOpenAPI {
    private String pathname;
    private Operation operation;
    private RequestBody requestBody;
    private ApiResponses apiResponses;
    private Consumer<CustomOpenAPI> function;
    private String tag;
    private String summary;

    public static class Decorator {
        private CustomOpenAPI api;

        public static Decorator get(OpenAPI openAPI) {
            return new Decorator((api) -> {
                PathItem pathItem = new PathItem().get(api.operation);
                if(StringUtils.hasText(api.summary)) {
                    pathItem.description(api.summary);
                }
                openAPI.path(api.pathname, pathItem);
            });
        }

        public static Decorator post(OpenAPI openAPI) {
            return new Decorator((api) -> {
                PathItem pathItem = new PathItem().post(api.operation);
                if(StringUtils.hasText(api.summary)) {
                    pathItem.description(api.summary);
                }
                openAPI.path(api.pathname, pathItem);
            });
        }

        public Decorator(Consumer<CustomOpenAPI> function) {
            api = new CustomOpenAPI();
            api.function = function;
        }

        public Decorator summary(String summary) {
            api.summary = summary;
            return this;
        }

        public Decorator pathname(String pathname) {
            api.pathname = pathname;
            return this;
        }

        public Decorator tag(String tag) {
            api.tag = tag;
            return this;
        }


        public Decorator requestBody(List<Tuple2<String, String>> paramList) {
            api.requestBody= new RequestBody().content(this.content(paramList));
            return this;
        }

        public Decorator response(String code, String description ) {
            return response(code, description, null);
        }

        public Decorator response(String code, List<Tuple2<String, String>> paramList) {
            return response(code, null, paramList);
        }

        public Decorator response(String code, String description, List<Tuple2<String, String>> paramList) {
            if(api.apiResponses == null) {
                api.apiResponses = new ApiResponses();
            }

            ApiResponse response = new ApiResponse();
            if(StringUtils.hasText(description)) {
                response.setDescription(description);
            }
            response.setContent(content(paramList));

            api.apiResponses.addApiResponse(code, response);
            return this;
        }

        private Content content(List<Tuple2<String, String>> paramList) {
            if(paramList == null) return new Content();

            MediaType mediaType = new MediaType();
            Schema<Object> schema = new Schema<>();
            for(Tuple2<String, String> tuple : paramList) {
                schema.addProperty(tuple.getT1(), new Schema().type(tuple.getT2()));
            }

            return new Content()
                    .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            mediaType.schema(schema)
                    );
        }

        public void build() {
            api.operation = new Operation();
            if(api.requestBody != null) {
                api.operation.setRequestBody(api.requestBody);
            }
            if(api.apiResponses != null) {
                api.operation.setResponses(api.apiResponses);
            }
            if(StringUtils.hasText(api.tag)) {
                api.operation.setTags(List.of(api.tag));
            }
            if(StringUtils.hasText(api.summary)) {
                api.operation.setSummary(api.summary);
            }

            api.function.accept(api);
        }
    }
}
