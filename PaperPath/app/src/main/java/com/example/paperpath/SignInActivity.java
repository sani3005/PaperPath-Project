package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private ImageView ivPasswordToggle;
    private MaterialButton btnSignIn;
    private TextView tvForgotPassword;
    private View btnGoogleSignIn;
    private TextView tvSignUpLink;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvSignUpLink = findViewById(R.id.tvSignUpLink);
    }

    private void setupListeners() {
        // Password Visibility Toggle
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

        // Login CTA
        btnSignIn.setOnClickListener(v -> handleSignIn());

        // Google SSO
        if (btnGoogleSignIn != null) {
            btnGoogleSignIn.setOnClickListener(v -> {
                Toast.makeText(this, "Signing in with Google...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.putExtra("USER_EMAIL", "saniya.g23@gmail.com");
                intent.putExtra("USER_NAME", "Saniya Gaonkar");
                startActivity(intent);
                finish();
            });
        }

        // Navigate to Sign Up
        tvSignUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Forgot Password
        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter your email above to reset password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Password reset instructions sent to " + email, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSignIn() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.err_empty_email));
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.err_empty_password));
            etPassword.requestFocus();
            return;
        }

        Toast.makeText(this, getString(R.string.success_sign_in), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("USER_EMAIL", email);
        startActivity(intent);
        finish();
    }
}
