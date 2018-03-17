package cn.dongjak.mybatis.generator.plugins;

import cn.dongjak.mybatis.generator.utils.ImportUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.Date;
import java.util.List;

public class DataFieldJsonSerializerPlugin extends PluginAdapter {

    private String serializerName;
    private String deserializerName;
    private String jsonSerializeAnnoName = "com.fasterxml.jackson.databind.annotation.JsonSerialize";
    private String jsonDeserializeAnnoName = "com.fasterxml.jackson.databind.annotation.JsonDeserialize";

    @Override
    public boolean validate(List<String> list) {
        this.serializerName = this.getProperties().getProperty("serializerName");
        this.deserializerName = this.getProperties().getProperty("deserializerName");
        return StringUtils.isNotBlank(this.serializerName) && StringUtils.isNotBlank(this.deserializerName);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {

        if (!ImportUtils.checkIsImported(topLevelClass, jsonSerializeAnnoName))
            topLevelClass.addImportedType(jsonSerializeAnnoName);

        if (!ImportUtils.checkIsImported(topLevelClass, serializerName))
            topLevelClass.addImportedType(serializerName);

        if (!ImportUtils.checkIsImported(topLevelClass, deserializerName))
            topLevelClass.addImportedType(deserializerName);


        if (field.getType().getFullyQualifiedName().equals(Date.class.getName())) {
            field.addAnnotation(String.format("@JsonSerialize(using = %s.class)", new FullyQualifiedJavaType(serializerName).getShortName()));
            field.addAnnotation(String.format("@JsonDeserialize(using = %s.class)", new FullyQualifiedJavaType(deserializerName).getShortName()));
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}
