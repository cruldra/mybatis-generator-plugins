package cn.dongjak.mybatis.generator.plugins;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;

import java.util.ArrayList;
import java.util.List;

public class SpringDataServicePlugin extends PluginAdapter {

    private String targetProject;

    private String packageName;

    @Override
    public boolean validate(List<String> list) {

        this.packageName = this.getProperties().getProperty("packageName");
        this.targetProject = this.getProperties().getProperty("targetProject");
        return StringUtils.isNotBlank(this.packageName) && StringUtils.isNotBlank(this.targetProject);
    }


    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        List<GeneratedJavaFile> javaFiles = super.contextGenerateAdditionalJavaFiles(introspectedTable);
        if (CollectionUtils.isEmpty(javaFiles)) javaFiles = new ArrayList<>();

        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String modelParameterName = modelType.getShortName().substring(0, 1).toLowerCase() + modelType.getShortName().substring(1, modelType.getShortName().length());
        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        String exampleParameterName = exampleType.getShortName().substring(0, 1).toLowerCase() + exampleType.getShortName().substring(1, exampleType.getShortName().length());
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        listType.addTypeArgument(modelType);
        FullyQualifiedJavaType intType = FullyQualifiedJavaType.getIntInstance();
        FullyQualifiedJavaType longType = new FullyQualifiedJavaType("java.lang.Long");
        FullyQualifiedJavaType boolType = FullyQualifiedJavaType.getBooleanPrimitiveInstance();
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        StringBuilder controlUrlBuilder = new StringBuilder("/");
        for (String namePart : ArrayUtils.subarray(tableName.split("_"), 1, tableName.split("_").length))
            controlUrlBuilder.append(namePart).append("/");
        controlUrlBuilder = new StringBuilder(controlUrlBuilder.substring(0, controlUrlBuilder.length() - 1));


        //生成service
        if (StringUtils.isNotBlank(this.packageName)) {
            TopLevelClass serviceClass = new TopLevelClass(this.packageName + "." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "Service");
            serviceClass.setVisibility(JavaVisibility.PUBLIC);
            serviceClass.addImportedType("org.springframework.stereotype.Service");
            serviceClass.addImportedType(modelType);
            serviceClass.addImportedType(mapperType);
            serviceClass.addImportedType(exampleType);
            serviceClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
            serviceClass.addImportedType("org.springframework.transaction.annotation.Transactional");
            serviceClass.addImportedType("java.util.List");
            serviceClass.addAnnotation("@Service");
            serviceClass.addAnnotation("@Transactional");

            Field mapperField = new Field();
            mapperField.setName("mapper");
            mapperField.setVisibility(JavaVisibility.PRIVATE);
            mapperField.setType(mapperType);
            mapperField.addAnnotation("@Autowired");
            serviceClass.addField(mapperField);

            Method serviceGetByIdMethod = new Method();
            serviceGetByIdMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceGetByIdMethod.setName("getById");
            serviceGetByIdMethod.addParameter(new Parameter(intType.getPrimitiveTypeWrapper(), "id"));
            serviceGetByIdMethod.addBodyLine(" return mapper.selectByPrimaryKey(id);");
            serviceGetByIdMethod.setReturnType(modelType);
            serviceClass.addMethod(serviceGetByIdMethod);


            Method serviceGetAllMethod = new Method();
            serviceGetAllMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceGetAllMethod.setName("getAll");
            serviceGetAllMethod.addBodyLine(" return mapper.selectByExample(new " + exampleType.getShortName() + "());");
            serviceGetAllMethod.setReturnType(listType);
            serviceClass.addMethod(serviceGetAllMethod);

            Method serviceGetByExampleMethod = new Method();
            serviceGetByExampleMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceGetByExampleMethod.setName("getByExample");
            serviceGetByExampleMethod.addParameter(new Parameter(exampleType, exampleParameterName));
            serviceGetByExampleMethod.addBodyLine("return mapper.selectByExample(" + exampleParameterName + ");");
            serviceGetByExampleMethod.setReturnType(listType);
            serviceClass.addMethod(serviceGetByExampleMethod);

            Method serviceAddMethod = new Method();
            serviceAddMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceAddMethod.setName("add");
            serviceAddMethod.addParameter(new Parameter(modelType, modelParameterName));
            serviceAddMethod.addBodyLine("return mapper.insertSelective(" + modelParameterName + ") > 0;");
            serviceAddMethod.setReturnType(boolType);
            serviceClass.addMethod(serviceAddMethod);

            Method serviceUpdateMethod = new Method();
            serviceUpdateMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceUpdateMethod.setName("update");
            serviceUpdateMethod.addParameter(new Parameter(modelType, modelParameterName));
            serviceUpdateMethod.addBodyLine("return mapper.updateByPrimaryKeySelective(" + modelParameterName + ") > 0;");
            serviceUpdateMethod.setReturnType(boolType);
            serviceClass.addMethod(serviceUpdateMethod);

            Method serviceDeleteMethod = new Method();
            serviceDeleteMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceDeleteMethod.setName("deleteById");
            serviceDeleteMethod.addParameter(new Parameter(intType.getPrimitiveTypeWrapper(), "id"));
            serviceDeleteMethod.addBodyLine("return mapper.deleteByPrimaryKey(id) > 0;");
            serviceDeleteMethod.setReturnType(boolType);
            serviceClass.addMethod(serviceDeleteMethod);


            Method getCountMethod = new Method();
            getCountMethod.setVisibility(JavaVisibility.PUBLIC);
            getCountMethod.setName("getCount");
            getCountMethod.addBodyLine(" return mapper.countByExample(new " + exampleType.getShortName() + "());");
            getCountMethod.setReturnType(longType);
            serviceClass.addMethod(getCountMethod);


            Method countByExampleMethod = new Method();
            countByExampleMethod.setVisibility(JavaVisibility.PUBLIC);
            countByExampleMethod.setName("countByExample");
            countByExampleMethod.addParameter(new Parameter(exampleType, exampleParameterName));
            countByExampleMethod.addBodyLine(" return countByExample(" + exampleParameterName + ");");
            countByExampleMethod.setReturnType(longType);
            serviceClass.addMethod(countByExampleMethod);

            GeneratedJavaFile serviceFile = new GeneratedJavaFile(serviceClass, this.targetProject, "UTF-8", new DefaultJavaFormatter());
            javaFiles.add(serviceFile);
        }


        return javaFiles;
    }
}
