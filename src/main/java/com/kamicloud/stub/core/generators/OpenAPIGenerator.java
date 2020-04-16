package com.kamicloud.stub.core.generators;

import com.kamicloud.stub.core.generators.components.common.FileCombiner;
import com.kamicloud.stub.core.generators.components.openapi.v3.*;
import com.kamicloud.stub.core.stubs.OutputStub;
import com.kamicloud.stub.core.stubs.ParameterStub;
import com.kamicloud.stub.core.stubs.TemplateStub;
import definitions.annotations.Get;
import definitions.official.TypeSpec;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class OpenAPIGenerator extends BaseGenerator {
    private final HashMap<TypeSpec, String> returnTypeMap = new HashMap<TypeSpec, String>() {{
        put(TypeSpec.BOOLEAN, "boolean");
        put(TypeSpec.DATE, "string");
        put(TypeSpec.INTEGER, "integer");
        put(TypeSpec.FILE, "string");
        put(TypeSpec.FLOAT, "float");
        put(TypeSpec.STRING, "string");
        put(TypeSpec.MODEL, "object");
        put(TypeSpec.ENUM, "string");
    }};

    private final HashMap<TypeSpec, String> formatMap = new HashMap<TypeSpec, String>() {{
        put(TypeSpec.FILE, "binary");
    }};

    @Override
    public void postConstruct() {

    }

    @Override
    public void render(OutputStub o) {
        o.getTemplates().forEach((version, templateStub) -> {
            MainSegment mainSegment = createMainSegment(version, templateStub);

            DumperOptions dumperOptions = new DumperOptions();
//            dumperOptions.setPrettyFlow(true);

            Yaml yaml = new Yaml(dumperOptions);
            StringWriter sw = new StringWriter();
            yaml.dump(mainSegment.toYaml(), sw);

            String path = config.getGenerators().getOpenapi().getPath();

            FileCombiner fileCombiner = new FileCombiner();
            fileCombiner.setFileName(path + "/openapi_" + version.toLowerCase() + ".yaml");
            fileCombiner.addMultiLines(sw.toString());
            try {
                fileCombiner.toFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private MainSegment createMainSegment(String version, TemplateStub templateStub) {
        MainSegment mainSegment = new MainSegment();

        InfoSegment infoSegment = new InfoSegment()
            .setVersion(version)
            .setDescription(templateStub.getCommentBody());

        mainSegment.setOpenapi("3.0.0");
        mainSegment.setInfo(infoSegment);



        ComponentSegment componentSegment = new ComponentSegment();

        templateStub.getControllers().forEach(controllerStub -> {
            controllerStub.getActions().forEach(actionStub -> {
                PathSegment.PathEntity pathEntity = new PathSegment.PathEntity();

                pathEntity.getTags().add(controllerStub.getName());
                pathEntity.setSummary(actionStub.getCommentBody());

                PathSegment pathSegment = new PathSegment();

                if (actionStub.hasAnnotation(Get.class)) {
                    pathSegment.setGet(pathEntity);
                } else {
                    pathSegment.setPost(pathEntity);

                    PathRequestBodySegment pathRequestBodySegment = new PathRequestBodySegment();
                    PathRequestBodyContentSegment pathRequestBodyContentSegment = new PathRequestBodyContentSegment();

                    SchemaSegment schemaSegment = new SchemaSegment();
                    pathRequestBodyContentSegment.setSchema(schemaSegment);

                    pathRequestBodySegment.getContent().put("multipart/form-data", pathRequestBodyContentSegment);


                    actionStub.getRequests().forEach(requestStub -> {
                        schemaSegment.getProperties().put(requestStub.getName(), createSchemaSegmentProperty(requestStub));
                    });
                    pathEntity.setRequestBody(pathRequestBodySegment);
                }



                PathResponseSegment pathResponseSegment = new PathResponseSegment();
                PathResponseSegment.PathResponseContentEntity pathResponseContentEntity = new PathResponseSegment.PathResponseContentEntity();
                SchemaSegment schemaSegment = new SchemaSegment();
                pathResponseContentEntity.setSchema(schemaSegment);
                pathResponseSegment.getContent().put("application/json", pathResponseContentEntity);
                actionStub.getResponses().forEach(response -> {
                    schemaSegment.getProperties().put(response.getName(), createSchemaSegmentProperty(response));
                });


                pathEntity.getResponses().put("200", pathResponseSegment);

                mainSegment.getPaths().put(actionStub.getFullUri(), pathSegment);
            });
        });

        templateStub.getModels().forEach(model -> {
            SchemaSegment schemeSegment = new SchemaSegment();

            model.getParameters().forEach(parameterStub -> {
                SchemaSegment.Property propertySegment = new SchemaSegment.Property();

                propertySegment.setType(returnTypeMap.get(parameterStub.getTypeSpec()));

                if (parameterStub.getTypeSpec().equals(TypeSpec.MODEL)) {
                    propertySegment.setRef("#/components/schemas/" + parameterStub.getUpperCamelName());
                }

                propertySegment.setNullable(parameterStub.hasAnnotation(Optional.class));

                if (parameterStub.hasAnnotation(Optional.class)) {
                    schemeSegment.getRequired().add(parameterStub.getLowerCamelName());
                }
                schemeSegment.getProperties().put(parameterStub.getLowerCamelName(), propertySegment);
            });

            componentSegment.getSchemas().put(model.getUpperCamelName(), schemeSegment);
        });

        mainSegment.setComponents(componentSegment);

        return mainSegment;
    }

    private SchemaSegment createSchemaSegment(ParameterStub parameterStub) {
        SchemaSegment schemaSegment = new SchemaSegment();

        return schemaSegment;
    }

    private SchemaSegment.Property createSchemaSegmentProperty(ParameterStub parameterStub) {
        SchemaSegment.Property propertySegment = new SchemaSegment.Property();

//        if (parameterStub.isArray()) {
//            propertySegment.setType("array");
//        } else {
            if (parameterStub.isModel()) {
                propertySegment.setRef("#/components/schemas/" + parameterStub.getTypeSimpleName());
            } else {
                propertySegment.setType(returnTypeMap.get(parameterStub.getTypeSpec()));
            }
//        }


        return propertySegment;
    }

    private SchemaSegment.Property createSchemaSegmentItem(ParameterStub parameterStub) {
        SchemaSegment.Property propertySegment = new SchemaSegment.Property();

//        if (parameterStub.isArray()) {
//            propertySegment.setType("array");
//        } else {
        if (parameterStub.isModel()) {
            propertySegment.setRef("#/components/schemas/" + parameterStub.getTypeSimpleName());
        } else {
            propertySegment.setType(returnTypeMap.get(parameterStub.getTypeSpec()));
        }
//        }


        return propertySegment;
    }
}

