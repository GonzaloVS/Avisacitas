package com.gvs.avisacitas.main;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gvs.avisacitas.model.sqlite.EventsRepository;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

	// MutableLiveData para la lista de eventos
	private final MutableLiveData<List<String>> eventsList = new MutableLiveData<>();
	private Context context;
	private final EventsRepository eventsRepository;

	public SharedViewModel(Application application) {
		super();
		eventsRepository = new EventsRepository(application);
	}

	public SharedViewModel(EventsRepository eventsRepository) {
		this.eventsRepository = eventsRepository;
		// Inicializar con una lista vacía
		eventsList.setValue(new ArrayList<>());
	}

	// Método para obtener LiveData de la lista de eventos

	public LiveData<List<String>> getEventsList() {
		return eventsRepository.getEventsList();
	}

	// Método para actualizar la lista de eventos
	public void updateEvents(List<String> newEvents) {
		eventsList.setValue(newEvents);
	}

	// Método para agregar un evento
	public void addEvent(String event) {
		List<String> currentEvents = eventsList.getValue();
		if (currentEvents != null) {
			currentEvents.add(event);
			eventsList.setValue(currentEvents);
		}
	}
}

