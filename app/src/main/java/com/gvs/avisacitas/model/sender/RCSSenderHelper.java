package com.gvs.avisacitas.model.sender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.gvs.avisacitas.autoSend.AvisacitasAccessibilityService;
import com.gvs.avisacitas.utils.error.LogHelper;

public class RCSSenderHelper {
	public static void sendRCS(Context context, String phone, String message) {
		try {
			// Preparar y enviar el mensaje
			Intent rcsIntent = new Intent(Intent.ACTION_VIEW);
			rcsIntent.setData(Uri.parse("sms:" + phone));
			rcsIntent.putExtra("sms_body", message);
			rcsIntent.setPackage("com.google.android.apps.messaging");
			rcsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Use NEW_TASK to start a new task
			context.startActivity(rcsIntent);

			// Usar un Handler para retrasar la acción de clic
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					AvisacitasAccessibilityService.getInstance().clickOnSendBtnRCS();
				}
			}, 2000); // 2 segundos de retraso


		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}
}
