package com.cisowski.schoolmanagement.utility;

import com.cisowski.schoolmanagement.SchoolmanagementApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DbLogger {
    /*
         This wrapper is in use, because only INFO messages from code should be logged in DB.
         Any INFO messages from server are omitted and are not saved to DB.

         Method 'buildInfoMessage' is temporary and should be fixed differently.
  */

    private static final Logger logger = LoggerFactory.getLogger(SchoolmanagementApplication.class);
    public static final String INFO_LOG_MARKER = "*INFO:  ";

    public static Logger getLogger(){
        return logger;
    }
    public static String buildInfoMessage(String message){
        return INFO_LOG_MARKER + message;
    }

}
