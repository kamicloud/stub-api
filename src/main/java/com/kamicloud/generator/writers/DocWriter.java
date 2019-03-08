package com.kamicloud.generator.writers;

import com.kamicloud.generator.writers.components.common.FileCombiner;
import com.kamicloud.generator.writers.components.common.MultiLinesCombiner;
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
//            (new File(outputDir.getAbsolutePath() + "/generated/apis")).mkdirs();

            writeIndex(templateStub);
            writeModels(templateStub);
            writeAPIs(templateStub);
//            writeErrors();
            writeEnums(templateStub);
        });
        writeErrors(output);
    }

    private void writeIndex(TemplateStub o) {
        try {
            FileCombiner index = new FileCombiner();
            index.setFileName(outputDir.getAbsolutePath() + "/index.md");

            index.addBlock(new MultiLinesCombiner(
                "- ## Get Started",
                "  - [Overview](/docs/{{version}}/overview)",
                "- ## 数据字典",
                "  - [ErrorCodes](/docs/ErrorCodes)",
                "  - [Enums](/docs/{{version}}/generated/enums)",
                "  - [Models](/docs/{{version}}/generated/models)",
                "- ## 接口文档"
            ));

            o.getControllers().forEach(controller -> {
                index.addLine("  - [" + controller.getName() + "](/docs/{{version}}/generated/apis/" + controller.getName() + ")");
            });

            index.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeAPIs(TemplateStub output) {
        output.getControllers().forEach(controller -> {
            try {
                FileCombiner file = new FileCombiner();
                file.setFileName(outputDir.getAbsolutePath() + "/generated/apis/" + controller.getName() + ".md");

                file.addLine("# " + controller.getName());

                if (controller.getComment() != null) {
                    file.addLine("\n> {warning} " + transformLfToBr(controller.getComment()) + "\n");
                }
                file.addLine("\n---\n");

                controller.getActions().forEach((actionName, action) -> {
                    file.addLine("  - [" + action.getName() + "](#" + action.getName() + ")");
                });

                file.addLine("");
                controller.getActions().forEach((actionName, action) -> {
                    file.addBlock(new MultiLinesCombiner(
                        "<a name=\"" + action.getName() + "\"></a>",
                        "## " + action.getName()
                    ));
                    if (action.getComment() != null) {
                        file.addLine("\n> {warning} " + transformLfToBr(action.getComment()) + "\n");
                    }
                    writeParameters("Requests", file, action.getRequests());
                    file.addLine("");
                    writeParameters("Responses", file, action.getResponses());
                });
                file.toFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void writeErrors(OutputStub output) {
        try {
            FileCombiner overview = new FileCombiner();
            overview.setFileName(docPath.getAbsolutePath() + "/ErrorCodes/overview.md");
            MultiLinesCombiner blocks = new MultiLinesCombiner();
            blocks.addLine("|ErrorCode|Key|Description|");
            blocks.addLine("|:-|:-|:-|");

            output.getErrors().forEach(error -> {
                String comment = error.getComment() == null ? "" : error.getComment().replace("\n", "<br>");
                // 输出模型每一个请求参数
                blocks.addLine("|" + error.getCode() + "|" + error.getName() + "|" + comment + " |");
            });

            overview.addBlock(blocks);

            FileCombiner index = new FileCombiner();
            index.setFileName(docPath.getAbsolutePath() + "/ErrorCodes/index.md");
            index.addBlock(new MultiLinesCombiner(
                "- ## Get Started",
                "  - [Overview](/docs/{{version}}/overview)\n"
            ));


            overview.toFile();
            index.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeModels(TemplateStub output) {
        try {
            FileCombiner file = new FileCombiner();
            file.setFileName(outputDir.getAbsolutePath() + "/generated/models.md");

            output.getModels().forEach((modelName, model) -> {
                file.addBlock(new MultiLinesCombiner(
                    "  - [" + modelName + "](#" + modelName + ")"
                ));
            });

            file.addBlock(new MultiLinesCombiner(""));

            output.getModels().forEach((modelName, model) -> {
                file.addBlock(new MultiLinesCombiner(
                    "<a name=\"" + model.getName() + "\"></a>",
                    "## " + model.getName()
                ));

                if (model.getComment() != null) {
                    file.addBlock(new MultiLinesCombiner("\n> {warning} " + model.getComment() + "\n"));
                }
                writeParameters("Attributes", file, model.getParameters());
            });

            file.toFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeEnums(TemplateStub output) {
        try {
            FileCombiner file = new FileCombiner();
            file.setFileName(outputDir.getAbsolutePath() + "/generated/enums.md");


            output.getEnums().forEach(enumStub -> {
                file.addBlock(new MultiLinesCombiner(
                    "<a name=\"" + enumStub.getName() + "\"></a>",
                    "## " + enumStub.getName()
                ));
                if (enumStub.getComment() != null) {
                    file.addBlock(new MultiLinesCombiner(
                        "> {warning} " + enumStub.getComment(),
                        ""
                    ));
                }
                file.addBlock(new MultiLinesCombiner(
                    "|Key|Value|Description|",
                    "|:-|:-|:-|"
                ));

                HashMap<String, EnumStub.EnumStubItem> enumItems = enumStub.getItems();

                enumItems.forEach((key, value) -> {
                    file.addLine(
                        "|" + key + "|" + value.getName() + "|" + (value.getComment() == null ? " " : transformLfToBr(value.getComment())) + "|"
                    );
                });
            });

            file.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param title 标题
     * @param file 输出流
     * @param parameters 参数
     */
    private void writeParameters(String title, FileCombiner file, HashMap<String, ParameterStub> parameters) {
        file.addBlock(new MultiLinesCombiner(
            "### " + title,
            "|Key|Description|Type|Required|",
            "|:-|:-|:-|:-|"
        ));
        parameters.forEach((parameterName, parameter) -> {
            file.addBlock(new MultiLinesCombiner(
                "|" + parameter.getName() + " |" +
                    (parameter.getComment() == null ? " " : transformLfToBr(parameter.getComment())) + writeLink(parameter) +
                    (parameter.hasAnnotation(Optional.name) ? " " : "true") + "|"
            ));
        });
    }


    private String writeLink(ParameterStub parameter) {
        String type = parameter.getType();
        if (parameter.isModel()) {
            return ("|[`Models." + type + "`](/docs/{{version}}/generated/models#" + type.replace("[]", "") + ")|");
        } else if (parameter.isEnum()) {
            return ("|[`Enums." + type + "`](/docs/{{version}}/generated/enums#" + type.replace("[]", "") + ")|");
        } else {
            return ("|`" + type + "`|");
        }
    }

    private String transformLfToBr(String lf) {
        return lf.replace("\n", "<br>");
    }
}
