package com.gvs.avisacitas.login.ui.login;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.gvs.avisacitas.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

	private ActivityLoginBinding binding; // Declaración de View Binding

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflar el diseño utilizando View Binding
		binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		Log.d("LoginActivity", "Activity Created");
	}
}
