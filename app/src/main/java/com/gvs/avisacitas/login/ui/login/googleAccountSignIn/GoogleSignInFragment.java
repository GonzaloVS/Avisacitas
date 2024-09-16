package com.gvs.avisacitas.login.ui.login.googleAccountSignIn;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.gvs.avisacitas.R;

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
				.build();
		mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

		SignInButton googleSignInButton = view.findViewById(R.id.sign_in_google_button);
		googleSignInButton.setOnClickListener(v -> signInWithGoogle());

		return view;
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
			// Handle successful sign in
			// You can now navigate to the next screen or handle user data
		} catch (ApiException e) {
			// Handle error
		}
	}

}