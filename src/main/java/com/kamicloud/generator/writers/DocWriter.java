package com.kamicloud.generator.writers;

import definitions.annotations.Optional;
import com.kamicloud.generator.stubs.EnumStub;
import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.stubs.TemplateStub;
import com.kamicloud.generator.stubs.ParameterStub;
import com.kamicloud.generator.utils.FileUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.Observable;

public class DocWriter extends BaseWriter {
    private File docPath;
    private File outputDir;

    public DocWriter() {
        docPath = new File(Objects.requireNonNull(env.getProperty("generator.doc-path")) + "/resources/docs");
    }

    @Override
    public void update(OutputStub output) {
        output.getTemplates().forEach((version, templateStub) -> {
            outputDir = new File(docPath.getAbsolutePath() + "/" + version);
            if (outputDir.exists()) {
                FileUtil.deleteAllFilesOfDir(outputDir);
            }
            (new File(outputDir.getAbsolutePath() + "/generated/apis")).mkdirs();

            writeIndex(templateStub);
            writeModels(templateStub);
            writeAPIs(templateStub);
            writeErrors();
            writeEnums(templateStub);
        });
        writeErrors(output);
    }

    private void writeIndex(TemplateStub o) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() + "/index.md");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            outputStreamWriter.write("- ## Get Started\n" +
                "  - [Overview](/docs/{{version}}/overview)\n" +
//                "  - [Example](/docs/{{version}}/example)\n" +
                "- ## 数据字典\n" +
                "  - [ErrorCodes](/docs/{{version}}/generated/error-codes)\n" +
                "  - [Enums](/docs/{{version}}/generated/enums)\n" +
                "  - [Models](/docs/{{version}}/generated/models)\n" +
                "- ## 接口文档\n");
            o.getControllers().forEach(controller -> {
                try {
                    outputStreamWriter.write("  - [" + controller.getName() + "](/docs/{{version}}/generated/apis/" + controller.getName() + ")\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            outputStreamWriter.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeAPIs(TemplateStub output) {
        output.getControllers().forEach(controller -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() + "/generated/apis/" + controller.getName() + ".md");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);


                outputStreamWriter.write("# " + controller.getName() + "\n");
                if (controller.getComment() != null) {
                    outputStreamWriter.write("\n> {warning} " + transformLfToBr(controller.getComment()) + "\n\n");
                }
                outputStreamWriter.write("\n---\n\n");

                controller.getActions().forEach((actionName, action) -> {
                    try {
                        outputStreamWriter.write("  - [" + action.getName() + "](#" + action.getName() + ")\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                outputStreamWriter.write("\n");

                controller.getActions().forEach((actionName, action) -> {
                    try {
                        outputStreamWriter.write("<a name=\"" + action.getName() + "\"></a>\n");
                        outputStreamWriter.write("## " + action.getName() + "\n");
                        if (action.getComment() != null) {
                            outputStreamWriter.write("\n> {warning} " + transformLfToBr(action.getComment()) + "\n\n");
                        }
                        writeParameters("Requests", outputStreamWriter, action.getRequests());
                        writeParameters("Responses", outputStreamWriter, action.getResponses());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                outputStreamWriter.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void writeErrors(OutputStub output) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(docPath.getAbsolutePath() + "/error-codes.md");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            outputStreamWriter.write("|ErrorCode|Key|Description|\n|:-|:-|:-|\n");
            output.getErrors().forEach(error -> {
                try {
                    String comment = error.getComment() == null ? "" : error.getComment().replace("\n", "<br>");
                    // 输出模型每一个请求参数
                    outputStreamWriter.write("|" + error.getCode() + "|" + error.getName() + "|" + comment + " |\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputStreamWriter.write("\n");

            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeErrors() {
        try {
            String path;
            if (env.getProperty("generator.env", "prod").equals("dev")) {
                path = "./stubs/error-codes-symlink.stub";
            } else {
                path = "./vendor/kamicloud/yet-another-generator/stubs/error-codes-symlink.stub";
            }
            FileCopyUtils.copy(new File(path), new File(outputDir.getAbsolutePath() + "/generated/error-codes.md"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeModels(TemplateStub output) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() + "/generated/models.md");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            output.getModels().forEach((modelName, model) -> {
                try {
                    outputStreamWriter.write("  - [" + modelName + "](#" + modelName + ")\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputStreamWriter.write("\n");

            output.getModels().forEach((modelName, model) -> {
                try {
                    outputStreamWriter.write("<a name=\"" + model.getName() + "\"></a>\n");
                    outputStreamWriter.write("## " + model.getName() + "\n");
                    if (model.getComment() != null) {
                        outputStreamWriter.write("\n> {warning} " + model.getComment() + "\n\n");
                    }
                    writeParameters("Attributes", outputStreamWriter, model.getParameters());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            outputStreamWriter.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeEnums(TemplateStub output) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() + "/generated/enums.md");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            output.getEnums().forEach(enumStub -> {
                try {
                    outputStreamWriter.write("<a name=\"" + enumStub.getName() + "\"></a>\n");
                    outputStreamWriter.write("## " + enumStub.getName() + "\n");
                    if (enumStub.getComment() != null) {
                        outputStreamWriter.write("\n> {warning} " + enumStub.getComment() + "\n\n");
                    }

                    HashMap<String, EnumStub.EnumStubItem> enumItems = enumStub.getItems();

                    outputStreamWriter.write("|Key|Value|Description|\n|:-|:-|:-|\n");
                    enumItems.forEach((key, value) -> {
                        try {
                            outputStreamWriter.write("|" + key + "|");
                            outputStreamWriter.write(value.getName());
                            outputStreamWriter.write("|");
                            outputStreamWriter.write(" ");
                            outputStreamWriter.write("|\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputStreamWriter.write("\n");

            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param title 标题
     * @param outputStreamWriter 输出流
     * @param parameters 参数
     * @throws IOException IO异常
     */
    private void writeParameters(String title, OutputStreamWriter outputStreamWriter, HashMap<String, ParameterStub> parameters) throws IOException {
        outputStreamWriter.write("### " + title + "\n");
        writeParameters(outputStreamWriter, parameters);
        outputStreamWriter.write("\n");
    }

    /**
     *
     * @param outputStreamWriter 输出流
     * @param parameters 参数
     * @throws IOException IO异常
     */
    private void writeParameters(OutputStreamWriter outputStreamWriter, HashMap<String, ParameterStub> parameters) throws IOException {
        outputStreamWriter.write("|Key|Description|Type|Required|\n|:-|:-|:-|:-|\n");
        // 输出模型每一个请求参数
        parameters.forEach((parameterName, parameter) -> writeParameter(outputStreamWriter, parameter));
    }

    private void writeParameter(OutputStreamWriter outputStreamWriter, ParameterStub parameter) {
        try {
            outputStreamWriter.write("|" + parameter.getName() + " |");
            outputStreamWriter.write((parameter.getComment() == null ? " " : transformLfToBr(parameter.getComment())));
            outputStreamWriter.write(writeLink(parameter));
            outputStreamWriter.write((parameter.hasAnnotation(Optional.name) ? " " : "true") + "|");
            outputStreamWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String writeLink(ParameterStub parameter) {
        String type = parameter.getType();
        if (parameter.isModel()) {
            return ("|[`Models." + type + "`](/docs/{{version}}/generated/models#" + type + ")|");
        } else if (parameter.isEnum()) {
            return ("|[`Enums." + type + "`](/docs/{{version}}/generated/enums#" + type + ")|");
        } else {
            return ("|`" + type + "`|");
        }
    }

    private String transformLfToBr(String lf) {
        return lf.replace("\n", "<br>");
    }
}
