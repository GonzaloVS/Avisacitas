package com.gvs.avisacitas.model.calendar;

import java.io.Serializable;

public class CalendarEvent implements Serializable {

    int pk_id;
    int pk_calendarId;
    String pk_mcid;
    String eventTitle;
    String eventDescription;
    String eventTimeZone;
    String eventLocation;
    String eventEmailCreated;
    long eventStartEpoch;
    long eventEndEpoch;
    int eventStatus;
    int deleted;
    String targetPhone;

    long i_createdGetDateEpoch;
    long i_createdSentDateEpoch;
    String i_typeDataCreated;
    long i_2880GetDateEpoch;
    long i_2880SentDateEpoch;
    String i_typeData2880;
    long i_1440GetDateEpoch;
    long i_1440SentDateEpoch;
    String i_typeData1440;
    long i_60GetDateEpoch;
    long i_60SentDateEpoch;
    String i_typeData60;
    long i_epoch30GetDate;
    long i_epoch30SentDate;
    String i_typeData30;
    long i_epoch10GetDate;
    long i_epoch10SentDate;
    String i_typeData10;
    long i_epoch5GetDate;
    long i_epoch5SentDate;
    String i_typeData5;
    String log;

    int i_2880SentStatus;
    String i_2880SentError;
    int i_1440SentStatus;
    String i_1440SentError;
    int i_60SentStatus;
    String i_60SentError;
    int i_createdSentStatus;
    String i_createdSentError;

    int i_createdTryCount;
    int i_48TryCount;
    int i_24TryCount;
    int i_60TryCount;


    // CONSTRUCTOR-----------------------------------------------------
    public CalendarEvent() {

    }


    public CalendarEvent(int pk_id,
                         int pk_calendarId,
                         String pk_mcid,
                         String eventTitle,
                         String eventDescription,
                         String eventTimeZone,
                         String eventLocation,
                         String eventEmailCreated,
                         long eventStartEpoch,
                         long eventEndEpoch,
                         int eventStatus,
                         int deleted,
                         String targetPhone,
                         long i_createdGetDateEpoch,
                         long i_createdSentDateEpoch,
                         String i_typeDataCreated,
                         long i_2880GetDateEpoch,
                         long i_2880SentDateEpoch,
                         String i_typeData2880,
                         long i_1440GetDateEpoch,
                         long i_1440SentDateEpoch,
                         String i_typeData1440,
                         long i_60GetDateEpoch,
                         long i_60SentDateEpoch,
                         String i_typeData60,
                         long i_epoch30GetDate,
                         long i_epoch30SentDate,
                         String i_typeData30,
                         long i_epoch10GetDate,
                         long i_epoch10SentDate,
                         String i_typeData10,
                         long i_epoch5GetDate,
                         long i_epoch5SentDate,
                         String i_typeData5,
                         String log) {

        this.pk_id = pk_id;
        this.pk_calendarId = pk_calendarId;
        this.pk_mcid = pk_mcid;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventTimeZone = eventTimeZone;
        this.eventLocation = eventLocation;
        this.eventEmailCreated = eventEmailCreated;
        this.eventStartEpoch = eventStartEpoch;
        this.eventEndEpoch = eventEndEpoch;
        this.eventStatus = eventStatus;
        this.deleted = deleted;
        this.targetPhone = targetPhone;

        this.i_createdGetDateEpoch = i_createdGetDateEpoch;
        this.i_createdSentDateEpoch = i_createdSentDateEpoch;
        this.i_typeDataCreated = i_typeDataCreated;

        this.i_2880GetDateEpoch = i_2880GetDateEpoch;
        this.i_2880SentDateEpoch = i_2880SentDateEpoch;
        this.i_typeData2880 = i_typeData2880;
        this.i_1440GetDateEpoch = i_1440GetDateEpoch;
        this.i_1440SentDateEpoch = i_1440SentDateEpoch;
        this.i_typeData1440 = i_typeData1440;
        this.i_60GetDateEpoch = i_60GetDateEpoch;
        this.i_60SentDateEpoch = i_60SentDateEpoch;
        this.i_typeData60 = i_typeData60;
        this.i_epoch30GetDate = i_epoch30GetDate;
        this.i_epoch30SentDate = i_epoch30SentDate;
        this.i_typeData30 = i_typeData30;
        this.i_epoch10GetDate = i_epoch10GetDate;
        this.i_epoch10SentDate = i_epoch10SentDate;
        this.i_typeData10 = i_typeData10;
        this.i_epoch5GetDate = i_epoch5GetDate;
        this.i_epoch5SentDate = i_epoch5SentDate;
        this.i_typeData5 = i_typeData5;
        this.log = log;

    }



    public CalendarEvent(long eventStartEpoch) {
        this.eventStartEpoch = eventStartEpoch;
    }

    // GETTERS----------------------------------------------------------

    public int getPk_id() {return pk_id;}
    public String getPk_mcid() {return pk_mcid;}
    public String getEventTitle() {return eventTitle;}
    public String getEventDescription() {return eventDescription;}
    public String getEventTimeZone() {return eventTimeZone;}
    public Long getEventStartEpoch() {return eventStartEpoch;}
    public String getTargetPhone() {return targetPhone;}
    public long getI_createdSentDateEpoch() {
        return i_createdSentDateEpoch;
    }
    public long get2880SentDateEpoch() {
        return i_2880SentDateEpoch;
    }
    public long get1440SentDateEpoch() {
        return i_1440SentDateEpoch;
    }
    public long get60SentDateEpoch() {
        return i_60SentDateEpoch;
    }
    public String getLog() {
        return log;
    }


    // SETTERS-----------------------------------------------------

    public void setPk_id(int pk_id) {this.pk_id = pk_id;}
    public void setPk_calendarId(int pk_calendarId){this.pk_calendarId = pk_calendarId;}
    public void setPk_mcid(String pk_mcid) {this.pk_mcid = pk_mcid;}
    public void setEventTitle(String eventTitle) {this.eventTitle = eventTitle;}
    public void setEventDescription(String eventDescription) {this.eventDescription = eventDescription;}
    public void setEventTimeZone(String eventTimeZone) {this.eventTimeZone = eventTimeZone;}
    public void setEventLocation(String eventLocation) {this.eventLocation = eventLocation;}
    public void setEventEmailCreated(String eventEmailCreated) {this.eventEmailCreated = eventEmailCreated;}
    public void setEventStartEpoch(long eventStartEpoch) {this.eventStartEpoch = eventStartEpoch;}
    public void setEventEndEpoch(long eventEndEpoch) {this.eventEndEpoch = eventEndEpoch;}
    public void setEventStatus(int eventStatus){this.eventStatus = eventStatus;}
    public void setDeleted(int deleted){this.deleted = deleted;}
    public void setTargetPhone(String targetPhone) {this.targetPhone = targetPhone;}


    public void setI_createdSentDateEpoch(long i_createdSentDateEpoch) {this.i_createdSentDateEpoch = i_createdSentDateEpoch;}

    public void setI_2880SentDateEpoch(long i_2880SentDateEpoch) {this.i_2880SentDateEpoch = i_2880SentDateEpoch;}

    public void setI_1440SentDateEpoch(long i_1440SentDateEpoch) {this.i_1440SentDateEpoch = i_1440SentDateEpoch;}
    public void setI_60SentDateEpoch(long i_60SentDateEpoch) {this.i_60SentDateEpoch = i_60SentDateEpoch;}

    public void setLog(String log) {this.log = log;}



    public long getI_createdGetDateEpoch() {return i_createdGetDateEpoch;}
    public long getI_2880GetDateEpoch() {return i_2880GetDateEpoch;}

    public long getI_1440GetDateEpoch() {return i_1440GetDateEpoch;}

    public long getI_60GetDateEpoch() {return i_60GetDateEpoch;}



    public int getI_2880SentStatus() {
        return i_2880SentStatus;
    }
    public void setI_2880SentStatus(int i_2880SentStatus) {this.i_2880SentStatus = i_2880SentStatus;}


    public int getI_1440SentStatus() {
        return i_1440SentStatus;
    }
    public void setI_1440SentStatus(int i_1440SentStatus) {this.i_1440SentStatus = i_1440SentStatus;}


    public int getI_60SentStatus() {return i_60SentStatus;}
    public void setI_60SentStatus(int i_60SentStatus) {this.i_60SentStatus = i_60SentStatus;}


    public int getI_createdSentStatus() {return i_createdSentStatus;}
    public void setI_createdSentStatus(int i_createdSentStatus) {this.i_createdSentStatus = i_createdSentStatus;}


}
