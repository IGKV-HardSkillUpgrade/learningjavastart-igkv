package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class StringFilteringTest {

    // ========== ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ ==========

    @Test
    void filterStrings_NullList_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> StringFiltering.stringsComparison(null, "test"));
    }

    @Test
    void filterStrings_NullElement_ThrowsNullPointerException() {
        List<String> input = Arrays.asList("test", null, "testing");
        assertThrows(NullPointerException.class,
                () -> StringFiltering.stringsComparison(input, "test"));
    }

    @Test
    void scanArrayList_NegativeCount_ReturnsEmptyList() {
        String simulatedInput = "-1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        List<String> result = StringFiltering.scanArrayList(scanner);
        assertTrue(result.isEmpty(), "При отрицательном количестве должен вернуться пустой список");
    }

    @Test
    void printResult_NullList_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> StringFiltering.printResult(null));
    }

    @Test
    void printResult_EmptyList_PrintsNothing() {
        String result = capturePrintedResult(List.of());
        assertEquals("", result);
    }

    @Test
    void filterStrings_UnicodeStrings() {
        List<String> input = List.of("Привет мир", "Hello world", "こんにちは世界");
        List<String> result = StringFiltering.stringsComparison(input, "мир");
        assertEquals(List.of("Привет мир"), result);
    }

    @Test
    void filterStrings_CaseSensitive() {
        List<String> input = List.of("Hello", "HELLO", "hello");
        List<String> result = StringFiltering.stringsComparison(input, "hello");
        assertEquals(List.of("hello"), result, "Фильтрация должна быть регистрозависимой");
    }

    @Test
    void filterStrings_LongStrings() {
        String longString = "a".repeat(10000) + "target" + "b".repeat(10000);
        List<String> input = List.of(longString, "short", "target");
        List<String> result = StringFiltering.stringsComparison(input, "target");
        assertEquals(2, result.size());
        assertTrue(result.contains(longString));
        assertTrue(result.contains("target"));
    }

    @Test
    void filterStrings_RepeatedSubstrings() {
        List<String> input = List.of("testtest", "test", "testing", "notest");
        List<String> result = StringFiltering.stringsComparison(input, "test");
        assertEquals(List.of("testtest", "test", "testing", "notest"), result);
    }

    @Test
    void filterStrings_SpecialCharacters() {
        List<String> input = List.of("test@email.com", "test#hash", "test$dollar", "normal");
        List<String> result = StringFiltering.stringsComparison(input, "@");
        assertEquals(List.of("test@email.com"), result);
    }

    @Test
    void filterStrings_SubstringAtStartAndEnd() {
        List<String> input = List.of("start_test", "test_start", "middle_test_middle", "no_match");
        List<String> result = StringFiltering.stringsComparison(input, "test");
        assertEquals(3, result.size());
        assertTrue(result.contains("start_test"));
        assertTrue(result.contains("test_start"));
        assertTrue(result.contains("middle_test_middle"));
        assertFalse(result.contains("no_match"));
    }

    @Test
    void filterStrings_WhitespaceSubstring() {
        List<String> input = List.of("hello world", "helloWorld", "hello world again");
        List<String> result = StringFiltering.stringsComparison(input, " ");
        assertEquals(2, result.size());
        assertTrue(result.contains("hello world"));
        assertTrue(result.contains("hello world again"));
    }

    @Test
    void main_NoMatches_ProducesEmptyOutput() {
        String input = "2\nFirst car\nSecond door\nkek\n";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(out));

        StringFiltering.main(new String[]{});

        assertEquals("", out.toString().trim());
    }

    @Test
    void main_SingleMatch_ProducesSingleOutput() {
        String input = "3\napple\nbanana\ncherry\nban\n";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(out));

        StringFiltering.main(new String[]{});

        assertEquals("banana", out.toString().trim());
    }

    // ========== ВСПОМОГАТЕЛЬНЫЙ МЕТОД ==========
    private String capturePrintedResult(List<String> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {
            StringFiltering.printResult(list);
        } finally {
            System.setOut(originalOut);
        }
        return out.toString().trim();
    }
}