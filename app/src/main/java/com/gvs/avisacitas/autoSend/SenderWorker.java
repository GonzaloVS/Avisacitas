package com.gvs.avisacitas.autoSend;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class SenderWorker extends Worker {

	public SenderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}

	@NonNull
	@Override
	public Result doWork() {
		SenderEngine senderEngine = new SenderEngine(getApplicationContext());
		senderEngine.start();
		return Result.success();
	}
}

