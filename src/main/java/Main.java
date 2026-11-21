import java.util.List;

public class Main {
    static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        EmployeeConverter converter = new EmployeeConverter();

        List<Employee> listCsv = converter.parseCSV(columnMapping, "data.csv");
        converter.writeString(converter.listToJson(listCsv), "data.json");

        List<Employee> listXml = converter.parseXML("data.xml");
        converter.writeString(converter.listToJson(listXml), "data2.json");

        String jsonNew = converter.readString("new_data.json");
        List<Employee> list = converter.jsonToList(jsonNew);
        for (Employee employee : list) {
            System.out.println(employee);
        }
    }
}
