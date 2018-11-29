package com.zhh.generator.writers;

import com.zhh.generator.annotations.Optional;
import com.zhh.generator.stubs.EnumStub;
import com.zhh.generator.stubs.OutputStub;
import com.zhh.generator.stubs.TemplateStub;
import com.zhh.generator.stubs.ParameterStub;
import com.zhh.generator.utils.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class DocWriter extends BaseWriter {
    private File outputDir = new File(dir.getAbsolutePath() + "/src/main/php/laravel/resources/docs/1.0");

    @Override
    public void update(Observable o, Object arg) {
        OutputStub output = (OutputStub) o;
        output.getTemplates().forEach((version, templateStub) -> {
            boolean res;
            if (outputDir.exists()) {
                res = outputDir.delete();
                FileUtil.deleteAllFilesOfDir(outputDir);
            }
            res = outputDir.mkdir();
            res = (new File(outputDir.getAbsolutePath() + "/generated")).mkdir();
            res = (new File(outputDir.getAbsolutePath() + "/generated/apis")).mkdir();

            writeIndex(templateStub);
            writeModels(templateStub);
            writeAPIs(templateStub);
            writeErrors(templateStub);
            writeEnums(templateStub);
        });
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

    private void writeOverview() {

    }

    private void writeAPIs(TemplateStub output) {
        output.getControllers().forEach(controller -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() + "/generated/apis/" + controller.getName() + ".md");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);


                outputStreamWriter.write("# " + controller.getName() + "\n");
                if (controller.getComment() != null) {
                    outputStreamWriter.write("\n> {warning} " + controller.getComment() + "\n\n");
                }
                outputStreamWriter.write("\n---\n\n");

                controller.getActions().forEach(action -> {
                    try {
                        outputStreamWriter.write("  - [" + action.getName() + "](#" + action.getName() + ")\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                outputStreamWriter.write("\n");

                controller.getActions().forEach(action -> {
                    try {
                        outputStreamWriter.write("<a name=\"" + action.getName() + "\"></a>\n");
                        outputStreamWriter.write("## " + action.getName() + "\n");
                        if (action.getComment() != null) {
                            outputStreamWriter.write("\n> {warning} " + action.getComment() + "\n\n");
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

    private void writeErrors(TemplateStub output) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() + "/generated/error-codes.md");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            outputStreamWriter.write("|ErrorCode|Key|Description|\n|:-|:-|:-|\n");
            output.getErrors().forEach(error -> {
                try {

                    // 输出模型每一个请求参数
                    outputStreamWriter.write("|" + error.getCode() + "|" + error.getName() + "|" + error.getComment() + " |\n");
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

    private void writeModels(TemplateStub output) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() + "/generated/models.md");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            output.getModels().forEach(model -> {
                try {
                    outputStreamWriter.write("  - [" + model.getName() + "](#" + model.getName() + ")\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputStreamWriter.write("\n");

            output.getModels().forEach(model -> {
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
    private void writeParameters(String title, OutputStreamWriter outputStreamWriter, ArrayList<ParameterStub> parameters) throws IOException {
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
    private void writeParameters(OutputStreamWriter outputStreamWriter, ArrayList<ParameterStub> parameters) throws IOException {
        outputStreamWriter.write("|Key|Description|Type|Required|\n|:-|:-|:-|:-|\n");
        // 输出模型每一个请求参数
        parameters.forEach(parameter -> writeParameter(outputStreamWriter, parameter));
    }

    private void writeParameter(OutputStreamWriter outputStreamWriter, ParameterStub parameter) {
        try {
            outputStreamWriter.write("|" + parameter.getName() + " |");
            outputStreamWriter.write((parameter.getComment() == null ? " " : parameter.getComment().replace("\n", "<br>")));
            outputStreamWriter.write(writeLink(parameter.getType()));
            outputStreamWriter.write((parameter.hasAnnotation(Optional.name) ? " " : "true") + "|");
            outputStreamWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String writeLink(String template) {
        if (template.startsWith("Models.")) {
            return ("|[`" + template + "`](/docs/{{version}}/generated/models#" + template.substring(7).replace("[]", "") + ")|");
        } else if (template.startsWith("Enums.")) {
            return ("|[`" + template + "`](/docs/{{version}}/generated/enums#" + template.substring(6).replace("[]", "") + ")|");
        } else {
            return ("|`" + template + "`|");
        }
    }
}
