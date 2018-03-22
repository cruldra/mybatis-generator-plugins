package cn.dongjak.mybatis.generator.plugins;

import cn.dongjak.mybatis.generator.utils.ParameterType;
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

public class SpringWebControllerPlugin extends PluginAdapter {

    private String targetProject;

    private String packageName;

    private String servicePackage;

    @Override
    public boolean validate(List<String> list) {

        this.packageName = this.getProperties().getProperty("packageName");
        this.targetProject = this.getProperties().getProperty("targetProject");
        this.servicePackage = this.getProperties().getProperty("servicePackage");
        return StringUtils.isNotBlank(this.packageName) && StringUtils.isNotBlank(this.targetProject) && StringUtils.isNotBlank(this.servicePackage);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        List<GeneratedJavaFile> javaFiles = super.contextGenerateAdditionalJavaFiles(introspectedTable);
        if (CollectionUtils.isEmpty(javaFiles)) javaFiles = new ArrayList<>();

        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String modelParameterName = modelType.getShortName().substring(0, 1).toLowerCase() + modelType.getShortName().substring(1, modelType.getShortName().length());
        String bindResultParameterName = "bindResult";
        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        String exampleParameterName = exampleType.getShortName().substring(0, 1).toLowerCase() + exampleType.getShortName().substring(1, exampleType.getShortName().length());
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        //region List<当前模型>
        FullyQualifiedJavaType genericListTypeOfModelType = FullyQualifiedJavaType.getNewListInstance();
        genericListTypeOfModelType.addTypeArgument(modelType);
        //endregion
        FullyQualifiedJavaType exampleFormType = new FullyQualifiedJavaType("org.apache.org.apache.ibatis.form.ExampleForm");
        listType.addTypeArgument(modelType);
        FullyQualifiedJavaType intType = FullyQualifiedJavaType.getIntInstance();
        FullyQualifiedJavaType boolType = FullyQualifiedJavaType.getBooleanPrimitiveInstance();
        FullyQualifiedJavaType responseEntityType = new FullyQualifiedJavaType("org.springframework.http.ResponseEntity");
        FullyQualifiedJavaType bindResultType = new FullyQualifiedJavaType("org.springframework.validation.BindingResult");
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        StringBuilder controlUrlBuilder = new StringBuilder("/");
        for (String namePart : ArrayUtils.subarray(tableName.split("_"), 1, tableName.split("_").length))
            controlUrlBuilder.append(namePart).append("/");
        controlUrlBuilder = new StringBuilder(controlUrlBuilder.substring(0, controlUrlBuilder.length() - 1));

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(this.servicePackage + "." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "Service");
        //生成controller
        TopLevelClass controllerClass = new TopLevelClass(this.packageName + "." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "Controller");
        controllerClass.setVisibility(JavaVisibility.PUBLIC);
        //region 导入包
        controllerClass.addImportedType("org.springframework.web.bind.annotation.RestController");
        controllerClass.addImportedType("io.swagger.annotations.Api");
        controllerClass.addImportedType("io.swagger.annotations.ApiParam");
        controllerClass.addImportedType(responseEntityType);
        controllerClass.addImportedType("org.springframework.web.bind.annotation.RequestBody");
        controllerClass.addImportedType("org.springframework.web.bind.annotation.RequestMapping");
        controllerClass.addImportedType("org.springframework.web.bind.annotation.ResponseBody");
        controllerClass.addImportedType("org.springframework.web.bind.annotation.RequestMethod");
        controllerClass.addImportedType("org.springframework.web.bind.annotation.PathVariable");
        controllerClass.addImportedType("cn.dongjak.mybatis.generator.utils.ExampleForm");
        controllerClass.addImportedType("org.springframework.validation.annotation.Validated");
        controllerClass.addImportedType(exampleFormType);
        controllerClass.addImportedType("io.swagger.annotations.ApiOperation");
        controllerClass.addImportedType(bindResultType);
        controllerClass.addImportedType("java.util.Optional");
        controllerClass.addImportedType(serviceType);
        controllerClass.addImportedType(modelType);
        controllerClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        //endregion
        controllerClass.addAnnotation("@RestController");
        controllerClass.addAnnotation("@RequestMapping(\"" + controlUrlBuilder.toString() + "\")");
        controllerClass.addAnnotation("@Api(tags = {\"" + modelType.getShortName() + "\"}, description = \"" + modelType.getShortName() + "\", protocols = \"http\")");

        Field serviceField = new Field();
        serviceField.setName("service");
        serviceField.setVisibility(JavaVisibility.PRIVATE);
        serviceField.setType(serviceType);
        serviceField.addAnnotation("@Autowired");
        controllerClass.addField(serviceField);


        //region 生成add
        /*
        @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("add CkbSysUser")
    public boolean add(@RequestBody @ApiParam CkbSysUser user) {

        return service.add(user);
    }
         */

        Method addMethod = new Method();
        addMethod.addAnnotation("@RequestMapping( method = RequestMethod.POST)");
        addMethod.addAnnotation("@ApiOperation(value=\"add" + modelType.getShortName() + "\")");
        addMethod.setVisibility(JavaVisibility.PUBLIC);
        //返回值
        FullyQualifiedJavaType addMethodResponseEntityType = newResponseEntityType(null);
        addMethodResponseEntityType.addTypeArgument(ParameterType.BOOLEAN.getType());
        addMethod.setReturnType(addMethodResponseEntityType);
        //方法名
        addMethod.setName("add");
        //参数
        Parameter modelParameter = new Parameter(modelType, modelParameterName);
        modelParameter.addAnnotation("@RequestBody ");
        modelParameter.addAnnotation("@ApiParam(required = true)");
        modelParameter.addAnnotation("@Validated(" + modelType + ".Create.class)");
        addMethod.addParameter(modelParameter);

        Parameter bindResultParameter = new Parameter(bindResultType, bindResultParameterName);
        addMethod.addParameter(bindResultParameter);
        //方法体
        addMethod.addBodyLine(" return ResponseEntity.ok(service.add(" + modelParameterName + "));");

        controllerClass.addMethod(addMethod);
        //endregion


        //region 生成get by id
        /**
         * @RequestMapping(value = "/{id}", method = RequestMethod.GET)
         @ApiOperation("get CkbSysUser by id")
         public ResponseEntity<CkbSysUser> get(@PathVariable @ApiParam Integer id) {
         return ResponseEntity.ok(service.getById(id));
         }
         */

        Method getMethod = new Method();
        getMethod.addAnnotation("@RequestMapping(value = \"/{id}\", method = RequestMethod.GET)");
        getMethod.addAnnotation("@ApiOperation(value=\"get " + modelType.getShortName() + " by id\")");
        getMethod.setVisibility(JavaVisibility.PUBLIC);
        //返回值
        FullyQualifiedJavaType getMethodResponseEntityType = newResponseEntityType(null);
        getMethodResponseEntityType.addTypeArgument(modelType);
        getMethod.setReturnType(getMethodResponseEntityType);
        //方法名
        getMethod.setName("get");
        //参数
        Parameter parameter = new Parameter(ParameterType.INT.getType(), "id");
        parameter.addAnnotation("@PathVariable ");
        parameter.addAnnotation("@ApiParam(required = true)");
        getMethod.addParameter(parameter);
        //方法体
        getMethod.addBodyLine(" return ResponseEntity.ok(service.getById(id));");

        controllerClass.addMethod(getMethod);
        //endregion

        //region 生成 get by example
        /*
        @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("get CkbSysUser by example")
    public ResponseEntity<List<CkbSysUser>> get(@RequestBody @ApiParam ExampleForm example) {
        return ResponseEntity.ok(service.getByExample(example));
    }
         */

        Method getByExampleMethod = new Method();
        getByExampleMethod.addAnnotation("@RequestMapping(method = RequestMethod.GET)");
        getByExampleMethod.addAnnotation("@ApiOperation(value=\"get " + modelType.getShortName() + " by example\")");
        getByExampleMethod.setVisibility(JavaVisibility.PUBLIC);
        //返回值
        FullyQualifiedJavaType getByExampleMethodResponseEntityType = newResponseEntityType(null);
        getByExampleMethodResponseEntityType.addTypeArgument(genericListTypeOfModelType);
        getByExampleMethod.setReturnType(getByExampleMethodResponseEntityType);
        //方法名
        getByExampleMethod.setName("get");
        //参数
        Parameter getByExampleMethodParameter1 = new Parameter(exampleFormType, "example");
        getByExampleMethodParameter1.addAnnotation("@RequestBody ");
        getByExampleMethodParameter1.addAnnotation("@ApiParam(required = true)");
        getByExampleMethod.addParameter(getByExampleMethodParameter1);
        //方法体
        getByExampleMethod.addBodyLine(" return ResponseEntity.ok(service.getByExample(" + exampleType.getShortName() + ".initialize().of(example)));");

        controllerClass.addMethod(getByExampleMethod);

//endregion

        //region 生成update方法
        /*
        @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation("update CkbSysUser")
    public ResponseEntity put(@PathVariable @ApiParam Integer id, @RequestBody @ApiParam CkbSysUser user) {
        return ResponseEntity.ok(service.update(user));
    }
         */
        Method controllerUpdateMethod = new Method();
        controllerUpdateMethod.addAnnotation("@RequestMapping(value = \"/{id}\", method = RequestMethod.PUT)");
        controllerUpdateMethod.addAnnotation("@ApiOperation(value=\"update " + modelType.getShortName() + "\")");
        controllerUpdateMethod.setVisibility(JavaVisibility.PUBLIC);
        controllerUpdateMethod.setName("update");
        Parameter idParameter = new Parameter(intType, "id");
        idParameter.addAnnotation("@PathVariable(required = true)");
        idParameter.addAnnotation("@ApiParam");
        controllerUpdateMethod.addParameter(idParameter);
        Parameter modelParameter2 = new Parameter(modelType, modelParameterName);
        modelParameter2.addAnnotation("@RequestBody");
        modelParameter2.addAnnotation("@ApiParam");
        modelParameter2.addAnnotation("@Validated(" + modelType + ".Update.class)");


        controllerUpdateMethod.addParameter(modelParameter2);
        controllerUpdateMethod.addParameter(bindResultParameter);
        controllerUpdateMethod.addBodyLine(" return ResponseEntity.ok(service.update(" + modelParameterName + "));");
        FullyQualifiedJavaType controllerUpdateMethodResultType = newResponseEntityType(null);
        controllerUpdateMethodResultType.addTypeArgument(ParameterType.BOOLEAN.getType());
        controllerUpdateMethod.setReturnType(controllerUpdateMethodResultType);
        controllerClass.addMethod(controllerUpdateMethod);
        //endregion

        //region 生成delete方法
/*
@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation("delete CkbSysUser by id")
    public ResponseEntity<Boolean> delete(@PathVariable(required = true) @ApiParam Integer id) throws IOException {
        return ResponseEntity.ok(service.deleteById(id));
    }
 */
        Method controllerDeleteMethod = new Method();
        controllerDeleteMethod.addAnnotation("@RequestMapping(value = \"/{id}\", method = RequestMethod.DELETE)");
        controllerDeleteMethod.addAnnotation("@ApiOperation(value=\"delete " + modelType.getShortName() + " by id\")");
        controllerDeleteMethod.setVisibility(JavaVisibility.PUBLIC);
        controllerDeleteMethod.setName("delete");
        controllerDeleteMethod.addParameter(idParameter);
        controllerDeleteMethod.addBodyLine(" return ResponseEntity.ok(service.deleteById(id));");
        controllerDeleteMethod.setReturnType(controllerUpdateMethodResultType);
        controllerClass.addMethod(controllerDeleteMethod);
        //endregion
        GeneratedJavaFile controllerFile = new GeneratedJavaFile(controllerClass, this.targetProject, "UTF-8", new DefaultJavaFormatter());
        javaFiles.add(controllerFile);

        return javaFiles;
    }


    private FullyQualifiedJavaType newResponseEntityType(ParameterType type) {
        //org.springframework.http.ResponseEntity
        FullyQualifiedJavaType responseEntityType = new FullyQualifiedJavaType("org.springframework.http.ResponseEntity");
        if (Optional.ofNullable(type).isPresent())
            switch (type) {
                case LIST:

                    break;
            }

        return responseEntityType;
    }


}


