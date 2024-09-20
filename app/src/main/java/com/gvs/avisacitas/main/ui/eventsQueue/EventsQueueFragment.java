package com.gvs.avisacitas.main.ui.eventsQueue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gvs.avisacitas.databinding.FragmentEventsQueueBinding;
import com.gvs.avisacitas.main.SharedViewModel;
import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.util.List;

public class EventsQueueFragment extends Fragment {

	private FragmentEventsQueueBinding binding;
	private SharedViewModel sharedViewModel;
	private EventsQueueAdapter eventsAdapter;
	private List<CalendarEvent> eventList;

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState) {

//		EventsQueueViewModel eventsQueueViewModel =
//				new ViewModelProvider(this).get(EventsQueueViewModel.class);
//
//		binding = FragmentEventsQueueBinding.inflate(inflater, container, false);
//		View root = binding.getRoot();
//
//		// Obtener el mismo ViewModel compartido con la Activity
//		sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
//
//		return root;

		try {
			binding = FragmentEventsQueueBinding.inflate(inflater, container, false);

			// Inicializar el RecyclerView
			RecyclerView recyclerView = binding.recyclerViewEvents;
			recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

			// Obtener el ViewModel compartido con la Activity
			sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

			// Observar los eventos desde el ViewModel compartido
			sharedViewModel.getEventsList().observe(getViewLifecycleOwner(), events -> {
				eventList = events;
				// Configurar el adaptador con la lista de eventos
				eventsAdapter = new EventsQueueAdapter(eventList);
				recyclerView.setAdapter(eventsAdapter);
			});

			return binding.getRoot();
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}

		return binding.getRoot();
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}