package test;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YAMLParser {

	public static void main(String[] args) throws IOException {
		// Load the YAML file
		String yamlContent = new String(Files
				.readAllBytes(Paths.get("C:\\SivaTempFiles\\Testing\\generated\\absence.yaml")));

		// Parse the YAML content
		Yaml yaml = new Yaml();
		Map<String, Object> yamlMap = yaml.load(yamlContent);

		// Split the YAML content based on HTTP request methods
		Map<String, Object> paths = (Map<String, Object>) yamlMap.get("paths");
		for (Map.Entry<String, Object> path : paths.entrySet()) {
			String httpMethod = path.getValue().toString().split("=")[0].toUpperCase();
			String outputFileName = httpMethod + "_" + path.getKey().replaceAll("/", "_") + ".yaml";
			Map<String, Object> outputMap = new Yaml().load(yamlContent);
			Map<String, Object> pathMap = (Map<String, Object>) outputMap.get("paths");
			pathMap.clear();
			pathMap.put(path.getKey(), path.getValue());
			outputMap.put("paths", pathMap);

			// Write the output YAML file
			try (FileWriter writer = new FileWriter(new File(outputFileName))) {
				yaml.dump(outputMap, writer);
			}
		}
	}
}