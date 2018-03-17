package cn.dongjak.mybatis.generator.utils;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

public enum ParameterType {
    STRING(new FullyQualifiedJavaType(String.class.getName())),
    INT(FullyQualifiedJavaType.getIntInstance()),
    INTEGER(new FullyQualifiedJavaType(Integer.class.getName())),
    BOOL(FullyQualifiedJavaType.getBooleanPrimitiveInstance()),
    BOOLEAN(new FullyQualifiedJavaType(Boolean.class.getName()));

    ParameterType(FullyQualifiedJavaType type) {
        this.type = type;
    }

    private FullyQualifiedJavaType type;

    public FullyQualifiedJavaType getType() {
        return type;
    }
}
