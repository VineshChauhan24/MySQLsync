package com.palibre.mysqlsync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static com.palibre.mysqlsync.DbContract.NAME;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS_FAILED;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase ourDB = null;
    EditText edName;
    RecyclerView rcvContacts;
    RecyclerView.LayoutManager lom;
    ContactsAdapter contactsAdapter;
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
        contactsAdapter = new ContactsAdapter(this, contactArray);
        rcvContacts.setAdapter(contactsAdapter);
        readFromLocalStorage();
    }

    public void addNew(View v) {
        String name = edName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "New name is empty", Toast.LENGTH_LONG).show();
            return;
        }
        saveToLocalStorage(name);
        edName.setText("");
    }

    private void readFromLocalStorage () {
        DBhelper dBhelper = new DBhelper(this);
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        Cursor cursor = dBhelper.readFromLocalDatabase(db);
        contactArray.clear();
        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            int syncStatus = cursor.getInt(cursor.getColumnIndex(SYNC_STATUS));
            contactArray.add(new Contact(name, syncStatus));
        }
        contactsAdapter.notifyDataSetChanged();
        cursor.close();
        dBhelper.close();
    }

    private void saveToLocalStorage(String name) {
        DBhelper dBhelper = new DBhelper(this);
        SQLiteDatabase db = dBhelper.getWritableDatabase();
        if (checkNetworkConnection()) {
            dBhelper.saveToLocalDatabase(name, SYNC_STATUS_FAILED, db);
            readFromLocalStorage();
            dBhelper.close();
        }
        else {

        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }



    private void setupTestData() {
        contactArray.add(new Contact("Dennis", 0));
        contactArray.add(new Contact("Patty", 1));
    }
}
