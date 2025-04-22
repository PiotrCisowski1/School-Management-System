package com.cisowski.schoolmanagement.classroom.service;

import com.cisowski.schoolmanagement.classroom.model.*;

import java.util.Collection;

public interface ClassroomService {
    public ClassroomDetailedResponse addClassroom(ClassroomRequest request);

    void deleteClassroom(Integer classroomId);

    ClassroomDetailedResponse getClassroomById(Integer classroomId);

    Collection<ClassroomSummaryResponse> getAllClassrooms();

    ClassroomDetailedResponse updateClassroom(PatchClassroomRequest request, Integer classroomId);

    ClassroomEntity fetchClassroom(Integer classroomId);
}
