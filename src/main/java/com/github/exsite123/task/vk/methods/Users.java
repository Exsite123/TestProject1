package com.github.exsite123.task.vk.methods;

import com.github.exsite123.task.vk.ExecuteMethods;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URISyntaxException;

public class Users extends ExecuteMethods {

    public static String get(String userIds, String fields) throws URISyntaxException, IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("user_ids", userIds);
        body.addProperty("fields", fields);
        return execute("users.get", body);
    }
}
