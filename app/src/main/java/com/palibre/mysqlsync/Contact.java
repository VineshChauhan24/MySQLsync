package com.palibre.mysqlsync;

public class Contact {


    private String name;
    private int syncStatus;


    Contact (String name, int syncStatus){
        setName(name);
        setSyncStatus(syncStatus);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }





}
