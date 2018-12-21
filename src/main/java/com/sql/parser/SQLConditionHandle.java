package com.sql.parser;

import com.sql.constant.SQLConstant;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * SQL语句空条件表达式处理
 */
public class SQLConditionHandle {

    /**
     * 删除SQL语句中带有空值
     *
     * @param sql
     * @param expressionList
     * @return
     */
    public static String deleteSQLEmptyFlagCondition(String sql, List<Expression> expressionList) {

        System.out.println("表达式列表：" + expressionList);

        for (Expression expression : expressionList) {
            if (expression.toString().contains(SQLConstant.PARAMS_NO_VALUE_FLAG)) {
                sql = sql.replace(expression.toString(), SQLConstant.SQL_NO_VALUE_EXPRESSION_REPLACE_FLAG);
            }
        }
        //替换1=1标志
        for (String s : SQLConstant.SQL_EXPRESSION_DELETE_FLAG) {
            sql = sql.replaceAll(s, "");
        }

        //替换所有多余的空格
        while (sql.indexOf("  ") != -1) {
            sql = sql.trim().replaceAll("  ", " ");
        }

        //检查是不是 where SQL_NO_VALUE_EXPRESSION_REPLACE_FLAG  结尾  是就删除
        if (sql.endsWith("WHERE " + SQLConstant.SQL_NO_VALUE_EXPRESSION_REPLACE_FLAG)) {
            sql = sql.replace("WHERE " + SQLConstant.SQL_NO_VALUE_EXPRESSION_REPLACE_FLAG, "");
        }

        //替换
        for (String key : SQLConstant.SQL_REPLACE_MAP.keySet()) {
            sql = sql.replace(key, SQLConstant.SQL_REPLACE_MAP.get(key));
        }

        //将SQLConstant.PARAMS_NO_VALUE_FLAG替换为空字符串
        sql = sql.replace(SQLConstant.PARAMS_NO_VALUE_FLAG, "");

        return sql;
    }


}