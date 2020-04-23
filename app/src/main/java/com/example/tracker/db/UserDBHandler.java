package com.example.tracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tracker.utils.LocationHolder;

public class UserDBHandler extends SQLiteOpenHelper {

    //----------------------------------------------------------------------------------------------Databse name and version
    private static int DB_VERSION = 2;
    private static String DB_NAME = "ConTra";

    //----------------------------------------------------------------------------------------------Table Names
    public static final String TABLE_USERS = "userProfile";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String TABLE_SYMPTOMS = "symptoms";

    //----------------------------------------------------------------------------------------------Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_UNIQUEID = "uniqueId";

    //----------------------------------------------------------------------------------------------Userprofile column names
    private static final String KEY_DATEOFBIRTH = "dateOfBirth";

    //----------------------------------------------------------------------------------------------Location column names
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_CREATEDAT = "createdAt";

    //----------------------------------------------------------------------------------------------Symptoms columns names
    private static final String KEY_SYMPTOMS = "symptoms";


    public UserDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //Create Tables
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_USERPROFILES_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_UNIQUEID + " TEXT,"                                //0
            + KEY_DATEOFBIRTH + " INTEGER " + ")";                   //1
        db.execSQL(CREATE_USERPROFILES_TABLE);

        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_LATITUDE + " REAL,"                            //2
                + KEY_LONGITUDE + " REAL,"                           //3
                + KEY_CREATEDAT + " REAL " + ")";                    //4
        db.execSQL(CREATE_LOCATIONS_TABLE);

        String CREATE_SYMPTOMS_TABLE = "CREATE TABLE " + TABLE_SYMPTOMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_SYMPTOMS + " TEXT " + ")";                     //2
        db.execSQL(CREATE_SYMPTOMS_TABLE);
    }

    private static final int NOT_AVAILABLE = -10000;

    private static final String DB_ALTER_TABLE_USERS_TO_V2 = "ALTER TABLE " + TABLE_USERS
            + " ADD COLUMN " + KEY_DATEOFBIRTH + " INTEGER DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_LOCATIONS_TO_V2 = "ALTER TABLE " + TABLE_LOCATIONS
            + " ADD COLUMN " + KEY_CREATEDAT + " REAL DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_SYMPTOMS_TO_V2 = "ALTER TABLE " + TABLE_SYMPTOMS
            + " ADD COLUMN " + KEY_SYMPTOMS + " TEXT DEFAULT " + NOT_AVAILABLE +";";

    //Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch (oldVersion){
            case 1:
                db.execSQL(DB_ALTER_TABLE_USERS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_LOCATIONS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_SYMPTOMS_TO_V2);
        }
        Log.w("ConTra", "[#] DatabaseHandler.java - onUpgrade: DB upgraded to version " + newVersion);
    }

    //Add new location and update the corresponding track
    public void addLocation (LocationHolder locationHolder) {
        SQLiteDatabase db = this.getWritableDatabase();
    }

}
