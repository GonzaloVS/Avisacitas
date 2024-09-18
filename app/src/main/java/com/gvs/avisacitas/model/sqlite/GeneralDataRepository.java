package com.gvs.avisacitas.model.sqlite;

import android.content.Context;

public class GeneralDataRepository {

	private final AvisacitasSQLiteOpenHelper dbHelper;

	public GeneralDataRepository(Context context) {
		dbHelper = AvisacitasSQLiteOpenHelper.getInstance(context);
	}
}
