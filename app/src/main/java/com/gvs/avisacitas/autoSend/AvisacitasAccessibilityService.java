package com.gvs.avisacitas.autoSend;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.gvs.avisacitas.main.MainActivity;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class AvisacitasAccessibilityService extends AccessibilityService {

	private static AvisacitasAccessibilityService instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public static AvisacitasAccessibilityService getInstance() {
		return instance;
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
		if (rootNode != null) {
			List<String> keywordsSendSMS = new ArrayList<>();
			findInChildNodes(rootNode, "*", keywordsSendSMS, keywordsSendSMS);
		}
	}

	public void clickOnSendBtnWhatsapp() {
		List<String> keywordsSendWhatsapp = new ArrayList<>(Arrays.asList("send", "Send", "enviar", "Enviar"));
		List<String> blacklistWhatsapp = new ArrayList<>(Arrays.asList("SMS"));

		AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
		if (rootNode != null) {
			findInChildNodes(rootNode, "com.whatsapp", keywordsSendWhatsapp, blacklistWhatsapp);
		}
	}

	public void clickOnSendBtnWB() {
		List<String> keywordsSendWhatsapp = new ArrayList<>(Arrays.asList("send", "enviar"));
		List<String> blacklistWhatsapp = new ArrayList<>();

		AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
		if (rootNode != null) {
			findInChildNodes(rootNode, "com.whatsapp.w4b", keywordsSendWhatsapp, blacklistWhatsapp);
		}
	}

	public void clickOnSendBtnRCS() {
		List<String> keywordsSendSMS = new ArrayList<>(Arrays.asList("send", "enviar", "Enviar mensaje cifrado", "Enviar"));
		List<String> blacklistSMS = new ArrayList<>(Arrays.asList("SMS"));

		AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
		if (rootNode != null) {
			findInChildNodes(rootNode, "com.google.android.apps.messaging", keywordsSendSMS, blacklistSMS);
		}
	}

	@Override
	public void onInterrupt() {
		// Código para manejar la interrupción del servicio
	}

	private boolean findInChildNodes(
			AccessibilityNodeInfo parentNode,
			String packageName,
			List<String> keywordListInLowerCase,
			List<String> blacklistInLowerCase) {

		try {
			if (parentNode == null || (!packageName.equals("*") && !parentNode.getPackageName().equals(packageName))) {
				return false;
			}

			int childCount = parentNode.getChildCount();

			for (int pos = 0; pos < childCount; pos++) {
				AccessibilityNodeInfo currentChildNode = parentNode.getChild(pos);

				if (currentChildNode == null) continue;

				// Verifica que el nodo sea clickeable y tenga la descripción "Enviar"
				if (currentChildNode.isClickable() &&
						"android.widget.ImageButton".equals(currentChildNode.getClassName()) &&
						"Enviar".equals(currentChildNode.getContentDescription())) {

					boolean clicked = currentChildNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					LogHelper.addLogInfo("Clicked on node with description 'Enviar': " + clicked);
					if (!clicked) {
						LogHelper.addLogError("Failed to click on node with description 'Enviar'");
					}

					// Retornar verdadero si el clic fue exitoso
					if (clicked) {
						// Esperar unos segundos para asegurar que el clic se procese
						new Handler(Looper.getMainLooper()).postDelayed(() -> {
							// Volver a la aplicación
							Intent backToAppIntent = new Intent(AvisacitasAccessibilityService.this, MainActivity.class);
							backToAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(backToAppIntent);
						}, 1000); // 1000 ms de espera

						return true;
					}
				}

				// Busca recursivamente en los nodos hijos
				if (currentChildNode.getChildCount() > 0 &&
						findInChildNodes(currentChildNode, packageName, keywordListInLowerCase, blacklistInLowerCase)) {
					return true;
				}

				currentChildNode.recycle();
			}

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}

		return false;
	}


//	public void sendRCSInsertingValues(String phoneNumber, String message) {
//		AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
//
//		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
//
//		new Handler(Looper.getMainLooper()).postDelayed(() -> {
//			// Buscar el campo de texto para el número de teléfono
//			if (rootNode != null) {
//				List<AccessibilityNodeInfo> phoneNumberFields = rootNode.findAccessibilityNodeInfosByViewId("com.android.mms:id/recipients_editor");
//				if (!phoneNumberFields.isEmpty()) {
//					AccessibilityNodeInfo phoneNumberField = phoneNumberFields.get(0);
//					phoneNumberField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, createSetTextArguments(phoneNumber));
//				}
//
//				// Buscar el campo de texto para el mensaje
//				List<AccessibilityNodeInfo> messageFields = rootNode.findAccessibilityNodeInfosByViewId("com.android.mms:id/embedded_text_editor");
//				if (!messageFields.isEmpty()) {
//					AccessibilityNodeInfo messageField = messageFields.get(0);
//					messageField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, createSetTextArguments(message));
//				}
//
//				// Buscar el botón de enviar y hacer clic
//				List<AccessibilityNodeInfo> sendButtons = rootNode.findAccessibilityNodeInfosByViewId("com.android.mms:id/send_button");
//				for (AccessibilityNodeInfo sendButton : sendButtons) {
//					sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//				}
//			}
//
//			new Handler(Looper.getMainLooper()).postDelayed(() -> {
//				// Volver a tu aplicación
//				Intent backToAppIntent = new Intent(AvisacitasAccessibilityService.this, MainActivity.class);
//				backToAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(backToAppIntent);
//			}, 2000);
//		}, 2000);
//	}

	private Bundle createSetTextArguments(String text) {
		Bundle arguments = new Bundle();
		arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
		return arguments;
	}

	public static boolean isServiceRunning() {
		return instance != null;
	}

	public boolean containsAnyKeyword(String text, List<String> keywordListInLowerCase) {
		if (text == null || text.length() == 0 || keywordListInLowerCase == null || keywordListInLowerCase.size() == 0)
			return false;

		String lowerCaseText = text.toLowerCase();
		String[] words = lowerCaseText.split("\\s+");

		for (String word : words) {
			if (keywordListInLowerCase.contains(word)) {
				return true;
			}
		}
		return false;
	}
}

