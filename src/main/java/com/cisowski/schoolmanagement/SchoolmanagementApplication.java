package com.cisowski.schoolmanagement;

import com.cisowski.schoolmanagement.utility.DbLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class SchoolmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolmanagementApplication.class, args);
	}

}
