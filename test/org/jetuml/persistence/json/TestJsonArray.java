package org.jetuml.persistence.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestJsonArray {
    @Test
    @DisplayName("Should throw when trying to get an item with an invalid array index")
    void shouldThrowOnInvalidIndices() {
        JsonArray array = new JsonArray();
        array.add(1);
        assertThrows(JsonException.class, () -> array.get(1));
    }

    @Test
    @DisplayName("Should return correct string")
    void shouldGetValidStrings() {
        final String value = "test";
        JsonArray array = new JsonArray();
        array.add(value);
        assertEquals(value, array.getString(0));
    }

    @Test
    @DisplayName("Should return correct boolean")
    void shouldGetValidBooleans() {
        JsonArray array = new JsonArray();
        array.add(true);
        array.add(false);
        assertTrue(array.getBoolean(0));
        assertFalse(array.getBoolean(1));
    }

    @Test
    @DisplayName("Should return correct JSon array")
    void shouldGetValidJSonArrays() {
        JsonArray insert = new JsonArray();
        JsonArray array = new JsonArray();
        array.add(insert);
        assertEquals(insert, array.getJsonArray(0));
    }

    @Test
    @DisplayName("Should return correct string for a JSON array")
    void shouldReturnCorrectString() {
        JsonArray array = new JsonArray();
        array.add(1);
        array.add(2);
        assertEquals("[1,2]", array.toString());
    }

    @Test
    @DisplayName("Adding null to array should throw exception")
    void shouldThrowExceptionAddingNull() {
        JsonArray array = new JsonArray();
        assertThrows(JsonException.class, () -> array.add(null));
    }
}
