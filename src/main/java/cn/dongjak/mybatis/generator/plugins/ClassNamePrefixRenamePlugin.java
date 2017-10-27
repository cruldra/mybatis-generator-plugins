package cn.dongjak.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassNamePrefixRenamePlugin extends PluginAdapter {

    private String old;
    private String neww;
    private Pattern pattern;

    public ClassNamePrefixRenamePlugin() {
    }

    public boolean validate(List<String> warnings) {
        this.old = this.properties.getProperty("old");
        this.neww = this.properties.getProperty("neww");
        boolean valid = StringUtility.stringHasValue(this.old) && StringUtility.stringHasValue(this.neww);
        if (valid) {
            this.pattern = Pattern.compile(this.old);
        } else {
            if (!StringUtility.stringHasValue(this.old)) {
                warnings.add(Messages.getString("ValidationError.18", "RenameExampleClassPlugin", "old"));
            }

            if (!StringUtility.stringHasValue(this.neww)) {
                warnings.add(Messages.getString("ValidationError.18", "RenameExampleClassPlugin", "neww"));
            }
        }

        return valid;
    }

    public void initialized(IntrospectedTable introspectedTable) {
        String oldBaseRecordType = introspectedTable.getBaseRecordType();
        Matcher matcher = this.pattern.matcher(oldBaseRecordType);
        oldBaseRecordType = matcher.replaceAll(this.neww);
        introspectedTable.setBaseRecordType(oldBaseRecordType);

        String oldExampleType = introspectedTable.getExampleType();
        matcher = this.pattern.matcher(oldExampleType);
        oldExampleType = matcher.replaceAll(this.neww);
        introspectedTable.setExampleType(oldExampleType);

        String mapperType = introspectedTable.getMyBatis3JavaMapperType();
        matcher = this.pattern.matcher(mapperType);
        mapperType = matcher.replaceAll(this.neww);
        introspectedTable.setMyBatis3JavaMapperType(mapperType);
        introspectedTable.setMyBatis3XmlMapperFileName(mapperType.substring(mapperType.lastIndexOf(".") + 1, mapperType.length()) + ".xml");
    }


}
