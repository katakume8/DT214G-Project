package org.jetuml.persistence.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestJsonValueValidator {
    @Test
    @DisplayName("Should only accept Boolean class")
    void shouldOnlyAcceptBooleanClass() {
        final String value = "test";
        assertThrows(JsonException.class, () -> JsonValueValidator.asBoolean(value));
    }
}
