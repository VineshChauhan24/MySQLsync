package com.palibre.mysqlsync;

public class DbContract {

    public static final int SYNC_STATUS_OK = 0;
    public static final int SYNC_STATUS_FAILED = 1;

    public static final String serverURL = "http://palibre.com/dbsync/sqlitesync.php";


    public static final String DATABASE_NAME ="contactDB";
    public static final String TABLE_NAME = "contactinfo";
    public static final String NAME = "name";
    public static final String SYNC_STATUS  = "syncstatus";


}
