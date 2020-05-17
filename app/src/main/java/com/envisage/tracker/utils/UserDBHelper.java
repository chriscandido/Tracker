package com.envisage.tracker.utils;

public class UserDBHelper {


    private String unique_Id;

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

