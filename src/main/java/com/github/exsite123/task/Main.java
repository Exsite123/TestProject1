package com.github.exsite123.task;

import com.github.exsite123.task.vk.gson.users.Response;
import com.github.exsite123.task.vk.methods.Users;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static Config cfg = Config.load(new File("config.json"));

    private static final Gson gson = new Gson();

    static {
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
    }

    public static void main(String[] args) throws Exception {
        var db = cfg.mysql;
        SQL.login(
                db.get("host").getAsString(),
                db.get("port").getAsInt(),
                db.get("user").getAsString(),
                db.get("pass").getAsString(),
                db.get("table").getAsString(),
                db.get("threads").getAsInt()
        );

        List<String> userIds = new ArrayList<>();
        var rs = SQL.executeQuery("SELECT DISTINCT user_id FROM user_info");
        while(rs.next()) {
            userIds.add(rs.getString("user_id"));
        }
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += 100) {
            chunks.add(userIds.subList(i, Math.min(i + 100, userIds.size())));
        }

        for(var user_ids:chunks) {
            var answer = Users.get(String.join(",", user_ids), "bdate, city, contacts");
            var response = gson.fromJson(answer, Response.class);
            if(response == null || response.getResponse() == null) {
                throw new Exception(answer);
            }
            for(var item:response.getResponse()) {
                List<String> contacts = new ArrayList<>();
                if(item.getMobilePhone() != null && !item.getMobilePhone().isBlank()) {
                    contacts.add(item.getMobilePhone());
                }
                if(item.getHomePhone() != null && !item.getHomePhone().isBlank()) {
                    contacts.add(item.getHomePhone());
                }
                String cityName = item.getCity() != null?item.getCity().getTitle():"";
                SQL.execute(
                        "UPDATE user_info SET user_f_name = ?, user_l_name = ?, user_b_date = ?, user_city = ?, user_contacts = ? WHERE user_id = ?",
                        item.getFirstName(),
                        item.getLastName(),
                        item.getBdate(),
                        cityName,
                        String.join(", ", contacts),
                        item.getId()
                );
                System.out.println("Пользователь "+item.getFirstName()+" "+item.getLastName()+" [ID: "+item.getId()+"] добавлен в базу данных");
            }
        }
    }
}