package com.gvs.avisacitas;


import android.net.Uri;
import android.provider.ContactsContract;

public class GlobalReferences {

    public static final String ACCOUNT_TYPE = "com.gvs.avisacitas";
    public static final String ACCOUNT_NAME = "Avisacitas";
    public static final String ANDROID_ACCOUNT_NAME = "Avisacitas";
    public static final String CONTENT_AUTHORITY = "com.gvs.avisacitas.provider";
    public static final String INSERT_ORDER = "insert";
    public static final String UPDATE_ORDER = "update";
    public static final String DELETE_ORDER = "delete";
    public static final String IGNORE_ORDER = "ignore";

    public static final int PERMISSION_REQUEST_CODE = 200;

    public static final String URL_LOGIN = "https://app.wachatbot.com/club/code/login";
    public static final String URL_SIGNIN = "https://app.wachatbot.com/android/signin";
    public static final String URL_DOWNLOAD = "https://app.wachatbot.com/android/contacts/get";
    public static final String URL_SIGNUP = "https://app.wachatbot.com/android/signup";
    public static final String URL_SEND = "https://api.wachatbot.com/send";
    public static final String URL_SEND_TOKEN = "https://app.wachatbot.com/android/fcmtoken/update";

    public static final boolean NOTIFICATION_NOT_PERSISTENT = false;
    public static final boolean NOTIFICATION_PERSISTENT = true;


    public static final int ACCOUNT_STATUS_NEVERSYNCED = 0;
    public static final int ACCOUNT_STATUS_SYNCCANCELED = 1;
    public static final int ACCOUNT_STATUS_SYNCFINISHED = 2;
    public static final int ACCOUNT_STATUS_SYNCERROR = -1;



    //NOTIFICATION_ID
    public static final int NOTIFICATION_ID_PERMISSIONS_REQUIRED = 1;

}
