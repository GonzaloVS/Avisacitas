package com.gvs.avisacitas.main.ui.eventsQueue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventsQueueViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public EventsQueueViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is eventsQueue fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}
}