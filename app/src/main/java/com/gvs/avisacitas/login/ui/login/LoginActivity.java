package com.gvs.avisacitas.login.ui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gvs.avisacitas.R;
import com.gvs.avisacitas.databinding.ActivityLoginBinding;
import com.gvs.avisacitas.utils.error.LogHelper;

public class LoginActivity extends AppCompatActivity {

	private ActivityLoginBinding binding; // Declaración de View Binding

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflar el diseño utilizando View Binding
		binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

	}
}
