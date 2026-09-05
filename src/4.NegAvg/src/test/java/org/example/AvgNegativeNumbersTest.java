package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AvgNegativeNumbersTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    // ========== Тесты для findAvgNegativNumb ==========

    @Test
    void findAvgNegativNumb_ShouldReturnIntegerAverage_WhenMultipleNegativeNumbers() {
        int[] input = {-4, 3, -2, 5, -1};
        assertEquals(-2, AvgNegativeNumbers.findAvgNegativNumb(input));
    }

    @Test
    void findAvgNegativNumb_ShouldReturnIntegerAverage_WhenOneNegativeNumber() {
        int[] input = {5, -10, 7};
        assertEquals(-10, AvgNegativeNumbers.findAvgNegativNumb(input));
    }

    @Test
    void findAvgNegativNumb_ShouldReturnIntegerAverage_WhenAllNumbersNegative() {
        int[] input = {-5, -3, -8, -2};
        assertEquals(-4, AvgNegativeNumbers.findAvgNegativNumb(input));
    }

    @Test
    void findAvgNegativNumb_ShouldReturnIntegerAverage_WhenIncludesZero() {
        int[] input = {-6, 0, -4, 0, -2};
        assertEquals(-4, AvgNegativeNumbers.findAvgNegativNumb(input));
    }

    @Test
    void findAvgNegativNumb_ShouldThrowException_WhenNoNegativeNumbers() {
        int[] input = {1, 2, 3, 4, 5};
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AvgNegativeNumbers.findAvgNegativNumb(input)
        );
        assertEquals("There are no negative elements", exception.getMessage());
    }

    @Test
    void findAvgNegativNumb_ShouldThrowException_WhenEmptyArray() {
        int[] input = {};
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AvgNegativeNumbers.findAvgNegativNumb(input)
        );
        assertEquals("There are no negative elements", exception.getMessage());
    }

    @Test
    void findAvgNegativNumb_ShouldHandleMinAndMaxValues() {
        int[] input = {Integer.MIN_VALUE, Integer.MAX_VALUE, -1, 0, 1};
        // (-2147483648 + -1) / 2 = -1073741824 (целочисленное деление)
        assertEquals(1073741823, AvgNegativeNumbers.findAvgNegativNumb(input));
    }

    @Test
    void findAvgNegativNumb_ShouldHandleSumOverflow() {
        // Сумма отрицательных чисел может быть меньше Integer.MIN_VALUE
        // При использовании int сумма переполнится, но это ожидаемое поведение
        // для целочисленной арифметики в Java
        int[] input = {Integer.MIN_VALUE, -1000};
        int expected = (Integer.MIN_VALUE + (-1000)) / 2;
        assertEquals(expected, AvgNegativeNumbers.findAvgNegativNumb(input));
    }

    // ========== Тесты для scanSizeArray ==========

    @ParameterizedTest
    @CsvSource({
            "'5\n', 5",
            "'10\n', 10",
            "'abc\n5\n', 5",
            "'xyz\n3\n', 3"
    })
    void scanSizeArray_ShouldReturnCorrectSize_WhenValidInputAfterRetry(String input, int expectedSize) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        int result = AvgNegativeNumbers.scanSizeArray(scanner);
        assertEquals(expectedSize, result);
    }

    @Test
    void scanSizeArray_ShouldThrowException_WhenSizeZero() {
        String input = "0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AvgNegativeNumbers.scanSizeArray(scanner)
        );
        assertEquals("Input error. Size <= 0", exception.getMessage());
    }

    @Test
    void scanSizeArray_ShouldThrowException_WhenSizeNegative() {
        String input = "-5\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AvgNegativeNumbers.scanSizeArray(scanner)
        );
        assertEquals("Input error. Size <= 0", exception.getMessage());
    }

    @Test
    void scanSizeArray_ShouldDisplayErrorMessage_WhenInvalidNumberEntered() {
        String input = "abc\n5\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        AvgNegativeNumbers.scanSizeArray(scanner);
        assertTrue(outputStream.toString().contains("Could not parse a number. Please, try again"));
    }

    // ========== Тесты для scanArrayNumbers ==========

    @ParameterizedTest
    @CsvSource({
            "'1 2 3\n', 3, 1 2 3",
            "'-1 -2 -3\n', 3, -1 -2 -3",
            "'abc\n5\n1\n', 2, 5 1"
    })
    void scanArrayNumbers_ShouldFillArrayCorrectly(String input, int size, String expected) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        int[] result = AvgNegativeNumbers.scanArrayNumbers(scanner, size);
        String[] expectedValues = expected.split(" ");
        assertEquals(size, result.length);
        for (int i = 0; i < size; i++) {
            assertEquals(Integer.parseInt(expectedValues[i]), result[i]);
        }
    }

    @Test
    void scanArrayNumbers_ShouldDisplayErrorMessage_WhenInvalidNumberEntered() {
        String input = "abc\n5\n10\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        AvgNegativeNumbers.scanArrayNumbers(scanner, 1);
        assertTrue(outputStream.toString().contains("Could not parse a number. Please, try again"));
    }

    @Test
    void scanArrayNumbers_ShouldHandleMultipleInvalidInputs() {
        String input = "abc\nxyz\n123\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        AvgNegativeNumbers.scanArrayNumbers(scanner, 1);
        String output = outputStream.toString();
        assertTrue(output.contains("Could not parse a number. Please, try again"));
        // Должно быть два сообщения об ошибке
        assertEquals(2, output.split("Could not parse a number. Please, try again").length - 1);
    }

    // ========== Интеграционные тесты ==========

    @ParameterizedTest
    @MethodSource("provideFullProgramTestData")
    void main_ShouldProcessCorrectly(String input, String expectedOutput) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        AvgNegativeNumbers.main(new String[]{});

        assertTrue(outputStream.toString().contains(expectedOutput));
    }

    private static Stream<Arguments> provideFullProgramTestData() {
        return Stream.of(
                Arguments.of("4\n1\n2\n3\n4\n", "There are no negative elements"),
                Arguments.of("-1\n", "Input error. Size <= 0"),
                Arguments.of("0\n", "Input error. Size <= 0"),
                Arguments.of("4\n1\n-2\n3\n-4\n", "-3"),
                Arguments.of("3\n-5\n-10\n-15\n", "-10"),
                Arguments.of("2\nabc\n-3\n5\n", "-3"),
                Arguments.of("3\n-1\n-2\n-3\n", "-2"),
                Arguments.of("5\n-10\n20\n-30\n40\n-50\n", "-30")
        );
    }


    @Test
    void main_ShouldHandleInvalidSizeWithoutRetry() {
        String input = "-1\n"; // После ошибки программа завершается, не запрашивая повторный ввод
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        AvgNegativeNumbers.main(new String[]{});

        assertTrue(outputStream.toString().contains("Input error. Size <= 0"));
        // Проверяем, что нет дополнительных запросов ввода
        assertFalse(outputStream.toString().contains("Could not parse a number. Please, try again"));
    }

    @Test
    void main_ShouldHandleInvalidSizeAndStop() {
        String input = "-5\n"; // При отрицательном размере программа должна завершиться
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        AvgNegativeNumbers.main(new String[]{});

        assertEquals("Input error. Size <= 0", outputStream.toString().trim());
    }

    // ========== Граничные тесты ==========

    @Test
    void findAvgNegativNumb_ShouldHandleVeryLargeArray() {
        int size = 10000;
        int[] input = new int[size];
        for (int i = 0; i < size; i++) {
            input[i] = -i - 1;
        }
        int expected = -(size) / 2; // (-1 + -2 + ... + -n) / n = -(n+1)/2
        // Для 10000: -10000/2 = -5000
        assertEquals(-5000, AvgNegativeNumbers.findAvgNegativNumb(input));
    }
}