import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TimeTranslateAppTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    // ---------- Тесты timeTranslate ----------

    @ParameterizedTest
    @CsvSource({
            "0, '00:00:00'",
            "1, '00:00:01'",
            "59, '00:00:59'",
            "60, '00:01:00'",
            "61, '00:01:01'",
            "3599, '00:59:59'",
            "3600, '01:00:00'",
            "3601, '01:00:01'",
            "86399, '23:59:59'",
            "86400, '24:00:00'",
            "90061, '25:01:01'",
            "2147483647, '596523:14:07'"
    })
    @DisplayName("timeTranslate: корректный перевод секунд в hh:mm:ss")
    void timeTranslate_shouldReturnCorrectFormat(int seconds, String expected) {
        assertEquals(expected, TimeTranslateApp.timeTranslate(seconds));
    }

    // ---------- Тесты readSeconds ----------

    @ParameterizedTest
    @ValueSource(strings = {"123", "0", "-5", "999999"})
    @DisplayName("readSeconds: корректное чтение числа")
    void readSeconds_shouldReadValidInteger(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        int expected = Integer.parseInt(input);

        int result = TimeTranslateApp.readSeconds(scanner);

        assertEquals(expected, result);
        scanner.close();
    }

    @Test
    @DisplayName("readSeconds: повторный запрос при некорректном вводе, затем успех")
    void readSeconds_shouldRetryOnInvalidInput_thenSucceed() {
        String input = "abc\n42\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = TimeTranslateApp.readSeconds(scanner);

        assertEquals(42, result);
        assertTrue(outputStream.toString().contains("Could not parse a number. Please, try again"));
        scanner.close();
    }

    @Test
    @DisplayName("readSeconds: несколько некорректных вводов подряд")
    void readSeconds_shouldRetryMultipleTimes() {
        String input = "abc\ndef\nghi\n7\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = TimeTranslateApp.readSeconds(scanner);

        assertEquals(7, result);
        String output = outputStream.toString();
        long errorCount = output.lines()
                .filter(line -> line.equals("Could not parse a number. Please, try again"))
                .count();
        assertEquals(3, errorCount);
        scanner.close();
    }

    // ---------- Тесты printNewTimeFormat ----------

    @Test
    @DisplayName("printNewTimeFormat: корректный вывод строки времени")
    void printNewTimeFormat_shouldPrintTime() {
        TimeTranslateApp.printNewTimeFormat("01:02:03");

        assertEquals("01:02:03", outputStream.toString());
    }

    @Test
    @DisplayName("printNewTimeFormat: вывод пустой строки не падает")
    void printNewTimeFormat_shouldHandleEmptyString() {
        assertDoesNotThrow(() -> TimeTranslateApp.printNewTimeFormat(""));
        assertEquals("", outputStream.toString());
    }

    // ---------- Интеграционные тесты main ----------

    @Test
    @DisplayName("main: корректный ввод положительного числа")
    void main_shouldPrintFormattedTime() {
        String input = "3601\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        TimeTranslateApp.main(new String[]{});

        assertEquals("01:00:01", outputStream.toString().trim());
    }

    @Test
    @DisplayName("main: отрицательное число → Incorrect time")
    void main_shouldPrintIncorrectTimeForNegative() {
        String input = "-100\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        TimeTranslateApp.main(new String[]{});

        assertEquals("Incorrect time", outputStream.toString().trim());
    }

    @Test
    @DisplayName("main: некорректный ввод → сообщение и повтор")
    void main_shouldRetryOnInvalidInput() {
        String input = "abc\n59\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        TimeTranslateApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Could not parse a number. Please, try again"));
        assertTrue(output.contains("00:00:59"));
    }

    @Test
    @DisplayName("main: ноль → 00:00:00")
    void main_shouldHandleZero() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        TimeTranslateApp.main(new String[]{});

        assertEquals("00:00:00", outputStream.toString().trim());
    }

    @Test
    @DisplayName("main: Integer.MAX_VALUE")
    void main_shouldHandleMaxInteger() {
        String input = Integer.MAX_VALUE + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        assertDoesNotThrow(() -> TimeTranslateApp.main(new String[]{}));
        assertFalse(outputStream.toString().isEmpty());
    }

    @Test
    @DisplayName("main: Integer.MIN_VALUE → Incorrect time (отрицательное)")
    void main_shouldHandleMinIntegerAsNegative() {
        String input = Integer.MIN_VALUE + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        TimeTranslateApp.main(new String[]{});

        assertEquals("Incorrect time", outputStream.toString().trim());
    }
}