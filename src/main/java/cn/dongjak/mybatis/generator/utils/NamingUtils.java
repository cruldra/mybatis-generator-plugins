package cn.dongjak.mybatis.generator.utils;

public class NamingUtils {
    public static String hump(String name) {
        return NameStyleConverter.convert(name, "^*$/ru");
    }
}
