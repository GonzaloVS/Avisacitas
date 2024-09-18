package com.gvs.avisacitas.login.ui.login.googleAccountSignIn;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.gvs.avisacitas.R;
import com.gvs.avisacitas.utils.error.LogHelper;

public class GoogleSignInFragment extends Fragment {

	private GoogleSignInViewModel mViewModel;

	private GoogleSignInClient mGoogleSignInClient;
	private static final int RC_SIGN_IN = 9001;

	public GoogleSignInFragment() {
		// Required empty public constructor
	}
	public static GoogleSignInFragment newInstance() {
		return new GoogleSignInFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_google_sign_in, container, false);

		// Configure Google Sign-In
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestIdToken("1001618390215-l1c2ok947h8ej60irjvtfdcle8nglur1.apps.googleusercontent.com")
				.build();
		mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

		SignInButton googleSignInButton = view.findViewById(R.id.sign_in_google_button);
		googleSignInButton.setOnClickListener(v -> signInWithGoogle());

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		try {

			mViewModel = new GoogleSignInViewModel(requireActivity().getApplication());

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = new ViewModelProvider(this).get(GoogleSignInViewModel.class);
		// TODO: Use the ViewModel
	}

	private void signInWithGoogle() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			GoogleSignInAccount account = completedTask.getResult(ApiException.class);

			if (account == null)
				return;

			// Extraer información del usuario
			String accountName = account.getEmail();
			String displayName = account.getDisplayName();
			Uri photoUrl = account.getPhotoUrl();

			// Lógica para manejar la cuenta y navegar a otra pantalla o manejar los datos
			if (accountName == null || displayName == null || photoUrl == null) {
				Toast.makeText(getContext(), "Account already exists", Toast.LENGTH_SHORT).show();
				return;
			}

			// Por ejemplo, puedes pasar estos datos a tu ViewModel
			mViewModel.saveAccountWithProfileImage(accountName, displayName, photoUrl);
			Toast.makeText(getContext(), "Account saved: " + accountName, Toast.LENGTH_SHORT).show();

		} catch (ApiException e) {
			// Manejar errores de inicio de sesión
			Toast.makeText(getContext(), "Sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			int statusCode = e.getStatusCode();
			handleSignInError(statusCode);
		}
	}

	private void handleSignInError(int statusCode) {
		switch (statusCode) {
			case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
				Toast.makeText(getContext(), "Sign in cancelled", Toast.LENGTH_SHORT).show();
				LogHelper.addLogInfo("Sign in cancelled " + statusCode);
				break;
			case GoogleSignInStatusCodes.SIGN_IN_FAILED:
				Toast.makeText(getContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
				LogHelper.addLogInfo("Sign in failed " + statusCode);
				break;
			case GoogleSignInStatusCodes.NETWORK_ERROR:
				Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
				LogHelper.addLogInfo("Network error " + statusCode);
				break;
			default:
				Toast.makeText(getContext(), "Error code: " + statusCode, Toast.LENGTH_SHORT).show();
				LogHelper.addLogInfo("Error code: " + statusCode);
				break;
		}
	}

}