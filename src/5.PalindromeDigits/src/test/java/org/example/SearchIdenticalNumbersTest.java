package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SearchIdenticalNumbersTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Тест метода equallyFirstLast с однозначными числами")
    void testEquallyFirstLastWithSingleDigitNumbers() {
        assertAll("Однозначные числа всегда true",
                () -> assertTrue(SearchIdenticalNumbers.equallyFirstLast(0)),
                () -> assertTrue(SearchIdenticalNumbers.equallyFirstLast(5)),
                () -> assertTrue(SearchIdenticalNumbers.equallyFirstLast(-7)),
                () -> assertTrue(SearchIdenticalNumbers.equallyFirstLast(9)),
                () -> assertTrue(SearchIdenticalNumbers.equallyFirstLast(-9))
        );
    }

    @ParameterizedTest
    @CsvSource({
            "11, true",
            "101, true",
            "202, true",
            "200005, false",
            "301213, true",
            "100, false",
            "200, false",
            "300, false",
            "400, false",
            "-101, true",
            "-11, true",
            "-100, false",
            "-123, false"
    })
    @DisplayName("Тест equallyFirstLast с многозначными числами")
    void testEquallyFirstLastWithMultiDigitNumbers(int number, boolean expected) {
        assertEquals(expected, SearchIdenticalNumbers.equallyFirstLast(number));
    }

    @Test
    @DisplayName("Тест equallyFirstLast с Integer.MIN_VALUE и MAX_VALUE")
    void testEquallyFirstLastWithExtremeValues() {
        assertAll("Экстремальные значения",
                // Integer.MIN_VALUE = -2147483648: первая цифра 2, последняя 8 → false
                () -> assertFalse(SearchIdenticalNumbers.equallyFirstLast(Integer.MIN_VALUE)),
                // Integer.MAX_VALUE = 2147483647: первая цифра 2, последняя 7 → false
                () -> assertFalse(SearchIdenticalNumbers.equallyFirstLast(Integer.MAX_VALUE)),
                // -2147483647: первая цифра 2, последняя 7 → false
                () -> assertFalse(SearchIdenticalNumbers.equallyFirstLast(-2147483647)),
                // -2147483642: первая цифра 2, последняя 2 → true
                () -> assertTrue(SearchIdenticalNumbers.equallyFirstLast(-2147483642)),
                // 2147483642: первая цифра 2, последняя 2 → true
                () -> assertTrue(SearchIdenticalNumbers.equallyFirstLast(2147483642))
        );
    }

    @ParameterizedTest
    @MethodSource("provideArraysForSearch")
    @DisplayName("Тест searchMatchingNumbers")
    void testSearchMatchingNumbers(int[] input, int[] expected) {
        assertArrayEquals(expected, SearchIdenticalNumbers.searchMatchingNumbers(input));
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> provideArraysForSearch() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{1, 202, 300, 200005, 301213},
                        new int[]{1, 202, 301213}
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{100, 200, 300, 400},
                        new int[]{}
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{},
                        new int[]{}
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{-101, -100, 0, 11, 12},
                        new int[]{-101, 0, 11}
                )
        );
    }

    @Test
    @DisplayName("Тест printResult с пустым массивом")
    void testPrintResultWithEmptyArray() {
        SearchIdenticalNumbers.printResult(new int[]{});
        assertTrue(outputStream.toString().contains("There are no such elements"));
    }

    @Test
    @DisplayName("Тест printResult с непустым массивом")
    void testPrintResultWithNonEmptyArray() {
        SearchIdenticalNumbers.printResult(new int[]{1, 202, 301213});
        assertTrue(outputStream.toString().contains("1 202 301213"));
    }

    @Test
    @DisplayName("Тест scanSizeArray с валидным вводом")
    void testScanSizeArrayWithValidInput() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("5\n".getBytes()));
        assertEquals(5, SearchIdenticalNumbers.scanSizeArray(scanner));
    }

    @Test
    @DisplayName("Тест scanSizeArray с нулём")
    void testScanSizeArrayWithZero() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("0\n".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> {
            SearchIdenticalNumbers.scanSizeArray(scanner);
        });
    }

    @Test
    @DisplayName("Тест scanSizeArray с отрицательным числом")
    void testScanSizeArrayWithNegativeNumber() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("-1\n".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> {
            SearchIdenticalNumbers.scanSizeArray(scanner);
        });
    }

    @Test
    @DisplayName("Тест scanSizeArray с некорректным вводом")
    void testScanSizeArrayWithInvalidInput() {
        String input = "abc\n5\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        assertEquals(5, SearchIdenticalNumbers.scanSizeArray(scanner));
        assertTrue(outputStream.toString().contains("Could not parse a number. Please, try again"));
    }

    @Test
    @DisplayName("Тест scanArrayNumbers с валидным вводом")
    void testScanArrayNumbersWithValidInput() {
        String input = "100 200 300 400\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        int[] result = SearchIdenticalNumbers.scanArrayNumbers(scanner, 4);
        assertArrayEquals(new int[]{100, 200, 300, 400}, result);
    }

    @Test
    @DisplayName("Тест scanArrayNumbers с некорректным вводом")
    void testScanArrayNumbersWithInvalidInput() {
        String input = "abc\n100\n200\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        int[] result = SearchIdenticalNumbers.scanArrayNumbers(scanner, 2);
        assertArrayEquals(new int[]{100, 200}, result);
        assertTrue(outputStream.toString().contains("Could not parse a number. Please, try again"));
    }

    @Test
    @DisplayName("Интеграционный тест main с пустым результатом")
    void testMainWithEmptyResult() {
        String input = "4\n100 200 300 400\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        SearchIdenticalNumbers.main(new String[]{});

        assertTrue(outputStream.toString().contains("There are no such elements"));
    }

    @Test
    @DisplayName("Интеграционный тест main с найденными числами")
    void testMainWithFoundNumbers() {
        String input = "5\n1 202 300 200005 301213\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        SearchIdenticalNumbers.main(new String[]{});

        assertTrue(outputStream.toString().contains("1 202 301213"));
    }

    @Test
    @DisplayName("Интеграционный тест main с некорректным размером")
    void testMainWithInvalidSize() {
        String input = "-1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        SearchIdenticalNumbers.main(new String[]{});

        assertTrue(outputStream.toString().contains("Input error. Size <= 0"));
    }
}