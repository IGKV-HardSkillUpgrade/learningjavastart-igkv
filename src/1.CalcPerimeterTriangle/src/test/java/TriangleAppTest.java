import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TriangleAppEdgeTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    // ============= ТЕСТЫ МЕТОДА distanceTo() =============

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0, 0, 0.000",           // Одинаковые точки
            "1, 1, 1, 1, 0.000",           // Одинаковые точки с ненулевыми координатами
            "0, 0, 3, 4, 5.000",           // Стандартный случай
            "-1, -1, 2, 3, 5.000",          // Отрицательные координаты
            "1.5, 2.3, 4.7, 6.8, 5.522",    // Дробные координаты
            "0, 0, 1, 1, 1.414",            // Корень из 2
            "0, 0, 2, 0, 2.000",            // По горизонтали
            "0, 0, 0, 3, 3.000",            // По вертикали
            "-1.5, -2.5, 3.5, 4.5, 8.602",  // Смешанные координаты
            "0.001, 0.002, 0.003, 0.004, 0.003", // Очень маленькие числа
            "1000.5, 2000.3, 3000.7, 4000.2, 2828.498", // Большие числа
    })
    void testDistanceToPrecision(double x1, double y1, double x2, double y2, double expected) {
        double result = TriangleApp.distanceTo(x1, y1, x2, y2);
        // Округляем до 3 знаков для сравнения
        double roundedResult = Math.round(result * 1000.0) / 1000.0;
        assertEquals(expected, roundedResult, 0.001,
                "Расстояние между (" + x1 + "," + y1 + ") и (" + x2 + "," + y2 + ")");
    }

    // ============= ТЕСТЫ МЕТОДА isValidateTriangle() =============

    @ParameterizedTest
    @MethodSource("provideTriangleValidationCases")
    void testIsValidateTriangleEdgeCases(double a, double b, double c, boolean expected) {

        assertEquals(expected, TriangleApp.isValidateTriangle(a, b, c),
                "Треугольник со сторонами " + a + ", " + b + ", " + c);
    }

    static Stream<Arguments> provideTriangleValidationCases() {
        return Stream.of(
                // Граничные случаи неравенства треугольника
                Arguments.of(1.000, 1.000, 1.999, true),    // Чуть больше (валидный)
                Arguments.of(1.000, 1.000, 2.001, false),   // Чуть меньше (невалидный)
                Arguments.of(1.000, 1.000, 2.000, false),   // Равно (невалидный - вырожденный)

                // Нулевые и отрицательные стороны
                Arguments.of(0.000, 1.000, 1.000, false),   // Нулевая сторона
                Arguments.of(-1.000, 2.000, 2.000, false),  // Отрицательная сторона
                Arguments.of(0.000, 0.000, 0.000, false),   // Все стороны нулевые

                // Очень маленькие стороны
                Arguments.of(0.001, 0.001, 0.001, true),    // Микротреугольник
                Arguments.of(0.001, 0.001, 0.002, false),   // Сумма равна
                Arguments.of(0.001, 0.001, 0.0019, true),   // Чуть меньше суммы

                // Классические треугольники
                Arguments.of(3.000, 4.000, 5.000, true),    // Прямоугольный
                Arguments.of(5.000, 12.000, 13.000, true),  // Прямоугольный
                Arguments.of(2.000, 2.000, 3.000, true),    // Равнобедренный
                Arguments.of(2.000, 2.000, 4.000, false),   // Вырожденный равнобедренный
                Arguments.of(6.000, 8.000, 10.000, true),   // Удвоенный египетский

                // Соотношения сторон
                Arguments.of(3.500, 4.500, 5.500, true),    // Произвольный
                Arguments.of(7.500, 8.500, 9.500, true),    // Произвольный

                // Почти вырожденные
                Arguments.of(3.000, 4.000, 6.999, true),    // Почти вырожденный
                Arguments.of(3.000, 4.000, 7.001, false),   // Чуть за пределом
                Arguments.of(3.000, 4.000, 7.000, false)    // Ровно вырожденный
        );
    }

    // ============= ТЕСТЫ МЕТОДА trianglePerimeter() =============

    @ParameterizedTest
    @CsvSource({
            "1.000, 1.000, 1.000, 3.000",
            "3.000, 4.000, 5.000, 12.000",
            "0.000, 0.000, 0.000, 0.000",
            "0.100, 0.200, 0.300, 0.600",
            "2.500, 3.500, 4.500, 10.500",
            "1.414, 1.414, 2.000, 4.828",  // Равнобедренный
            "3.162, 4.123, 5.099, 12.384", // Корни из чисел
            "5.500, 6.500, 7.500, 19.500",
            "0.333, 0.333, 0.333, 0.999",  // Периодические дроби
            "2.236, 3.606, 4.472, 10.314", // Корни
    })
    void testTrianglePerimeterPrecision(double a, double b, double c, double expected) {
        double result = TriangleApp.trianglePerimeter(a, b, c);
        double roundedResult = Math.round(result * 1000.0) / 1000.0;
        assertEquals(expected, roundedResult, 0.001);
    }

    // ============= ТЕСТЫ MAIN МЕТОДА С ПРОВЕРКОЙ ФОРМАТА ВЫВОДА =============

    @Test
    void testMainOutputFormat() {
        String input = "0\n0\n3\n0\n0\n4\n";
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.matches(".*Perimeter: \\d+\\.\\d{3}.*") ||
                        output.matches(".*Perimeter: \\d+,\\d{3}.*"),
                "Вывод должен содержать периметр с 3 знаками после запятой");
    }

    @Test
    void testMainWithValidTriangle() {
        String input = "0\n0\n3\n0\n0\n4\n";
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Perimeter: 12.000") ||
                        output.contains("Perimeter: 12,000"),
                "Периметр прямоугольного треугольника 3-4-5 должен быть 12.000");
    }

    @Test
    void testMainWithEquilateralTriangle() {
        String input = "0\n0\n2\n0\n1\n1,732\n"; // Равносторонний со стороной 2
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Perimeter: 6.000") ||
                        output.contains("Perimeter: 6,000"),
                "Периметр равностороннего треугольника со стороной 2 должен быть 6.000");
    }

    @Test
    void testMainWithIsoscelesTriangle() {
        String input = "0\n0\n4\n0\n2\n3\n"; // Равнобедренный
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Perimeter: 11.211") ||
                        output.contains("Perimeter: 11,211"),
                "Периметр должен быть 11.211");
    }

    @Test
    void testMainWithPrecisionCheck() {
        // Треугольник со сторонами, дающими иррациональный периметр
        String input = "0\n0\n1\n0\n0,5\n0,866\n";
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        // Проверяем, что периметр выведен с 3 знаками
        assertTrue(output.matches(".*Perimeter: \\d+\\.\\d{3}.*") ||
                output.matches(".*Perimeter: \\d+,\\d{3}.*"));
    }

    @Test
    void testMainWithDecimalCoordinates() {
        String input = "1,5\n2,3\n4,7\n6,8\n3,2\n4,5\n";
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        // Проверяем, что вывод содержит числа с плавающей точкой
        assertTrue(output.contains(".") || output.contains(","));
    }

    @Test
    void testMainWithInvalidInput() {
        String input = "abc\n1\n2\n3\n4\n5\n6\n";
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Could not parse a number"));
    }

    @Test
    void testMainWithNonTriangle() {
        String input = "0\n0\n2\n0\n4\n0\n"; // Точки на одной прямой
        provideInput(input);

        TriangleApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("It's not a triangle"));
    }

    @Test
    void testMainWithMixedInput() {
        // Тест с восстановлением после неверного ввода
        String input = "abc\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n";
        provideInput(input);

        assertDoesNotThrow(() -> TriangleApp.main(new String[]{}));
    }

    @Test
    void testMainWithNegativeAndPositive() {
        String input = "-1,5\n-2,3\n3,7\n4,8\n-2,1\n5,4\n";
        provideInput(input);

        assertDoesNotThrow(() -> TriangleApp.main(new String[]{}));

        String output = outputStream.toString();
        assertTrue(output.contains("Perimeter:"));

        // Проверяем формат
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.contains("Perimeter:")) {
                assertTrue(line.matches(".*Perimeter: -?\\d+\\.\\d{3}.*") ||
                        line.matches(".*Perimeter: -?\\d+,\\d{3}.*"));
            }
        }
    }

    @Test
    void testAllMethodsWithPrecision() {
        // Интеграционный тест всех методов

        // Координаты равностороннего треугольника
        double x1 = 0, y1 = 0;
        double x2 = 2, y2 = 0;
        double x3 = 1, y3 = 1.7320508075688772; // √3

        double a = TriangleApp.distanceTo(x1, y1, x2, y2);
        double b = TriangleApp.distanceTo(x2, y2, x3, y3);
        double c = TriangleApp.distanceTo(x3, y3, x1, y1);

        // Проверяем, что стороны равны с точностью до 3 знаков
        a = Math.round(a * 1000.0) / 1000.0;
        b = Math.round(b * 1000.0) / 1000.0;
        c = Math.round(c * 1000.0) / 1000.0;

        assertEquals(2.000, a, 0.001);
        assertEquals(2.000, b, 0.001);
        assertEquals(2.000, c, 0.001);

        // Проверяем валидацию
        assertTrue(TriangleApp.isValidateTriangle(a, b, c));

        // Проверяем периметр
        double perimeter = TriangleApp.trianglePerimeter(a, b, c);
        perimeter = Math.round(perimeter * 1000.0) / 1000.0;
        assertEquals(6.000, perimeter, 0.001);
    }
}