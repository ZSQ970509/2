package com.xsq.czy.beans;

/**
 * Created by lan on 2017/3/27.
 */

public class BleScanResult {
    private String name;
    private String address;
    private String vol;
    private int rssi;

    public BleScanResult(String name, String address, String vol, int rssi) {
        this.name = name;
        this.address = address;
        this.vol = vol;
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BleScanResult bleScanResult = (BleScanResult) o;

        return address.equals(bleScanResult.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
