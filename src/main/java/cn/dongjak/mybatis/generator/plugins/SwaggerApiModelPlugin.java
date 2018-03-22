package cn.dongjak.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class SwaggerApiModelPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");

        StringBuilder annotationStringBuilder = new StringBuilder("@ApiModelProperty");
        if (StringUtils.isNotBlank(introspectedColumn.getRemarks())) {
            //introspectedColumn.getJdbcTypeName()
            BufferedReader reader = new BufferedReader(new StringReader(introspectedColumn.getRemarks()));
            try {
                annotationStringBuilder.append("(").append("value=\"").append(reader.readLine()).append("\"").append(")");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // if(introspectedColumn.getDefaultValue())
        }
        // String.format("@ApiModelProperty(value=\"%s\",example=%s)", introspectedColumn.getRemarks(), introspectedColumn.getDefaultValue());
        field.addAnnotation(annotationStringBuilder.toString());
        return true;
    }
}
