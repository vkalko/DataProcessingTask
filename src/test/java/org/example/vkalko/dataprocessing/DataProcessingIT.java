package org.example.vkalko.dataprocessing;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataProcessingIT {

    @Test
    public void mainPageIsOK() throws IOException {
        String port = System.getenv("PORT");
        if (port == null) {
            port = "8081";
        }

        String url = System.getenv("SERVICE_URL");
        if (url == null) {
            url = "http://localhost:" + port;
        }

        OkHttpClient ok =
                new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

        Request request = new Request.Builder().url(url + "/").get().build();

        String expected = "Index";
        Response response = ok.newCall(request).execute();
        assertThat(response.body().string(), containsString(expected));
        assertThat(response.code(), equalTo(200));
    }

    @Test
    public void handleBadRequest() throws IOException {
        String port = System.getenv("PORT");
        if (port == null) {
            port = "8081";
        }

        String url = System.getenv("SERVICE_URL");
        if (url == null) {
            url = "http://localhost:" + port;
        }
        OkHttpClient ok =
                new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

        Request request = new Request.Builder().url(url + "/gs/created").get().build();

        Response response = ok.newCall(request).execute();
        assertThat(response.code(), equalTo(400));
    }

}
