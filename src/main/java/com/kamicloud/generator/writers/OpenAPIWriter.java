package com.kamicloud.generator.writers;

import com.kamicloud.generator.stubs.core.OutputStub;
import com.kamicloud.generator.stubs.core.TemplateStub;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.*;

public class OpenAPIWriter extends BaseWriter {
    @Override
    String getName() {
        return "openapi";
    }

    @Override
    void postConstruct() {

    }

    @Override
    void update(OutputStub o) {
        o.getTemplates().forEach((version, templateStub) -> {
            LinkedHashMap<String, Object> obj = writeTemplate(version, templateStub);

            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setPrettyFlow(true);

            Yaml yaml = new Yaml(dumperOptions);
            StringWriter sw = new StringWriter();
            yaml.dump(obj, sw);
//            System.out.println(sw.toString());
        });
    }

    private LinkedHashMap<String, Object> writeTemplate(String version, TemplateStub templateStub) {
        return new LinkedHashMap<String, Object>() {{
            put("openapi", "3.0.0");
            put("info", new HashMap<String, Object>() {{

                put("title", templateStub.getCommentTitle());
                put("description", templateStub.getCommentBody());
                put("version", version);
            }});

            writeEnums(this, templateStub);
            writeAPIs(this, templateStub);
            writeModels(this, templateStub);

//            put("services", new LinkedList<HashMap<String, String>>() {{
//                add(new HashMap<String, String>() {{
//                    put("url", "http://api.example.com/v1");
//                    put("description", "Optional server description, e.g. Main (production) server");
//                }});
//                add(new HashMap<String, String>() {{
//                    put("url", "http://api.example.com/v1");
//                    put("description", "Optional server description, e.g. Main (production) server");
//                }});
//            }});
        }};
    }

    private void writeAPIs(LinkedHashMap<String, Object> obj, TemplateStub templateStub) {
        obj.put("paths", new HashMap<String, Object>() {{
            put("/pet", new LinkedHashMap<String, Object>() {{
                put("put", new LinkedHashMap<String, Object>() {{
                    put("responses", new LinkedHashMap<String, Object>() {{
                        put("400", new LinkedHashMap<String, String>() {{
                            put("description", "Invalid ID supplied");
                        }});
                    }});
                }});
            }});
        }});
    }

    private void writeEnums(LinkedHashMap<String, Object> obj, TemplateStub templateStub) {

    }

    private void writeModels(LinkedHashMap<String, Object> obj, TemplateStub templateStub) {
        obj.put("components", new LinkedHashMap<String, Object>() {{
            put("schemas", new LinkedHashMap<String, Object>() {{
                templateStub.getModels().forEach(modelStub -> {
                    put(modelStub.getName(), new LinkedHashMap<String, Object>() {{
                        put("type", "object");
                        put("properties", new LinkedHashMap<String, Object>() {{

                            modelStub.getParameters().forEach(parameterStub -> {
                                put(parameterStub.getName(), new LinkedHashMap<String, String>() {{
                                    put("type", "string");
                                }});
                            });
                        }});
                    }});
                });
            }});
        }});
    }
}

