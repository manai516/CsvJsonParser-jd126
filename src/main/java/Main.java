import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        List<Employee> listCsv = parseCSV(columnMapping, "data.csv");
        String json = listToJson(listCsv);
        writeString(json, "data.json");

        List<Employee> listXml = parseXML("data.xml");
        String json2 = listToJson(listXml);
        writeString(json2, "data2.json");
    }

    private static List<Employee> parseCSV (String[] columnMapping, String fileName) {
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать" + fileName, e);
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось записать" + fileName, e);
        }
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fileName);

            Node root = document.getDocumentElement();
            NodeList employeeNodes = root.getChildNodes();

            for (int i = 0; i < employeeNodes.getLength(); i++) {
                Node node = employeeNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    employees.add(new Employee(
                            Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось разобрать " + fileName, e);
        }
        return employees;
    }

}
