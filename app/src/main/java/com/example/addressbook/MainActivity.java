package com.example.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    private ArrayAdapter<String> arrayAdapter;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize database
        mydb = new DBHelper(this);
        
        // Set up the list view with contacts
        refreshContactList();
        
        // Set up FAB click listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Start DisplayContact with ID 0 to indicate a new contact
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("id", 0);
            Intent intent = new Intent(getApplicationContext(), DisplayContact.class);
            intent.putExtras(dataBundle);
            startActivity(intent);
        });
        
        // Set up list view and load contacts
        obj = findViewById(R.id.listView1);
        // The list item click listener is set up in refreshContactList()
        // No need to set it up again here
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == R.id.item1) {
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("id", 0);

            Intent intent = new Intent(getApplicationContext(), DisplayContact.class);
            intent.putExtras(dataBundle);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the contact list when returning to this activity
        refreshContactList();
    }
    
    private void refreshContactList() {
        ArrayList<String> array_list = mydb.getAllContacts();
        arrayAdapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_list_item_1,
            array_list
        );
        if (obj != null) {
            obj.setAdapter(arrayAdapter);
            obj.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                int id_To_Search = arg2 + 1;
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);
                Intent intent = new Intent(getApplicationContext(), DisplayContact.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            });
        }
    }
}
