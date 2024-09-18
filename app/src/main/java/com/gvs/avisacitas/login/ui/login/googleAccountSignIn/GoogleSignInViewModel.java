package com.gvs.avisacitas.login.ui.login.googleAccountSignIn;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.gvs.avisacitas.Manifest;
import com.gvs.avisacitas.main.MainActivity;
import com.gvs.avisacitas.model.accounts.Account;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class GoogleSignInViewModel extends ViewModel {
	// TODO: Implement the ViewModel

    private final Context context = null;
    private final AvisacitasSQLiteOpenHelper dbHelper = new AvisacitasSQLiteOpenHelper(context);
    private static final int STARTING_PK_MCID = 8000;

    private final MutableLiveData<Boolean> _saveAccountStatus = new MutableLiveData<>();
    public LiveData<Boolean> saveAccountStatus = _saveAccountStatus;

    public GoogleSignInViewModel() {
    }

    public boolean doesAccountExist(String email) {
        List<Account> existingAccounts = dbHelper.getAllAccounts();
        for (Account account : existingAccounts) {
            if (account.getEmail().equalsIgnoreCase(email))
                return true;

        }
        return false;
    }

    public void saveAccountWithProfileImage(String accountName, String displayName, Uri photoUrl) {
        if (doesAccountExist(accountName)) {
            _saveAccountStatus.setValue(false);
            return;
        }

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(photoUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                        try {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] profileImageBytes = stream.toByteArray();
                            saveAccount(accountName, displayName, profileImageBytes);

                            _saveAccountStatus.setValue(true); // Indicar éxito

                        } catch (Exception ex) {
                            LogHelper.addLogError(ex);
                            _saveAccountStatus.setValue(false); // Indicar fallo
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Manejar la limpieza de recursos si es necesario
                    }
                });
    }


    private boolean saveAccount(String accountName, String displayName, byte[] profileImageBytes) {
        String pkMcid = getAvailablePkMcid();
        String phoneNumber = getPhoneNumber();

        Account account = new Account(
                displayName != null ? displayName : "",
                "13133131311313",
                phoneNumber,
                pkMcid,
                accountName,
                0,
                "true",
                0L,
                0L,
                0L,
                "true",
                "false",
                "true",
                "true",
                "true",
                "true",
                "false",
                "",
                "",
                "",
                "",
                "",
                "false",
                0L
        );

        account.setProfileImage(profileImageBytes);
        dbHelper.insertOrUpdateFromObjectList(List.of(account));
        return true;
    }


    private String getAvailablePkMcid() {
        int pkMcid = STARTING_PK_MCID;
        List<Account> existingAccounts = dbHelper.getAllAccounts();
        while (true) {
            boolean isAvailable = true;
            String pkMcidString = String.valueOf(pkMcid);
            for (Account account : existingAccounts) {
                if (account.getPk_mcid().equals(pkMcidString)) {
                    isAvailable = false;
                    break;
                }
            }
            if (isAvailable)
                return pkMcidString;

            pkMcid++;  // Incrementa el pkMcid si ya existe
        }
    }


    private String getPhoneNumber() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (subscriptionManager != null) {
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

            if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
                // Asumiendo que quieres el número de la primera SIM activa
                SubscriptionInfo subscriptionInfo = subscriptionInfoList.get(0);
                String phoneNumber = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    phoneNumber = subscriptionManager.getPhoneNumber(subscriptionInfo.getSubscriptionId());
                }

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    return phoneNumber;
                }
            }
        }


        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            String phoneNumber = telephonyManager.getLine1Number();
//            String phoneIso = telephonyManager.getNetworkCountryIso();
//            String phoneSimIso = telephonyManager.getSimCountryIso();
//            int phoneType = telephonyManager.getPhoneType();
//            String phoneOperator = telephonyManager.getNetworkOperator();
//            String phoneOperatorName = telephonyManager.getNetworkOperatorName();
            if (phoneNumber != null && !phoneNumber.isEmpty())
                return phoneNumber;

        }
        // Si llegamos aquí, no se pudo obtener el número de teléfono
        return "";
    }

}