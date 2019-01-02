package com.palibre.mysqlsync;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase ourDB = null;
    TextView txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtName = findViewById(R.id.new_name);

    }

    public void addNew (View v) {
        String name = txtName.getText().toString();
        if (name.isEmpty() ) {
            Toast.makeText(this, "New name is empty", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Adding " + name + " to database", Toast.LENGTH_LONG).show();
    }
}
