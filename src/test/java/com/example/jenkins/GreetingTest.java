package com.example.jenkins;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GreetingTest {
    @Test void greetsWorldByDefault() {
        assertEquals("Hello, World!", Greeting.message(null));
        assertEquals("Hello, World!", Greeting.message("  "));
    }

    @Test void trimsNamesAndPreservesUnicode() {
        assertEquals("Hello, Balaji!", Greeting.message(" Balaji "));
        assertEquals("Hello, உலகம்!", Greeting.message("உலகம்"));
    }

    @Test void limitsNameLength() {
        assertEquals("Hello, " + "a".repeat(80) + "!", Greeting.message("a".repeat(80)));
        assertThrows(IllegalArgumentException.class, () -> Greeting.message("a".repeat(81)));
    }
}
