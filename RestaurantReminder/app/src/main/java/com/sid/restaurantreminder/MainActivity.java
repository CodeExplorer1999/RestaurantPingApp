package com.sid.restaurantreminder;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    private Button addAddress, addBtn, viewBtn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAddress = (Button) findViewById(R.id.addAddress);
        addBtn = (Button) findViewById(R.id.addBtn);
        viewBtn = (Button) findViewById(R.id.viewBtn);
        editText = (EditText) findViewById(R.id.editTextName);
        dbHelper = new DatabaseHelper(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (text.length() > 0) {
                    AddData(text);
                    editText.setText("");
                }
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                startActivity(intent);
            }
        });
    }

    public void AddData(String data) {
        dbHelper.addData(data);
    }
}