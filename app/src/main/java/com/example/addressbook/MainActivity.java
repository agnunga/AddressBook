package com.example.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    private ContactAdapter arrayAdapter;
    private DBHelper mydb;
    private EditText searchEditText;
    private String currentSortField = DBHelper.CONTACTS_COLUMN_NAME;
    private String currentSortOrder = "ASC";
    private ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize database
        mydb = new DBHelper(this);
        
        // Initialize the ListView
        obj = findViewById(R.id.listView1);
        
        // Set up the list view with contacts
        refreshContactList(null);
        
        // Set up item click listener for the list
        obj.setOnItemClickListener((parent, view, position, id) -> {
            // Get the contact ID from the selected item
            String contactIdStr = contactList.get(position).get("id");
            try {
                int contactId = Integer.parseInt(contactIdStr);
                Intent intent = new Intent(getApplicationContext(), DisplayContact.class);
                intent.putExtra("id", contactId);
                startActivity(intent);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error: Invalid contact ID", Toast.LENGTH_SHORT).show();
            }
        });
        
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
        refreshContactList(searchEditText != null ? searchEditText.getText().toString() : "");
    }

    private void refreshContactList(String searchQuery) {
        new LoadContactsTask().execute(searchQuery);
    }

    private class LoadContactsTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            String searchQuery = params.length > 0 ? params[0] : "";
            if (searchQuery != null && !searchQuery.isEmpty()) {
                return mydb.searchContacts(searchQuery);
            } else {
                return mydb.getAllContactsWithDetails(currentSortField, currentSortOrder);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            contactList = result;
            ArrayList<String> contactNames = new ArrayList<>();
            for (HashMap<String, String> contact : contactList) {
                contactNames.add(contact.get("name"));
            }

            if (arrayAdapter == null) {
                arrayAdapter = new ContactAdapter(MainActivity.this, contactList, contactNames);
                obj.setAdapter(arrayAdapter);
            } else {
                arrayAdapter.clear();
                arrayAdapter.addAll(contactNames);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }
}
