package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchAdultUsersTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ==================== ТЕСТЫ ДЛЯ scanUsers() ====================

    @Test
    void scanUsers_shouldAddAllUsersWithAgeGreaterThanZero() {
        String input = "4\nAlice\n25\nBob\n30\nCharlie\n22\nDavid\n28\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(4, users.size());
        assertAll("All users with age > 0 should be added",
                () -> assertEquals("Alice", users.get(0).getName()),
                () -> assertEquals(25, users.get(0).getAge()),
                () -> assertEquals("Bob", users.get(1).getName()),
                () -> assertEquals(30, users.get(1).getAge()),
                () -> assertEquals("Charlie", users.get(2).getName()),
                () -> assertEquals(22, users.get(2).getAge()),
                () -> assertEquals("David", users.get(3).getName()),
                () -> assertEquals(28, users.get(3).getAge())
        );
    }

    @Test
    void scanUsers_shouldAddUnderageUsersToo() {
        String input = "3\nAlice\n16\nBob\n17\nCharlie\n15\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(3, users.size());
        assertAll("Underage users should also be in list",
                () -> assertEquals("Alice", users.get(0).getName()),
                () -> assertEquals(16, users.get(0).getAge()),
                () -> assertEquals("Bob", users.get(1).getName()),
                () -> assertEquals(17, users.get(1).getAge()),
                () -> assertEquals("Charlie", users.get(2).getName()),
                () -> assertEquals(15, users.get(2).getAge())
        );
    }

    @Test
    void scanUsers_shouldSkipNegativeAgeAndRetry() {
        String input = "1\nAlice\n-5\nBob\n20\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(1, users.size());
        assertEquals("Bob", users.get(0).getName());
        assertEquals(20, users.get(0).getAge());
        assertTrue(outContent.toString().contains("Incorrect input. Age <= 0"));
    }

    @Test
    void scanUsers_shouldSkipZeroAgeAndRetry() {
        String input = "1\nAlice\n0\nBob\n20\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(1, users.size());
        assertEquals("Bob", users.get(0).getName());
        assertEquals(20, users.get(0).getAge());
        assertTrue(outContent.toString().contains("Incorrect input. Age <= 0"));
    }

    @Test
    void scanUsers_shouldSkipMultipleNegativeAges() {
        String input = "1\nAlice\n-5\nBob\n-10\nCharlie\n25\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(1, users.size());
        assertEquals("Charlie", users.get(0).getName());
        assertEquals(25, users.get(0).getAge());

        String output = outContent.toString();
        assertTrue(output.contains("Incorrect input. Age <= 0"));
        assertEquals(2, output.split("Incorrect input. Age <= 0").length - 1);
    }

    @Test
    void scanUsers_shouldHandleInvalidNumberFormat() {
        String input = "2\nAlice\nabc\n25\nBob\n30\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(2, users.size());
        assertTrue(outContent.toString().contains("Could not parse a number. Please, try again"));
    }

    @Test
    void scanUsers_shouldHandleMultipleInvalidNumberFormats() {
        String input = "2\nAlice\nxyz\nabc\n25\nBob\n30\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(2, users.size());
        String output = outContent.toString();
        assertTrue(output.contains("Could not parse a number. Please, try again"));
    }

    @Test
    void scanUsers_shouldHandleInvalidNumberAndNegativeAge() {
        String input = "2\nAlice\nxyz\n25\nBob\n-5\nBob\n30\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(2, users.size());
        String output = outContent.toString();
        assertAll("Should handle both errors",
                () -> assertTrue(output.contains("Could not parse a number. Please, try again")),
                () -> assertTrue(output.contains("Incorrect input. Age <= 0"))
        );
    }

    @Test
    void scanUsers_shouldHandleNamesWithSpaces() {
        String input = "2\nJohn Doe\n25\nJane Smith\n30\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(2, users.size());
        assertAll("Names with spaces should be preserved",
                () -> assertEquals("John Doe", users.get(0).getName()),
                () -> assertEquals("Jane Smith", users.get(1).getName())
        );
    }

    @Test
    void scanUsers_shouldHandleBoundaryAges() {
        String input = "4\nAlice\n17\nBob\n18\nCharlie\n19\nDavid\n100\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(4, users.size());
        assertAll("All ages > 0 should be added",
                () -> assertEquals("Alice", users.get(0).getName()),
                () -> assertEquals(17, users.get(0).getAge()),
                () -> assertEquals("Bob", users.get(1).getName()),
                () -> assertEquals(18, users.get(1).getAge()),
                () -> assertEquals("Charlie", users.get(2).getName()),
                () -> assertEquals(19, users.get(2).getAge()),
                () -> assertEquals("David", users.get(3).getName()),
                () -> assertEquals(100, users.get(3).getAge())
        );
    }

    @Test
    void scanUsers_shouldHandleMaximumAge() {
        String input = "1\nMaxAge\n2147483647\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(1, users.size());
        assertEquals("MaxAge", users.get(0).getName());
        assertEquals(2147483647, users.get(0).getAge());
    }

    @Test
    void scanUsers_shouldReturnEmptyListWhenNoUsers() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertTrue(users.isEmpty());
    }

    @Test
    void scanUsers_shouldRetryOnNegativeAgeUntilValid() {
        String input = "1\nAlice\n-5\nAlice\n-10\nAlice\n25\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals(25, users.get(0).getAge());

        String output = outContent.toString();
        assertEquals(2, output.split("Incorrect input. Age <= 0").length - 1);
    }

    @Test
    void scanUsers_shouldRetryOnInvalidNumberUntilValid() {
        String input = "1\nAlice\nabc\nxyz\n25\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals(25, users.get(0).getAge());

        String output = outContent.toString();
        assertTrue(output.contains("Could not parse a number. Please, try again"));
    }

    // ==================== ТЕСТЫ ДЛЯ main() ====================

    @Test
    void main_shouldPrintOnlyAdultUsers() {
        String input = "4\nAlice\n17\nBob\n18\nCharlie\n19\nDavid\n100\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("Bob, Charlie, David", outContent.toString().trim());
    }

    @Test
    void main_shouldPrintAllAdultUsers() {
        String input = "3\nAlice\n25\nBob\n30\nCharlie\n22\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("Alice, Bob, Charlie", outContent.toString().trim());
    }

    @Test
    void main_shouldPrintEmptyStringWhenNoAdultUsers() {
        String input = "3\nAlice\n16\nBob\n17\nCharlie\n15\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("", outContent.toString().trim());
    }

    @Test
    void main_shouldPrintOnlyAdultWhenMixedAges() {
        String input = "5\nAlice\n25\nBob\n16\nCharlie\n18\nDavid\n14\nEve\n20\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("Alice, Charlie, Eve", outContent.toString().trim());
    }

    @Test
    void main_shouldHandleBoundaryAge18() {
        String input = "2\nYoung\n18\nOld\n17\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("Young", outContent.toString().trim());
    }

    @Test
    void main_shouldHandleSingleAdultUser() {
        String input = "1\nSolo\n25\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("Solo", outContent.toString().trim());
    }

    @Test
    void main_shouldHandleSingleUnderageUser() {
        String input = "1\nYoung\n16\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("", outContent.toString().trim());
    }

    @Test
    void main_shouldHandleEmptyList() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals("", outContent.toString().trim());
    }

    @Test
    void main_shouldHandleNegativeAgeAndStillFilterCorrectly() {
        String input = "2\nAlice\n-5\nAlice\n25\nBob\n30\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        // Alice (25) и Bob (30) - оба совершеннолетние
        assertTrue(outContent.toString().contains("Alice, Bob"));
    }

    // ==================== ПАРАМЕТРИЗОВАННЫЕ ТЕСТЫ ====================

    @ParameterizedTest
    @CsvSource({
            "'2\nAlice\n25\nBob\n30\n', 'Alice, Bob'",
            "'3\nAlice\n25\nBob\n16\nCharlie\n18\n', 'Alice, Charlie'",
            "'3\nAlice\n16\nBob\n17\nCharlie\n30\n', 'Charlie'",
            "'1\nAlice\n18\n', 'Alice'",
            "'1\nAlice\n17\n', ''",
            "'4\nAlice\n17\nBob\n18\nCharlie\n19\nDavid\n20\n', 'Bob, Charlie, David'"
    })
    void main_shouldPrintCorrectAdultNames(String input, String expectedOutput) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();

        SearchAdultUsers.main(new String[]{});

        assertEquals(expectedOutput, outContent.toString().trim());
    }

    @ParameterizedTest
    @CsvSource({
            "1, Alice, 18, true",
            "1, Bob, 17, true",
            "1, Charlie, 16, true",
            "1, David, 0, false",
            "1, Eve, -5, false"
    })
    void scanUsers_shouldHandleVariousAges(int count, String name, int age, boolean shouldBeAdded) {
        String input = count + "\n" + name + "\n" + age + "\n";
        if (age <= 0) {
            input += name + "\n25\n";
        }
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        if (shouldBeAdded) {
            assertEquals(1, users.size());
            assertEquals(name, users.get(0).getName());
            assertEquals(age, users.get(0).getAge());
        } else {
            // При age <= 0 пользователь НЕ добавляется, после retry добавляется с 25
            assertEquals(1, users.size());
            assertEquals(name, users.get(0).getName());
            assertEquals(25, users.get(0).getAge());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'2\nAlice\n25\nBob\n30\n', 2",
            "'3\nAlice\n16\nBob\n17\nCharlie\n15\n', 3",
            "'1\nAlice\n-5\nBob\n20\n', 1",
            "'1\nAlice\n0\nAlice\n25\n', 1"
    })
    void scanUsers_shouldReturnCorrectNumberOfUsers(String input, int expectedSize) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        List<User> users = SearchAdultUsers.scanUsers();

        assertEquals(expectedSize, users.size());
    }
}