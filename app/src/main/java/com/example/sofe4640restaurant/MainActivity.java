package com.example.sofe4640restaurant;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    Place currentPlace;
    private Button addAddress, addBtn, viewBtn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SDK and create new places client instance
        Places.initialize(getApplicationContext(), "AIzaSyC0i6HrkgawswgC1nM9nue6siR9sDFmlsY");
        PlacesClient placesClient = Places.createClient(this);

        // Initialize AutoComplete
        AutocompleteSupportFragment autoCompleteFragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autoComplete);
        autoCompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autoCompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        addAddress = (Button) findViewById(R.id.addAddress);
        addBtn = (Button) findViewById(R.id.addBtn);
        viewBtn = (Button) findViewById(R.id.viewBtn);
        //editText = (EditText) findViewById(R.id.editTextName);
        dbHelper = new DatabaseHelper(this);

        autoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            private static final String TAG = "My Activity";

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                currentPlace = place;
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String text = editText.getText().toString();
//                if (text.length() > 0) {
//                    AddData(text);
//                    editText.setText("");
//                }

                // TODO: Bug when user enters location into the search field but then deletes it. After delete if user clicks add button it will save last selected location
                if (!(currentPlace == null))
                {
                    dbHelper.addData(currentPlace);
                    autoCompleteFragment.setText("");
                    Toast.makeText(MainActivity.this, "Location Added", Toast.LENGTH_SHORT).show();
                    currentPlace = null;
                }
                else {
                    Toast.makeText(MainActivity.this, "Please enter location", Toast.LENGTH_SHORT).show();
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

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("places", currentPlace);
                startActivity(intent);
            }
        });
    }

//    public void AddData(Place currentPlace) {
//        dbHelper.addData(currentPlace);
//    }
}