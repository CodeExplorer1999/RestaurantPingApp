package com.example.sofe4640restaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        dbHelper = new DatabaseHelper(this);
        listView = (ListView) findViewById(R.id.listView);

        showData();
    }

    private void showData() {
        Cursor data = dbHelper.getData();
        ArrayList<String> list = new ArrayList<>();

        while(data.moveToNext()) {
            list.add(data.getString(1));
        }

        // TODO: If have time add address to list view (can use simple adapter instead of listadapter)
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // TODO: Add edit activity page if have time
//        listView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ListDataActivity.this, EditActivity.class);
//            }
//        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String str = parent.getItemAtPosition(position).toString();

                new AlertDialog.Builder(ListDataActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.delItem(str);

                                // TODO: does not display since it changes to new intent right after
                                Toast.makeText(ListDataActivity.this, "Location deleted", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ListDataActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }
}
