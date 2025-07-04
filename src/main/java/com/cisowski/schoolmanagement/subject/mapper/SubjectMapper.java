package com.cisowski.schoolmanagement.subject.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.subject.model.*;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(config = BaseMapperConfig.class,
        uses = {TeacherMapper.class, YearbookMapper.class})
public interface SubjectMapper {
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "yearbooksTakingSubject", ignore = true)
    @Mapping(target = "subjectType", ignore = true)
    SubjectEntity toSubjectEntity(AddSubjectRequest subjectRequest);

    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "yearbooksTakingSubject", ignore = true)
    @Mapping(target = "subjectType", ignore = true)
    SubjectEntity toSubjectEntity(PatchSubjectRequest subjectRequest);

    @Mapping(target = "teachers", source = "teachers", qualifiedByName = "toTeacherSummaryResponse")
    @Mapping(target = "yearbooksTakingSubject", source = "yearbooksTakingSubject", qualifiedByName = "toYearbookSummaryResponse")
    @Mapping(target = "subjectType", source = "savedEntity.subjectType.name")
    SubjectDetailedResponse toDetailedResponse(SubjectEntity savedEntity);


    void patchSubject(@MappingTarget SubjectEntity targetEntity, SubjectEntity requestEntity);

    Collection<SubjectSummaryResponse> toSubjectSummaryResponseList(List<SubjectEntity> subjects);

    @Named("toSubjectSummaryResponse")
    SubjectSummaryResponse toSubjectSummaryResponse(SubjectEntity subjectEntity);

}
