package com.cisowski.schoolmanagement.classroom.service;

import com.cisowski.schoolmanagement.classroom.model.ClassroomDetailedResponse;
import com.cisowski.schoolmanagement.classroom.model.ClassroomRequest;
import com.cisowski.schoolmanagement.classroom.model.ClassroomSummaryResponse;

import java.util.Collection;

public interface ClassroomService {
    public ClassroomDetailedResponse addClassroom(ClassroomRequest request);

    void deleteClassroom(Integer classroomId);

    ClassroomDetailedResponse getClassroomById(Integer classroomId);

    Collection<ClassroomSummaryResponse> getAllClassrooms();
}
