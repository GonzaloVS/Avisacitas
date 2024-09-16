package com.gvs.avisacitas.model.accounts;

import androidx.annotation.NonNull;

import com.gvs.avisacitas.GlobalReferences;

import java.io.Serializable;

public class Account implements Serializable {

	private String name;
	private String waToken;
	private String phone;
	private String pk_mcid;
	private String email;
	private int lastSyncStatus = GlobalReferences.ACCOUNTWCB_STATUS_NEVERSYNCED;
	private String isConnect;
	private long epochUTCLastSync = (long)0;
	private long epochUTCAdded = (long)0;
	private long lastSyncTime;
	private String isActive = "false";

	private byte[] profileImage;
	private String companyName;

	private String sendWhatsAndSms = "true";
	private String sendOnlySms = "false";
	private String eventCreatedReminder = "false";
	private String twoDaysReminder = "false";
	private String previousDayReminder = "false";
	private String fiveMinutesReminder = "false";
	private String postSaleMessage = "false";
	private String customOncreateMsg = "";
	private String customTwoDaysMsg = "";
	private String customOneDayMsg = "";
	private String customFiveMinMsg = "";
	private String customPostMsg = "";
	private long workingFrom = (long)0;
	private int mondayTimeStart = 32400000;
	private int mondayTimeEnd = 75600000;
	private int tuesdayTimeStart = 32400000;
	private int tuesdayTimeEnd = 75600000;
	private int wednesdayTimeStart = 32400000;
	private int wednesdayTimeEnd = 75600000;
	private int thursdayTimeStart = 32400000;
	private int thursdayTimeEnd = 75600000;
	private int fridayTimeStart = 32400000;
	private int fridayTimeEnd = 75600000;
	private int saturdayTimeStart = 32400000;
	private int saturdayTimeEnd = 75600000;
	private int sundayTimeStart = 32400000;
	private int sundayTimeEnd = 75600000;

	public Account() {

	}

	public Account(
			String name,
			String waToken,
			String phone,
			String pk_mcid,
			String email,
			int lastSyncStatus,
			String isConnect,
			long epochUTCLastSync,
			long epochUTCAdded,
			long lastSyncTime,
			String isActive,
			String sendWhatsAndSms,
			String sendOnlySms,
			String eventCreatedReminder,
			String twoDaysReminder,
			String previousDayReminder,
			String fiveMinutesReminder,
			String postSaleMessage,
			String customOncreateMsg,
			String customTwoDaysMsg,
			String customOneDayMsg,
			String customFiveMinMsg,
			String customPostMsg,
			long workingFrom) {
		this.name = name;
		this.waToken = waToken;
		this.phone = phone;
		this.pk_mcid = pk_mcid;
		this.email = email;
		this.lastSyncStatus = lastSyncStatus;
		this.isConnect = isConnect;
		this.epochUTCLastSync = epochUTCLastSync;
		this.epochUTCAdded = epochUTCAdded;
		this.lastSyncTime = lastSyncTime;
		this.isActive = isActive;
		this.sendWhatsAndSms = sendWhatsAndSms;
		this.sendOnlySms = sendOnlySms;
		this.eventCreatedReminder = eventCreatedReminder;
		this.twoDaysReminder = twoDaysReminder;
		this.previousDayReminder = previousDayReminder;
		this.fiveMinutesReminder = fiveMinutesReminder;
		this.postSaleMessage = postSaleMessage;
		this.customOncreateMsg = customOncreateMsg;
		this.customTwoDaysMsg = customTwoDaysMsg;
		this.customOneDayMsg = customOneDayMsg;
		this.customFiveMinMsg = customFiveMinMsg;
		this.customPostMsg = customPostMsg;
		this.workingFrom = workingFrom;
	}

	// GETTERS----------------------------------------------------------
	public long getEpochUTCAdded(){
		return epochUTCAdded;
	}
	public String getName() {
		return name;
	}
	public String getPhone() {
		return phone;
	}
	public String getPk_mcid() {
		return pk_mcid;
	}
	public String getEmail() {
		return email;
	}
	public String getConnect() {
		return isConnect;
	}
	public String getWaToken(){return waToken;}
	public long getEpochUTCLastSync() {
		return epochUTCLastSync;
	}
	public int getLastSyncStatus() {
		return lastSyncStatus;
	}
	public String isActive() {return isActive;}
	public String isSendWhatsAndSms() {return sendWhatsAndSms;}
	public String isSendOnlySms() {return sendOnlySms;}
	public String isEventCreatedReminder() {return eventCreatedReminder;}
	public String isTwoDaysReminder() {return twoDaysReminder;}
	public String isPreviousDayReminder() {return previousDayReminder;}
	public String isFiveMinutesReminder() {return fiveMinutesReminder;}
	public String isPostSaleMessage() {return postSaleMessage;}
	public String getCustomOncreateMsg() {return customOncreateMsg;}
	public String getCustomTwoDaysMsg() {return customTwoDaysMsg;}
	public String getCustomOneDayMsg() {return customOneDayMsg;}
	public String getCustomFiveMinMsg() {return customFiveMinMsg;}
	public String getCustomPostMsg() {return customPostMsg;}
	public long getWorkingFrom() {return workingFrom;}
	public int getTime(int pos){

		switch (pos){
			case 0: return mondayTimeStart;
			case 1: return mondayTimeEnd;
			case 2: return tuesdayTimeStart;
			case 3: return tuesdayTimeEnd;
			case 4: return wednesdayTimeStart;
			case 5: return wednesdayTimeEnd;
			case 6: return thursdayTimeStart;
			case 7: return thursdayTimeEnd;
			case 8: return fridayTimeStart;
			case 9: return fridayTimeEnd;
			case 10: return saturdayTimeStart;
			case 11: return saturdayTimeEnd;
			case 12: return sundayTimeStart;
			case 13: return sundayTimeEnd;
			default: return 0;

		}
	}

	public String getCompanyName() {
		return companyName;
	}
	public byte[] getProfileImage() {return profileImage;}


	// SETTERS-----------------------------------------------------
	public void setName(String name) {this.name = name;}
	public void setWaToken(String waToken) {this.waToken = waToken;}
	public void setPhone(String phone) {this.phone = phone;}
	public void setPk_mcid(String mcid) {this.pk_mcid = mcid;}
	public void setEmail(String email) {this.email = email;}
	public void setConnect(String connect) {isConnect = connect;}
	public void setEpochUTCAdded(long epochUTCAdded) {this.epochUTCAdded = epochUTCAdded;}
	public void setEpochUTCLastSync(long epochUTC){
		this.lastSyncStatus = GlobalReferences.ACCOUNTWCB_STATUS_SYNCFINISHED;
		this.epochUTCLastSync = epochUTC;
	}
	public void setLastSyncStatus(int lastSyncStatus) {this.lastSyncStatus = lastSyncStatus;}
	public void setActive(String active) {isActive = active;}
	public void setSendWhatsAndSms(String sendWhatsAndSms) {this.sendWhatsAndSms = sendWhatsAndSms;}
	public void setSendOnlySms(String sendOnlySms) {this.sendOnlySms = sendOnlySms;}
	public void setEventCreatedReminder(String eventCreatedReminder) {this.eventCreatedReminder = eventCreatedReminder;}
	public void setTwoDaysReminder(String twoDaysReminder) {this.twoDaysReminder = twoDaysReminder;}
	public void setPreviousDayReminder(String previousDayReminder) {this.previousDayReminder = previousDayReminder;}
	public void setFiveMinutesReminder(String fiveMinutesReminder) {this.fiveMinutesReminder = fiveMinutesReminder;}
	public void setPostSaleMessage(String postSaleMessage) {this.postSaleMessage = postSaleMessage;}
	public void setCustomOncreateMsg(String customOncreateMsg) {this.customOncreateMsg = customOncreateMsg;}
	public void setCustomTwoDaysMsg(String customTwoDaysMsg) {this.customTwoDaysMsg = customTwoDaysMsg;}
	public void setCustomOneDayMsg(String customOneDayMsg) {this.customOneDayMsg = customOneDayMsg;}
	public void setCustomFiveMinMsg(String customFiveMinMsg) {this.customFiveMinMsg = customFiveMinMsg;}
	public void setCustomPostMsg(String customPostMsg) {this.customPostMsg = customPostMsg;}
	public void setWorkingFrom(long workingFrom) {this.workingFrom = workingFrom;}
	public void setMondayTimeStart(int mondayTimeStart) {this.mondayTimeStart = mondayTimeStart;}
	public void setMondayTimeEnd(int mondayTimeEnd) {this.mondayTimeEnd = mondayTimeEnd;}
	public void setTuesdayTimeStart(int tuesdayTimeStart) {this.tuesdayTimeStart = tuesdayTimeStart;}
	public void setTuesdayTimeEnd(int tuesdayTimeEnd) {this.tuesdayTimeEnd = tuesdayTimeEnd;}
	public void setWednesdayTimeStart(int wednesdayTimeStart) {this.wednesdayTimeStart = wednesdayTimeStart;}
	public void setWednesdayTimeEnd(int wednesdayTimeEnd) {this.wednesdayTimeEnd = wednesdayTimeEnd;}
	public void setThursdayTimeStart(int thursdayTimeStart) {this.thursdayTimeStart = thursdayTimeStart;}
	public void setThursdayTimeEnd(int thursdayTimeEnd) {this.thursdayTimeEnd = thursdayTimeEnd;}
	public void setFridayTimeStart(int fridayTimeStart) {this.fridayTimeStart = fridayTimeStart;}
	public void setFridayTimeEnd(int fridayTimeEnd) {this.fridayTimeEnd = fridayTimeEnd;}
	public void setSaturdayTimeStart(int saturdayTimeStart) {this.saturdayTimeStart = saturdayTimeStart;}
	public void setSaturdayTimeEnd(int saturdayTimeEnd) {this.saturdayTimeEnd = saturdayTimeEnd;}
	public void setSundayTimeStart(int sundayTimeStart) {this.sundayTimeStart = sundayTimeStart;}
	public void setSundayTimeEnd(int sundayTimeEnd) {this.sundayTimeEnd = sundayTimeEnd;}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}
	@NonNull
	public String toString() {
		return "Account{" +
				"name='" + name + '\'' +
				", waToken='" + waToken + '\'' +
				", phone='" + phone + '\'' +
				", lastSync='" + epochUTCLastSync + '\'' +
				", lastSyncStatus='"+ lastSyncStatus + '\'' +
				", mcId='" + pk_mcid + '\'' +
				", email='" + email + '\'' +
				", isConnect=" + isConnect +
				", epochAdded=" + epochUTCAdded +
				'}';
	}

}

