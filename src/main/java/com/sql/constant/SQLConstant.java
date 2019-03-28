package com.sql.constant;

import java.util.HashMap;
import java.util.Map;

public class SQLConstant {

    /**
     * SQL 参数匹配标志
     * 如下SQL语句参数为userId和name
     * SELECT * FROM tb WHERE userId=${userId} and name=${name}
     */
    public static final String[] REG_PARAMS_PATTERN = {"\\$\\{(.*?)\\}", "\\#\\{(.*?)\\}"};

    /**
     * 顺序需与上面的保持一致，只是加了一个单引号
     */
    public static final String[] SINGLE_QUOTE_MARK_REG_PARAMS_PATTERN = {"'\\$\\{(.*?)\\}'", "'\\#\\{(.*?)\\}'"};


    /**
     * 解析SQL参数替换时参数中没有值的代替标志
     */
    public static final String PARAMS_NO_VALUE_FLAG = "-00000000";


    /**
     * 将含有PARAMS_NO_VALUE_FLAG值的表达式替换为1=1
     */
    public static final String SQL_NO_VALUE_EXPRESSION_REPLACE_FLAG = " 1=1 ";


    /**
     * 删除SQL中含有该表达式的条件，需与参数SQL_NO_VALUE_EXPRESSION_REPLACE_FLAG对应
     * <p>
     * "1=1" 不能使用
     */
    public static final String[] SQL_EXPRESSION_DELETE_FLAG = {"AND  1=1", "AND 1=1", "OR 1=1", "OR  1=1"};

    public static final String[] SQL_EXPRESSION_END_DELETE_FLAG = {"WHERE  1=1", "WHERE 1=1"};


    /**
     * SQL替换  将sql语句中含有key字符的替换为value内容
     */
    public static final Map<String, String> SQL_REPLACE_MAP = new HashMap<>();

    static {
        SQL_REPLACE_MAP.put("WHERE 1=1 ORDER", "ORDER");
        SQL_REPLACE_MAP.put("WHERE 1=1 LIMIT", "LIMIT");
        SQL_REPLACE_MAP.put("WHERE 1=1 AND", "WHERE");
        SQL_REPLACE_MAP.put("WHERE 1=1 GROUP BY", "GROUP BY");
    }

}
