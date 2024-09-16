package com.gvs.avisacitas.main.ui.eventsQueue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gvs.avisacitas.databinding.FragmentEventsQueueBinding;
import com.gvs.avisacitas.main.SharedViewModel;

public class EventsQueueFragment extends Fragment {

	private FragmentEventsQueueBinding binding;
	private SharedViewModel sharedViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState) {
		EventsQueueViewModel eventsQueueViewModel =
				new ViewModelProvider(this).get(EventsQueueViewModel.class);

		binding = FragmentEventsQueueBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		// Obtener el mismo ViewModel compartido con la Activity
		sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}