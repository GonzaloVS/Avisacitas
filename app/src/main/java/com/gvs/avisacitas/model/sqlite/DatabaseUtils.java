package com.gvs.avisacitas.model.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.gvs.avisacitas.utils.error.LogHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DatabaseUtils {
	public static <T> T executeWithTransaction(AvisacitasSQLiteOpenHelper.DatabaseTask<T> task, AvisacitasSQLiteOpenHelper dbHelper) {

		T result = null;
		SQLiteDatabase db = null;
		boolean lockAcquired = false;
		try {
			lockAcquired = dbHelper.getLock().tryLock(15, TimeUnit.SECONDS);
			if (!lockAcquired)
				throw new RuntimeException("Could not acquire lock for database transaction");

			db = dbHelper.getWritableDatabase();
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
				dbHelper.unlockLock();
		}
		return result;

	}


	public static <T> T executeWithoutTransaction(AvisacitasSQLiteOpenHelper.DatabaseTask<T> task, AvisacitasSQLiteOpenHelper dbHelper) {

		T result = null;
		boolean lockAcquired = false;

		try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
			lockAcquired = dbHelper.getLock().tryLock(15, TimeUnit.SECONDS);  // Espera hasta 15 segundos para adquirir el lock

			if (!lockAcquired)
				throw new RuntimeException("Could not acquire lock for database read operation");

			result = task.execute(db);

		} catch (Exception e) {
			LogHelper.addLogError(e);
		} finally {
			if (lockAcquired)
				dbHelper.unlockLock();
		}
		return result;
	}


	public static <T> T executeReadOneRow(SQLiteDatabase db, String query, String[] selectionArgs, Class<T> clazz) {

			T returnObject = null;
			try (Cursor cursor = db.rawQuery(query, selectionArgs)) {
				if (cursor.moveToFirst()) {
					returnObject = clazz.newInstance();
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						field.setAccessible(true);
						int columnIndex = cursor.getColumnIndex(field.getName());
						if (columnIndex == -1) continue; // Column not found in result set

						/**
						 * Mirar si se puede cambiar por:
						 * field.set(returnObject, getFieldValueFromCursor(cursor, columnIndex));
						 */

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


	public static <T> List<T> executeReadMultipleRows(SQLiteDatabase db, String query, String[] selectionArgs, Class<T> clazz) {

		List<T> results = new ArrayList<>();

		try (Cursor cursor = db.rawQuery(query, selectionArgs)) {
			while (cursor.moveToNext()) {
				T returnObject = clazz.newInstance(); // Crea una nueva instancia de la clase para cada fila
				Field[] fields = clazz.getDeclaredFields(); // Obtiene los campos de la clase

				for (Field field : fields) {
					field.setAccessible(true);
					int columnIndex = cursor.getColumnIndex(field.getName());

					if (columnIndex == -1) continue; // Column not found in result set

					/**
					 * Mirar si se puede cambiar por:
					 * field.set(returnObject, getFieldValueFromCursor(cursor, columnIndex));
					 */

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

	public static Object executeReadOneValue(SQLiteDatabase db, String query, String[] selectionArgs, String columnName) {

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

	private static Object getFieldValueFromCursor(Cursor cursor, int columnIndex) {

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


	public static void insertOrUpdateFromObjectList(List<?> objectList, AvisacitasSQLiteOpenHelper dbHelper) {

		AvisacitasSQLiteOpenHelper.DatabaseTask<Void> task = new AvisacitasSQLiteOpenHelper.DatabaseTask<Void>() {
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

		executeWithTransaction(task, dbHelper);
	}
}

