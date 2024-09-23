package com.gvs.avisacitas.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gvs.avisacitas.model.sqlite.EventsRepository;

public class SharedViewModelFactory implements ViewModelProvider.Factory {

	private final Application application;
	private final EventsRepository eventsRepository;

	// Constructor para aceptar Application o EventsRepository
	public SharedViewModelFactory(Application application) {
		this.application = application;
		this.eventsRepository = new EventsRepository(application);  // O puedes pasarlo desde fuera
	}

	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		if (modelClass.isAssignableFrom(SharedViewModel.class)) {
			return (T) new SharedViewModel(application);
		}
		throw new IllegalArgumentException("Unknown ViewModel class");
	}
}

