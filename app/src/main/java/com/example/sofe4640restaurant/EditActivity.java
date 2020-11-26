package com.example.sofe4640restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    private Button delBtn;
    private TextView addressText;
    private EditText editName;

    DatabaseHelper dbHelper;

    private String name;
    private String address;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        delBtn = (Button) findViewById(R.id.delBtn);
        addressText = (TextView) findViewById(R.id.addressText);
        editName = (EditText) findViewById(R.id.editName);
        dbHelper = new DatabaseHelper(this);

        Intent receive = getIntent();
        id = receive.getIntExtra("id", -1);
        name = receive.getStringExtra("name");
        address = receive.getStringExtra("address");
        editName.setText(name);

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.delItem(name);
                editName.setText("");
            }
        });
    }
}

