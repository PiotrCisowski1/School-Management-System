package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.common.mapper.DateMapper;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.time.DateTimeException;
import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(InstancioExtension.class)
public class DateMapperTest {

    private DateMapper dateMapper;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, 10);

    @BeforeEach
    public void setUp(){
        dateMapper = Mappers.getMapper(DateMapper.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void mapDayOfWeek(Integer dayOfWeek){
        DayOfWeek result = dateMapper.map(dayOfWeek);
        assertNotNull(result);
    }

    @ParameterizedTest
    @InstancioSource(samples = 10)
    void mapDayOfWeek_error(Integer dayOfWeek){
        DateTimeException result = assertThrows(
                DateTimeException.class,
                () -> dateMapper.map(dayOfWeek)
        );
    }
}
