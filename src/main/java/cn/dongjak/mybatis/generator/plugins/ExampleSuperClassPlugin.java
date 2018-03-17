package cn.dongjak.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class ExampleSuperClassPlugin extends PluginAdapter {

    private String superClass;

    @Override
    public boolean validate(List<String> list) {
        this.superClass = this.getProperties().getProperty("superClass");
        if (StringUtils.isNotBlank(this.superClass)) return true;
        return false;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.setSuperClass(this.superClass);
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }
}
