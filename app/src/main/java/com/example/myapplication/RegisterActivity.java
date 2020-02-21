package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Utilities.UtilityFunctions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "register";
    private FirebaseAuth mAuth;
    private Button btnLogin, btnRegister;
    EditText etEmail, etPassword;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialiseAllViews();

        initaliseFireBaseVariables();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void register() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.length() != 0 && password.length() != 0) {
            if (UtilityFunctions.isValidEmailId(email)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getResult(),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            } else {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void initaliseFireBaseVariables() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void initialiseAllViews() {
        etEmail = findViewById(R.id.register_email);
        etPassword = findViewById(R.id.register_password);
        btnLogin = findViewById(R.id.register_login);
        btnRegister = findViewById(R.id.register_register);
    }
}
