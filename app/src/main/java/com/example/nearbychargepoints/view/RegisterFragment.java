package com.example.nearbychargepoints.view;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterFragment extends AppCompatActivity {

    private EditText etRegisterEmail, etRegisterPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    private DBController dbController;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        // Initialize UI elements
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Initialize database
        dbController = new DBController(this);
        db = dbController.getWritableDatabase();

        // Set onClick listener for Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // Set onClick listener for Login link
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterFragment.this, LoginFragment.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser() {
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            etRegisterEmail.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etRegisterPassword.setError("Password is required.");
            return;
        }

        if (password.length() < 6) {
            etRegisterPassword.setError("Password must be at least 6 characters.");
            return;
        }


        // Insert into database
        long result = dbController.addUser(email, password);

        if (result != -1) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            // Redirect to LoginActivity
            Intent intent = new Intent(RegisterFragment.this, LoginFragment.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration Failed. Email may already be in use.", Toast.LENGTH_SHORT).show();
        }
    }
}
