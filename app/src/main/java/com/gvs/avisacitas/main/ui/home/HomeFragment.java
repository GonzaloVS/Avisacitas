package com.gvs.avisacitas.main.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gvs.avisacitas.databinding.FragmentHomeBinding;
import com.gvs.avisacitas.main.SharedViewModel;

public class HomeFragment extends Fragment {

	private FragmentHomeBinding binding;
	private SharedViewModel sharedViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState) {
		HomeViewModel homeViewModel =
				new ViewModelProvider(this).get(HomeViewModel.class);

		binding = FragmentHomeBinding.inflate(inflater, container, false);
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