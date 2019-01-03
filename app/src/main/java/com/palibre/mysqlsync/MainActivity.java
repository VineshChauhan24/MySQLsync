package com.palibre.mysqlsync;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase ourDB = null;
    EditText edName;
    RecyclerView rcvContacts;
    RecyclerView.LayoutManager  lom;
    ArrayList<Contact> contactArray = new ArrayList<Contact>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edName = findViewById(R.id.new_name);
        rcvContacts = findViewById(R.id.rcv_names);
        lom = new LinearLayoutManager(this);
        rcvContacts.setLayoutManager(lom);
        rcvContacts.setHasFixedSize(true);
        setupTestData();
        ContactsAdapter ca = new ContactsAdapter(contactArray);
        rcvContacts.setAdapter(ca);
    }

    public void addNew (View v) {
        String name = edName.getText().toString();
        if (name.isEmpty() ) {
            Toast.makeText(this, "New name is empty", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Adding " + name + " to database", Toast.LENGTH_LONG).show();
    }

    private void setupTestData () {
        contactArray.add(new Contact ("Dennis", 0));
        contactArray.add(new Contact ("Patty", 1));
    }
}
