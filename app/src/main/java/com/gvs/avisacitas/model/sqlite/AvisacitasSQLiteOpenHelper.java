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
	private final String accountTableName = Account.class.getSimpleName().toLowerCase();
	private static AvisacitasSQLiteOpenHelper instance;
	private static final Lock lock = new ReentrantLock();


	public AvisacitasSQLiteOpenHelper(@Nullable Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.context = context;
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
		// Aquí puedes realizar cualquier configuración adicional cada vez que se abre la base de datos
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

	private <T> T executeWithTransaction(DatabaseTask<T> task) {

		T result = null;
		SQLiteDatabase db = null;
		boolean lockAcquired = false;
		try {
			lockAcquired = lock.tryLock(15, TimeUnit.SECONDS);
			if (!lockAcquired)
				throw new RuntimeException("Could not acquire lock for database transaction");

			db = getWritableDatabase();
			db.beginTransaction();

			// Aquí se ejecuta la lógica específica de la tarea
			result = task.execute(db);

			// Marcar la transacción como exitosa si no se produjo ninguna excepción
			db.setTransactionSuccessful();
		} catch (Exception e) {
			LogHelper.addLogError(e);
		} finally {
			if (db != null) {
				db.endTransaction(); // Esto revertirá la transacción si no se marcó como exitosa
				db.close();
			}
			if (lockAcquired)
				lock.unlock();
		}
		return result;
	}


	private <T> T executeWithoutTransaction(DatabaseTask<T> task) {

		T result = null;
		boolean lockAcquired = false;

		try (SQLiteDatabase db = getInstance(context).getReadableDatabase()) {
			lockAcquired = lock.tryLock(15, TimeUnit.SECONDS);  // Espera hasta 15 segundos para adquirir el lock

			if (!lockAcquired)
				throw new RuntimeException("Could not acquire lock for database read operation");

			result = task.execute(db);

		} catch (Exception e) {
			LogHelper.addLogError(e);
		} finally {
			if (lockAcquired)
				lock.unlock();
		}
		return result;
	}

	private Object getFieldValueFromCursor(Cursor cursor, int columnIndex) {

		if (cursor == null || cursor.isClosed() || columnIndex < 0 || columnIndex >= cursor.getColumnCount()) {
			return null;
		}

		int type = cursor.getType(columnIndex);
		switch (type) {
			case Cursor.FIELD_TYPE_STRING:
				return cursor.getString(columnIndex);
			case Cursor.FIELD_TYPE_INTEGER:
				return cursor.getInt(columnIndex);
			case Cursor.FIELD_TYPE_FLOAT:
				return cursor.getFloat(columnIndex);
			case Cursor.FIELD_TYPE_BLOB:
				return cursor.getBlob(columnIndex);
			case Cursor.FIELD_TYPE_NULL:
				return null;
			default:
				throw new IllegalArgumentException("Unsupported data type");
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

	public void insertOrUpdateFromObjectList(List<?> objectList) {

		DatabaseTask<Void> task = new DatabaseTask<Void>() {
			@Override
			public Void execute(SQLiteDatabase db) {

				Object currentObjectValue,
						firstObject;
				Field[] classAttributes;
				List<String> columnsNamesList = new ArrayList<>(),
						valuesPlaceholdersList = new ArrayList<>(),
						primaryKeys = new ArrayList<>();
				StringBuilder updateStringBuilder = new StringBuilder(),
						conditionStringBuilder = new StringBuilder();
				String currentFieldName,
						sql,
						tableName,
						columnsString,
						valuesString,
						primaryKeyString,
						condition;
				int index;


				try {

					if (objectList == null || objectList.isEmpty())
						return null;

					firstObject = objectList.get(0);
					tableName = firstObject.getClass().getSimpleName().toLowerCase();
					classAttributes = firstObject.getClass().getDeclaredFields();

					for (Field currentAttribute : classAttributes) {

						currentAttribute.setAccessible(true);
						currentObjectValue = currentAttribute.get(firstObject);
						currentFieldName = currentAttribute.getName();

						columnsNamesList.add(currentFieldName);
						valuesPlaceholdersList.add("?");

						// Definir primaryKeys (suponiendo que los campos comienzan con "pk_")
						if (currentFieldName.startsWith("pk_"))
							primaryKeys.add(currentFieldName);

						// Generar la cláusula UPDATE SET
						if (!currentFieldName.startsWith("i_") && !currentFieldName.startsWith("pk_")) {
							if (updateStringBuilder.length() > 0) {
								updateStringBuilder.append(", ");
							}
							updateStringBuilder.append(currentFieldName).append(" = excluded.").append(currentFieldName);
						}
					}


					// Generar la condición WHERE para que la actualización solo ocurra si los valores son diferentes
					for (String columnName : columnsNamesList) {
						if (!columnName.startsWith("i_") && !columnName.startsWith("pk_")) {
							if (conditionStringBuilder.length() > 0) {
								conditionStringBuilder.append(" OR ");
							}
							conditionStringBuilder.append(tableName).append(".").append(columnName).append(" != ").append("excluded.").append(columnName);
						}
					}

					columnsString = String.join(", ", columnsNamesList);
					valuesString = String.join(", ", valuesPlaceholdersList);
					primaryKeyString = String.join(", ", primaryKeys);
					condition = conditionStringBuilder.toString();

					sql = String.format(
							"INSERT INTO %s (%s) VALUES (%s) ON CONFLICT(%s) DO UPDATE SET %s WHERE %s",
							tableName,
							columnsString,
							valuesString,
							primaryKeyString,
							updateStringBuilder,
							condition);

					LogHelper.addLogInfo(sql);

					try (SQLiteStatement statement = db.compileStatement(sql)) {

						for (Object currentObject : objectList) {

							index = 0;  // Los índices de SQLite comienzan en 1

							for (Field currentAttribute : classAttributes) {

								index++;
								currentAttribute.setAccessible(true);
								currentObjectValue = currentAttribute.get(currentObject);

								if (currentObjectValue == null) {
									statement.bindNull(index);

									continue;
								}

								String type = currentObjectValue.getClass().getSimpleName();


								if (currentObjectValue instanceof String)
									statement.bindString(index, (String) currentObjectValue);
								else if (currentObjectValue instanceof Integer)
									statement.bindLong(index, (Integer) currentObjectValue);
								else if (currentObjectValue instanceof Long)
									statement.bindLong(index, (Long) currentObjectValue);
								else if (currentObjectValue instanceof Double)
									statement.bindDouble(index, (Double) currentObjectValue);
								else if (currentObjectValue instanceof Float)
									statement.bindDouble(index, ((Float) currentObjectValue).doubleValue());
								else if (currentObjectValue instanceof byte[])
									statement.bindBlob(index, (byte[]) currentObjectValue);
								else
									throw new IllegalArgumentException("Unsupported data type for binding: " + currentObjectValue.getClass().getSimpleName());



//								switch (currentObjectValue.getClass().getSimpleName()) {
//									case "String":
//										statement.bindString(index, (String) currentObjectValue);
//										break;
//									case "Integer":
//										statement.bindLong(index, (Integer) currentObjectValue);
//										break;
//									case "Long":
//										statement.bindLong(index, (Long) currentObjectValue);
//										break;
//									case "Double":
//										statement.bindDouble(index, (Double) currentObjectValue);
//										break;
//									case "Float":
//										statement.bindDouble(index, ((Float) currentObjectValue).doubleValue());
//										break;
//									case "byte[]":
//										statement.bindBlob(index, (byte[]) currentObjectValue);
//									default:
//										throw new IllegalArgumentException("Unsupported data type for binding");
//								}
							}

							statement.executeInsert();

						}
					} catch (Exception ex) {
						LogHelper.addLogError(ex);
					}

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}

				return null;
			}
		};

		executeWithTransaction(task);
	}


//	private <T> T executeReadOneRow(SQLiteDatabase db, String query, String[] selectionArgs, Class<T> clazz) {
//
//		T returnObject = null;
//		Cursor cursor = db.rawQuery(query, selectionArgs);
//
//		if (cursor.moveToFirst()) {
//			try {
//				// Crea una instancia de la clase genérica
//				returnObject = clazz.newInstance();
//				Field[] fields = clazz.getDeclaredFields();
//				int columnIndex;
//
//				for (Field field : fields) {
//
//					field.setAccessible(true);
//					columnIndex = cursor.getColumnIndex(field.getName());
//
//					// Si el índice es válido (existe la columna en el cursor)
//					if (columnIndex == -1)
//						continue;
//
//					// Determinar el tipo del campo y establecer el valor correspondiente
//					field.set(returnObject, getFieldValueFromCursor(cursor, columnIndex));
//				}
//			} catch (Exception ex) {
//				LogHelper.addLogError(ex);
//			}
//		}
//		cursor.close();
//		return returnObject;
//	}

	private <T> T executeReadOneRow(SQLiteDatabase db, String query, String[] selectionArgs, Class<T> clazz) {
		T returnObject = null;
		try (Cursor cursor = db.rawQuery(query, selectionArgs)) {
			if (cursor.moveToFirst()) {
				returnObject = clazz.newInstance();
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					int columnIndex = cursor.getColumnIndex(field.getName());
					if (columnIndex == -1) continue; // Column not found in result set

					if (field.getType().equals(Long.TYPE) || field.getType().equals(Long.class))
						field.set(returnObject, cursor.getLong(columnIndex));
					if (field.getType().equals(String.class))
						field.set(returnObject, cursor.getString(columnIndex));
					if (field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class))
						field.set(returnObject, cursor.getInt(columnIndex));
					if (field.getType().equals(Double.TYPE) || field.getType().equals(Double.class))
						field.set(returnObject, cursor.getDouble(columnIndex));
					if (field.getType().equals(Float.TYPE) || field.getType().equals(Float.class))
						field.set(returnObject, cursor.getFloat(columnIndex));
					if (field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class)) {
						//boolean values are stored as "true" or "false"
						String booleanValue = cursor.getString(columnIndex);
						field.set(returnObject, "true".equalsIgnoreCase(booleanValue));
					}
					if (field.getType().equals(byte[].class))
						field.set(returnObject, cursor.getBlob(columnIndex));

				}
			}
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
		return returnObject;
	}

	private Object executeReadOneValue(SQLiteDatabase db, String query, String[] selectionArgs, String columnName) {

		try (Cursor cursor = db.rawQuery(query, selectionArgs)) {
			if (cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndex(columnName);
				// Si el índice es válido (existe la columna en el cursor)
				if (columnIndex != -1)
					return getFieldValueFromCursor(cursor, columnIndex);
			}
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
		return null;
	}

	private <T> List<T> executeReadMultipleRows(SQLiteDatabase db, String query, String[] selectionArgs, Class<T> clazz) {

		List<T> results = new ArrayList<>();

		try (Cursor cursor = db.rawQuery(query, selectionArgs)) {
			while (cursor.moveToNext()) {
				T returnObject = clazz.newInstance(); // Crea una nueva instancia de la clase para cada fila
				Field[] fields = clazz.getDeclaredFields(); // Obtiene los campos de la clase

				for (Field field : fields) {
					field.setAccessible(true);
					int columnIndex = cursor.getColumnIndex(field.getName());

					if (columnIndex == -1) continue; // Column not found in result set

					if (field.getType().equals(Long.TYPE) || field.getType().equals(Long.class))
						field.set(returnObject, cursor.getLong(columnIndex));
					if (field.getType().equals(String.class))
						field.set(returnObject, cursor.getString(columnIndex));
					if (field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class))
						field.set(returnObject, cursor.getInt(columnIndex));
					if (field.getType().equals(Double.TYPE) || field.getType().equals(Double.class))
						field.set(returnObject, cursor.getDouble(columnIndex));
					if (field.getType().equals(Float.TYPE) || field.getType().equals(Float.class))
						field.set(returnObject, cursor.getFloat(columnIndex));
					if (field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class)) {
						//boolean values are stored as "true" or "false"
						String booleanValue = cursor.getString(columnIndex);
						field.set(returnObject, "true".equalsIgnoreCase(booleanValue));
					}
					if (field.getType().equals(byte[].class))
						field.set(returnObject, cursor.getBlob(columnIndex));

				}
			}
		} catch (Exception ex) {
			LogHelper.addLogError(ex);
		}
		return results;
	}

	public void updateLastSync(String mcid, long lastSync) {
		DatabaseTask<Integer> task = new DatabaseTask<>() {
			@Override
			public Integer execute(SQLiteDatabase db) {
				try {
					ContentValues values = new ContentValues();
					values.put("epochUTCLastSync", lastSync); // Asigna el valor de lastSyncTime
					values.put("lastSyncStatus", 2);

					// Realiza la actualización en la base de datos
					return db.update(accountTableName, values, "pk_mcid = ?", new String[]{mcid});

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return -1;
			}
		};

		executeWithTransaction(task);
	}







	public void updateEvent(final long id, String columName, final long epochDateSend) {
		DatabaseTask<Integer> task = new DatabaseTask<>() {
			@Override
			public Integer execute(SQLiteDatabase db) {
				try {

					ContentValues values = new ContentValues();
					values.put(columName, epochDateSend);
					return db.update(TABLE_CALENDAR_EVENT, values, "pk_id = ?", new String[]{String.valueOf(id)});
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return -1;
			}
		};
		executeWithTransaction(task);
	}

	public void deleteEntryIfExists(String tableName, String whereClase, String[] whereArgs) { //primaryKeyValue suele ser mcId

		getInstance(context).getWritableDatabase().delete(tableName, whereClase, whereArgs);
	}




	@SuppressLint("Range")
	public CalendarEvent getNextEventData(String tableName) {


		/**
		 * Actualizar la fecha en la que se recupera para enviar el evento actual de base de datos
		 * Y poner el sistema por el que se envía
		 */
		DatabaseTask<CalendarEvent> task = new DatabaseTask<>() {
			@Override
			public CalendarEvent execute(SQLiteDatabase db) {

				CalendarEvent eventWCB = null;
				String query =
						"SELECT * " +
								"FROM eventwcb " +
								"WHERE eventStartEpoch > strftime('%s', 'now') " +
								"ORDER BY eventStartEpoch ASC " +
								"LIMIT 1";

				try {

					eventWCB = executeReadOneRow(
							db,
							query,
							new String[]{},
							CalendarEvent.class
					);

				} catch (Exception ex) {
					LogHelper.addLogError("[AvisacitasSQLiteOpenHelper] getNextEventData " + ex);

				}
				return eventWCB;
			}
		};

		return executeWithoutTransaction(task);

	}









//	public Event getNextEventToSendAndUpdateTransaction(SQLiteDatabase db) {
//		// Obtener la fecha actual en formato epoch
//		long currentEpoch = System.currentTimeMillis() / 1000L;
//
//		// Lista de campos que queremos consultar en orden
//		String[] fieldsToCheck = {
//				"i_epoch5SentDate",
//				"i_epoch10SentDate",
//				"i_epoch30SentDate",
//				"i_60SentDateEpoch",
//				"i_1440SentDateEpoch",
//				"i_2880SentDateEpoch"
//		};
//
//		Event event = null;
//
//		// Iniciar la transacción
//		db.beginTransaction();
//		try {
//			// Iterar sobre cada campo hasta encontrar uno donde el valor sea NULL
//			for (String field : fieldsToCheck) {
//				// Consulta para encontrar el evento cuyo campo específico es NULL
//				String query = "SELECT * FROM events WHERE " + field + " IS NULL LIMIT 1";
//				Cursor cursor = db.rawQuery(query, null);
//
//				if (cursor != null && cursor.moveToFirst()) {
//					// Obtenemos los datos del evento
//					event = getEventFromCursor(cursor);
//
//					// Actualizamos el campo correspondiente con la fecha actual en formato epoch
//					ContentValues values = new ContentValues();
//					values.put(field, currentEpoch); // Actualiza el campo con la fecha actual
//
//					// Ejecuta la actualización en la base de datos
//					db.update("events", values, "pk_id = ?", new String[]{String.valueOf(event.getPkId())});
//
//					// Cerrar cursor y salir del ciclo
//					cursor.close();
//					break;
//				}
//
//				// Cerrar el cursor si no hay resultados
//				if (cursor != null) {
//					cursor.close();
//				}
//			}
//
//			// Si llegamos aquí, la transacción ha sido exitosa
//			db.setTransactionSuccessful();
//		} catch (Exception e) {
//			e.printStackTrace(); // Manejo de errores
//		} finally {
//			// Termina la transacción, ya sea con éxito o fallo
//			db.endTransaction();
//		}
//
//		// Retornar el evento encontrado, o null si no hay ninguno
//		return event;
//	}

	// Método para obtener el objeto Event desde el cursor
//	private Event getEventFromCursor(Cursor cursor) {
//		Event event = new Event();
//
//		event.setPkId(cursor.getInt(cursor.getColumnIndex("pk_id")));
//		event.setPkCalendarId(cursor.getInt(cursor.getColumnIndex("pk_calendarId")));
//		event.setPkMcid(cursor.getString(cursor.getColumnIndex("pk_mcid")));
//		event.setEventTitle(cursor.getString(cursor.getColumnIndex("eventTitle")));
//		event.setEventDescription(cursor.getString(cursor.getColumnIndex("eventDescription")));
//		// Obtener el resto de campos del evento...
//
//		return event;
//	}































	public CalendarEvent getNextEventData() {
		DatabaseTask<CalendarEvent> task = new DatabaseTask<>() {
			@Override
			public CalendarEvent execute(SQLiteDatabase db) {
				CalendarEvent eventWCB = null;
				long currentTime = System.currentTimeMillis();
				String query =

						"SELECT * " +
								"FROM eventwcb " +
								"WHERE i_createdSentDateEpoch = 0 " +
								"AND eventStartEpoch > ? " +
								"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
								"ORDER BY eventStartEpoch ASC " +
								"LIMIT 1";

//					"SELECT * " +
//					"FROM eventwcb " +
//					"WHERE " +
//					"(i_createdSentDateEpoch = 0 OR i_60SentDateEpoch = 0 OR i_1440SentDateEpoch = 0 OR i_2880SentDateEpoch = 0) " +
//					"AND eventStartEpoch > ? " +
//					"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
//					"ORDER BY " +
//					"CASE " +
//					"WHEN i_createdSentDateEpoch = 0 THEN 1 " +
//					"WHEN i_60SentDateEpoch = 0 THEN 2 " +
//					"WHEN i_1440SentDateEpoch = 0 THEN 3 " +
//					"WHEN i_2880SentDateEpoch = 0 THEN 4 " +
//					"END, " +
//					"eventStartEpoch ASC " +
//					"LIMIT 1";

				try {
					eventWCB = executeReadOneRow(
							db,
							query,
							new String[]{String.valueOf(currentTime)}, // Pass the current time as a parameter
							CalendarEvent.class
					);
				} catch (Exception ex) {
					LogHelper.addLogError("[AvisacitasSQLiteOpenHelper] getNextEventData " + ex);
				}
				//LogHelper.addLogInfo("epoch: " + eventWCB.getEventStartEpoch());
				return eventWCB;
			}
		};

		return executeWithoutTransaction(task);
	}


//	public List<CalendarEvent> getQueueEvents() {
//
//		DatabaseTask<List<CalendarEvent>> task = new DatabaseTask<>() {
//			@Override
//			public List<CalendarEvent> execute(SQLiteDatabase db) {
//				List<CalendarEvent> eventList = new ArrayList<>();
//				long currentTime = System.currentTimeMillis();
//
//				try {
//					// Obtener el próximo evento
//					String nextEventQuery =
//							"SELECT * FROM eventwcb " +
//									"WHERE i_createdSentDateEpoch = 0 " +
//									"AND eventStartEpoch > ? " +
//									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
//									"ORDER BY eventStartEpoch ASC " +
//									"LIMIT 1";
//					CalendarEvent nextEvent = executeReadOneRow(db, nextEventQuery, new String[]{String.valueOf(currentTime)}, CalendarEvent.class);
//
//					if (nextEvent != null) {
//						long nextEventStartEpoch = nextEvent.getEventStartEpoch();
//
//						// Obtener los dos eventos anteriores al próximo evento
//						String previousEventsQuery =
//								"SELECT * FROM eventwcb " +
//										"WHERE eventStartEpoch < ? " +
//										"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
//										"ORDER BY eventStartEpoch DESC " +
//										"LIMIT 2";
//						CalendarEvent firstPreviousEvent = executeReadOneRow(db, previousEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);
//						CalendarEvent secondPreviousEvent = executeReadOneRow(db, previousEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);
//
//						// Obtener los dos eventos siguientes al próximo evento
//						String nextEventsQuery =
//								"SELECT * FROM eventwcb " +
//										"WHERE eventStartEpoch > ? " +
//										"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
//										"ORDER BY eventStartEpoch ASC " +
//										"LIMIT 2";
//						CalendarEvent firstNextEvent = executeReadOneRow(db, nextEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);
//						CalendarEvent secondNextEvent = executeReadOneRow(db, nextEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);
//
//						// Añadir los eventos anteriores si existen
//						if (firstPreviousEvent == null)
//							firstPreviousEvent = createEmptyEvent();
//						eventList.add(firstPreviousEvent);
//						if (secondPreviousEvent == null)
//							secondPreviousEvent = createEmptyEvent();
//						eventList.add(secondPreviousEvent);
//
//						// Añadir el próximo evento
//						eventList.add(nextEvent);
//
//						// Añadir los eventos siguientes si existen
//						if (firstNextEvent == null)
//							firstNextEvent = createEmptyEvent();
//						eventList.add(firstNextEvent);
//						if (secondNextEvent == null)
//							secondNextEvent = createEmptyEvent();
//						eventList.add(secondNextEvent);
//					}
//
//				} catch (Exception ex) {
//					LogHelper.addLogError("[AvisacitasSQLiteOpenHelper] getSurroundingEvents " + ex);
//				}
//
//				return eventList;
//			}
//		};
//
//		return executeWithoutTransaction(task);
//	}


	public List<CalendarEvent> getQueueEvents() {

		DatabaseTask<List<CalendarEvent>> task = new DatabaseTask<>() {
			@Override
			public List<CalendarEvent> execute(SQLiteDatabase db) {
				List<CalendarEvent> eventList = new ArrayList<>();
				long currentTime = System.currentTimeMillis();

				try {
					// Obtener el próximo evento
					String nextEventQuery =
//							"SELECT * FROM eventwcb " +
//									"WHERE i_createdSentDateEpoch = 0 " +
//									"AND eventStartEpoch > ? " +
//									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
//									"ORDER BY eventStartEpoch ASC " +
//									"LIMIT 1";
							"SELECT * " +
					"FROM eventwcb " +
					"WHERE " +
					"(i_createdSentDateEpoch = 0 OR i_60SentDateEpoch = 0 OR i_1440SentDateEpoch = 0 OR i_2880SentDateEpoch = 0) " +
					"AND eventStartEpoch > ? " +
					"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
					"ORDER BY " +
					"CASE " +
					"WHEN i_createdSentDateEpoch = 0 THEN 1 " +
					"WHEN i_60SentDateEpoch = 0 THEN 2 " +
					"WHEN i_1440SentDateEpoch = 0 THEN 3 " +
					"WHEN i_2880SentDateEpoch = 0 THEN 4 " +
					"END, " +
					"eventStartEpoch ASC " +
					"LIMIT 1";
					CalendarEvent nextEvent = executeReadOneRow(db, nextEventQuery, new String[]{String.valueOf(currentTime)}, CalendarEvent.class);

					if (nextEvent == null) {
						for (int i = 0; i < 5; i++) {
							eventList.add(createEmptyEvent());
						}
						return eventList;
					}

					long nextEventStartEpoch = nextEvent.getEventStartEpoch();

					// Obtener los dos eventos anteriores al próximo evento (orden descendente)
					String previousEventsQuery =
							"SELECT * FROM eventwcb " +
									"WHERE eventStartEpoch < ? " +
									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
									"ORDER BY eventStartEpoch DESC " +
									"LIMIT 2";  // Limitar a 2 eventos anteriores

					List<CalendarEvent> previousEvents = executeReadMultipleRows(db, previousEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);

					// Obtener los dos eventos siguientes al próximo evento (orden ascendente)
					String nextEventsQuery =
							"SELECT * FROM eventwcb " +
									"WHERE eventStartEpoch > ? " +
									"AND (targetPhone IS NOT NULL AND targetPhone <> '' AND targetPhone <> '0') " +
									"ORDER BY eventStartEpoch ASC " +
									"LIMIT 2";  // Limitar a 2 eventos siguientes

					List<CalendarEvent> nextEvents = executeReadMultipleRows(db, nextEventsQuery, new String[]{String.valueOf(nextEventStartEpoch)}, CalendarEvent.class);


					// Añadir eventos anteriores o vacíos si no hay suficientes
					if (previousEvents.size() < 2)
						previousEvents.add(createEmptyEvent());
					if (previousEvents.size() < 2)
						previousEvents.add(createEmptyEvent());
					eventList.addAll(previousEvents);

					// Añadir el próximo evento
					eventList.add(nextEvent);

					// Añadir eventos siguientes o vacíos si no hay suficientes
					if (nextEvents.size() < 2)
						nextEvents.add(createEmptyEvent());
					if (nextEvents.size() < 2)
						nextEvents.add(createEmptyEvent());
					eventList.addAll(nextEvents);

				} catch (Exception ex) {
					LogHelper.addLogError("[AvisacitasSQLiteOpenHelper] getSurroundingEvents " + ex);
				}

				return eventList;
			}
		};

		return executeWithoutTransaction(task);
	}

	private CalendarEvent createEmptyEvent() {
		CalendarEvent emptyEvent = new CalendarEvent();
		emptyEvent.setEventTitle("-");
		emptyEvent.setEventDescription("-");
		emptyEvent.setTargetPhone("000000000");
		emptyEvent.setEventStartEpoch(0);  // Puedes elegir otro valor por defecto si lo necesitas
		return emptyEvent;
	}




/*	public void updatePanicBtnFromActiveAccount(boolean isChecked) {

		// Crear una lista con un solo objeto Account para actualizar
		List<Account> accountList = new ArrayList<>();
		// Aquí debes obtener la cuenta activa y actualizar su campo panicBtn
		Account activeAccount = getActiveAccountWCB();
		DatabaseTask<Void> task = new DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {

					if (activeAccount != null) {
						activeAccount.setPanicBtn(String.valueOf(isChecked));
						accountList.add(activeAccount);
						// Llamar a insertOrUpdateFromObjectList con la lista de cuentas
						insertOrUpdateFromObjectList(accountList);
					} else {
						LogHelper.addLogError(new Exception("No active account found"));
					}
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		executeWithTransaction(task);
	}*/


	public void updateReminderFormActiveAccount(String typeReminder, boolean isChecked) {

		DatabaseTask<Void> task = new DatabaseTask<>() {
			@Override
			public Void execute(SQLiteDatabase db) {
				try {
					ContentValues values = new ContentValues();
					values.put(typeReminder, isChecked ? 1 : 0);
					db.update(accountTableName, values, "isActive = ?", new String[]{"1"});
				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
				return null;
			}
		};

		executeWithTransaction(task);
	}



	public boolean checkIfExistTable(String tableName) {
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
		return executeWithoutTransaction(task);
	}




	public String getPanicBtn() {

		DatabaseTask<String> task = new DatabaseTask<>() {
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

		return executeWithoutTransaction(task);

	}

	public void updatePanicBtn(boolean isChecked) {

		LogHelper.addLogInfo("updatePanicBtn(boolean isChecked) updated to = " + isChecked);

		DatabaseTask<Void> task = new DatabaseTask<>() {
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

		executeWithTransaction(task);
	}



}
