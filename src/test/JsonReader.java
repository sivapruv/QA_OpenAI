package test;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;


public class JsonReader {

    public static HashMap<String, String> getMapFromJson(String filePath, String testCaseId) {

        String jsonFile = filePath;

        JSONParser jsonParser = new JSONParser();
        Object obj;
        HashMap<String, String> testCaseValues = new HashMap<String, String>();
        try {
            obj = jsonParser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            Object tc = jsonObject.get(testCaseId);
            // System.out.println(tc.toString().replace("{", "").replace("}",
            // "").replace("\"","").replace("\\",""));
			/*String[] abc = tc.toString().replace("\":\"", "\"::\"").replace("{", "").replace("}", "").replace("\"", "")
					.replace("\\", "").split(",");*/
            String[] abc = tc.toString().replace("\":\"", "\"::\"").replace("{", "").replace("}", "").replace("\\", "").split("\",\"");
            for (String a : abc) {
                String[] mapValues = a.split("::");
                if (mapValues.length > 1)
                    testCaseValues.put(mapValues[0].replace("\"", ""), mapValues[1].replace("\"", ""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return testCaseValues;
    }

	/*public static void main(String args[]){
		HashMap<String,String> abc = getMapFromJson("src/test/resources/testData/PPE_JSON/absence.json", "TC_36");

	}*/


    public static void abc(String[] args) {
        getMapFromJson("src/test/resources/testData/PPE_JSON/CoreHrData.json", "TC_03");
        String jsonFilePath = "src/test/resources/testData/PPE_JSON";
        File folder = new File(jsonFilePath);
        File[] jsonFiles = folder.listFiles();
        for (File jsonFile : jsonFiles) {

            Scanner file;
            Scanner file_2;
            PrintWriter writer;

            try {

                file = new Scanner(jsonFile);
                writer = new PrintWriter(jsonFilePath + jsonFile.getName() + "new");

                while (file.hasNext()) {
                    String line = file.nextLine();
                    if (!line.isEmpty() && !line.contains("\"\"")) {

                        writer.write(line);
                        writer.write("\n");
                    }
                }

                file.close();
                writer.close();

            } catch (FileNotFoundException ex) {

            }

            try {

                file = new Scanner(new File(jsonFilePath + jsonFile.getName() + "new"));
                file_2 = new Scanner(new File(jsonFilePath + jsonFile.getName() + "new"));
                writer = new PrintWriter(jsonFilePath + jsonFile.getName() + "new2");
                if (file_2.hasNextLine())
                    file_2.nextLine();
                if (file.hasNextLine())

                    while (file.hasNext() && file_2.hasNextLine()) {
                        String line = file.nextLine();
                        String line_2 = file_2.nextLine();
                        if (line_2.contains("}")) {

                            if (line.contains(","))
                                line = line.substring(0, line.length() - 1);
                            writer.write(line);
                            writer.write("\n");
                        } else {
                            writer.write(line);
                            writer.write("\n");
                        }

                    }

                writer.write(file.next());
                writer.write("\n");

                file.close();
                writer.close();

            } catch (FileNotFoundException ex) {

            }
        }
    }

	/*public static void main(String args[]){
		JsonReader.updateJsonFileValue("C:\\Users\\vagrant\\git\\CTP_PayrollAutomation\\src\\test\\resources\\testData\\PPE_JSON\\absence.json", "TC_19", "absenceStartdate", "testDate");
	}*/

    /**
     * @author Siva Prasad U V
     */
    @SuppressWarnings("unchecked")
    public static void updateJsonFileValue(String filePath, String testCaseId, String fieldName, String value) {

        String jsonFile = filePath;

        JSONParser jsonParser = new JSONParser();
        Object obj;
        HashMap<String, String> testCaseValues = new HashMap<String, String>();
        try {
            obj = jsonParser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;

            Object tc = jsonObject.get(testCaseId);

            String[] abc = tc.toString().replace("\":\"", "\"::\"").replace("{", "").replace("}", "").replace("\\", "")
                    .split("\",\"");
            for (String a : abc) {
                String[] mapValues = a.split("::");
                if (mapValues.length > 1)
                    testCaseValues.put(mapValues[0].replace("\"", ""), mapValues[1].replace("\"", ""));
            }
            try {
                if (!value.equals(null)) {
                    testCaseValues.replace(fieldName, value);
                }
            } catch (NullPointerException e) {
                testCaseValues.replace(fieldName, "");
            }

            jsonObject.replace(testCaseId, testCaseValues);
            // jsonObject.put(testCaseId,testCaseValues);

            FileWriter fileWriter = new FileWriter(filePath);

            fileWriter.write(jsonObject.toJSONString());
            fileWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
