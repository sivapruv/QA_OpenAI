package test;



import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlSplitter {

	public static void main(String[] args) throws IOException {
		String inputFilePath = "input.yaml";
		String outputDirectoryPath = "output";
		String key = "method"; // Split based on the value of this key

		// Create output directory if it doesn't exist
		File outputDirectory = new File(outputDirectoryPath);
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}

		// Read input YAML file
		Map<String, Object> inputYaml = readYamlFile(inputFilePath);

		// Split YAML into multiple files based on the value of the key
		Map<Object, List<Map<String, Object>>> splitYaml = splitYaml(inputYaml, key);

		// Write each split YAML to a separate file
		for (Map.Entry<Object, List<Map<String, Object>>> entry : splitYaml.entrySet()) {
			String outputFilePath = outputDirectoryPath + "/" + entry.getKey() + ".yaml";
			writeYamlFile(entry.getValue(), outputFilePath);
		}
	}

	private static Map<String, Object> readYamlFile(String filePath) throws IOException {
		InputStream inputStream = new FileInputStream(filePath);
		Yaml yaml = new Yaml();
		return yaml.load(inputStream);
	}

	private static void writeYamlFile(Object data, String filePath) throws IOException {
		Writer writer = new FileWriter(filePath);
		Yaml yaml = new Yaml();
		yaml.dump(data, writer);
	}

	private static Map<Object, List<Map<String, Object>>> splitYaml(Map<String, Object> yamlData, String key) {
		Map<Object, List<Map<String, Object>>> splitYaml = new HashMap<>();

		List<Map<String, Object>> httpCalls = (List<Map<String, Object>>) yamlData.get(key);
		for (Map<String, Object> httpCall : httpCalls) {
			Object splitKey = httpCall.get(key);
			if (!splitYaml.containsKey(splitKey)) {
				splitYaml.put(splitKey, new ArrayList<Map<String, Object>>());
			}
			splitYaml.get(splitKey).add(httpCall);
		}

		return splitYaml;
	}

}