package com.palibre.mysqlsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.palibre.mysqlsync.DbContract.NAME;
import static com.palibre.mysqlsync.DbContract.SERVER_URL;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS_FAILED;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS_OK;
import static com.palibre.mysqlsync.DbContract.UI_UPDATE_BROADCAST;

public class NetworkMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
         if(checkNetworkConnection(context)) {
             final DBhelper dBhelper = new DBhelper(context);
             final SQLiteDatabase db = dBhelper.getWritableDatabase();
             Cursor cursor = dBhelper.readFromLocalDatabase(db);
             while (cursor.moveToNext()) {
                 int syncStatus = cursor.getInt(cursor.getColumnIndex(SYNC_STATUS));
                 if (syncStatus == SYNC_STATUS_FAILED) {
                     final String name = cursor.getString(cursor.getColumnIndex(NAME));
                     StringRequest stringRequest = new StringRequest(Request.Method.PATCH, SERVER_URL,
                             new Response.Listener<String>() {
                                 @Override
                                 public void onResponse(String response) {
                                     try {
                                         JSONObject jsonObject = new JSONObject(response);
                                         String serverResponse = jsonObject.getString("response");
                                         if (serverResponse.equals("OK")) {
                                             dBhelper.updateLocalDatabase(name, SYNC_STATUS_OK, db);
                                             context.sendBroadcast(new Intent(UI_UPDATE_BROADCAST));
                                         }
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }, new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError error) {

                         }
                     }) {
                         @Override
                         protected Map<String, String> getParams() throws AuthFailureError {
                             Map<String, String> params = new HashMap<String, String>();
                             params.put ("name", name);
                             return params;
                         }
                     };
                     MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                 }
             }
             dBhelper.close();
         }
    }

    public boolean checkNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

}
