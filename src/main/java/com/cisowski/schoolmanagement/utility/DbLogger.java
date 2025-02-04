package com.cisowski.schoolmanagement.utility;

import com.cisowski.schoolmanagement.SchoolmanagementApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DbLogger {
    /*
         This wrapper is in use, because only INFO messages from code should be logged in DB.
         Any INFO messages from server are omitted and are not saved to DB.

         Errors are logged automatically.
  */
    private static final Logger logger = LoggerFactory.getLogger(SchoolmanagementApplication.class);
    protected static final String INFO_LOG_MARKER = "*INFO*  ";

    public static void info(String message){
        logger.info(buildInfoMessage(message));
    }
    private static String buildInfoMessage(String message){
        return INFO_LOG_MARKER + message;
    }

    public static void error(String message){
        logger.error(message);
    }

}
