package com.gvs.avisacitas.autoSend;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.model.sender.WhatSenderHelper;
import com.gvs.avisacitas.model.sqlite.EventsRepository;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.util.concurrent.TimeUnit;

public class SenderEngine {
	private EventsRepository eventsRepository;
	private Context context;

	public SenderEngine(Context context) {
		this.context = context.getApplicationContext();
	}

	public void start() {
		scheduleNextEvent();
	}

	private void scheduleNextEvent() {

		eventsRepository = new EventsRepository(context);
		CalendarEvent event = eventsRepository.getNextEventData();
		if (event != null) {
			long currentTime = System.currentTimeMillis() / 1000;
			long eventStart = event.getEventStartEpoch();

			long delayInSeconds = 30; // Default delay
			if (eventStart - currentTime <= 48 * 3600 && event.getI_2880SentStatus() == 0) {
				delayInSeconds = eventStart - currentTime - 48 * 3600;
				sendEvent(event, "48h", event.getTargetPhone(), "Recordatorio de cita en 48 horas: " + eventStart);
			} else if (eventStart - currentTime <= 24 * 3600 && event.getI_1440SentStatus() == 0) {
				delayInSeconds = eventStart - currentTime - 24 * 3600;
				sendEvent(event, "24h", event.getTargetPhone(), "Recordatorio de cita en 24 horas: " + eventStart);
			} else if (eventStart - currentTime <= 3600 && event.getI_60SentStatus() == 0) {
				delayInSeconds = eventStart - currentTime - 3600;
				sendEvent(event, "1h", event.getTargetPhone(), "Recordatorio de cita en 1 hora: " + eventStart);
			} else if (event.getI_createdSentStatus() == 0) {
				sendEvent(event, "created", event.getTargetPhone(), "Recordatorio de cita creada: " + eventStart);
			}

			scheduleWork(delayInSeconds);
		} else {
			// No hay eventos, reprogramar en 5 minutos
			scheduleWork(300);
		}
	}

	private void sendEvent(CalendarEvent event, String type, String phone, String message) {
		try {
			WhatSenderHelper.sendWhatsapp(context, event, phone, message);
			updateEventStatusAndSentDateEpoch(event, type);
		} catch (Exception e) {
			LogHelper.addLogError(e);
		}
	}

	private void updateEventStatusAndSentDateEpoch(CalendarEvent event, String type) {
		long currentTime = System.currentTimeMillis() / 1000;

		switch (type) {
			case "created":
				eventsRepository.updateEvent(event.getPk_id(), "i_createdGetDateEpoch", currentTime);
				eventsRepository.updateEvent(event.getPk_id(), "i_createdSentStatus", 1);
				break;
			case "48h":
				eventsRepository.updateEvent(event.getPk_id(), "i_2880GetDateEpoch", currentTime);
				eventsRepository.updateEvent(event.getPk_id(), "i_2880SentStatus", 1);
				break;
			case "24h":
				eventsRepository.updateEvent(event.getPk_id(), "i_1440GetDateEpoch", currentTime);
				eventsRepository.updateEvent(event.getPk_id(), "i_1440SentStatus", 1);
				break;
			case "1h":
				eventsRepository.updateEvent(event.getPk_id(), "i_60GetDateEpoch", currentTime);
				eventsRepository.updateEvent(event.getPk_id(), "i_60SentStatus", 1);
				break;
			default:
				LogHelper.addLogError(new Exception("Tipo de recordatorio desconocido: " + type));
		}
	}

	private void scheduleWork(long delayInSeconds) {
		Constraints constraints = new Constraints.Builder()
				.setRequiresBatteryNotLow(true)
				.setRequiresDeviceIdle(false)
				.build();

		OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SenderWorker.class)
				.setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
				.setConstraints(constraints)
				.build();

		WorkManager.getInstance(context).enqueue(workRequest);
	}
}

