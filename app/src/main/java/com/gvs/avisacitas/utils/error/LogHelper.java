package com.gvs.avisacitas.utils.error;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class LogHelper {

    private static final Integer MAX_LOGS = 30;
    private static final List<String> logs = new ArrayList<>();
    private static Integer index = 0;

    public static void addLogError(Exception ex){

        addLogError(getExceptionInfo(ex));

    }

    public static void addLogError(String exceptionInfo){

        Log.e("ExceptionInfo", exceptionInfo);

        //addLogHistory(exceptionInfo);

    }

    public static void addLogInfo(Exception ex){

        addLogInfo(getExceptionInfo(ex));

    }

    public static void addLogInfo(String exceptionInfo){

        Log.i("ExceptionInfo", exceptionInfo);

        //addLogHistory(exceptionInfo);

    }

    private static String getExceptionInfo(Exception ex) {
        StringBuilder info = new StringBuilder();
        info.append("Error en función: ").append(ex.getMessage()).append("\n\n");

        // Obtener la pila de llamadas (stack trace)
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            // Agregar información sobre la llamada donde se produjo la excepción
            info.append("Clase: ").append(stackTrace[0].getClassName()).append("\n\n");
            info.append("Método: ").append(stackTrace[0].getMethodName()).append("\n\n");
            info.append("Línea: ").append(stackTrace[0].getLineNumber()).append("\n\n");
            info.append("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-").append("\n");
        }
        return info.toString();
    }

/*    public static void addLogHistory(String log) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateString = formatter.format(date);

        logs.add(dateString + "\n" + log);
        if (logs.size() > MAX_LOGS) {
            logs.remove(0);
        }
        index = logs.size();

        String ListToString = String.join(",", logs); // Usa coma como separador

        EncryptedSharedPreferencesClientHelper.saveLogOnDisk(this, ListToString);

    }*/

    public static List<String> getLogsList() {
            return logs;

    }

}
