package com.cisowski.schoolmanagement.yearbook.service;

import com.cisowski.schoolmanagement.yearbook.model.*;

import java.util.Collection;

public interface YearbookService {

    YearbookDetailedResponse addYearbook(AddYearbookRequest request);
    YearbookDetailedResponse updateYearbook(PatchYearbookRequest request, Integer yearbookId);
    void deleteYearbook(Integer yearbookId);
    YearbookDetailedResponse getYearbook(Integer yearbookId);
    Collection<YearbookSummaryResponse> getYearbooks();
    YearbookEntity fetchYearbookEntity(Integer yearbookId);

}
