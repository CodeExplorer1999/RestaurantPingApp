package com.sid.restaurantreminder;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();

                Cursor data = dbHelper.getItemID(str);
                int itemID = -1;
                while(data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                Intent intent = new Intent(ListDataActivity.this, EditActivity.class);
                intent.putExtra("id", itemID);
                intent.putExtra("name", str);
                startActivity(intent);
            }
        });
    }
}
