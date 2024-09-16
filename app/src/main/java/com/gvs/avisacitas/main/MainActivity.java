package com.gvs.avisacitas.main;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gvs.avisacitas.R;
import com.gvs.avisacitas.databinding.ActivityMainBinding;
import com.gvs.avisacitas.main.ui.eventsQueue.EventsQueueViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;
	private SharedViewModel sharedViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
		NavigationUI.setupWithNavController(binding.navView, navController);

		// Obtener el ViewModel asociado a esta Activity
		sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

		// Observar cambios en la lista de eventos
		sharedViewModel.getEventsList().observe(this, new Observer<List<String>>() {
			@Override
			public void onChanged(List<String> events) {
				// Actualizar la interfaz de usuario con la lista de eventos
				// Aquí puedes actualizar un TextView, RecyclerView, etc.
				//updateUIWithEvents(events);
			}
		});
	}

}