package cn.dongjak.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class MySQLLimitPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    /**
     * 为每个Example类添加limit和offset属性已经set、get方法
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        //java.lang.Integer
        FullyQualifiedJavaType integerType = new FullyQualifiedJavaType("java.lang.Integer");

        Field start = new Field();
        start.setName("start");
        start.setVisibility(JavaVisibility.PROTECTED);
        start.setType(integerType);
        topLevelClass.addField(start);

        Method startGetter = new Method();
        startGetter.setVisibility(JavaVisibility.PUBLIC);
        startGetter.setReturnType(integerType);
        startGetter.setName("getStart");
        startGetter.addBodyLine("return this.start;");
        topLevelClass.addMethod(startGetter);

        Method startSetter = new Method();
        startSetter.setVisibility(JavaVisibility.PUBLIC);
        startSetter.setName("setStart");
        startSetter.addParameter(new Parameter(integerType, "start"));
        startSetter.addBodyLine("this.start = start;");
        topLevelClass.addMethod(startSetter);


        Field limit = new Field();
        limit.setName("limit");
        limit.setVisibility(JavaVisibility.PROTECTED);
        limit.setType(integerType);
        topLevelClass.addField(limit);

        Method limitGetter = new Method();
        limitGetter.setVisibility(JavaVisibility.PUBLIC);
        limitGetter.setReturnType(integerType);
        limitGetter.setName("getLimit");
        limitGetter.addBodyLine("return this.limit;");
        topLevelClass.addMethod(limitGetter);


        Method limitSetter = new Method();
        limitSetter.setVisibility(JavaVisibility.PUBLIC);
        limitSetter.setName("setLimit");
        limitSetter.addParameter(new Parameter(integerType, "limit"));
        limitSetter.addBodyLine("this.limit = limit;");
        topLevelClass.addMethod(limitSetter);


        return true;
    }

    /**
     * 为Mapper.xml的selectByExample添加limit
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {

        XmlElement ifStartAndLimitNotNullElement = new XmlElement("if");
        ifStartAndLimitNotNullElement.addAttribute(new Attribute("test", "start != null and limit != null"));
        ifStartAndLimitNotNullElement.addElement(new TextElement("limit ${start}, ${limit}"));
        element.addElement(ifStartAndLimitNotNullElement);
        return true;
    }
}
