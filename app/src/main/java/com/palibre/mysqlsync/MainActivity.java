package com.palibre.mysqlsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.palibre.mysqlsync.DbContract.NAME;
import static com.palibre.mysqlsync.DbContract.SERVER_URL;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS_FAILED;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS_OK;
import static com.palibre.mysqlsync.DbContract.UI_UPDATE_BROADCAST;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase ourDB = null;
    EditText edName;
    RecyclerView rcvContacts;
    RecyclerView.LayoutManager lom;
    ContactsAdapter contactsAdapter;
    ArrayList<Contact> contactArray = new ArrayList<Contact>();
    BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };
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
        saveToAppServer(name);
        edName.setText("");
    }

    private void readFromLocalStorage() {
        DBhelper dBhelper = new DBhelper(this);
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        Cursor cursor = dBhelper.readFromLocalDatabase(db);
        contactArray.clear();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            int syncStatus = cursor.getInt(cursor.getColumnIndex(SYNC_STATUS));
            contactArray.add(new Contact(name, syncStatus));
        }
        contactsAdapter.notifyDataSetChanged();
        cursor.close();
        dBhelper.close();
    }

    private void saveToAppServer(final String name) {
        if (checkNetworkConnection()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String serverResponse = jsonObject.getString("response");
                        if (serverResponse.equals("OK")) {
                            saveToLocalStorage(name, SYNC_STATUS_OK);
                        } else {
                            saveToLocalStorage(name, SYNC_STATUS_FAILED);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    saveToLocalStorage(name, SYNC_STATUS_FAILED);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put ("name", name);
                    return params;
                }
            };

            MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

            //dBhelper.saveToLocalDatabase(name, SYNC_STATUS_FAILED, db);
            // readFromLocalStorage();
            // dBhelper.close();
        } else {
            Log.i ("DMN", "No Network Connection");
            saveToLocalStorage(name, SYNC_STATUS_FAILED);
        }
    }

    private void saveToLocalStorage(String name, int syncStatus) {
        DBhelper dBhelper = new DBhelper(this);
        SQLiteDatabase db = dBhelper.getWritableDatabase();
        dBhelper.saveToLocalDatabase(name, syncStatus, db);
        readFromLocalStorage();
        dBhelper.close();
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

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
