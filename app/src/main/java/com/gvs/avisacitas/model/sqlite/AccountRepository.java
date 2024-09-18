package com.gvs.avisacitas.model.sqlite;

import android.content.Context;

public class AccountRepository {

	private final AvisacitasSQLiteOpenHelper dbHelper;

	public AccountRepository(Context context) {
		dbHelper = AvisacitasSQLiteOpenHelper.getInstance(context);
	}
}
