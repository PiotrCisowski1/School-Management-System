package com.cisowski.schoolmanagement.subject.service;


import com.cisowski.schoolmanagement.subject.model.*;

import java.util.Collection;

public interface SubjectService {
    SubjectDetailedResponse addSubject(AddSubjectRequest subjectRequest);
    SubjectDetailedResponse patchSubject(PatchSubjectRequest subjectRequest, Integer subjectId);
    Collection<SubjectSummaryResponse> getAllSubjects();
    SubjectDetailedResponse getSubjectById(Integer subjectId);
    SubjectDetailedResponse getSubjectByCode(String subjectCode);
    Collection<SubjectSummaryResponse> getSubjectsByType(String subjectType);
    Collection<SubjectEntity> fetchSubjects(Collection<Integer> subjectIds);
    void deleteSubject(Integer subjectId);
}
