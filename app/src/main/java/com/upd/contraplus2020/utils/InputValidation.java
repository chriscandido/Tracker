package com.upd.contraplus2020.utils;

import android.content.Context;
import android.util.Log;

public class InputValidation {

    private Context context;

    public InputValidation(Context context){
        this.context = context;
    }

    public boolean isInputUniqueId (String uniqueId){
        if (uniqueId.isEmpty()){
            Log.e("Error", "[#] LoginActivity.java - ERROR: UNIQUE ID is Empty");
            return false;
        } else {
            Log.e("Error", "[#] UNIQUE ID is generated");
        }
        return true;
    }
}
