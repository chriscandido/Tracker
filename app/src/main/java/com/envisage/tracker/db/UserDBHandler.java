package com.envisage.tracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.envisage.tracker.utils.LatLng;
import com.envisage.tracker.utils.LocationHolder;
import com.envisage.tracker.utils.UserDBBluetoothHelper;
import com.envisage.tracker.utils.UserDBContactHelper;
import com.envisage.tracker.utils.UserDBHelper;
import com.envisage.tracker.utils.UserDBSymptoms;

import java.util.ArrayList;
import java.util.List;

public class UserDBHandler extends SQLiteOpenHelper {

    private final Context context;

    //----------------------------------------------------------------------------------------------Databse name and version
    private static int DB_VERSION = 2;
    private static String DB_NAME = "ConTra";

    //----------------------------------------------------------------------------------------------Table Names
    public static final String TABLE_USERS = "userProfile";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String TABLE_SYMPTOMS = "symptoms";
    public static final String TABLE_BLUETOOTH = "bluetooth";
    public static final String TABLE_CONTACT = "contact";

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

    //----------------------------------------------------------------------------------------------Bluetooth Column names
    private static final String KEY_MCADDRESS = "mcaddress";
    private static final String KEY_DISTANCE = "distance";

    //----------------------------------------------------------------------------------------------Manual Contact
    private static final String KEY_NUMBER = "number";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATE = "date";


    public UserDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void initializeDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }

    //----------------------------------------------------------------------------------------------Create Tables
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_USERPROFILES_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_DATEOFBIRTH + " INTEGER " + ")";               //2
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
                + KEY_SYMPTOMS + " TEXT,"                            //2
                + KEY_CREATEDAT + " REAL " + ")";                    //3
        db.execSQL(CREATE_SYMPTOMS_TABLE);

        String CREATE_BLUETOOTH_TABLE = "CREATE TABLE " + TABLE_BLUETOOTH + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_MCADDRESS + " TEXT,"                           //2
                + KEY_DISTANCE + " REAL,"                            //3
                + KEY_CREATEDAT + " REAL " + ")";                    //4
        db.execSQL(CREATE_BLUETOOTH_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACT + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_NUMBER + " TEXT,"                              //2
                + KEY_ADDRESS + " TEXT,"                             //3
                + KEY_DATE + " TEXT " + ")";                         //4
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    private static final int NOT_AVAILABLE = -10000;

    private static final String DB_ALTER_TABLE_USERS_TO_V2 = "ALTER TABLE " + TABLE_USERS
            + " ADD COLUMN " + KEY_DATEOFBIRTH + " INTEGER DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_LOCATIONS_TO_V2 = "ALTER TABLE " + TABLE_LOCATIONS
            + " ADD COLUMN " + KEY_CREATEDAT + " REAL DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_SYMPTOMS_TO_V2 = "ALTER TABLE " + TABLE_SYMPTOMS
            + " ADD COLUMN " + KEY_CREATEDAT + " REAL DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_BLUETOOTH_TO_V2 = "ALTER TABLE " + TABLE_BLUETOOTH
            + " ADD COLUMN " + KEY_CREATEDAT + " REAL DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_CONTACTS_TO_V2 = "ALTER TABLE " + TABLE_CONTACT
            + " ADD COLUMN " + KEY_DATE + " TEXT DEFAULT " + NOT_AVAILABLE +";";

    //----------------------------------------------------------------------------------------------Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch (oldVersion){
            case 1:
                db.execSQL(DB_ALTER_TABLE_USERS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_LOCATIONS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_SYMPTOMS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_BLUETOOTH_TO_V2);
                db.execSQL(DB_ALTER_TABLE_CONTACTS_TO_V2);
        }
        Log.w("ConTra+", "[#] DatabaseHandler.java - onUpgrade: DB upgraded to version " + newVersion);
    }

    //----------------------------------------------------------------------------------------------add location of user
    public void addLocation(LocationHolder locationHolder, UserDBHelper userDBHelper){
        SQLiteDatabase db = this.getWritableDatabase();
        Location loc = locationHolder.getLocation();

        ContentValues locValues = new ContentValues();
        locValues.put(KEY_UNIQUEID, userDBHelper.getUnique_Id());
        locValues.put(KEY_LATITUDE, loc.getLatitude());
        locValues.put(KEY_LONGITUDE, loc.getLongitude());
        locValues.put(KEY_CREATEDAT, loc.getTime());

        db.beginTransaction();
        db.insert(TABLE_LOCATIONS, null, locValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add unique id of the user
    public void addUser (UserDBHelper userDBHelper){
        SQLiteDatabase db = this.getWritableDatabase();
        String uid = userDBHelper.getUnique_Id();

        ContentValues userValues = new ContentValues();
        userValues.put(KEY_UNIQUEID, uid);

        db.beginTransaction();
        db.insert(TABLE_USERS, null, userValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add symptoms of the user
    public void addSymptoms (String symptoms, Integer time){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues symptomValues = new ContentValues();
        symptomValues.put(KEY_SYMPTOMS, symptoms);
        symptomValues.put(KEY_CREATEDAT, time);

        db.beginTransaction();
        db.insert(TABLE_SYMPTOMS, null, symptomValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add bluetooth connections
    public void addScannedBluetooth (UserDBBluetoothHelper userDBBluetoothHelper, UserDBHelper userDBHelper){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues bluetoothValues = new ContentValues();
        bluetoothValues.put(KEY_UNIQUEID, userDBHelper.getUnique_Id());
        bluetoothValues.put(KEY_MCADDRESS, userDBBluetoothHelper.getMcaddress());
        bluetoothValues.put(KEY_DISTANCE, userDBBluetoothHelper.getDistance());
        bluetoothValues.put(KEY_CREATEDAT, userDBBluetoothHelper.getCreatedAd());

        db.beginTransaction();
        db.insert(TABLE_BLUETOOTH, null, bluetoothValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add contacts of the user
    public void addContact(String number, String address, String date, UserDBHelper userDBHelper){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contactValues = new ContentValues();
        contactValues.put(KEY_UNIQUEID, userDBHelper.getUnique_Id());
        contactValues.put(KEY_NUMBER, number);
        contactValues.put(KEY_ADDRESS, address);
        contactValues.put(KEY_DATE, date);

        db.beginTransaction();
        db.insert(TABLE_CONTACT, null, contactValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------get single location using id
    public LocationHolder getLocation(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        LocationHolder locationHolder = null;

        Cursor cursor = db.query(TABLE_LOCATIONS, new String[] {
                KEY_ID,
                KEY_UNIQUEID,
                KEY_LATITUDE,
                KEY_LONGITUDE,
                KEY_CREATEDAT}, KEY_ID + " = ?",
                new String[] {String.valueOf(id)}, null,null,null,null);

        if (cursor != null){
            cursor.moveToFirst();
            Location loc = new Location("DB");
            loc.setLatitude(cursor.getDouble(2));
            loc.setLongitude(cursor.getDouble(3));
            loc.setTime(cursor.getLong(4));

            locationHolder = new LocationHolder(loc);
            cursor.close();

        }
        return locationHolder;
    }

    //----------------------------------------------------------------------------------------------get list of location using id
    public List<LatLng> getLatLngList(long Id){
        List<LatLng> latLngList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_LATITUDE + "," + KEY_LONGITUDE + " FROM " + TABLE_LOCATIONS
                + " WHERE " + KEY_ID + " = " + Id + " ORDER BY " + KEY_ID;
        Log.v("SELECT: ", query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    LatLng latLng = new LatLng();
                    latLng.latitude = cursor.getDouble(0);
                    latLng.longitude = cursor.getDouble(1);
                    latLngList.add(latLng);
                } while (cursor.moveToNext());
            } cursor.close();
        }
        return latLngList;
    }

    //----------------------------------------------------------------------------------------------get the last id
    public int getLastId(){
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY ="SELECT " + KEY_ID + " FROM " + TABLE_LOCATIONS + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        int id = 0;
        if (cursor != null){
            if(cursor.moveToFirst()){
                id =cursor.getInt(0);
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Location Table is Empty");
            cursor.close();
        } return id;
    }

    //----------------------------------------------------------------------------------------------get last location
    public LocationHolder getLastLocation(){
        SQLiteDatabase db = this.getWritableDatabase();
        LocationHolder locationHolder = null;

        String QUERY ="SELECT "
                + KEY_ID + ","
                + KEY_LATITUDE + ","
                + KEY_LONGITUDE + ","
                + KEY_CREATEDAT + " FROM "
                + TABLE_LOCATIONS + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null){
            cursor.moveToFirst();
            Location loc = new Location("DB");
            loc.setLatitude(cursor.getDouble(1));
            loc.setLongitude(cursor.getDouble(2));
            loc.setTime(cursor.getLong(3));
            Log.d("DATABASE", "[#] DatabaseHandler.java - LOCATION: " + cursor.getDouble(1)
                    +  " && " + cursor.getDouble(2));
            locationHolder = new LocationHolder(loc);
            cursor.close();
        }
        return locationHolder;
    }

    //----------------------------------------------------------------------------------------------get Last Scanned Bluetooth
    public UserDBBluetoothHelper getLastScannedBluetooth() {
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBBluetoothHelper userDBBluetoothHelper = null;

        String QUERY = "SELECT "
                + KEY_ID + ","
                + KEY_UNIQUEID + ","
                + KEY_MCADDRESS + ","
                + KEY_DISTANCE + ","
                + KEY_CREATEDAT + " FROM "
                + TABLE_BLUETOOTH + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String mcaddress = cursor.getString(2);
            Float distance = cursor.getFloat(3);
            Integer createdAt = cursor.getInt(4);
            Log.d("DATABASE", "[#] DatabaseHandler.java - BLUETOOTH: " + cursor.getString(2)
                    + " && " + cursor.getFloat(3));
            userDBBluetoothHelper = new UserDBBluetoothHelper(mcaddress, distance, createdAt);
        } else {
            Log.v("DATABASE", "[#] DatabaseHandler.java - Location Table is Empty");
            cursor.close();
        } return userDBBluetoothHelper;

    }

    //----------------------------------------------------------------------------------------------get the last symptoms
    public UserDBSymptoms getLastSymptoms(){
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBSymptoms userDBSymptoms = null;

        String QUERY = "SELECT "
                + KEY_SYMPTOMS + ","
                + KEY_CREATEDAT + " FROM "
                + TABLE_SYMPTOMS + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                String symptoms = cursor.getString(0);
                Integer date = cursor.getInt(1);
                userDBSymptoms = new UserDBSymptoms(symptoms, date);
                Log.d("DATABASE", "[#] DatabaseHandler.java - SYMPTOMS: " + userDBSymptoms.getSymptoms()
                + " && " + "TIME: " + userDBSymptoms.getDate());
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Symptoms Table is Empty");
            cursor.close();
        }
        return userDBSymptoms;
    }

    //----------------------------------------------------------------------------------------------get last contact of the user
    public UserDBContactHelper getLastContact(){
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBContactHelper userDBContactHelper = null;

        String QUERY = "SELECT "
                + KEY_NUMBER + ","
                + KEY_ADDRESS + ","
                + KEY_DATE + " FROM "
                + TABLE_CONTACT + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                String number = cursor.getString(0);
                String address = cursor.getString(1);
                String date = cursor.getString(2);
                userDBContactHelper = new UserDBContactHelper(number, address, date);
                Log.d("DATABASE", "[#] DatabaseHandler.java - CONTACT: " + userDBContactHelper.getNumber()
                        + " && " + "TIME: " + userDBContactHelper.getDate());
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Contacts Table is Empty");
            cursor.close();
        }
        return userDBContactHelper;
    }

    //----------------------------------------------------------------------------------------------get unique id of the user
    public String getKeyUniqueid(){
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY = "SELECT " + KEY_UNIQUEID + " FROM " + TABLE_USERS + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        String result = "";

        if (cursor != null){
            if(cursor.moveToFirst()){
                result = cursor.getString(0);
                Log.d("DATABASE", "[#] DatabaseHandler.java - UNIQUE ID: " + result);
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Location Table is Empty");
            cursor.close();
        } return result;
    }

    //----------------------------------------------------------------------------------------------check unique id of the user
    public boolean checkUniqueId (String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[] {
                        KEY_ID }, KEY_UNIQUEID + " = ?",
                new String[] { uniqueId }, null,null,null,null);

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0){
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------delete table
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_LOCATIONS, null, null);
        db.close();
    }
}
