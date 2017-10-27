package cn.dongjak.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

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
            annotationStringBuilder.append("(").append("value=\"").append(introspectedColumn.getRemarks()).append("\"");

            // if(introspectedColumn.getDefaultValue())
        }
        // String.format("@ApiModelProperty(value=\"%s\",example=%s)", introspectedColumn.getRemarks(), introspectedColumn.getDefaultValue());
        //field.addAnnotation(annotationString);
        return true;
    }
}
