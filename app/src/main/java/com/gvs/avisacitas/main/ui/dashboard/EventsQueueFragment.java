package com.gvs.avisacitas.main.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gvs.avisacitas.databinding.FragmentDashboardBinding;

public class EventsQueueFragment extends Fragment {

	private FragmentDashboardBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState) {
		EventsQueueViewModel eventsQueueViewModel =
				new ViewModelProvider(this).get(EventsQueueViewModel.class);

		binding = FragmentDashboardBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		final TextView textView = binding.textDashboard;
		eventsQueueViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}