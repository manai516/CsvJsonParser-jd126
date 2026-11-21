import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeConverterTest {
    private final EmployeeConverter converter = new EmployeeConverter();

    @Test
    void testListToJson() {
        // given
        List<Employee> list = List.of(
                new Employee(1, "John", "Smith", "USA", 25),
                new Employee(2, "Inav", "Petrov", "RU", 23)
        );

        // when
        String json = converter.listToJson(list);

        // then
        String expected = """
                [
                  {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Smith",
                    "country": "USA",
                    "age": 25
                  },
                  {
                    "id": 2,
                    "firstName": "Inav",
                    "lastName": "Petrov",
                    "country": "RU",
                    "age": 23
                  }
                ]""";
        assertEquals(expected, json);
    }

    @Test
    void testJsonToList() {
        // given
        String json = """
                [
                  {"id": 1, "firstName": "John", "lastName": "Smith", "country": "USA", "age": 25},
                  {"id": 2, "firstName": "Inav", "lastName": "Petrov", "country": "RU", "age": 23}
                ]""";

        // when
        List<Employee> list = converter.jsonToList(json);

        // then
        assertEquals(2, list.size());
        Employee first = list.get(0);
        assertEquals(1, first.id);
        assertEquals("John", first.firstName);
        assertEquals(25, first.age);
        Employee second = list.get(1);
        assertEquals("RU", second.country);
        assertEquals(23, second.age);
    }

    @Test
    void testParseXml(@TempDir Path tempDir) throws IOException {
        // given
        String xml = """
                <staff>
                    <employee>
                        <id>10</id>
                        <firstName>Anna</firstName>
                        <lastName>Ivanova</lastName>
                        <country>KZ</country>
                        <age>30</age>
                    </employee>
                </staff>""";
        Path file = tempDir.resolve("test.xml");
        Files.writeString(file, xml);

        // when
        List<Employee> list = converter.parseXML(file.toString());

        // then
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).id);
        assertEquals("Anna", list.get(0).firstName);
        assertEquals("KZ", list.get(0).country);
        assertEquals(30, list.get(0).age);
    }

    @Test
    void testParseCsv(@TempDir Path tempDir) throws IOException {
        // given
        Path file = tempDir.resolve("test.csv");
        Files.writeString(file, "10,Anna,Ivanova,KZ,30");

        // when
        List<Employee> list = converter.parseCSV(
                new String[]{"id", "firstName", "lastName", "country", "age"},
                file.toString()
        );

        // then
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).id);
        assertEquals("Anna", list.get(0).firstName);
        assertEquals("KZ", list.get(0).country);
        assertEquals(30, list.get(0).age);
    }
}

