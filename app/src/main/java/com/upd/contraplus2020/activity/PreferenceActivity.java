package com.upd.contraplus2020.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.upd.contraplus2020.utils.Constants;

public class PreferenceActivity {

    public PreferenceActivity(){
    }

    public static boolean saveUniqueId(String uniqueId, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(Constants.KEY_UNIQUEID, uniqueId);
        preferencesEditor.apply();
        return true;
    }

    public String getUniqueId(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.KEY_UNIQUEID, null);
    }
}
