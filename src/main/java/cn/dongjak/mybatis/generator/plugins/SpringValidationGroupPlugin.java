package cn.dongjak.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class SpringValidationGroupPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        InnerClass create = new InnerClass("Create");
        create.setVisibility(JavaVisibility.PUBLIC);
        InnerClass update = new InnerClass("Update");
        update.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addInnerClass(create);
        topLevelClass.addInnerClass(update);
        return true;
    }
}
