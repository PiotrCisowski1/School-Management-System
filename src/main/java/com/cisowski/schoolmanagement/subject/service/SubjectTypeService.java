package com.cisowski.schoolmanagement.subject.service;

import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeRequest;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeResponse;

import java.util.Collection;

public interface SubjectTypeService {
    SubjectTypeResponse addSubjectType(SubjectTypeRequest request);
    SubjectTypeResponse patchSubjectType(SubjectTypeRequest request, Integer subjectTypeId);
    void deleteSubjectType(Integer subjectTypeId);
    Collection<SubjectTypeResponse> getSubjectTypes();
    SubjectTypeResponse getSubjectType(Integer subjectTypeId);
    SubjectTypeEntity fetchSubjectType(Integer subjectTypeId);

}
