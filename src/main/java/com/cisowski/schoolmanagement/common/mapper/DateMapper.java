package com.cisowski.schoolmanagement.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.DayOfWeek;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DateMapper {

    @Named("toDayOfWeek")
    default DayOfWeek map(Integer dayOfWeek){
        if(dayOfWeek == null)
            return null;
        return DayOfWeek.of(dayOfWeek);
    }
}
