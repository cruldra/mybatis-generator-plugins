package cn.dongjak.mybatis.generator.plugins;

import cn.dongjak.mybatis.generator.utils.ParameterFactory;
import cn.dongjak.mybatis.generator.utils.ParameterType;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.Iterator;
import java.util.List;

public class ExampleStreamPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        // private static ThreadLocal<CkbSysUserExample> instance = new ThreadLocal<>();
        Field instanceField = new Field();
        instanceField.setVisibility(JavaVisibility.PRIVATE);
        instanceField.setStatic(true);
        FullyQualifiedJavaType threadLocalType = new FullyQualifiedJavaType(ThreadLocal.class.getName());
        threadLocalType.addTypeArgument(topLevelClass.getType());
        instanceField.setType(threadLocalType);
        instanceField.setName("instance");
        instanceField.setInitializationString("new " + threadLocalType.getShortName() + "()");
        topLevelClass.addField(instanceField);

        // public static initialize()
        Method initializeMethod = new Method();
        initializeMethod.setVisibility(JavaVisibility.PUBLIC);
        initializeMethod.setStatic(true);
        initializeMethod.setName("initialize");
        initializeMethod.addBodyLine(String.format("%s example=new %s();", topLevelClass.getType().getShortName(), topLevelClass.getType().getShortName()));
        initializeMethod.addBodyLine("instance.set(example);");
        initializeMethod.addBodyLine("return example;");
        initializeMethod.setReturnType(topLevelClass.getType());
        topLevelClass.addMethod(initializeMethod);


        Method orderByMethod = new Method();
        orderByMethod.setVisibility(JavaVisibility.PUBLIC);
        orderByMethod.setName("orderBy");
        orderByMethod.addParameter(ParameterFactory.get(ParameterType.STRING, "clause"));
        orderByMethod.addBodyLine("this.setOrderByClause(clause);");
        orderByMethod.addBodyLine("return this;");
        orderByMethod.setReturnType(topLevelClass.getType());
        topLevelClass.addMethod(orderByMethod);


        Method startMethod = new Method();
        startMethod.setVisibility(JavaVisibility.PUBLIC);
        startMethod.setName("start");
        startMethod.addParameter(ParameterFactory.get(ParameterType.INT, "start"));
        startMethod.addBodyLine("this.setStart(start);");
        startMethod.addBodyLine("return this;");
        startMethod.setReturnType(topLevelClass.getType());
        topLevelClass.addMethod(startMethod);


        Method limitMethod = new Method();
        limitMethod.setVisibility(JavaVisibility.PUBLIC);
        limitMethod.setName("limit");
        limitMethod.addParameter(ParameterFactory.get(ParameterType.INT, "limit"));
        limitMethod.addBodyLine("this.setStart(limit);");
        limitMethod.addBodyLine("return this;");
        limitMethod.setReturnType(topLevelClass.getType());
        topLevelClass.addMethod(limitMethod);


        Method distinctMethod = new Method();
        distinctMethod.setVisibility(JavaVisibility.PUBLIC);
        distinctMethod.setName("distinct");
        distinctMethod.addParameter(ParameterFactory.get(ParameterType.BOOL, "distinct"));
        distinctMethod.addBodyLine("this.setDistinct(distinct);");
        distinctMethod.addBodyLine("return this;");
        distinctMethod.setReturnType(topLevelClass.getType());
        topLevelClass.addMethod(distinctMethod);


        Iterator<InnerClass> iterator = topLevelClass.getInnerClasses().iterator();
        while (iterator.hasNext()) {
            InnerClass innerClass = iterator.next();
            if (innerClass.getType().getShortName().equals("GeneratedCriteria")) {
                Method getMethod = new Method();
                getMethod.setVisibility(JavaVisibility.PUBLIC);
                getMethod.setName("get");
                getMethod.addBodyLine("return instance.get();");
                getMethod.setReturnType(topLevelClass.getType());
                innerClass.addMethod(getMethod);
            }
        }

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }
}
