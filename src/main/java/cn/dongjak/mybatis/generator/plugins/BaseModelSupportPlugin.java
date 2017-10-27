package cn.dongjak.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class BaseModelSupportPlugin extends PluginAdapter {


    private String type;


    @Override
    public boolean validate(List<String> list) {
        this.type = this.properties.getProperty("type");
        return StringUtils.isNotBlank(this.type);
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType baseModelType = new FullyQualifiedJavaType(this.type);
        if (!baseModelType.getPackageName().equals(topLevelClass.getType().getPackageName()))
            topLevelClass.addImportedType(this.type);
        topLevelClass.setSuperClass(baseModelType.getShortName());
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }
}
