package com.gvs.avisacitas.model.sender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.gvs.avisacitas.autoSend.AvisacitasAccessibilityService;
import com.gvs.avisacitas.autoSend.SenderEngine;
import com.gvs.avisacitas.main.MainActivity;
import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.model.sqlite.AccountRepository;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class WhatSenderHelper {

	private boolean isPowerOn = true;
	private SenderEngine senderEngine;


	public static void sendWhatsapp(Context context, CalendarEvent event, String phone, String message) {

		//Comprobar que CalendarContract.Events.STATUS =! 2 [STATUS_CANCELED Constant Value: 2 (0x00000002)]
		//Comprobar que CalendarContract.Events.DELETED ==0 (o =!1) [1 = marcar para borrar (no tenemos mamnera de cambiarlo sin API, pero asegurarse que no recuperamos ninguno pendiente de borrar)]
		try {
			Intent intent = msgBuilder(context, event, phone, message);

			intent.setPackage("com.whatsapp");

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (context != null)
				context.startActivity(intent);

			Thread.sleep(2000);

			Runnable task = new Runnable() {
				@Override
				public void run() {
					try {
						AvisacitasAccessibilityService.getInstance().clickOnSendBtnWhatsapp();
					} catch (Exception ex) {
						LogHelper.addLogError(ex);
					}
				}
			};

			new Thread(task).start();


		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}


	public static void sendWB(Context context, CalendarEvent event, String phone, String message) {

		try {

			Intent intent = msgBuilder(context, event, phone, message);

			intent.setPackage("com.whatsapp.w4b");

			//String url = "https://b.whatsapp.com/send?phone=" + phone;
			// Establecer la URI del enlace en el Intent
			//intent.setData(Uri.parse(url));

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (context != null)
				context.startActivity(intent);

			// Esperar un momento para permitir que WhatsApp se abra
			Thread.sleep(2000);

			Runnable task = new Runnable() {
				@Override
				public void run() {
					try {
						AvisacitasAccessibilityService.getInstance().clickOnSendBtnWB();
					} catch (Exception ex) {
						LogHelper.addLogError(ex);
					}
				}
			};

			new Thread(task).start();


		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}


	private static Intent msgBuilder(Context context, CalendarEvent event, String phone, String message) {

		//Comprobar que CalendarContract.Events.STATUS =! 2 [STATUS_CANCELED Constant Value: 2 (0x00000002)]
		//Comprobar que CalendarContract.Events.DELETED ==0 (o =!1) [1 = marcar para borrar (no tenemos mamnera de cambiarlo sin API, pero asegurarse que no recuperamos ninguno pendiente de borrar)]

		Intent intent = null;

		try {
			if (context.getApplicationContext() instanceof MainActivity) {
				//enterPiPMode();
			}

			// Esperar un momento para permitir que WhatsApp se abra
			Thread.sleep(2000);

			// Formatear fecha del evento
			long eventStartEpoch = event.getEventStartEpoch();
			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM", Locale.getDefault());
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

			String formattedDate = sdfDate.format(new Date(eventStartEpoch));
			String formattedTime = sdfTime.format(new Date(eventStartEpoch));

			String formattedDateTime = "\uD83D\uDDD3 " + formattedDate + "  \uD83D\uDD53 " + formattedTime + "h";

			AccountRepository accountRepository = new AccountRepository(context);
			String companyName = "";
			if (accountRepository.getCompanyName() != null)
				companyName = accountRepository.getCompanyName().toUpperCase();

			String customMessage = "";
			if (accountRepository.getCustomOncreateMsg() != null)
				customMessage = accountRepository.getCustomOncreateMsg();

			if (!phone.isEmpty() && !message.isEmpty()) {
				// Crear el intento para enviar el mensaje de WhatsApp
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("whatsapp://send?phone=" + phone +
						"&text=" + "*Prox. Cita: " + formattedDateTime +
						"*\n" + "*" + companyName + "*" + "\n\n" +
						customMessage));// + nombre));

			}

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}

		return intent;
	}
}
