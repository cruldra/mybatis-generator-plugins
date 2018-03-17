package cn.dongjak.mybatis.generator.utils;

import org.mybatis.generator.api.dom.java.Parameter;

public class ParameterFactory {


    private ParameterType type;
    private String name;

    public ParameterFactory(ParameterType type, String name) {
        this.type = type;
        this.name = name;
    }

    public static Parameter get(ParameterType type, String name) {

        return new Parameter(type.getType(), name);
    }
}

