package cn.dongjak.mybatis.generator.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.logging.Logger;

public final class NameStyleConverter {

    private static Logger logger = Logger.getLogger(NameStyleConverter.class
            .getName());

    /**
     * 根据指定的规则转换名称
     *
     * @param name 名称
     * @param rule 规则
     * @return 转换后表名
     */
    public static String convert(String name, String rule) {
        String[] options = getOptions(rule);

        String resultName = name;

        for (String option : options) {

            if (option.matches("n\\d+"))
                resultName = excludeIndex(resultName,
                        Integer.parseInt(option.substring(1, option.length())));
            else if (option.equals("ru-fu"))
                resultName = removeUnderlineAndSetFirstUpper(resultName);
            else if (option.equals("ru"))
                resultName = removeUnderline(resultName);
        }

        logger.info(String.format("依据规则%s转换名称%s到%s", rule, name, resultName));
        return resultName;

    }

    private static String removeUnderline(String name) {
        String str = removeUnderlineAndSetFirstUpper(name);
        return String.format("%s%s", str.substring(0, 1).toLowerCase(),
                str.substring(1, str.length()));
    }

    /**
     * 排除位于指定索引位置上的字符串
     * <p>
     * <pre>
     * <b>注:这里的索引位置是指字符串以下划线分隔后的字符串数组的索引位置</b>
     * </pre>
     *
     * @param itemName 表、列或Java实体名
     * @param index    索引位置
     * @return
     */
    private static String excludeIndex(String itemName, int index) {
        String[] arr = itemName.split("_");
        if (arr.length == 1)
            return itemName;
        arr = ArrayUtils.remove(arr, index);
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : arr)
            stringBuilder.append(string).append("_");
        return stringBuilder.substring(0, stringBuilder.length() - 1)
                .toString();
    }

    private static String removeUnderlineAndSetFirstUpper(String tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : tableName.split("_"))
            stringBuilder.append(String.format("%s%s", word.substring(0, 1)
                    .toUpperCase(), word.substring(1, word.length())));
        return stringBuilder.toString();
    }

    private static String[] getOptions(String rule) {
        return rule.substring(rule.indexOf("/") + 1, rule.length()).split("&");
    }

    // private static String getExp(String rule) {
    // return rule.substring(1, rule.lastIndexOf("$"));
    // }

}
