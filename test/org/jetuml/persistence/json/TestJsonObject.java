package org.jetuml.persistence.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJsonObject {
    @Test
    @DisplayName("Should throw exception when adding property where name is null")
    void shouldThrowWhenAddingWithNullName() {
        JsonObject object = new JsonObject();
        assertThrows(JsonException.class, () -> object.put(null, 42));
    }

    @Test
    @DisplayName("Should throw exception when getting value where property name is null")
    void shouldThrowWhenGettingValueWithNullName() {
        JsonObject object = new JsonObject();

        try {
            object.get(null);
        } catch (JsonException exception) {
            assertEquals("Property name cannot be null", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should return correct JsonObject value")
    void shouldReturnCorrectJsonObjectValue() {
        final String name = "test";
        JsonObject insert = new JsonObject();
        JsonObject object = new JsonObject();
        object.put(name, insert);
        assertEquals(insert, object.getJsonObject(name));
    }

    @Test
    @DisplayName("Should return correct boolean value")
    void shouldReturnCorrectBooleanValue() {
        final String name1 = "testTrue";
        final String name2 = "testFalse";
        JsonObject object = new JsonObject();
        object.put(name1, true);
        object.put(name2, false);
        assertTrue(() -> object.getBoolean(name1));
        assertFalse(() -> object.getBoolean(name2));
    }

    @Test
    @DisplayName("Should not allow invalid types to be added")
    void shouldNotAllowInvalidTypesToBeAdded() {
        final String name = "test";
        final Double value = 1.00;
        JsonObject object = new JsonObject();
        assertThrows(JsonException.class, () -> object.put(name, value));
    }
}
