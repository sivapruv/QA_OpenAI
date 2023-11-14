package test;

import io.swagger.oas.models.*;
import io.swagger.parser.v3.OpenAPIV3Parser;
import io.swagger.core.filter.OpenAPISpecFilter;
import io.swagger.oas.models.media.ArraySchema;
import io.swagger.oas.models.media.Schema;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.oas.models.PathItem;
import io.swagger.parser.models.ParseOptions;
import io.swagger.util.Yaml;
import joptsimple.internal.Strings;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class iSplit {
    private static final String schemaComponentString = "#/components/schemas/";
    private static final int schemaComponentStringLength = schemaComponentString.length();

    public static void main(String[] args) throws Exception {

        String inputFile = "C:\\CodeBase\\PayslipQA\\Cy\\absence.yaml";
        String outputFolder = "C:\\CodeBase\\PayslipQA\\Cy\\Logss\\";

        if (args.length >= 2) {
            inputFile = args[0];
            outputFolder = args[1];
        }

        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);

        OpenAPI openAPI = new OpenAPIV3Parser().read(inputFile, null, parseOptions);

        List<String> paths = openAPI.getPaths().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());

        Map<String, OpenAPI> openAPIs = new HashMap<>();

        for (String path: paths)
        {
            OpenAPI o = new OpenAPI();
            String openApiVar = openAPI.getOpenapi();
            o.setOpenapi(openApiVar);
            o.setInfo(openAPI.getInfo());
            o.setServers(openAPI.getServers());
            o.setSecurity(openAPI.getSecurity());
            o.setExternalDocs(openAPI.getExternalDocs());
            o.setExtensions(openAPI.getExtensions());

            Optional<Map.Entry<String, PathItem>> opt = openAPI.getPaths().entrySet().stream().filter((e) -> e.getKey().toString().equals(path)).findFirst();

            if (opt.isPresent()) {
                o.setPaths(new Paths().addPathItem(opt.get().getKey(), opt.get().getValue()));
            } else {
                o.setPaths(new Paths().addPathItem(opt.get().getKey(), opt.get().getValue()));
            }
            openAPIs.put(path, o);
        }

        Map<String, Set<String>> refsMap = new HashMap<>();  // ref Map to path mapping

        for (Map.Entry<String, PathItem> pie : openAPI.getPaths().entrySet()) {
            String pathName = pie.getKey();
            PathItem pi = pie.getValue();

            processOperation(pathName, pi, pi.getGet(), openAPIs, "Get", refsMap);
            processOperation(pathName, pi, pi.getPost(), openAPIs, "Post", refsMap);
            processOperation(pathName, pi, pi.getPut(), openAPIs, "Put", refsMap);
            processOperation(pathName, pi, pi.getDelete(), openAPIs, "Delete", refsMap);
            processOperation(pathName, pi, pi.getOptions(), openAPIs, "Options", refsMap);
            processOperation(pathName, pi, pi.getHead(), openAPIs, "Head", refsMap);
            processOperation(pathName, pi, pi.getPatch(), openAPIs, "Patch", refsMap);
            processOperation(pathName, pi, pi.getTrace(), openAPIs, "Trace", refsMap);
        }

        // 4. parse the components and map them to the respective openAPI object
        Components components = openAPI.getComponents();

        // 4a. map schema of components
        for (Map.Entry<String, Schema> s : components.getSchemas().entrySet()) {
            dereferenceSchemaObject(s.getKey(), components.getSchemas(), refsMap);
        }

        for (Map.Entry<String, Schema> s : components.getSchemas().entrySet()) {
            Set<String> pathsMapped;

            if ((pathsMapped = refsMap.get(schemaComponentString + s.getKey())) != null) {
                pathsMapped.forEach(path -> {
                    OpenAPI oa = openAPIs.get(path);
                    Components c = oa.getComponents();

                    if (c == null) {
                        c = new Components();
                    }
                    c.addSchemas(s.getKey(), s.getValue());
                    oa.setComponents(c);
                    openAPIs.put(path, oa);
                });
            }
        }

        for (Map.Entry<String, OpenAPI> e : openAPIs.entrySet()) {

            validateAndWriteOutputFile(e.getKey().replace(" ", "-") + "-Splitted.yml",
                    outputFolder,
                    Yaml.pretty().writeValueAsString(e.getValue()));
        }
    }

    private static void dereferenceSchemaObject(String currRef,
                            Map<String, Schema> schemas,
                            Map<String, Set<String>> refsMap) {

        Schema currSchema;
        if ((currSchema = schemas.get(currRef)) != null && currSchema.getProperties() != null) {
            Map<String, Schema> props = (Map<String, Schema>) currSchema.getProperties();

            Set<String> currPaths;
            if ((currPaths = refsMap.get(schemaComponentString + currRef)) != null) {

                for (Map.Entry<String, Schema> propSchemaPair : props.entrySet()) {
                    Schema propSchema = propSchemaPair.getValue();
                    String newRef = propSchema.get$ref();
                    if (propSchema.getType() != null && propSchema.getType().equals("array")) {
                        newRef = ((ArraySchema) propSchema).getItems().get$ref();
                    }
                    if (Strings.isNullOrEmpty(newRef)) {
                        continue;
                    }
                    boolean flag = false;
                    for (String currPath: currPaths) {

                        if (refsMap.get(newRef) == null || !(refsMap.get(newRef).contains(currPath))) {
                            flag = true;
                            addToRefMap(newRef, currPath, refsMap);
                        }
                    }
                    if (flag) {
                        dereferenceSchemaObject(newRef.substring(newRef.indexOf(schemaComponentString) + schemaComponentStringLength), schemas, refsMap);
                    }
                }
            }
        }
    }

    private static void processOperation(String pathName, PathItem pi, Operation op,
                                         Map<String, OpenAPI> openAPIs, String opType,
                                         Map<String, Set<String>> refsMap) {
        if (pi == null) {
            return;
        }

        // get eligible openAPI objects for the given operation
        List<OpenAPI> oas = openAPIs.entrySet().stream()
                            .filter(pp -> (pp.getKey().equals(pathName)))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        for (OpenAPI oa : oas) {
            // Add the input operation's Path to each eligible OpenAPI object
            PathItem currentPathItem;
            if (oa.getPaths() == null || (currentPathItem = oa.getPaths().get(pathName)) == null) {
                currentPathItem = new PathItem();

                currentPathItem.summary(pi.getSummary());
                currentPathItem.description(pi.getDescription());
                currentPathItem.servers(pi.getServers());
                currentPathItem.setExtensions(pi.getExtensions());
                currentPathItem.parameters(pi.getParameters());
                currentPathItem.$ref(pi.get$ref());
            }

            if( op != null) {
                switch (opType) {
                    case "Get":
                        currentPathItem.get(op);
                        break;
                    case "Post":
                        currentPathItem.post(op);
                        break;
                    case "Put":
                        currentPathItem.put(op);
                        break;
                    case "Delete":
                        currentPathItem.delete(op);
                        break;
                    case "Options":
                        currentPathItem.options(op);
                        break;
                    case "Head":
                        currentPathItem.head(op);
                        break;
                    case "Patch":
                        currentPathItem.patch(op);
                        break;
                    case "Trace":
                        currentPathItem.trace(op);
                        break;
                }

                // Add path to the current openAPI object
                oa.path(pathName, currentPathItem);

                // Map refs to current openAPI object
                String currOaPath = oa.getPaths().entrySet().stream().findFirst().get().getKey().toString();

                if (pi.getParameters() != null) {
                    for (Parameter p : pi.getParameters()) {
                        addToRefMap(p.get$ref(), currOaPath, refsMap);
                    }
                }
                addToRefMap(pi.get$ref(), currOaPath, refsMap);
                if (op.getParameters() != null) {
                    for (Parameter p : op.getParameters()) {
                        addToRefMap(p.get$ref(), currOaPath, refsMap);
                    }
                }
                if (op.getRequestBody() != null) {
                    addToRefMap(op.getRequestBody().get$ref(), currOaPath, refsMap);
                    op.getRequestBody().getContent().values().forEach(mediaType -> addToRefMap(mediaType.getSchema().get$ref(), currOaPath, refsMap));
                }
                if (op.getCallbacks() != null) {
                    op.getCallbacks().forEach((key, value) -> addToRefMap(value.get(key).get$ref(), currOaPath, refsMap));
                }
                if (op.getResponses() != null) {
                    op.getResponses().values().forEach(apiResponse -> {
                        addToRefMap(apiResponse.get$ref(), currOaPath, refsMap);
                        if (apiResponse.getContent() != null) {
                            apiResponse.getContent().values().forEach(mediaType -> addToRefMap(mediaType.getSchema().get$ref(), currOaPath, refsMap));
                        }
                    });
                }
            }
        }
    }

    private static void addToRefMap(String ref,
                                    String currPathName,
                                    Map<String, Set<String>> refsMap) {
        if (Strings.isNullOrEmpty(ref)) {
            return;
        }
        Set<String> paths;
        if ((paths = refsMap.get(ref)) == null) {
            paths = new HashSet<>();
        }
        paths.add(currPathName);
        refsMap.put(ref, paths);
    }


    private static void validateAndWriteOutputFile(String path,
                                  String outputFolderPath,
                                  String content) throws IOException {

        try {


            String outputFileName = "_" + ((String) path.toString()).replaceAll("/", "_") + ".yml";
            outputFileName = outputFileName.toUpperCase();
            File file = new File(outputFolderPath + outputFileName);
            boolean c = file.getParentFile().mkdirs();
            boolean a = file.createNewFile();
            Writer writer = new FileWriter(outputFolderPath + outputFileName);
            writer.write(content);
            writer.close();

            File f = new File(outputFolderPath + outputFileName);

            System.out.println("File Output generated and validated as per Open API specs at Path: \n" + outputFolderPath + outputFileName);
        }
        catch (Exception e)
        {
            System.out.println("File Output Generation failed");
        }
    }
}