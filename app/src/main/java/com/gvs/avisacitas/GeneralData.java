package com.gvs.avisacitas;

import androidx.annotation.NonNull;

import java.io.Serializable;

public final class GeneralData implements Serializable{

	private static String urlLogin = "https://app.wachatbot.com/club/code/login";
	private static String urlDownload = "https://app.wachatbot.com/android/contacts/get";
	private static String urlSend = "https://api.wachatbot.com/send";
	private static String urlSendToken = "https://app.wachatbot.com/android/fcmtoken/update";
	private String fcmToken, log;
	private String panicBtn;


	public GeneralData(
			String fcmToken,
			String log,
			String panicBtn,
			String urlLogin,
			String urlDownload,
			String urlSend,
			String urlSendToken) {

		this.fcmToken = fcmToken;
		this.log = log;
		this.panicBtn = panicBtn != null ? panicBtn : "true";
		GeneralData.urlLogin = urlLogin;
		GeneralData.urlDownload = urlDownload;
		GeneralData.urlSend = urlSend;
		GeneralData.urlSendToken = urlSendToken;
	}

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getPanicBtn() {
		return panicBtn;
	}

	public void setPanicBtn(String panicBtn) {
		this.panicBtn = panicBtn;
	}

	public String getUrlLogin() {
		return urlLogin;
	}

	public void setUrlLogin(String urlLogin) {
		GeneralData.urlLogin = urlLogin;
	}

	public String getUrlDownload() {
		return urlDownload;
	}

	public void setUrlDownload(String urlDownload) {
		GeneralData.urlDownload = urlDownload;
	}

	public String getUrlSend() {
		return urlSend;
	}

	public void setUrlSend(String urlSend) {
		GeneralData.urlSend = urlSend;
	}

	public String getUrlSendToken() {
		return urlSendToken;
	}

	public void setUrlSendToken(String urlSendToken) {
		GeneralData.urlSendToken = urlSendToken;
	}

	@NonNull
	public String toString() {
		return "General Data{" + '\'' +
				"FCM token='" + fcmToken + '\'' +
				", log='" + log + '\'' +
				", URL login='" + urlLogin + '\'' +
				", URL download='" + urlDownload + '\'' +
				", URL send=" + urlSend + '\'' +
				", URL send token=" + urlSendToken + '\'' +
				'}';
	}


}



