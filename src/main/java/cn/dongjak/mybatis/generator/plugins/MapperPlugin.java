package cn.dongjak.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class MapperPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return false;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        topLevelClass.addImportedType("");
        topLevelClass.addAnnotation("");

        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }
}
