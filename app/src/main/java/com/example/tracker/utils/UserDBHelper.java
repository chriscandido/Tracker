package com.example.tracker.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.tracker.db.UserDBHandler;

public class UserDBHelper {


    String unique_Id;

    public UserDBHelper(String unique_Id){
        this.unique_Id = unique_Id;
    }

    public void setUnique_Id(String unique_Id){
        this.unique_Id = unique_Id;
    }

    public String getUnique_Id() {
        return unique_Id;
    }



}

