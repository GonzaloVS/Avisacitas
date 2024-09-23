package com.gvs.avisacitas.main.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gvs.avisacitas.databinding.FragmentSettingsBinding;
import com.gvs.avisacitas.main.SharedViewModel;

public class SettingsFragment extends Fragment {

	private FragmentSettingsBinding binding;
	private SharedViewModel sharedViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState) {
		SettingsViewModel settingsViewModel =
				new ViewModelProvider(this).get(SettingsViewModel.class);

		binding = FragmentSettingsBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		// Obtener el mismo ViewModel compartido con la Activity
		sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

		// Pausar la reproducción al entrar en configuraciones
		sharedViewModel.stopPlayback();

		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}