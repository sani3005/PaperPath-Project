package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private ImageView ivPasswordToggle;
    private EditText etConfirmPassword;
    private ImageView ivConfirmPasswordToggle;
    private MaterialButton btnSignUp;
    private View btnGoogleSignUp;
    private TextView tvLoginLink;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivConfirmPasswordToggle = findViewById(R.id.ivConfirmPasswordToggle);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp);
        tvLoginLink = findViewById(R.id.tvLoginLink);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Password Visibility Toggles
        if (ivPasswordToggle != null) {
            ivPasswordToggle.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivPasswordToggle.setImageResource(R.drawable.ic_eye_off);
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivPasswordToggle.setImageResource(R.drawable.ic_eye);
                }
                etPassword.setSelection(etPassword.getText().length());
            });
        }

        if (ivConfirmPasswordToggle != null) {
            ivConfirmPasswordToggle.setOnClickListener(v -> {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                if (isConfirmPasswordVisible) {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_off);
                } else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye);
                }
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            });
        }

        // Sign Up CTA
        btnSignUp.setOnClickListener(v -> handleSignUp());

        // Google SSO
        if (btnGoogleSignUp != null) {
            btnGoogleSignUp.setOnClickListener(v -> {
                Toast.makeText(this, "Connecting with Google...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("USER_EMAIL", "saniya.g23@gmail.com");
                intent.putExtra("USER_NAME", "Saniya Gaonkar");
                startActivity(intent);
                finish();
            });
        }

        // Navigate to Login
        tvLoginLink.setOnClickListener(v -> finish());
    }

    private void handleSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.err_empty_name));
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.err_invalid_email));
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError(getString(R.string.err_short_password));
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.err_password_mismatch));
            etConfirmPassword.requestFocus();
            return;
        }

        Toast.makeText(this, getString(R.string.success_sign_up), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra("USER_NAME", name);
        intent.putExtra("USER_EMAIL", email);
        startActivity(intent);
        finish();
    }
}
