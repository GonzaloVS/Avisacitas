package com.gvs.avisacitas.main.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventsQueueViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public EventsQueueViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is dashboard fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}
}