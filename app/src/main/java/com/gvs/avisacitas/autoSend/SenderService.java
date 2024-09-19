//package com.gvs.avisacitas.autoSend;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//import androidx.annotation.Nullable;
//
//public class SenderService extends Service {
//	private SenderEngine senderEngine;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		senderEngine = new SenderEngine(this);
//		senderEngine.start();
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		if (!senderEngine.isRunning()) {
//			senderEngine.start();
//		}
//		return START_STICKY;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		senderEngine.stop();
//	}
//
//	@Nullable
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//}
