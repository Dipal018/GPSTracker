package com.dipal.list;

import java.io.Serializable;

public class GPSDeviceInfo implements Serializable {
	private String deviceID;
	private String deviceName;
    private String deviceIMEI;
    private String devicePhoneNumber;
    
    public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String orderName) {
        this.deviceName = orderName;
    }
    public String getDeviceIMEI() {
        return deviceIMEI;
    }
    public void setDeviceIMEI(String orderStatus) {
        this.deviceIMEI = orderStatus;
    }
	public String getDevicePhoneNumber() {
		return devicePhoneNumber;
	}
	public void setDevicePhoneNumber(String devicePhoneNumber) {
		this.devicePhoneNumber = devicePhoneNumber;
	}

}
