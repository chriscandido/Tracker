package com.example.tracker.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserDBSymptoms {

    Context context;
    String symptoms;
    Integer date;

    public UserDBSymptoms(String symptoms, Integer date) {
        this.symptoms = symptoms;
        this.date = date;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public List<String> getListString(String symptoms){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> outputString = gson.fromJson(symptoms, type);
        return outputString;
    }
}
