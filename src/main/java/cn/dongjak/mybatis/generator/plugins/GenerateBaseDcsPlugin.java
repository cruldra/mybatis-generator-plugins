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
import java.util.Optional;

public class GenerateBaseDcsPlugin extends PluginAdapter {

    private String fileEncoding = "utf-8";
    private String targetProject;
    private String baseDaoType;
    private String daoImplPackage;
    private String baseServiceType;
    private String serviceImplPackage;
    private String controlImplPackage;
    private String controlParametersPackage;
    private String jsonResponseClass;

    @Override
    public boolean validate(List<String> list) {
        this.fileEncoding = getPropertyIfExisted("fileEncoding");
        this.targetProject = getPropertyIfExisted("targetProject");
        this.baseDaoType = getPropertyIfExisted("baseDaoType");
        this.daoImplPackage = getPropertyIfExisted("daoImplPackage");
        this.baseServiceType = getPropertyIfExisted("baseServiceType");
        this.serviceImplPackage = getPropertyIfExisted("serviceImplPackage");
        this.controlImplPackage = getPropertyIfExisted("controlImplPackage");
        this.controlParametersPackage = getPropertyIfExisted("controlParametersPackage");
        this.jsonResponseClass = getPropertyIfExisted("jsonResponseClass");
        if (StringUtils.isBlank(targetProject)) {
            list.add(String.format("GenerateBaseDcsPlugin:无法正确写入Dao、Service及Controller文件,因为没有指定项目路径targetProject"));
            return false;
        }

        if (StringUtils.isBlank(daoImplPackage)) {
            list.add(String.format("GenerateBaseDcsPlugin:无法正确写入Dao文件,因为没有指定项目Dao实现类存储路径daoImplPackage"));
            return false;
        }
        return true;
    }


    private String getPropertyIfExisted(String key) {
        return StringUtils.isNotBlank(key) && this.properties.containsKey(key) ? this
                .properties.getProperty(key) : null;
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
        FullyQualifiedJavaType boolType = FullyQualifiedJavaType.getBooleanPrimitiveInstance();
        FullyQualifiedJavaType jsonResponseType = new FullyQualifiedJavaType(this.jsonResponseClass);
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        StringBuilder controlUrlBuilder = new StringBuilder("/");
        for (String namePart : ArrayUtils.subarray(tableName.split("_"), 1, tableName.split("_").length))
            controlUrlBuilder.append(namePart).append("/");
        controlUrlBuilder = new StringBuilder(controlUrlBuilder.substring(0, controlUrlBuilder.length() - 1));
        //生成dao
        TopLevelClass daoClass = new TopLevelClass(daoImplPackage + "." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "Dao");
        daoClass.setVisibility(JavaVisibility.PUBLIC);
        daoClass.addImportedType("org.springframework.stereotype.Repository");
        daoClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        daoClass.addImportedType("java.util.List");
        daoClass.addImportedType("org.apache.commons.collections.CollectionUtils");
        daoClass.addImportedType(mapperType);
        daoClass.addImportedType(modelType);
        daoClass.addImportedType(exampleType);
        daoClass.addAnnotation("@Repository");


        Field mapperField = new Field();
        mapperField.setName("mapper");
        mapperField.setVisibility(JavaVisibility.PRIVATE);
        mapperField.setType(mapperType);
        mapperField.addAnnotation("@Autowired");
        daoClass.addField(mapperField);


        Method getByIdMethod = new Method();
        getByIdMethod.setVisibility(JavaVisibility.PUBLIC);
        getByIdMethod.setName("getById");
        getByIdMethod.addParameter(new Parameter(intType.getPrimitiveTypeWrapper(), "id"));
        getByIdMethod.addBodyLine("return mapper.selectByPrimaryKey(id);");
        getByIdMethod.setReturnType(modelType);
        daoClass.addMethod(getByIdMethod);

        Method getFirstMethod = new Method();
        getFirstMethod.setVisibility(JavaVisibility.PUBLIC);
        getFirstMethod.setName("getFirst");
        getFirstMethod.addParameter(new Parameter(exampleType, exampleParameterName));
        getFirstMethod.addBodyLine("List<" + modelType.getShortName() + "> results;");
        getFirstMethod.addBodyLine("return CollectionUtils.isNotEmpty(results = listByExample(" + exampleParameterName + ")) ? results.get(0) : null;");
        getFirstMethod.setReturnType(modelType);
        daoClass.addMethod(getFirstMethod);

        Method listAllMethod = new Method();
        listAllMethod.setVisibility(JavaVisibility.PUBLIC);
        listAllMethod.setName("listAll");
        listAllMethod.addBodyLine("return listByExample(new " + exampleType.getShortName() + "());");
        listAllMethod.setReturnType(listType);
        daoClass.addMethod(listAllMethod);

        Method listByExampleMethod = new Method();
        listByExampleMethod.setVisibility(JavaVisibility.PUBLIC);
        listByExampleMethod.setName("listByExample");
        listByExampleMethod.addParameter(new Parameter(exampleType, exampleParameterName));
        listByExampleMethod.addBodyLine("return mapper.selectByExample(" + exampleParameterName + ");");
        listByExampleMethod.setReturnType(listType);
        daoClass.addMethod(listByExampleMethod);

        Method addMethod = new Method();
        addMethod.setVisibility(JavaVisibility.PUBLIC);
        addMethod.setName("add");
        addMethod.addParameter(new Parameter(modelType, modelParameterName));
        addMethod.addBodyLine("return mapper.insertSelective(" + modelParameterName + ");");
        addMethod.setReturnType(intType);
        daoClass.addMethod(addMethod);

        Method updateMethod = new Method();
        updateMethod.setVisibility(JavaVisibility.PUBLIC);
        updateMethod.setName("update");
        updateMethod.addParameter(new Parameter(modelType, modelParameterName));
        updateMethod.addBodyLine("return mapper.updateByPrimaryKeySelective(" + modelParameterName + ");");
        updateMethod.setReturnType(intType);
        daoClass.addMethod(updateMethod);

        Method deleteMethod = new Method();
        deleteMethod.setVisibility(JavaVisibility.PUBLIC);
        deleteMethod.setName("delete");
        deleteMethod.addParameter(new Parameter(intType.getPrimitiveTypeWrapper(), "id"));
        deleteMethod.addBodyLine("return mapper.deleteByPrimaryKey(id);");
        deleteMethod.setReturnType(intType);
        daoClass.addMethod(deleteMethod);


        Method countMethod = new Method();
        countMethod.setVisibility(JavaVisibility.PUBLIC);
        countMethod.setName("count");
        countMethod.addParameter(new Parameter(exampleType, exampleParameterName));
        countMethod.addBodyLine(" return (int)mapper.countByExample(" + exampleParameterName + ");");
        countMethod.setReturnType(intType);
        daoClass.addMethod(countMethod);


        GeneratedJavaFile daoFile = new GeneratedJavaFile(daoClass, this.targetProject, fileEncoding, new DefaultJavaFormatter());
        javaFiles.add(daoFile);


        //生成service
        if (StringUtils.isNotBlank(this.serviceImplPackage)) {
            TopLevelClass serviceClass = new TopLevelClass(this.serviceImplPackage + "." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "Service");
            serviceClass.setVisibility(JavaVisibility.PUBLIC);
            serviceClass.addImportedType("org.springframework.stereotype.Service");
            serviceClass.addImportedType(daoClass.getType());
            serviceClass.addImportedType(modelType);
            serviceClass.addImportedType(exampleType);
            serviceClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
            serviceClass.addImportedType("org.springframework.transaction.annotation.Transactional");
            serviceClass.addImportedType("java.util.List");
            serviceClass.addAnnotation("@Service");
            serviceClass.addAnnotation("@Transactional(rollbackFor = {Exception.class})");

            Field daoField = new Field();
            daoField.setName("dao");
            daoField.setVisibility(JavaVisibility.PRIVATE);
            daoField.setType(daoClass.getType());
            daoField.addAnnotation("@Autowired");
            serviceClass.addField(daoField);

            Method serviceGetByIdMethod = new Method();
            serviceGetByIdMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceGetByIdMethod.setName("get");
            serviceGetByIdMethod.addParameter(new Parameter(intType.getPrimitiveTypeWrapper(), "id"));
            serviceGetByIdMethod.addBodyLine(" return dao.getById(id);");
            serviceGetByIdMethod.setReturnType(modelType);
            serviceClass.addMethod(serviceGetByIdMethod);


            Method serviceGetFirstMethod = new Method();
            serviceGetFirstMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceGetFirstMethod.setName("get");
            serviceGetFirstMethod.addParameter(new Parameter(exampleType, exampleParameterName));
            serviceGetFirstMethod.addBodyLine("  return dao.getFirst(" + exampleParameterName + ");");
            serviceGetFirstMethod.setReturnType(modelType);
            serviceClass.addMethod(serviceGetFirstMethod);


            Method serviceListAllMethod = new Method();
            serviceListAllMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceListAllMethod.setName("list");
            serviceListAllMethod.addBodyLine(" return dao.listAll();");
            serviceListAllMethod.setReturnType(listType);
            serviceClass.addMethod(serviceListAllMethod);

            Method serviceListByExampleMethod = new Method();
            serviceListByExampleMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceListByExampleMethod.setName("list");
            serviceListByExampleMethod.addParameter(new Parameter(exampleType, exampleParameterName));
            serviceListByExampleMethod.addBodyLine("return dao.listByExample(" + exampleParameterName + ");");
            serviceListByExampleMethod.setReturnType(listType);
            serviceClass.addMethod(serviceListByExampleMethod);

            Method serviceAddMethod = new Method();
            serviceAddMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceAddMethod.setName("add");
            serviceAddMethod.addParameter(new Parameter(modelType, modelParameterName));
            serviceAddMethod.addBodyLine("return dao.add(" + modelParameterName + ") > 0;");
            serviceAddMethod.setReturnType(boolType);
            serviceClass.addMethod(serviceAddMethod);

            Method serviceUpdateMethod = new Method();
            serviceUpdateMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceUpdateMethod.setName("update");
            serviceUpdateMethod.addParameter(new Parameter(modelType, modelParameterName));
            serviceUpdateMethod.addBodyLine("return dao.update(" + modelParameterName + ") > 0;");
            serviceUpdateMethod.setReturnType(boolType);
            serviceClass.addMethod(serviceUpdateMethod);

            Method serviceDeleteMethod = new Method();
            serviceDeleteMethod.setVisibility(JavaVisibility.PUBLIC);
            serviceDeleteMethod.setName("delete");
            serviceDeleteMethod.addParameter(new Parameter(intType.getPrimitiveTypeWrapper(), "id"));
            serviceDeleteMethod.addBodyLine("return dao.delete(id) > 0;");
            serviceDeleteMethod.setReturnType(boolType);
            serviceClass.addMethod(serviceDeleteMethod);


            Method getTotalMethod = new Method();
            getTotalMethod.setVisibility(JavaVisibility.PUBLIC);
            getTotalMethod.setName("getTotal");
            getTotalMethod.addBodyLine(" return dao.count(new " + exampleType.getShortName() + "());");
            getTotalMethod.setReturnType(intType);
            serviceClass.addMethod(getTotalMethod);


            Method countByExampleMethod = new Method();
            countByExampleMethod.setVisibility(JavaVisibility.PUBLIC);
            countByExampleMethod.setName("countByExample");
            serviceDeleteMethod.addParameter(new Parameter(exampleType, exampleParameterName));
            countByExampleMethod.addBodyLine(" return dao.count(" + exampleParameterName + ");");
            countByExampleMethod.setReturnType(intType);
            serviceClass.addMethod(countByExampleMethod);

            GeneratedJavaFile serviceFile = new GeneratedJavaFile(serviceClass, this.targetProject, fileEncoding, new DefaultJavaFormatter());
            javaFiles.add(serviceFile);

            //生成controller
            if (StringUtils.isNotBlank(controlImplPackage)) {
                TopLevelClass parameterClass = null;
                if (StringUtils.isNotBlank(controlParametersPackage)) {
                    parameterClass = new TopLevelClass(this.controlParametersPackage + "." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "Bp");
                    parameterClass.setVisibility(JavaVisibility.PUBLIC);
                    parameterClass.addImportedType(exampleType);
                    FullyQualifiedJavaType abstractBpType = new FullyQualifiedJavaType("cn.dongjak.bp.AbstractBp");
                    abstractBpType.addTypeArgument(exampleType);
                    parameterClass.setSuperClass(abstractBpType);

                    Method createExampleInstanceMethod = new Method();
                    createExampleInstanceMethod.setVisibility(JavaVisibility.PROTECTED);
                    createExampleInstanceMethod.setName("createExampleInstance");
                    createExampleInstanceMethod.addBodyLine("return new " + exampleType.getShortName() + "();");
                    createExampleInstanceMethod.setReturnType(exampleType);
                    createExampleInstanceMethod.addAnnotation("@Override");
                    parameterClass.addMethod(createExampleInstanceMethod);

                    GeneratedJavaFile parameterFile = new GeneratedJavaFile(parameterClass, this.targetProject, fileEncoding, new DefaultJavaFormatter());
                    javaFiles.add(parameterFile);
                }

                TopLevelClass controllerClass = new TopLevelClass(this.controlImplPackage + "." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "Controller");
                controllerClass.setVisibility(JavaVisibility.PUBLIC);
                controllerClass.addImportedType("org.springframework.stereotype.Controller");
                controllerClass.addImportedType("io.swagger.annotations.Api");
                controllerClass.addImportedType("io.swagger.annotations.ApiParam");
                controllerClass.addImportedType(this.jsonResponseClass);
                controllerClass.addImportedType("org.springframework.web.bind.annotation.RequestBody");
                controllerClass.addImportedType("org.springframework.web.bind.annotation.RequestMapping");
                controllerClass.addImportedType("org.springframework.web.bind.annotation.ResponseBody");
                controllerClass.addImportedType("org.springframework.web.bind.annotation.RequestMethod");
                controllerClass.addImportedType("org.springframework.web.bind.annotation.PathVariable");
                controllerClass.addImportedType("org.springframework.validation.annotation.Validated");
                controllerClass.addImportedType("io.swagger.annotations.ApiOperation");
                controllerClass.addImportedType("org.springframework.validation.BindingResult");
                controllerClass.addImportedType("java.util.Optional");
                controllerClass.addImportedType(serviceClass.getType());
                controllerClass.addImportedType(modelType);
                controllerClass.addImportedType(parameterClass.getType());
                controllerClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
                controllerClass.addAnnotation("@Controller");
                controllerClass.addAnnotation("@RequestMapping(\"" + controlUrlBuilder.toString() + "\")");
                controllerClass.addAnnotation("@Api(tags = {\"" + modelType.getShortName() + "\"}, description = \"" + modelType.getShortName() + "\", protocols = \"http\")");

                Field serviceField = new Field();
                serviceField.setName("service");
                serviceField.setVisibility(JavaVisibility.PRIVATE);
                serviceField.setType(serviceClass.getType());
                serviceField.addAnnotation("@Autowired");
                controllerClass.addField(serviceField);

                Method controlListMethod = new Method();
                controlListMethod.addAnnotation("@RequestMapping(value = \"/list\", method = {RequestMethod.POST, RequestMethod.GET}, produces = {\"application/json\"})");
                controlListMethod.addAnnotation("@ApiOperation(value=\"list " + modelType.getShortName() + " by example\")");
                controlListMethod.addAnnotation("@ResponseBody");
                controlListMethod.setVisibility(JavaVisibility.PUBLIC);
                controlListMethod.setName("list");
                if (Optional.ofNullable(parameterClass).isPresent()) {
                    Parameter parameter = new Parameter(parameterClass.getType(), "bp");
                    parameter.addAnnotation("@RequestBody(required = false)");
                    parameter.addAnnotation("@ApiParam");
                    //parameter.addAnnotation("@Validated(" + modelType + ".Publish.class)");
                    controlListMethod.addParameter(parameter);
                    controlListMethod.addBodyLine("return JSONResponse.ok(Optional.ofNullable(bp).isPresent() ? service.list(bp.getExample()) : service.list());");
                } else controlListMethod.addBodyLine("return JSONResponse.ok( service.list());");

                controlListMethod.setReturnType(jsonResponseType);
                controllerClass.addMethod(controlListMethod);


                Method controllerAddMethod = new Method();
                controllerAddMethod.addAnnotation("@RequestMapping(value = \"/add\", method = RequestMethod.POST, produces = {\"application/json\"})");
                controllerAddMethod.addAnnotation("@ApiOperation(value=\"add new " + modelType.getShortName() + "\")");
                controllerAddMethod.addAnnotation("@ResponseBody");
                controllerAddMethod.setVisibility(JavaVisibility.PUBLIC);
                controllerAddMethod.setName("add");
                Parameter modelParameter = new Parameter(modelType, modelParameterName);
                modelParameter.addAnnotation("@RequestBody");
                modelParameter.addAnnotation("@ApiParam");
                modelParameter.addAnnotation("@Validated(" + modelType + ".Create.class)");
                controllerAddMethod.addParameter(modelParameter);
                Parameter bindResultParameter = new Parameter(new FullyQualifiedJavaType("org.springframework.validation.BindingResult"), "bindingResult");
                controllerAddMethod.addParameter(bindResultParameter);
                controllerAddMethod.addBodyLine(" return JSONResponse.auto(service.add(" + modelParameterName + "), \"add " + modelType.getShortName() + "\");");
                controllerAddMethod.setReturnType(jsonResponseType);
                controllerClass.addMethod(controllerAddMethod);


                Method controllerUpdateMethod = new Method();
                controllerUpdateMethod.addAnnotation("@RequestMapping(value = \"/update/{id}\", method = RequestMethod.POST, produces = {\"application/json\"})");
                controllerUpdateMethod.addAnnotation("@ApiOperation(value=\"update " + modelType.getShortName() + "\")");
                controllerUpdateMethod.addAnnotation("@ResponseBody");
                controllerUpdateMethod.setVisibility(JavaVisibility.PUBLIC);
                controllerUpdateMethod.setName("update");
                Parameter idParameter = new Parameter(intType, "id");
                idParameter.addAnnotation("@PathVariable");
                idParameter.addAnnotation("@ApiParam");
                controllerUpdateMethod.addParameter(idParameter);
                modelParameter = new Parameter(modelType, modelParameterName);
                modelParameter.addAnnotation("@RequestBody");
                modelParameter.addAnnotation("@ApiParam");
                modelParameter.addAnnotation("@Validated(" + modelType + ".Update.class)");
                controllerUpdateMethod.addParameter(modelParameter);
                controllerUpdateMethod.addParameter(bindResultParameter);
                controllerUpdateMethod.addBodyLine(" return JSONResponse.auto(service.update(" + modelParameterName + "), \"update " + modelType.getShortName() + "\");");
                controllerUpdateMethod.setReturnType(jsonResponseType);
                controllerClass.addMethod(controllerUpdateMethod);

                Method controllerDeleteMethod = new Method();
                controllerDeleteMethod.addAnnotation("@RequestMapping(value = \"/delete/{id}\", method = RequestMethod.POST, produces = {\"application/json\"})");
                controllerDeleteMethod.addAnnotation("@ApiOperation(value=\"delete " + modelType.getShortName() + " by id\")");
                controllerDeleteMethod.addAnnotation("@ResponseBody");
                controllerDeleteMethod.setVisibility(JavaVisibility.PUBLIC);
                controllerDeleteMethod.setName("delete");
                controllerDeleteMethod.addParameter(idParameter);
                controllerDeleteMethod.addBodyLine(" return JSONResponse.auto(service.delete(id), \"delete " + modelType.getShortName() + " by id\");");
                controllerDeleteMethod.setReturnType(jsonResponseType);
                controllerClass.addMethod(controllerDeleteMethod);
                GeneratedJavaFile controllerFile = new GeneratedJavaFile(controllerClass, this.targetProject, fileEncoding, new DefaultJavaFormatter());
                javaFiles.add(controllerFile);
            }
        }


        return javaFiles;
    }
}
