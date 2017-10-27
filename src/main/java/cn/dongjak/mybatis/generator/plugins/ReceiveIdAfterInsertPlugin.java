package cn.dongjak.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class ReceiveIdAfterInsertPlugin extends PluginAdapter {

    private String keyProperty = "id";
    private String resultType = "int";

    @Override
    public boolean validate(List<String> list) {
        if (this.properties.containsKey("keyProperty"))
            this.keyProperty = this.properties.getProperty("keyProperty");
        if (this.properties.containsKey("resultType"))
            this.resultType = this.properties.getProperty("resultType");
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        generateSelectKeyElement(element, introspectedTable);
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        generateSelectKeyElement(element, introspectedTable);
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    private void generateSelectKeyElement(XmlElement insertElement, IntrospectedTable introspectedTable) {


        XmlElement selectKeyElement = new XmlElement("selectKey");
        selectKeyElement.addAttribute(new Attribute("order", "AFTER"));
        selectKeyElement.addAttribute(new Attribute("keyProperty", this.keyProperty));
        selectKeyElement.addAttribute(new Attribute("resultType", this.resultType));
        selectKeyElement.addElement(new TextElement("SELECT LAST_INSERT_ID()"));
        insertElement.addElement(0, selectKeyElement);

    }
}
