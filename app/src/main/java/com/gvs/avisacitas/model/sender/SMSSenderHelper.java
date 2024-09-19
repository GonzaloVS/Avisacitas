package com.gvs.avisacitas.model.sender;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.gvs.avisacitas.utils.error.LogHelper;

public class SMSSenderHelper {

    private void sendSMS(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(context, "SMS enviado a " + phoneNumber + " : " + message, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, "Fallo al enviar el SMS: " + message + "phone: " +phoneNumber, Toast.LENGTH_LONG).show();
            LogHelper.addLogError(ex);
        }
    }
}
