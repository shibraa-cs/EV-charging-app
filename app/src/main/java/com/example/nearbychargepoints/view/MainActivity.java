package com.example.nearbychargepoints.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import com.example.nearbychargepoints.controller.DBController;
import com.example.nearbychargepoints.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private DBController controller;
    private ListView lv;
    private ArrayList<HashMap<String, String>> myList;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new DBController(this);
        LinearLayout layout2 = findViewById(R.id.Layout2);
        layout2.setVisibility(View.GONE);
        lv = findViewById(android.R.id.list);

        // Initialize UI components
        setupSearchView();
        setupLogoutButton();

        // Load data
        readingCSVFile();
        refreshData();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    private void setupLogoutButton() {
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginFragment.class));
            finish();
        });
    }

    private void refreshData() {
        myList = controller.getAllProducts();

        if (!myList.isEmpty()) {
            adapter = new SimpleAdapter(MainActivity.this, myList,
                    R.layout.lst_template,
                    new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i"},
                    new int[]{
                            R.id.txtrefe, R.id.txtlate, R.id.txtlong, R.id.txttown,
                            R.id.txtcoun, R.id.txtpost, R.id.txtchasta,
                            R.id.txtconnid, R.id.txtconnty
                    });

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new MyOnItemClickListener());
        }
    }

    private void readingCSVFile() {
        SQLiteDatabase db = controller.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBController.tableName);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.sample_national_chargepoints);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            ContentValues contentValues = new ContentValues();
            String line;
            int reading = 1;

            db.beginTransaction();
            while ((line = reader.readLine()) != null) {
                if (reading > 1) {
                    String[] str = line.split(",", 9);
                    contentValues.put(DBController.colId, str[0]);
                    contentValues.put(DBController.colLon, str[1]);
                    contentValues.put(DBController.colLat, str[2]);
                    contentValues.put(DBController.colTow, str[3]);
                    contentValues.put(DBController.colCou, str[4]);
                    contentValues.put(DBController.colPos, str[5]);
                    contentValues.put(DBController.colSta, str[6]);
                    contentValues.put(DBController.colConID, str[7]);
                    contentValues.put(DBController.colConty, str[8]);
                    db.insert(DBController.tableName, null, contentValues);
                }
                reading++;
            }
            db.setTransactionSuccessful();
        } catch (IOException e) {
            Toast.makeText(this, "Error reading CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                // Get the original position considering filtered results
                HashMap<String, String> selectedItem = (HashMap<String, String>) parent.getItemAtPosition(position);

                double lat = Double.parseDouble(selectedItem.get("b"));
                double lon = Double.parseDouble(selectedItem.get("c"));
                String postcode = selectedItem.get("f");
                String ref = selectedItem.get("a");

                LinearLayout layout2 = findViewById(R.id.Layout2);
                layout2.setVisibility(View.VISIBLE);

                if (layout2.getChildCount() > 0) {
                    layout2.removeAllViews();
                }

                SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(layout2.getId(), mapFragment)
                        .commit();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng location = new LatLng(lat, lon);
                        googleMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(ref)
                                .snippet("Postcode: " + postcode));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                    }
                });

            } catch (Exception e) {
                Toast.makeText(MainActivity.this,
                        "Error showing location: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}