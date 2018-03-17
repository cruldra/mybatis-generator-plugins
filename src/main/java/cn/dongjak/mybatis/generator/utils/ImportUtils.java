package cn.dongjak.mybatis.generator.utils;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.Iterator;

public class ImportUtils {


    public static boolean checkIsImported(TopLevelClass topLevelClass, String clsname) {
        Iterator<FullyQualifiedJavaType> iterator = topLevelClass.getImportedTypes().iterator();

        while (iterator.hasNext()) {
            FullyQualifiedJavaType javaType = iterator.next();
            if (javaType.getFullyQualifiedName().equals(clsname)) return true;
        }

        return false;
    }
}
