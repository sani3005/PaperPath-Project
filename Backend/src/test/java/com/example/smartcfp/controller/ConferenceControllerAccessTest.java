package com.example.smartcfp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConferenceControllerAccessTest {

    @Autowired
    private Environment environment;

    @Test
    void conferenceListIsPublicForAndroidClient() throws Exception {
        assertPublicGet("/api/conferences");
    }

    @Test
    void conferenceSearchIsPublicForAndroidClient() throws Exception {
        assertPublicGet("/api/conferences/search?keyword=AI");
    }

    private void assertPublicGet(String path) throws Exception {
        int port = environment.getRequiredProperty("local.server.port", Integer.class);
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try {
            assertEquals(200, connection.getResponseCode());
            try (InputStream responseBody = connection.getInputStream()) {
                responseBody.transferTo(OutputStream.nullOutputStream());
            }
        } finally {
            connection.disconnect();
        }
    }
}
