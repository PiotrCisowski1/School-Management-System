package com.cisowski.schoolmanagement.unit.helpers;

import com.cisowski.schoolmanagement.timetable.shared.TimetableHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimetableHelperTest {

    private static final int MINUTES_BEFORE = 15;

    @Nested
    class SingleDayWindowTests {

        private final LocalTime START_TIME = LocalTime.of(10, 0);
        private final LocalTime WINDOW_START = START_TIME.minusMinutes(MINUTES_BEFORE);
        private final LocalTime WINDOW_END = START_TIME;

        @Test
        void isStartWithinTimeWindow_AtStart() {
            LocalTime currentTime = WINDOW_START;
            assertTrue(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_Inside() {
            LocalTime currentTime = LocalTime.of(9, 50);
            assertTrue(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_AtEnd() {
            LocalTime currentTime = WINDOW_END;
            assertTrue(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_BeforeStart() {
            LocalTime currentTime = LocalTime.of(9, 40);
            assertFalse(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_AfterEnd() {
            LocalTime currentTime = LocalTime.of(10, 1);
            assertFalse(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }
    }

    @Nested
    class CrossMidnightWindowTests {

        private final LocalTime START_TIME = LocalTime.of(0, 10);
        private final LocalTime WINDOW_START = START_TIME.minusMinutes(MINUTES_BEFORE);
        private final LocalTime WINDOW_END = START_TIME;

        @Test
        void isStartWithinTimeWindow_AtStart_CrossMidnight() {
            LocalTime currentTime = WINDOW_START;
            assertTrue(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_Inside_BeforeMidnight() {
            LocalTime currentTime = LocalTime.of(23, 58);
            assertTrue(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_Inside_AfterMidnight() {
            LocalTime currentTime = LocalTime.of(0, 5);
            assertTrue(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_AtEnd_CrossMidnight() {
            LocalTime currentTime = WINDOW_END;
            assertTrue(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_Outside_AfterEnd() {
            LocalTime currentTime = LocalTime.of(0, 11);
            assertFalse(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }

        @Test
        void isStartWithinTimeWindow_Outside_BeforeStart() {
            LocalTime currentTime = LocalTime.of(23, 54);
            assertFalse(TimetableHelper.isStartWithinTimeWindow(currentTime, START_TIME, MINUTES_BEFORE));
        }
    }
}
