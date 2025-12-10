package com.example.addressbook;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_STREET = "street";
    public static final String CONTACTS_COLUMN_CITY = "place";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, street text,place text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact(String name, String phone, String email, String street, String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String name, String phone, String email, String street, String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

        public ArrayList<String> getAllContacts() {
        ArrayList<String> array_list = new ArrayList<>();
        ArrayList<HashMap<String, String>> contacts = getAllContactsWithDetails(CONTACTS_COLUMN_NAME, "ASC");
        for (HashMap<String, String> contact : contacts) {
            array_list.add(contact.get("name"));
        }
        return array_list;
    }

    public ArrayList<HashMap<String, String>> searchContacts(String query) {
        ArrayList<HashMap<String, String>> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {CONTACTS_COLUMN_ID, CONTACTS_COLUMN_NAME, CONTACTS_COLUMN_PHONE, 
                           CONTACTS_COLUMN_EMAIL, CONTACTS_COLUMN_STREET, CONTACTS_COLUMN_CITY};
        
        String selection = CONTACTS_COLUMN_NAME + " LIKE ? OR " + 
                          CONTACTS_COLUMN_PHONE + " LIKE ? OR " + 
                          CONTACTS_COLUMN_EMAIL + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"};
        
        Cursor cursor = db.query(CONTACTS_TABLE_NAME, columns, selection, selectionArgs, 
                               null, null, CONTACTS_COLUMN_NAME + " ASC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> contact = new HashMap<>();
                contact.put("id", cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_ID)));
                contact.put("name", cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_NAME)));
                contact.put("phone", cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_PHONE)));
                contactList.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return contactList;
    }

    public ArrayList<HashMap<String, String>> getAllContactsWithDetails(String sortBy, String sortOrder) {
        ArrayList<HashMap<String, String>> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String orderBy = sortBy + " " + sortOrder;
        Cursor cursor = db.query(CONTACTS_TABLE_NAME, null, null, null, null, null, orderBy);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> contact = new HashMap<>();
                contact.put("id", cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_ID)));
                contact.put("name", cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_NAME)));
                contact.put("phone", cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_PHONE)));
                contact.put("email", cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_EMAIL)));
                contactList.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return contactList;
    }
}
