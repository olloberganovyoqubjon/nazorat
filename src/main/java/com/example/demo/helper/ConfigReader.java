package com.example.demo.helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ConfigReader {

    public static List readConfigFileToList(String name) {
        try {
            FileReader reader = new FileReader("config.json");
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject notIgnoreUser = jsonObject.getAsJsonObject(name);
            reader.close();
            return new Gson().fromJson(notIgnoreUser.getAsJsonArray("id"), List.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static Boolean readConfigFileToBoolean(String name) {
        try {
            FileReader reader = new FileReader("config.json");
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject notIgnoreUser = jsonObject.getAsJsonObject(name);
            reader.close();
            return new Gson().fromJson(notIgnoreUser.getAsJsonObject(), Boolean.class);
        } catch (IOException e) {
            return null;
        }
    }
}

