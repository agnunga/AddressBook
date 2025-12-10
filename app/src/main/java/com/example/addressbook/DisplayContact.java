package com.example.addressbook;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DisplayContact extends AppCompatActivity {
    int from_Where_I_Am_Coming = 0;
    private DBHelper mydb;

    TextView name;
    TextView phone;
    TextView email;
    TextView street;
    TextView place;
    int id_To_Update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        
        // Set up the action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Contact Details");
        }

        // Initialize views
        name = findViewById(R.id.editTextName);
        phone = findViewById(R.id.editTextPhone);
        email = findViewById(R.id.editTextEmail);
        street = findViewById(R.id.editTextStreet);
        place = findViewById(R.id.editTextCountry);
        mydb = new DBHelper(this);

        // Get the contact ID from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_To_Update = extras.getInt("id");

            if (id_To_Update > 0) {
                // Load existing contact
                loadContact(id_To_Update);
            } else {
                // New contact - enable all fields by default
                enableEditMode(true);
                Button saveButton = findViewById(R.id.button1);
                saveButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadContact(int id) {
        Cursor rs = mydb.getData(id);
        if (rs != null && rs.moveToFirst()) {
            // Set contact data to views
            name.setText(rs.getString(rs.getColumnIndexOrThrow(DBHelper.CONTACTS_COLUMN_NAME)));
            phone.setText(rs.getString(rs.getColumnIndexOrThrow(DBHelper.CONTACTS_COLUMN_PHONE)));
            email.setText(rs.getString(rs.getColumnIndexOrThrow(DBHelper.CONTACTS_COLUMN_EMAIL)));
            street.setText(rs.getString(rs.getColumnIndexOrThrow(DBHelper.CONTACTS_COLUMN_STREET)));
            place.setText(rs.getString(rs.getColumnIndexOrThrow(DBHelper.CONTACTS_COLUMN_CITY)));

            if (!rs.isClosed()) {
                rs.close();
            }
        }
    }

    private void enableEditMode(boolean enable) {
        name.setEnabled(enable);
        name.setFocusableInTouchMode(enable);
        name.setClickable(enable);

        phone.setEnabled(enable);
        phone.setFocusableInTouchMode(enable);
        phone.setClickable(enable);

        email.setEnabled(enable);
        email.setFocusableInTouchMode(enable);
        email.setClickable(enable);

        street.setEnabled(enable);
        street.setFocusableInTouchMode(enable);
        street.setClickable(enable);

        place.setEnabled(enable);
        place.setFocusableInTouchMode(enable);
        place.setClickable(enable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.display_contact, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Show/hide menu items based on whether we're editing an existing contact
        MenuItem editItem = menu.findItem(R.id.Edit_Contact);
        MenuItem deleteItem = menu.findItem(R.id.Delete_Contact);
        
        if (id_To_Update > 0) {
            // Existing contact - show edit and delete
            if (editItem != null) editItem.setVisible(true);
            if (deleteItem != null) deleteItem.setVisible(true);
        } else {
            // New contact - hide both
            if (editItem != null) editItem.setVisible(false);
            if (deleteItem != null) deleteItem.setVisible(false);
        }
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        // Handle back button in action bar
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.Edit_Contact) {
            // Show the save button and enable editing
            Button saveButton = findViewById(R.id.button1);
            saveButton.setVisibility(View.VISIBLE);
            enableEditMode(true);
            return true;
        } else if (itemId == R.id.Delete_Contact) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete this contact?")
            .setPositiveButton("Yes", (dialog, id) -> {
                mydb.deleteContact(id_To_Update);
                Toast.makeText(getApplicationContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
                finish(); // Close this activity and return to the list
            })
            .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
            .setTitle("Confirm Delete")
            .show();
    }

    public void run(View view) {
        // Get values from the form
        String nameStr = name.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String streetStr = street.getText().toString().trim();
        String placeStr = place.getText().toString().trim();

        // Basic validation
        if (nameStr.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;
        if (id_To_Update > 0) {
            // Update existing contact
            success = mydb.updateContact(id_To_Update, nameStr, phoneStr, emailStr, streetStr, placeStr);
            if (success) {
                Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();
                // Set result to indicate success and finish the activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("contact_updated", true);
                setResult(RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insert new contact
            success = mydb.insertContact(nameStr, phoneStr, emailStr, streetStr, placeStr);
            if (success) {
                Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show();
                // Set result to indicate success and finish the activity
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }
        }

        // Return to the main activity
        finish();
    }
}

