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
import com.gvs.avisacitas.databinding.FragmentTermsAndConditionsBinding;
import com.gvs.avisacitas.utils.error.LogHelper;
import com.gvs.avisacitas.utils.links.LinkHelper;

public class TermsAndConditionsFragment extends Fragment {

	private FragmentTermsAndConditionsBinding binding;
	private TermsAndConditionsViewModel mViewModel;

	public TermsAndConditionsFragment() {
		// Required empty public constructor
	}

//	//public static TermsAndConditionsFragment newInstance() {
//		return new TermsAndConditionsFragment();
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);

		CheckBox checkBox = view.findViewById(R.id.checkBox);
		Button acceptButton = view.findViewById(R.id.pr_continue_btn);

		// Deshabilita el botón inicialmente
		acceptButton.setEnabled(false);

		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			acceptButton.setEnabled(isChecked);
			acceptButton.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
		});

		acceptButton.setOnClickListener(v -> {
			// Navegar a GoogleSignInFragment usando la acción definida en nav_graph_login
			Navigation.findNavController(v).navigate(R.id.action_termsAndConditionsFragment_to_googleSignInFragment);
		});

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setTextLinks();

		mViewModel = new ViewModelProvider(this).get(TermsAndConditionsViewModel.class);
		//mViewModel.init(this);  // Inicializa el ViewModel
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(TermsAndConditionsViewModel.class);
		// TODO: Use the ViewModel
	}

	private void setTextLinks(){
		try{
		// Frase
		String fullText = getString(R.string.agree_checkbox);

		// Palabras que deben tener un enlace
		String[] linkTexts = new String[]{
				"Terms of Service",
				"Privacy Policy."
		};

		//Enlaces
		String[] links = new String[]{
				getString(R.string.agree_terms_url),
				getString(R.string.agree_privacy_url)
		};

		// Aplicamos los enlaces usando LinkHelper
		LinkHelper.setTextLinks(requireContext(), binding.textCheckbox, fullText, linkTexts, links, R.color.link_blue);

		}catch (Exception ex){
			LogHelper.addLogError(ex);
		}

	}

}