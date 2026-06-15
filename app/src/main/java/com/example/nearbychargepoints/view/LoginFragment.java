package com.example.nearbychargepoints.view;



import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearbychargepoints.controller.DBController;
import com.example.nearbychargepoints.R;

public class LoginFragment extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        // Initialize UI elements
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Initialize database
        DBController dbController = new DBController(this);
        db = dbController.getReadableDatabase();

        // Set onClick listener for Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        // Set onClick listener for Register link
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginFragment.this, RegisterFragment.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError("Password is required.");
            return;
        }

        // Query the database for the user

        Cursor cursor = null;
        try {
            cursor = db.query(DBController.TABLE_USERS,
                    new String[]{DBController.USER_ID, DBController.USER_EMAIL, DBController.USER_PASSWORD},
                    DBController.USER_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") String storedPassword =
                        cursor.getString(cursor.getColumnIndex(DBController.USER_PASSWORD));

                // DIRECT COMPARISON (INSECURE!)
                if (password.equals(storedPassword)) {
                    // Login successful
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

}
