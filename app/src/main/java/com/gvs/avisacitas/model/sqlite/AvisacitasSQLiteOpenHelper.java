package com.gvs.avisacitas.model.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.gvs.avisacitas.GeneralData;
import com.gvs.avisacitas.model.accounts.Account;
import com.gvs.avisacitas.model.calendar.CalendarEvent;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//NO CONFUNDIR CON SQLiteOpenHelper
public class AvisacitasSQLiteOpenHelper extends SQLiteOpenHelper {

	/*
	(OPERACIONES ESTRUCTURA DE LAS TABLAS O INSERT OR IGNORE 1 row)
	db.execsql  > Solo usar en operaciones sobre la estructura de la tabla (borrar, crear, etc). Vulnerable a SQLi

	(SELECT > devuelve un cursor para poder leer los datos)
	db.query    > Más seguro ante SQLi, intentar usar siempre
	db.rawQuery > Para consultas complejas. Usar solo si no se puede usar db.query

	(DELETE 1 or multiple rows)
	db.delete   > Usar delete, excepto para operaciones complejas (execsql)

	(INSERT 1 row en casos excepcionales)(no recomendado, usar statement)
	1. Fallo si ya existe (falla: -1):
		db.insert (no recomendado)
	2. Sobreescribir si ya existe (no falla):
		db.replace (no recomendado)
	3. Ignorar si ya existe (no falla):
		db.execsql (no recomendado)

	(INSERT multiple rows)
	statement   > Se puede usar los puntos 1,2 y 3 anteriores
		Usar siempre >>> INSERT OR ROLLBACK: Similar a ABORT, pero deshace la transacción.

		INSERT OR IGNORE: Inserta la fila, pero si ya existe una fila con la misma clave, la inserción se ignora.
		INSERT OR REPLACE: Inserta la fila, y si ya existe una fila con la misma clave, la fila existente se reemplaza con la nueva fila.
		INSERT OR ABORT: Inserta la fila, pero si ya existe una fila con la misma clave, se cancela la operación de inserción y se lanza un error.
		INSERT OR FAIL: Similar a ABORT, pero no deshace la transacción.
		INSERT OR RAISE: Lanza una excepción si hay un conflicto

	(UPDATE 1 row)
	db.update + db.query > No permite limitar el número de filas a actualizar, hay que recuperar el primary key de la fila a actualizar con db.query

	(UPDATE multiple rows)
	1. db.update   > Solo si se le pone el mismo valor a todas las filas en ese campo (no recomendado)
	2. statement   > Si cambian los valores en cada fila
		2.1 UPDATE OR IGNORE: Ignora la actualización si ocurre un conflicto.
		2.2 UPDATE OR REPLACE: Reemplaza la fila existente si ocurre un conflicto.
		2.3 UPDATE OR ABORT: Aborta la operación y lanza un error si ocurre un conflicto.
		2.4 UPDATE OR FAIL: Similar a ABORT, pero no deshace la transacción.
		2.5 UPDATE OR ROLLBACK: Similar a ABORT, pero deshace la transacción.

	 */

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "avisacitasDB.db";
	public static final String TABLE_DATA = GeneralData.class.getSimpleName().toLowerCase();
	public static final String TABLE_ACCOUNT = Account.class.getSimpleName().toLowerCase();
	public static final String TABLE_CALENDAR_EVENT = CalendarEvent.class.getSimpleName().toLowerCase();
	private Context context;

	static String[] expectedTables;
	private static AvisacitasSQLiteOpenHelper instance;
	private static final Lock lock = new ReentrantLock();

	public AvisacitasSQLiteOpenHelper(@Nullable Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.context = context;
	}

	// Métodos públicos para acceder al lock
	public Lock getLock() {
		return lock;
	}

	public void unlockLock() {
		lock.unlock();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		createTableIfNotExist(db, TABLE_DATA, GeneralData.class);
		createTableIfNotExist(db, TABLE_ACCOUNT, Account.class);
		createTableIfNotExist(db, TABLE_CALENDAR_EVENT, CalendarEvent.class);

		ContentValues defaultValues = new ContentValues();
		defaultValues.put("fcmToken", "");  // Asignar valores predeterminados
		defaultValues.put("log", "");
		defaultValues.put("panicBtn", "false");
		defaultValues.put("urlLogin", "https://app.wachatbot.com/club/code/login");
		defaultValues.put("urlDownload", "https://app.wachatbot.com/android/contacts/get");
		defaultValues.put("urlSend", "https://api.wachatbot.com/send");
		defaultValues.put("urlSendToken", "https://app.wachatbot.com/android/fcmtoken/update");

		db.insert("generaldata", null, defaultValues);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		//Solo salta si se cambia DATABASE_VERSION al llamar por primera vez a getWritableDatabase y getReadableDatabase
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		//Solo salta si se cambia DATABASE_VERSION al llamar por primera vez a getWritableDatabase y getReadableDatabase
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
//
//		// Habilitar el modo WAL usando rawQuery
//		try (Cursor cursor = db.rawQuery("PRAGMA journal_mode=WAL", null)) {
//			if (cursor.moveToFirst()) {
//				String journalMode = cursor.getString(0);
//				LogHelper.addLogInfo("Journal mode: " + journalMode); // Esto debería mostrar "wal"
//			}
//		} catch (Exception e) {
//			LogHelper.addLogError(e);
//		}
	}




	@FunctionalInterface
	public interface DatabaseTask<T> {
		T execute(SQLiteDatabase db);
	}

	public static synchronized AvisacitasSQLiteOpenHelper getInstance(Context context) {

		if (instance == null) {
			instance = new AvisacitasSQLiteOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	public void createTableIfNotExist(String tableName, Class<?> cls) {

		try (SQLiteDatabase db = getInstance(context).getWritableDatabase()) {

			if (isTableExists(db, tableName))
				return;

			createTableIfNotExist(db, tableName, cls);
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}

	public void createTableIfNotExist(SQLiteDatabase db, String tableName, Class<?> cls) {

		if (isTableExists(db, tableName))
			return;

		Class<?> fieldType;
		String fieldName,
				sqlType,
				newColumnSQL,
				pkStr;
		StringBuilder queryBuilder,
				pkColumns,
				otherColumns;
		Field[] fields;

		try {
			// Crear la sentencia SQL para crear la tabla
			queryBuilder = new StringBuilder();
			queryBuilder.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

			// Obtener los atributos de la clase usando reflexión
			fields = cls.getDeclaredFields();

			pkStr = "";
			pkColumns = new StringBuilder();
			otherColumns = new StringBuilder();

			// Recorrer cada campo para construir parte de la sentencia de creación de la tabla
			for (Field field : fields) {
				field.setAccessible(true);  // Hacer accesible el campo en caso de ser privado

				// Obtener el nombre y tipo del campo
				fieldName = field.getName();
				fieldType = field.getType();
				sqlType = getColumnType(fieldType);  // Convertir tipo Java a tipo SQL

				newColumnSQL = fieldName + " " + sqlType;

				// Verificar si es clave primaria
				if (fieldName.startsWith("pk_")) {

					if (pkColumns.length() > 0)
						pkColumns.append(", ");

					pkColumns.append(newColumnSQL);

					// Agregar a la lista de claves primarias
					pkStr += (pkStr.length() == 0 ? ", PRIMARY KEY(" : ", ") + fieldName;
					continue;
				}

				if (otherColumns.length() > 0)
					otherColumns.append(", ");

				otherColumns.append(newColumnSQL);
			}

			// Cerrar la definición de PRIMARY KEY
			if (!pkStr.isEmpty())
				pkStr += ")";

			// Construir la sentencia final
			queryBuilder.append(pkColumns);

			if (otherColumns.length() > 0) {

				if (pkColumns.length() > 0)
					queryBuilder.append(", ");

				queryBuilder.append(otherColumns);
			}

			queryBuilder.append(pkStr).append(");");

			String createTableSQL = queryBuilder.toString();

			// Ejecutar la sentencia SQL para crear la tabla
			db.execSQL(createTableSQL);

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}

	public boolean isTableExists(SQLiteDatabase db, String tableName) {

		try (Cursor cursor = db.query(
				"sqlite_master", // Tabla
				new String[]{"name"}, // Columnas a devolver
				"type = ? AND name = ?", // Clausula WHERE
				new String[]{"table", tableName}, // Valores para la clausula WHERE
				null, // Group By
				null, // Having
				null // Order By
		)) {

			if (cursor != null && cursor.getCount() > 0)
				return true;

			return false;
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
			return false;
		}
	}

	private String getColumnType(Class<?> type) {

		try {
			if (Integer.TYPE.equals(type) || Integer.class.equals(type))
				return "INTEGER";
			if (String.class.equals(type))
				return "TEXT";
			if (Double.TYPE.equals(type) || Double.class.equals(type))
				return "REAL";
			if (Long.TYPE.equals(type) || Long.class.equals(type))
				return "INTEGER";
			if (Float.TYPE.equals(type) || Float.class.equals(type))
				return "REAL";
			if (Boolean.TYPE.equals(type) || Boolean.class.equals(type))
				return "INTEGER"; // SQLite usa INTEGER para booleanos (0 = false, 1 = true)
			if (byte[].class.equals(type))
				return "BLOB";
		} catch (Exception ex) {
			LogHelper.addLogError("CheckColumnType [DBHelper]" + ex);
		}

		return null;
	}

	private void recreateDB() {

		try (SQLiteDatabase db = getInstance(context).getWritableDatabase()) {

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
			//db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTWCB);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR_EVENT);
			onCreate(db);

		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
	}



	public static boolean isDatabaseStructureValid(SQLiteDatabase db) {

		int columnCount;

		try {
			for (String table : expectedTables) {
				// Obtener el número real de columnas en la tabla
				columnCount = db.rawQuery("PRAGMA table_info(" + table + ")", null).getCount();

				/**
				 * Hay que añadir el numero de columnas del objeto para comparar la tabla que entra con la que tenemos
				 */

				/*try(Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null)) {
					columnCount = cursor.getCount(); // El número de filas devueltas corresponde al número de columnas en la tabla
				}

				// Comparar el número de columnas con el número esperado
				if (columnCount != expectedColumnCountForTable(db, table))
					return false;*/
			}
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
			return false;
		}
		return true;
	}


	public void deleteEntryIfExists(String tableName, String whereClase, String[] whereArgs) { //primaryKeyValue suele ser mcId

		getInstance(context).getWritableDatabase().delete(tableName, whereClase, whereArgs);
	}


/**	public boolean checkIfExistTable(String tableName) {
		DatabaseTask<Boolean> task = new DatabaseTask<>() {
			@Override
			public Boolean execute(SQLiteDatabase db) {
				String checkTableExist = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
				try (Cursor cursor = db.rawQuery(checkTableExist, new String[]{tableName})) {
					return cursor.moveToFirst();
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
					return false;
				}
			}
		};
		return DatabaseUtils.executeWithoutTransaction(task);
	}*/

}
