package com.gvs.avisacitas.model.calendar;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;
import com.gvs.avisacitas.utils.error.LogHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalendarHelper {

    public static final String startMark = "\uD834\uDD79";
    public static final String endMark = "\uD834\uDD7A";
    public static final int endMarkLength = endMark.length();
    private static final String[] EVENT_PROJECTION = {
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.STATUS,
            CalendarContract.Events.DELETED,
            CalendarContract.Events.ORGANIZER,
            CalendarContract.Events.ACCOUNT_NAME,
            CalendarContract.Events.ACCOUNT_TYPE,
            CalendarContract.Events.OWNER_ACCOUNT
    };

    private static final String EVENT_SELECTION = "(" + CalendarContract.Events.DTSTART + " > ?)" +
            " AND (" + CalendarContract.Events.ORGANIZER + " = ?)";

    public static void insertOrUpdateCalendarEventsByEmail(Context context, String organizerEmail) {

        List<CalendarEvent> calendarEventsList = new ArrayList<>();
        long nowInMillis = Calendar.getInstance().getTimeInMillis();

        CalendarEvent currentEvent;
        List<String> phoneNumbers;

        String currentTitle, currentDesc, currentPhone, currentTitleAndDesc;
        String newTitlePrefix = startMark+"⚠\uFE0F"+"[FORMATO NO VÁLIDO]"+endMark+" ";
        String newDescriptionSuffix =  startMark+"<br><br><b>Formato no válido:</b><br>" +
                "Consulta:<br>" +
                "https://wachatbot.com/phone" +
                endMark;

        String[] eventSelectionArgs = new String[]{
                String.valueOf(nowInMillis),
                organizerEmail
        };

        AvisacitasSQLiteOpenHelper avisacitasSQLiteOpenHelper = new AvisacitasSQLiteOpenHelper(context);

        String pkMcid = avisacitasSQLiteOpenHelper.getPkMcidByEmail(organizerEmail);

        Cursor eventCursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                EVENT_PROJECTION, EVENT_SELECTION, eventSelectionArgs, null);

        try (eventCursor) {

            if (eventCursor == null || !eventCursor.moveToFirst())
                return;

            do {
                currentEvent = extractEventFromCursor(eventCursor);
                currentTitle = currentEvent.getEventTitle();
                currentDesc = currentEvent.getEventDescription();
                currentPhone = "";
                currentTitleAndDesc = currentTitle + " " + currentDesc;

                LogHelper.addLogInfo("Processing Event:\n" + currentTitleAndDesc);

                LogHelper.addLogInfo("Title: " + currentTitle);
                LogHelper.addLogInfo("Description: " + currentDesc);

                LogHelper.addLogInfo("--------------------------");
                phoneNumbers = findPhoneNumbers(cleanPrefixAndSuffixFromEvent(currentTitleAndDesc));

                if(phoneNumbers.isEmpty()) {
                    currentTitle = currentTitle.startsWith(newTitlePrefix) ? currentTitle : newTitlePrefix + currentTitle;
                    currentDesc = currentDesc.contains(newDescriptionSuffix) ? currentDesc : currentDesc + newDescriptionSuffix;
                }

                if (!phoneNumbers.isEmpty()) {
                    // Si se encuentra un número de teléfono, se guarda en el campo phone
                    currentPhone = phoneNumbers.get(0);  // Solo guarda el primer número encontrado

                    // Eliminar el prefijo del título y el sufijo de la descripción si existen
                    if (currentTitle.startsWith(newTitlePrefix))
                        currentEvent.setEventTitle(currentEvent.getEventTitle().replace(newTitlePrefix, ""));

                    if (currentDesc.contains(newDescriptionSuffix))
                        currentEvent.setEventDescription(currentEvent.getEventDescription().replace(newDescriptionSuffix, ""));

                }

                currentEvent.setEventTitle(currentTitle);
                currentEvent.setEventDescription(currentDesc);
                currentEvent.setTargetPhone(currentPhone);
                updateCalendarEvent(context, currentEvent.getPk_id(), currentTitle, currentDesc);

                currentEvent.setPk_mcid(pkMcid);
                calendarEventsList.add(currentEvent);

            } while (eventCursor.moveToNext());

            // Guardar eventos en la base de datos
            avisacitasSQLiteOpenHelper.insertOrUpdateFromObjectList(calendarEventsList);

            // Actualizar los teléfonos en la base de datos
            //avisacitasSQLiteOpenHelper.updatePhoneInEvents(calendarEventsList);

        } catch (Exception ex) {
            LogHelper.addLogError(ex);
        }

    }

    private static void updateCalendarEvent(Context context, int eventId, String newTitle, String newDescription) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, newTitle);
        values.put(CalendarContract.Events.DESCRIPTION, newDescription);

        String eventUriString = "content://com.android.calendar/events";
        Uri eventUri = ContentUris.withAppendedId(Uri.parse(eventUriString), eventId);

        context.getContentResolver().update(eventUri, values, null, null);
    }

    @SuppressLint("Range")
    private static CalendarEvent extractEventFromCursor(Cursor eventCursor) {

        CalendarEvent event = new CalendarEvent();

        event.setPk_id(eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events._ID)));
        event.setPk_calendarId(eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID)));
        event.setEventTitle(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.TITLE)));
        event.setEventDescription(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
        event.setEventTimeZone(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
        event.setEventLocation(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
        event.setEventEmailCreated(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.ORGANIZER)));
        event.setEventStatus(eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events.STATUS)));
        event.setDeleted(eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events.DELETED)));


        long dtStart = eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Events.DTSTART));
        long dtEnd = eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Events.DTEND));

        // Convertir a UTC si es necesario
        TimeZone timeZone = TimeZone.getTimeZone(event.getEventTimeZone());
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(dtStart);
        long offset = timeZone.getOffset(calendar.getTimeInMillis());
        event.setEventStartEpoch(dtStart - offset);

        calendar.setTimeInMillis(dtEnd);
        offset = timeZone.getOffset(calendar.getTimeInMillis());
        event.setEventEndEpoch(dtEnd - offset);


        return event;
    }

    public static ArrayList<String> findPhoneNumbers(String input) {
        String phoneNumber1, phoneNumber2, regionCode;
        String regex = "(?<!\\d)\\+?[0-9]{9,15}(?!\\d)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        ArrayList<String> phoneNumbers = new ArrayList<>();

        try {
            while (matcher.find()) {
                phoneNumber1 = matcher.group();
                phoneNumbers.add(phoneNumber1);
            }

            Iterator<String> iterator = phoneNumbers.iterator();

            while (iterator.hasNext()) {
                phoneNumber2 = iterator.next();

                try {
                    Phonenumber.PhoneNumber numberProto = phoneNumberUtil.parse(phoneNumber2, "ES"); // Especificar región
                    regionCode = phoneNumberUtil.getRegionCodeForNumber(numberProto);

                    // Verificar si el número es válido
                    if (!phoneNumberUtil.isValidNumberForRegion(numberProto, regionCode)) {
                        iterator.remove();
                    }
                } catch (Exception ex) {
                    LogHelper.addLogError(ex);
                    LogHelper.addLogError("Failed to parse phone number: " + phoneNumber2);
                    iterator.remove();
                }
            }

        } catch (Exception ex) {
            LogHelper.addLogError(ex);
        }
        return phoneNumbers;
    }


    public static void insertOrUpdateAllCalendarEvents(Context context) {

        AvisacitasSQLiteOpenHelper avisacitasSQLiteOpenHelper = new AvisacitasSQLiteOpenHelper(context);
        List<String> emails = avisacitasSQLiteOpenHelper.getAllEmailsFromAccount();

        for (String email : emails) {
            CalendarHelper.insertOrUpdateCalendarEventsByEmail(context, email);
        }
    }

    public static String cleanPrefixAndSuffixFromEvent(String rawText) {
        try {
            int startIndex = rawText.indexOf(startMark),
                    endIndex = rawText.indexOf(endMark);

            if (startIndex == -1 || endIndex == -1 || (startIndex > endIndex)) {
                LogHelper.addLogInfo("Cleaned Text: " + rawText);
                LogHelper.addLogInfo("--------------------------");
                return rawText.trim();
            }

            return cleanPrefixAndSuffixFromEvent(rawText.substring(0, startIndex) + rawText.substring(endIndex + endMarkLength));
        } catch (Exception ex) {
            LogHelper.addLogError(ex);
            // Evitar recursión infinita
            LogHelper.addLogInfo("Returning rawText due to exception in cleanPrefixAndSuffixFromEvent.");
            return rawText.trim();
        }
    }

}
