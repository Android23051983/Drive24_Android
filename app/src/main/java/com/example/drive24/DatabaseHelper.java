package com.example.drive24;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 14;
    private static final String DATABASE = "userstore.db";
    static final String TABLE = "users";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME= "last_name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL= "email";
    public static final String COLUMN_ROLE = "role";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    SQLiteDatabase dbWritable = this.getWritableDatabase();
    SQLiteDatabase dbReadable = this.getReadableDatabase();

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT, last_name TEXT, phone TEXT, email TEXT, role TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS client_cars(id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT, last_name TEXT, email TEXT, phone TEXT, role TEXT, car_id INTEGER, car_model TEXT, car_number TEXT, car_city_id INTEGER, car_cost float, city_id INTEGER, city TEXT, city_countryid INTEGER, country_id INTEGER, country TEXT, date_start INTEGER, date_end INTEGER, status TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS landlord_cars(id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT, last_name TEXT, email TEXT, phone TEXT, role TEXT, car_id INTEGER, car_model TEXT, car_number TEXT, car_city_id INTEGER, car_cost float, city_id INTEGER, city TEXT, city_countryid INTEGER, country_id INTEGER, country TEXT, date_start INTEGER, date_end INTEGER, status TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS client_cars");
        db.execSQL("DROP TABLE IF EXISTS landlord_cars");
        onCreate(db);

    }

 public List<LandlordCars> getLandlordCars() {
        List<LandlordCars> landlordCarsList = new ArrayList<>();
        Cursor cursor = dbReadable.query("landlord_cars", new String[]{"first_name", "last_name", "email", "phone", "role", "car_id", "car_model", "car_number", "car_cost", "city", "country", "date_start", "date_end", "status"}, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                LandlordCars landlordCar = new LandlordCars();
                landlordCar.setFirst_name(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                landlordCar.setLast_name(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                landlordCar.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                landlordCar.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                landlordCar.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
                landlordCar.setCar_id(cursor.getInt(cursor.getColumnIndexOrThrow("car_id")));
                landlordCar.setCar_model(cursor.getString(cursor.getColumnIndexOrThrow("car_model")));
                landlordCar.setCar_number(cursor.getString(cursor.getColumnIndexOrThrow("car_number")));
                landlordCar.setCar_cost(cursor.getFloat(cursor.getColumnIndexOrThrow("car_cost")));
                landlordCar.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
                landlordCar.setCountry(cursor.getString(cursor.getColumnIndexOrThrow("country")));
                landlordCar.setDate_start(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date_start"))));
                landlordCar.setDate_end(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date_end"))));
                landlordCar.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                landlordCarsList.add(landlordCar);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return landlordCarsList;
 }

    public long addLandlordCar(LandlordCars landlordCars) {
        ContentValues values = new ContentValues();
        values.put("first_name", landlordCars.getFirst_name());
        values.put("last_name", landlordCars.getLast_name());
        values.put("email", landlordCars.getEmail());
        values.put("phone", landlordCars.getPhone());
        values.put("role", landlordCars.getRole());
        values.put("car_id", landlordCars.getCar_id());
        values.put("car_model", landlordCars.getCar_model());
        values.put("car_number", landlordCars.getCar_number());
        values.put("car_cost", landlordCars.getCar_cost());
        values.put("city", landlordCars.getCity());
        values.put("country", landlordCars.getCountry());
        values.put("date_start", landlordCars.getDate_start().getTime());
        values.put("date_end", landlordCars.getDate_end().getTime());
        return dbWritable.insert("landlord_cars", null, values);
 }

    public long addClientCars(ClientCars clientCars){
        ContentValues values = new ContentValues();
        values.put("first_name", clientCars.getFirst_name());
        values.put("last_name", clientCars.getLast_name());
        values.put("email", clientCars.getEmail());
        values.put("phone", clientCars.getPhone());
        values.put("role", clientCars.getRole());
        values.put("car_id", clientCars.getCar_id());
        values.put("car_model", clientCars.getCar_model());
        values.put("car_number", clientCars.getCar_number());
        values.put("car_cost", clientCars.getCar_cost());
        values.put("city", clientCars.getCity());
        values.put("country", clientCars.getCountry());
        values.put("date_start", clientCars.getDate_start().getTime());
        values.put("date_end", clientCars.getDate_end().getTime());
        return dbWritable.insert("client_cars", null, values);
    }

    public List<ClientCars> getClientCars() {
        List<ClientCars> clientCarsList = new ArrayList<>();
        try (Cursor cursor = dbReadable.query("client_cars", new String[]{"first_name", "last_name", "email", "phone", "role", "car_id", "car_model", "car_number", "car_cost", "city", "country", "date_start", "date_end", "status"}, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    ClientCars clientCars = new ClientCars();
                    clientCars.setFirst_name(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                    clientCars.setLast_name(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                    clientCars.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                    clientCars.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                    clientCars.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
                    clientCars.setCar_id(cursor.getInt(cursor.getColumnIndexOrThrow("car_id")));
                    clientCars.setCar_model(cursor.getString(cursor.getColumnIndexOrThrow("car_model")));
                    clientCars.setCar_number(cursor.getString(cursor.getColumnIndexOrThrow("car_number")));
                    clientCars.setCar_cost(cursor.getFloat(cursor.getColumnIndexOrThrow("car_cost")));
                    clientCars.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
                    clientCars.setCountry(cursor.getString(cursor.getColumnIndexOrThrow("country")));
                    clientCars.setDate_start(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date_start"))));
                    clientCars.setDate_end(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date_end"))));
                    clientCars.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                    clientCarsList.add(clientCars);
                } while (cursor.moveToNext());
            } else {
                cursor.close();
                return null;
            }
        }
        return clientCarsList;
    }

    public long addUser(UserProfile profile) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, profile.getFirst_name());
        values.put(COLUMN_LAST_NAME, profile.getLast_name());
        values.put(COLUMN_PHONE, profile.getPhone());
        values.put(COLUMN_EMAIL,profile.getEmail());
        values.put(COLUMN_ROLE, profile.getRole());
        return dbWritable.insert(TABLE, null, values);
    }

    public UserProfile getUser(String id) {
        UserProfile user = new UserProfile();
        Cursor cursor = dbReadable.query("users", null, "id = ?", new String[] {id},null, null, null, null);
        try{
            if(cursor.moveToFirst()) {
                user.setFirst_name(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                user.setLast_name(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            } else {
                cursor.close();
                return null;
            }
        } finally {
            cursor.close();
        }
        return user;
    }


    public void deleteUser() {
        dbWritable.execSQL("DELETE FROM users");
        dbWritable.execSQL("DELETE FROM sqlite_sequence WHERE name='users'");
    }

    public void deleteClientCars() {
        dbWritable.execSQL("DELETE FROM client_cars");
        dbWritable.execSQL("DELETE FROM sqlite_sequence WHERE name='client_cars'");
    }

    public void deleteLandlordCars() {
        dbWritable.execSQL("DELETE FROM landlord_cars");
        dbWritable.execSQL("DELETE FROM sqlite_sequence WHERE name='landlord_cars'");
    }
}
