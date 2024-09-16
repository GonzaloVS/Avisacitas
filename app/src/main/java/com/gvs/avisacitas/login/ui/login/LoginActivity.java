package com.gvs.avisacitas.login.ui.login;

import android.os.Bundle;

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

		//NavController navController = Navigation.findNavController(this, R.id.nav_host_login_fragment); // Ajusta el ID si es necesario

	}
}
