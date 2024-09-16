package com.gvs.avisacitas.login.ui.login.termsAndConditions;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.gvs.avisacitas.R;

public class TermsAndConditionsFragment extends Fragment {

	private TermsAndConditionsViewModel mViewModel;

	public TermsAndConditionsFragment() {
		// Required empty public constructor
	}

	public static TermsAndConditionsFragment newInstance() {
		return new TermsAndConditionsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
//		View view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
//
//		CheckBox checkBox = view.findViewById(R.id.checkBox);
//		Button acceptButton = view.findViewById(R.id.accept_button);
//
//		// Disable button initially
//		acceptButton.setEnabled(false);
//
//		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//			acceptButton.setEnabled(isChecked);
//		});
//
//		acceptButton.setOnClickListener(v -> {
//			// Navegar a GoogleSignInFragment usando la acción definida en nav_graph_login
//			Navigation.findNavController(v).navigate(R.id.action_termsAndConditionsFragment_to_googleSignInFragment);
//		});
//
//		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(TermsAndConditionsViewModel.class);
		// TODO: Use the ViewModel
	}

}