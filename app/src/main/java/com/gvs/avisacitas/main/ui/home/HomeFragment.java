package com.gvs.avisacitas.main.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gvs.avisacitas.databinding.FragmentHomeBinding;
import com.gvs.avisacitas.main.SharedViewModel;
import com.gvs.avisacitas.model.accounts.Account;
import com.gvs.avisacitas.model.sqlite.AccountRepository;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;
import com.gvs.avisacitas.utils.error.LogHelper;

public class HomeFragment extends Fragment {

	private FragmentHomeBinding binding;
	private SharedViewModel sharedViewModel;
	private HomeViewModel viewModel;

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
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		try {
			viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

			AccountRepository accountRepository = new AccountRepository(getContext());
			Account account = accountRepository.getActiveAccount();
			if (account != null) {
				// Set company name if available
				String companyName = account.getCompanyName();
				if (companyName != null && !companyName.isEmpty()) {
					binding.editCompanyName.setText(companyName);
				}

				String customOncreateMsg = account.getCustomOncreateMsg();
				if (customOncreateMsg != null && !customOncreateMsg.isEmpty()) {
					binding.editMessageText.setText(customOncreateMsg);
				}
			}

			// Agregar TextWatcher para detectar cambios en el campo "Nombre de la empresa"
			binding.editCompanyName.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void afterTextChanged(Editable s) {

					accountRepository.saveCompanyNameToDatabase(s.toString());
				}
			});

			// Agregar TextWatcher para detectar cambios en el campo "Texto del mensaje"
			binding.editMessageText.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					accountRepository.saveMessageTextToDatabase(s.toString());
				}
			});


		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}