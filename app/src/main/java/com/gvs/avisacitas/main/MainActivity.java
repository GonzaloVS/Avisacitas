package com.gvs.avisacitas.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gvs.avisacitas.R;
import com.gvs.avisacitas.databinding.ActivityMainBinding;
import com.gvs.avisacitas.model.calendar.CalendarHelper;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;
	private SharedViewModel sharedViewModel;
	private TextView textEventTitle;
	private boolean isPowerOn = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			binding = ActivityMainBinding.inflate(getLayoutInflater());
			setContentView(binding.getRoot());

			//Recuperar eventos del calendario
			CalendarHelper.insertOrUpdateAllCalendarEvents(getApplicationContext());

			// Asegúrate de que todas las transacciones pendientes se completen
			getSupportFragmentManager().executePendingTransactions();

			NavHostFragment navHostFragment =
					(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
			NavController navController = navHostFragment.getNavController();

			NavigationUI.setupWithNavController(binding.navView, navController);


			// Obtener el TextView para el título del evento
			textEventTitle = findViewById(R.id.text_event_title);

			// Obtener el ViewModel asociado a esta Activity
			SharedViewModelFactory factory = new SharedViewModelFactory(getApplication());
			sharedViewModel = new ViewModelProvider(this, factory).get(SharedViewModel.class);

			// Observar cambios en la lista de eventos
			sharedViewModel.getEventsTitlesList().observe(this, events -> {
				if (events != null && !events.isEmpty()) {
					textEventTitle.setText(events.get(events.size() - 1)); // Muestra el título del último evento
				} else {
					textEventTitle.setText("No hay eventos disponibles");
				}
			});

			// Encuentra el ImageButton
			ImageButton playPauseButton = findViewById(R.id.button_play_pause);

			// Configura el listener para cambiar el icono cuando se hace clic
			playPauseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Llama al método playStopSender() para alternar entre play y pausa
					playStopSender();
				}
			});

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}

	private void myFunction() {
		// Lógica de reproducción, utiliza SharedViewModel para manejar el estado
		sharedViewModel.togglePlayPause();
	}

	public void playStopSender() {
		try {
			isPowerOn = !isPowerOn;

			ImageButton playPauseButton = findViewById(R.id.button_play_pause);

			playPauseButton.setImageResource(
					isPowerOn ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp
			);

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}

}