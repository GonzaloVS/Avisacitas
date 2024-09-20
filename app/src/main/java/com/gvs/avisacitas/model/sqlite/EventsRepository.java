package com.gvs.avisacitas.model.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gvs.avisacitas.GeneralData;
import com.gvs.avisacitas.model.accounts.Account;
import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EventsRepository {

	public static final String TABLE_CALENDAR_EVENT = CalendarEvent.class.getSimpleName().toLowerCase();
	private final AvisacitasSQLiteOpenHelper dbHelper;

	public EventsRepository(Context context) {
		dbHelper = AvisacitasSQLiteOpenHelper.getInstance(context);
	}

	public LiveData<List<String>> getEventsTitlesList() {
		MutableLiveData<List<String>> eventsListLiveData = new MutableLiveData<>();

		// Suponiendo que getNextEventData() devuelve un objeto CalendarEvent
		CalendarEvent nextEvent = getNextEventData();

		if (nextEvent != null) {
			List<String> eventsList = new ArrayList<>();
			eventsList.add(nextEvent.getEventTitle());
			eventsListLiveData.setValue(eventsList);
		} else {
			eventsListLiveData.setValue(new ArrayList<>()); // Lista vacía si no hay eventos
		}

		return eventsListLiveData;
	}

	public LiveData<List<CalendarEvent>> getEventsList() {
		MutableLiveData<List<CalendarEvent>> eventsListLiveData = new MutableLiveData<>();

		// Suponiendo que getNextEventData() devuelve un objeto CalendarEvent
		CalendarEvent nextEvent = getNextEventData();

		if (nextEvent != null) {
			List<CalendarEvent> eventsList = new ArrayList<>();
			eventsList.add(nextEvent);
			eventsListLiveData.setValue(eventsList);
		} else {
			eventsListLiveData.setValue(new ArrayList<>()); // Lista vacía si no hay eventos
		}

		return eventsListLiveData;
	}

	public void updateEvent(final long id, String columName, final long epochDateSend) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Integer> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Integer execute(SQLiteDatabase db) {
				try {

					ContentValues values = new ContentValues();
					values.put(columName, epochDateSend);
					return db.update(TABLE_CALENDAR_EVENT, values, "pk_id = ?", new String[]{String.valueOf(id)});
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return -1;
			}
		};
		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}



	@SuppressLint("Range")
	public CalendarEvent getNextEventData(String tableName) {


		/**
		 * Actualizar la fecha en la que se recupera para enviar el evento actual de base de datos
		 * Y poner el sistema por el que se envía
		 */
		AvisacitasSQLiteOpenHelper.DatabaseTask<CalendarEvent> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public CalendarEvent execute(SQLiteDatabase db) {

				CalendarEvent eventWCB = null;
				String query =
						"SELECT * " +
								"FROM eventwcb " +
								"WHERE eventStartEpoch > strftime('%s', 'now') " +
								"ORDER BY eventStartEpoch ASC " +
								"LIMIT 1";

				try {

					eventWCB = DatabaseUtils.executeReadOneRow(
							db,
							query,
							new String[]{},
							CalendarEvent.class
					);

				} catch (Exception ex) {
					LogHelper.addLogError("[AvisacitasSQLiteOpenHelper] getNextEventData " + ex);

				}
				return eventWCB;
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);

	}





	public CalendarEvent getNextEventData() {
		AvisacitasSQLiteOpenHelper.DatabaseTask<CalendarEvent> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public CalendarEvent execute(SQLiteDatabase db) {
				CalendarEvent eventWCB = null;
				long currentTime = System.currentTimeMillis();
				String query =

						"SELECT * " +
								"FROM eventwcb " +
								"WHERE i_createdSentDateEpoch = 0 " +
								"AND eventStartEpoch > ? " +
								"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
								"ORDER BY eventStartEpoch ASC " +
								"LIMIT 1";

//					"SELECT * " +
//					"FROM eventwcb " +
//					"WHERE " +
//					"(i_createdSentDateEpoch = 0 OR i_60SentDateEpoch = 0 OR i_1440SentDateEpoch = 0 OR i_2880SentDateEpoch = 0) " +
//					"AND eventStartEpoch > ? " +
//					"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
//					"ORDER BY " +
//					"CASE " +
//					"WHEN i_createdSentDateEpoch = 0 THEN 1 " +
//					"WHEN i_60SentDateEpoch = 0 THEN 2 " +
//					"WHEN i_1440SentDateEpoch = 0 THEN 3 " +
//					"WHEN i_2880SentDateEpoch = 0 THEN 4 " +
//					"END, " +
//					"eventStartEpoch ASC " +
//					"LIMIT 1";

				try {
					eventWCB = DatabaseUtils.executeReadOneRow(
							db,
							query,
							new String[]{String.valueOf(currentTime)}, // Pass the current time as a parameter
							CalendarEvent.class
					);
				} catch (Exception ex) {
					LogHelper.addLogError("[AvisacitasSQLiteOpenHelper] getNextEventData " + ex);
				}
				//LogHelper.addLogInfo("epoch: " + eventWCB.getEventStartEpoch());
				return eventWCB;
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);
	}


	public List<CalendarEvent> getQueueEvents() {

		AvisacitasSQLiteOpenHelper.DatabaseTask<List<CalendarEvent>> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public List<CalendarEvent> execute(SQLiteDatabase db) {
				List<CalendarEvent> eventList = new ArrayList<>();
				long currentTime = System.currentTimeMillis();

				try {
					// Obtener el próximo evento
					String nextEventQuery =
//							"SELECT * FROM eventwcb " +
//									"WHERE i_createdSentDateEpoch = 0 " +
//									"AND eventStartEpoch > ? " +
//									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
//									"ORDER BY eventStartEpoch ASC " +
//									"LIMIT 1";
							"SELECT * " +
									"FROM eventwcb " +
									"WHERE " +
									"(i_createdSentDateEpoch = 0 OR i_60SentDateEpoch = 0 OR i_1440SentDateEpoch = 0 OR i_2880SentDateEpoch = 0) " +
									"AND eventStartEpoch > ? " +
									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
									"ORDER BY " +
									"CASE " +
									"WHEN i_createdSentDateEpoch = 0 THEN 1 " +
									"WHEN i_60SentDateEpoch = 0 THEN 2 " +
									"WHEN i_1440SentDateEpoch = 0 THEN 3 " +
									"WHEN i_2880SentDateEpoch = 0 THEN 4 " +
									"END, " +
									"eventStartEpoch ASC " +
									"LIMIT 1";
					CalendarEvent nextEvent = DatabaseUtils.executeReadOneRow(db, nextEventQuery, new String[]{String.valueOf(currentTime)}, CalendarEvent.class);

					if (nextEvent == null) {
						for (int i = 0; i < 5; i++) {
							eventList.add(createEmptyEvent());
						}
						return eventList;
					}

					long nextEventStartEpoch = nextEvent.getEventStartEpoch();

					// Obtener los dos eventos anteriores al próximo evento (orden descendente)
					String previousEventsQuery =
							"SELECT * FROM eventwcb " +
									"WHERE eventStartEpoch < ? " +
									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
									"ORDER BY eventStartEpoch DESC " +
									"LIMIT 2";  // Limitar a 2 eventos anteriores

					List<CalendarEvent> previousEvents = DatabaseUtils.executeReadMultipleRows(db, previousEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);

					// Obtener los dos eventos siguientes al próximo evento (orden ascendente)
					String nextEventsQuery =
							"SELECT * FROM eventwcb " +
									"WHERE eventStartEpoch > ? " +
									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
									"ORDER BY eventStartEpoch ASC " +
									"LIMIT 2";  // Limitar a 2 eventos siguientes

					List<CalendarEvent> nextEvents = DatabaseUtils.executeReadMultipleRows(db, nextEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);


					// Añadir eventos anteriores o vacíos si no hay suficientes
					if (previousEvents.size() < 2)
						previousEvents.add(createEmptyEvent());
					if (previousEvents.size() < 2)
						previousEvents.add(createEmptyEvent());
					eventList.addAll(previousEvents);

					// Añadir el próximo evento
					eventList.add(nextEvent);

					// Añadir eventos siguientes o vacíos si no hay suficientes
					if (nextEvents.size() < 2)
						nextEvents.add(createEmptyEvent());
					if (nextEvents.size() < 2)
						nextEvents.add(createEmptyEvent());
					eventList.addAll(nextEvents);

				} catch (Exception ex) {
					LogHelper.addLogError("[AvisacitasSQLiteOpenHelper] getSurroundingEvents " + ex);
				}

				return eventList;
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);
	}

	private CalendarEvent createEmptyEvent() {
		CalendarEvent emptyEvent = new CalendarEvent();
		emptyEvent.setEventTitle("-");
		emptyEvent.setEventDescription("-");
		emptyEvent.setTargetPhone("000000000");
		emptyEvent.setEventStartEpoch(0);  // Puedes elegir otro valor por defecto si lo necesitas
		return emptyEvent;
	}



}

