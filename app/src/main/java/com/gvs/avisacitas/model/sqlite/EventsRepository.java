package com.gvs.avisacitas.model.sqlite;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class EventsRepository {

	private final AvisacitasSQLiteOpenHelper dbHelper;

	public EventsRepository(Context context) {
		dbHelper = AvisacitasSQLiteOpenHelper.getInstance(context);
	}

	public LiveData<List<String>> getEventsList() {
		MutableLiveData<List<String>> eventsListLiveData = new MutableLiveData<>();

		// Suponiendo que getNextEventData() devuelve un objeto CalendarEvent
		CalendarEvent nextEvent = dbHelper.getNextEventData();

		if (nextEvent != null) {
			List<String> eventsList = new ArrayList<>();
			eventsList.add(nextEvent.getEventTitle());
			eventsListLiveData.setValue(eventsList);
		} else {
			eventsListLiveData.setValue(new ArrayList<>()); // Lista vacía si no hay eventos
		}

		return eventsListLiveData;
	}
}

