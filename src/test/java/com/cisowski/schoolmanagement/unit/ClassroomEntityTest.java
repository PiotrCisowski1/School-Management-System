package com.cisowski.schoolmanagement.unit;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.classroom.model.Equipment;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClassroomEntityTest {
    @Test
    public void testRemoveExistingEquipment() {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(100);

        classroom.addEquipment(equipment, 2);
        assertFalse(classroom.getClassroomEquipments().isEmpty(),
                "Equipment should be add to Classroom");
        classroom.removeEquipment(equipment);
        assertTrue(classroom.getClassroomEquipments().isEmpty(),
                "Equipment should be removed from Classroom");
    }

    @Test
    public void testRemoveNonExistentEquipment() {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1);

        Equipment equipment = new Equipment();
        equipment.setId(100);

        SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class, () -> {
            classroom.removeEquipment(equipment);
        });
        String expectedMsgPart1 = "Equipment with ID: 100";
        String expectedMsgPart2 = "Classroom with ID: 1";
        assertTrue(exception.getMessage().contains(expectedMsgPart1));
        assertTrue(exception.getMessage().contains(expectedMsgPart2));
    }
}
