package com.cisowski.schoolmanagement.utility;

import com.cisowski.schoolmanagement.SchoolmanagementApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/*
         This wrapper is in use, because only DEBUG messages from code should be logged in DB.
         Any DEBUG messages from server are omitted and are not saved to DB.
  */
public class DbLogger {

    private static final Logger logger = LoggerFactory.getLogger(SchoolmanagementApplication.class);
    public static final String DEBUG_MARKER = "*DEBUG*:  ";

    public static Logger getLogger(){
        return logger;
    }
    public static String buildDebugMessage(String message){
        return DEBUG_MARKER + message;
    }
}
