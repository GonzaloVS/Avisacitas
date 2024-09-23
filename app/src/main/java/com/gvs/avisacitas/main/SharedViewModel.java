package com.gvs.avisacitas.main;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.model.sqlite.EventsRepository;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

	private final MutableLiveData<List<CalendarEvent>> eventsList = new MutableLiveData<>();
	private final EventsRepository eventsRepository;
	private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);


	// Constructor que acepta Application
	public SharedViewModel(Application application) {
		super();
		this.eventsRepository = new EventsRepository(application);
		// Inicializar con una lista vacía si es necesario
		eventsList.setValue(new ArrayList<>());
	}

	// Método para obtener LiveData de la lista de eventos
	public LiveData<List<CalendarEvent>> getEventsList() {
		return eventsRepository.getEventsList();
	}

	public LiveData<List<String>> getEventsTitlesList() {
		return eventsRepository.getEventsTitlesList();
	}

	// Otros métodos para manejar eventos
	public void updateEvents(List<CalendarEvent> newEvents) {
		eventsList.setValue(newEvents);
	}

	public void addEvent(CalendarEvent event) {
		List<CalendarEvent> currentEvents = eventsList.getValue();
		if (currentEvents != null) {
			currentEvents.add(event);
			eventsList.setValue(currentEvents);
		}
	}


	public LiveData<Boolean> getIsPlaying() {
		return isPlaying;
	}

	public void togglePlayPause() {
		Boolean currentState = isPlaying.getValue();
		if (currentState != null) {
			isPlaying.setValue(!currentState);
		}
	}

	public void stopPlayback() {
		isPlaying.setValue(false);
	}
}

