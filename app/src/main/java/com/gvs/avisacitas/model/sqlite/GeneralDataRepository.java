package com.gvs.avisacitas.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gvs.avisacitas.utils.error.LogHelper;

public class GeneralDataRepository {

	private final AvisacitasSQLiteOpenHelper dbHelper;

	public GeneralDataRepository(Context context) {
		dbHelper = AvisacitasSQLiteOpenHelper.getInstance(context);
	}

	public String getPanicBtn() {

		AvisacitasSQLiteOpenHelper.DatabaseTask<String> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public String execute(SQLiteDatabase db) {

				String panicBtn = null;
				String query = "SELECT panicBtn FROM generaldata LIMIT 1";

				try(Cursor cursor = db.rawQuery(query, null);) {

					if (cursor != null && cursor.moveToFirst()) {
						int panicBtnIndex =  cursor.getColumnIndex("panicBtn");
						panicBtn = cursor.getString(panicBtnIndex);
					}

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return panicBtn;
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);

	}

	public void updatePanicBtn(boolean isChecked) {

		LogHelper.addLogInfo("updatePanicBtn(boolean isChecked) updated to = " + isChecked);

		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					ContentValues values = new ContentValues();
					values.put("panicBtn", isChecked ? "true" : "false");
					db.update("generaldata", values, null, null);
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}
}
