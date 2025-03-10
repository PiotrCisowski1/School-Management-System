package com.cisowski.schoolmanagement.yearbook.service;

import com.cisowski.schoolmanagement.yearbook.model.PatchYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.YearbookDetailedResponse;
import com.cisowski.schoolmanagement.yearbook.model.AddYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;

import java.util.Collection;

public interface YearbookService {

    YearbookDetailedResponse addYearbook(AddYearbookRequest request);
    YearbookDetailedResponse updateYearbook(PatchYearbookRequest request, Integer yearbookId);
    void deleteYearbook(Integer yearbookId);
    YearbookDetailedResponse getYearbook(Integer yearbookId);
    Collection<YearbookSummaryResponse> getYearbooks();

}
