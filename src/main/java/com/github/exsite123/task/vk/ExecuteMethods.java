package com.github.exsite123.task.vk;

import com.github.exsite123.task.Main;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public abstract class ExecuteMethods {

    private static final double version = 5.131;

    public static String execute(String methodName, JsonObject body) throws URISyntaxException, IOException, InterruptedException {
        var access_token = Main.cfg.settings.get("access_token").getAsString();
        body.addProperty("access_token", access_token);
        body.addProperty("v", version);

        List<String> params = new ArrayList<>();
        for(var en:body.entrySet()) {
            params.add(en.getKey()+"="+en.getValue().getAsString());
        }

        var request = HttpRequest.newBuilder(new URI("https://api.vk.com/method/"+methodName))
                .POST(HttpRequest.BodyPublishers.ofString(String.join("&", params)))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
