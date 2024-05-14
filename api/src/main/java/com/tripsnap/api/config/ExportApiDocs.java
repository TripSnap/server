package com.tripsnap.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

public class ExportApiDocs {
    public static void run() throws URISyntaxException, IOException {
        Resource resource = new ClassPathResource("/application.properties");
        Properties props = PropertiesLoaderUtils.loadProperties(resource);

        String server = "http://localhost:" + props.get("server.port");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // swagger config 가져오기
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<?> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI(server + "/v3/api-docs/swagger-config"));

        ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);
        Map<String, Object> swaggerConfig = gson.fromJson(exchange.getBody(), new TypeToken<Map<String, Object>>() {}.getType());

        // API json 가져오기
        requestEntity = new RequestEntity<>(HttpMethod.GET,  new URI(server + swaggerConfig.get("url")));
        exchange = restTemplate.exchange(requestEntity, String.class);
        Map<String, Object> apiDocs = gson.fromJson(exchange.getBody(), new TypeToken<Map<String, Object>>() {}.getType());

        writeFile("swagger-config.json", gson.toJson(swaggerConfig));
        writeFile("api-docs.json", gson.toJson(apiDocs));
    }

    private static void writeFile(String filename, String content) throws IOException {
        String rootPath = System.getProperty("user.dir");

        String filepath = new StringBuilder()
                .append(rootPath)
                .append(File.separatorChar).append("..")
                .append(File.separatorChar).append("api-docs")
                .append(File.separatorChar).append("src")
                .append(File.separatorChar).append(filename)
                .toString();

        File file = new File(filepath);
        if(!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(content);
        bw.close();

    }
}

