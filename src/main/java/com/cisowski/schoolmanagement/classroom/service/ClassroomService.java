package com.cisowski.schoolmanagement.classroom.service;

import com.cisowski.schoolmanagement.classroom.model.ClassroomDetailedResponse;
import com.cisowski.schoolmanagement.classroom.model.ClassroomRequest;

public interface ClassroomService {
    public ClassroomDetailedResponse addClassroom(ClassroomRequest request);
}
