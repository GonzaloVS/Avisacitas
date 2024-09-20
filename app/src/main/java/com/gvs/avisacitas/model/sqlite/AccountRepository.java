package com.gvs.avisacitas.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.gvs.avisacitas.model.accounts.Account;
import com.gvs.avisacitas.utils.error.LogHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

	public static final String TABLE_ACCOUNT = Account.class.getSimpleName().toLowerCase();
	private final AvisacitasSQLiteOpenHelper dbHelper;

	public AccountRepository(Context context) {
		dbHelper = AvisacitasSQLiteOpenHelper.getInstance(context);
	}

	public Account getActiveAccount() {

		AvisacitasSQLiteOpenHelper.DatabaseTask<Account> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Account execute(SQLiteDatabase db) {
				Account account = null;
				String query = "SELECT * FROM " + TABLE_ACCOUNT + " WHERE isActive = \"true\" LIMIT 1";
				try {
					account = DatabaseUtils.executeReadOneRow(
							db,
							query,
							new String[]{},
							Account.class
					);

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return account;
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);

	}

	public Account getAccountFromDBByMcid(String mcid) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Account> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Account execute(SQLiteDatabase db) {
				try {
					return DatabaseUtils.executeReadOneRow(
							db,
							"SELECT * FROM " + TABLE_ACCOUNT + " WHERE pk_mcid = ?",
							new String[]{mcid},
							Account.class);

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		DatabaseUtils.executeWithoutTransaction(task, dbHelper);

		return null;
	}



	public List<Account> getAllAccounts() {
		AvisacitasSQLiteOpenHelper.DatabaseTask<List<Account>> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public List<Account> execute(SQLiteDatabase db) {
				List<Account> accountList = new ArrayList<>();
				try (Cursor cursor = db.query(
						Account.class.getSimpleName().toLowerCase(),
						null, // Recuperar todas las columnas
						null, // No hay condición WHERE
						null, // Sin argumentos para WHERE
						null, null, null // Sin GROUP BY, HAVING, ORDER BY
				)) {
					while (cursor.moveToNext()) {
						Account account = new Account();

						account.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
						account.setPk_mcid(cursor.getString(cursor.getColumnIndexOrThrow("pk_mcid")));
						accountList.add(account);
					}
				} catch (Exception ex) {
					// Manejo de errores, registro, etc.
					LogHelper.addLogError(ex);
					return null;
				}
				return accountList;
			}
		};
		return DatabaseUtils.executeWithTransaction(task, dbHelper);
	}



	public void dbInsertAccountBlocking(JSONObject valuesFromPost) {
//		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
//			@Override
//			public Void execute(SQLiteDatabase db) {
				try {
					JSONObject responseAccountJson = valuesFromPost;
					// JSONObject responseAccountJson = valuesFromPost.getJSONArray("phone").getJSONObject(0);
					String mcid = responseAccountJson.getString("pk_mcid");

					// Obtener la cuenta desde la base de datos usando el método recién creado
					Account existingAccount = getAccountFromDBByMcid(mcid);

					// Si la cuenta no existe, crear una nueva
					if (existingAccount != null)
						return;
						//return null;

					existingAccount = new Account();
					existingAccount.setPk_mcid(mcid);
					existingAccount.setPhone(responseAccountJson.getString("phone"));
					existingAccount.setName(responseAccountJson.getString("name"));
					existingAccount.setEmail(responseAccountJson.getString("email"));
					existingAccount.setWaToken(responseAccountJson.getString("waToken"));
					existingAccount.setConnect("true");
					existingAccount.setActive("true");
					existingAccount.setEpochUTCAdded(System.currentTimeMillis() / 1000L);


					// Insertar la nueva cuenta en la base de datos
					DatabaseUtils.insertOrUpdateFromObjectList(List.of(existingAccount), dbHelper);

					LogHelper.addLogInfo("Account added");

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
//				return null;
//			}
//		};
//
//		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}

/*	public void dbInsertAccountBlocking(JSONArray accountsArray) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					// Iterar sobre el JSONArray y procesar cada JSONObject
					for (int i = 0; i < accountsArray.length(); i++) {
						JSONObject responseAccountJson = accountsArray.getJSONObject(i);
						String mcid = responseAccountJson.getString("pk_mcid");

						// Obtener la cuenta desde la base de datos usando el método recién creado
						Account existingAccount = getAccountFromDBByMcid(mcid);

						// Si la cuenta no existe, crear una nueva
						if (existingAccount != null) {
							continue; // Pasar al siguiente objeto en el array si la cuenta ya existe
						}

						// Crear una nueva cuenta
						Account newAccount = new Account();
						newAccount.setPk_mcid(mcid);
						newAccount.setPhone(responseAccountJson.getString("phone"));
						newAccount.setName(responseAccountJson.getString("name"));
						newAccount.setEmail(responseAccountJson.getString("email"));
						newAccount.setWaToken(responseAccountJson.getString("waToken"));
						newAccount.setConnect("false");
						newAccount.setEpochUTCAdded(System.currentTimeMillis() / 1000L);

						// Insertar la nueva cuenta en la base de datos
						DatabaseUtils.insertOrUpdateFromObjectList(List.of(newAccount), dbHelper);
					}

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}*/


	public void setActiveAccount(String mcid) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					// Crear la sentencia SQL para actualizar el estado activo
					String sql = "UPDATE accountwcb SET isActive = (CASE WHEN pk_mcid = ? THEN 1 ELSE 0 END)";
					SQLiteStatement statement = db.compileStatement(sql);

					// Enlazar el parámetro mcid a la sentencia
					statement.bindString(1, mcid);

					// Ejecutar la sentencia y obtener el número de filas afectadas
					//statement.execute(); //Ejecuta una declaración SQL que no devuelve ningún valor, como CREATE, INSERT, UPDATE, o DELETE.
					int affectedRows = statement.executeUpdateDelete();
					LogHelper.addLogInfo("Rows affected: " + affectedRows);
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
					return null;
				}
				return null;
			}
		};
		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}

	public List<String> getAllEmailsFromAccount() {
		AvisacitasSQLiteOpenHelper.DatabaseTask<List<String>> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public List<String> execute(SQLiteDatabase db) {


				List<String> emails = new ArrayList<>();
				String selectQuery = "SELECT email FROM accountwcb";

				try (Cursor cursor = db.rawQuery(selectQuery, null)) {
					while (cursor.moveToNext()) {
						emails.add(cursor.getString(cursor.getColumnIndexOrThrow("email")));
					}

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
					return null;
				}
				return emails;
			}
		};
		return DatabaseUtils.executeWithTransaction(task, dbHelper);
	}

	public String getPkMcidByEmail(String email) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<String> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public String execute(SQLiteDatabase db) {
				try {
					String query = "SELECT * FROM " + TABLE_ACCOUNT + " WHERE email = ?";
					Account result = DatabaseUtils.executeReadOneRow(db, query, new String[]{email}, Account.class);
					return result != null ? result.getPk_mcid() : null;  // Asume que Account tiene un método getPkMcid()
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};
		return DatabaseUtils.executeWithTransaction(task, dbHelper);
	}

	public List<String> getAllMcidFromAccounts() {
		AvisacitasSQLiteOpenHelper.DatabaseTask<List<String>> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public List<String> execute(SQLiteDatabase db) {
				List<String> mcidList = new ArrayList<>();
				// Realizar una consulta para obtener todos los `mcid`
				try (Cursor cursor = db.query(
						Account.class.getSimpleName().toLowerCase(),
						new String[]{"pk_mcid"}, // Solo recuperar la columna `mcid`
						null, // No hay condición WHERE
						null, // Sin argumentos para WHERE
						null, null, null
				)) {
					while (cursor.moveToNext()) {
						mcidList.add(cursor.getString(cursor.getColumnIndexOrThrow("pk_mcid")));
					}

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
					return null;
				}
				return mcidList;
			}
		};
		return DatabaseUtils.executeWithTransaction(task, dbHelper);
	}





	public void saveCompanyNameToDatabase(String companyName) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					// Crear los valores que se van a actualizar
					ContentValues values = new ContentValues();
					values.put("companyName", companyName);

					// Actualizar el registro donde isActive = 1 (es decir, la cuenta activa)
					db.update("accountwcb", values, "isActive = ?", new String[]{"true"});

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}

	public String getCompanyName() {
		AvisacitasSQLiteOpenHelper.DatabaseTask<String> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public String execute(SQLiteDatabase db) {
				String companyName = null;
				String query = "SELECT companyName FROM accountwcb WHERE isActive = 'true' LIMIT 1";

				try (Cursor cursor = db.rawQuery(query, null)) {
					if (cursor != null && cursor.moveToFirst()) {
						companyName = cursor.getString(cursor.getColumnIndexOrThrow("companyName"));
					}
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return companyName != null ? companyName : "No company name";
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);
	}



	public void saveMessageTextToDatabase(String messageText) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					// Crear los valores que se van a actualizar
					ContentValues values = new ContentValues();
					values.put("customOncreateMsg", messageText);

					// Actualizar el registro donde isActive = 1 (es decir, la cuenta activa)
					db.update("accountwcb", values, "isActive = ?", new String[]{"true"});

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}

				return null;
			}
		};

		// Ejecutar la transacción
		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}

	public String getCustomOncreateMsg() {
		AvisacitasSQLiteOpenHelper.DatabaseTask<String> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public String execute(SQLiteDatabase db) {
				String customOncreateMsg = null;
				String query = "SELECT customOncreateMsg FROM accountwcb WHERE isActive = 'true' LIMIT 1";

				try (Cursor cursor = db.rawQuery(query, null)) {
					if (cursor != null && cursor.moveToFirst()) {
						customOncreateMsg = cursor.getString(cursor.getColumnIndexOrThrow("customOncreateMsg"));
					}
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return customOncreateMsg != null ? customOncreateMsg : "No message text";
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);
	}

	public String getCustomOncreateMsg(String pkMcid) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<String> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public String execute(SQLiteDatabase db) {
				try {

					return DatabaseUtils.executeReadOneValue(db, "SELECT customOncreateMsg FROM accountswcb WHERE pk_mcid = ?", new String[]{pkMcid}, "pk_mcid").toString();

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return "";
			}
		};

		return DatabaseUtils.executeWithoutTransaction(task, dbHelper);
	}



	public void updateCustomMessageFormActiveAccount(String messageType, String message) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					ContentValues values = new ContentValues();
					switch (messageType) {
						case "oncreate":
							values.put("customOncreateMsg", message);
							break;
						case "two_days":
							values.put("customTwoDaysMsg", message);
							break;
						case "one_day":
							values.put("customOneDayMsg", message);
							break;
						case "five_min":
							values.put("customFiveMinMsg", message);
							break;
						case "post":
							values.put("customPostMsg", message);
							break;
						default:
							break;
					}
					db.update(TABLE_ACCOUNT, values, "isActive = ?", new String[]{"1"});
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}


	public void updateMessageServiceFromActiveAccount(boolean sendWhatsAndSms, boolean sendOnlySms) {

		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					ContentValues values = new ContentValues();
					values.put("sendWhatsAndSms", sendWhatsAndSms ? 1 : 0);
					values.put("sendOnlySms", sendOnlySms ? 1 : 0);
					db.update(TABLE_ACCOUNT, values, "isActive = 1 LIMIT 1", null);
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return  null;
			}
		};

		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}



	public void updateScheduleFromActiveAccount(
			int mondayStart, int mondayEnd,
			int tuesdayStart, int tuesdayEnd,
			int wednesdayStart, int wednesdayEnd,
			int thursdayStart, int thursdayEnd,
			int fridayStart, int fridayEnd,
			int saturdatStart, int saturdayEnd,
			int sundayStart, int sundayEnd) {

		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {

					ContentValues values = new ContentValues();

					values.put("mondayTimeStart", mondayStart);
					values.put("mondayTimeEnd", mondayEnd);
					values.put("tuesdayTimeStart", tuesdayStart);
					values.put("tuesdayTimeEnd", tuesdayEnd);
					values.put("wednesdayTimeStart", wednesdayStart);
					values.put("wednesdayTimeEnd", wednesdayEnd);
					values.put("thursdayTimeStart", thursdayStart);
					values.put("thursdayTimeEnd", thursdayEnd);
					values.put("fridayTimeStart", fridayStart);
					values.put("fridayTimeEnd", fridayEnd);
					values.put("saturdayTimeStart", saturdatStart);
					values.put("saturdayTimeEnd", saturdayEnd);
					values.put("sundayTimeStart", sundayStart);
					values.put("sundayTimeEnd", sundayEnd);

					db.update(TABLE_ACCOUNT, values, "isActive = ?", new String[]{"true"});

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};
		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}



	public void updateReminderFormActiveAccount(String typeReminder, boolean isChecked) {

		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					ContentValues values = new ContentValues();
					values.put(typeReminder, isChecked ? 1 : 0);
					db.update(TABLE_ACCOUNT, values, "isActive = ?", new String[]{"1"});
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}

	public void updateLastSync(String mcid, long lastSync) {
		AvisacitasSQLiteOpenHelper.DatabaseTask<Integer> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<>() {
			@Override
			public Integer execute(SQLiteDatabase db) {
				try {
					ContentValues values = new ContentValues();
					values.put("epochUTCLastSync", lastSync); // Asigna el valor de lastSyncTime
					values.put("lastSyncStatus", 2);

					// Realiza la actualización en la base de datos
					return db.update(TABLE_ACCOUNT, values, "pk_mcid = ?", new String[]{mcid});

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return -1;
			}
		};

		DatabaseUtils.executeWithTransaction(task, dbHelper);
	}


}
