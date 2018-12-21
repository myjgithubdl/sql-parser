package com.sql.util;

import com.sql.constant.SQLConstant;
import com.sql.parser.SqlParser;
import net.sf.jsqlparser.JSQLParserException;

import java.util.Map;

/**
 * 解析SQL工具类
 */
public class SQLUtil {


    public static String replaceSQLParams(String sql, Map<String, Object> paramsValueMap) throws JSQLParserException {
        if (sql == null || sql.trim().length() < 1) {
            return null;
        }

        String sqlEp =ParamsUtil.replaceAllParams(sql , paramsValueMap , SQLConstant.PARAMS_NO_VALUE_FLAG );
        String finalSQL = SqlParser.deleteEmotyValueCondition(sqlEp );

        return finalSQL;

    }


}
