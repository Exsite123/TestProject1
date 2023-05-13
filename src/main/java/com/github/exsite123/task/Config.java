package com.github.exsite123.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;

public class Config {
	
	public JsonObject mysql, settings;

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public Config() {
		this.settings = new JsonObject();
		settings.addProperty("access_token", "dasdasd");
		settings.addProperty("xlsx_file", "test.xlsx");

		this.mysql = new JsonObject();
		mysql.addProperty("host", "127.0.0.1");
		mysql.addProperty("port", 3306);
		mysql.addProperty("user", "root");
		mysql.addProperty("pass", "12345678");
		mysql.addProperty("table", "TestTask1");
		mysql.addProperty("threads", 2);
	}

	public static Config load(File file) {
		Config instance = fromFile(file);

		if (instance == null) {
			instance = new Config();
			instance.toFile(file);
			System.exit(0);
		}
		return instance;
	}

	public void toFile(File file) {
		String jsonConfig = gson.toJson(this);
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(jsonConfig);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Config fromFile(File configFile) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
			return gson.fromJson(reader, Config.class);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}