package com.sql.util;

import com.sql.constant.SQLConstant;
import com.sql.entity.JdbcTemplateQueryParams;
import com.sql.parser.ReplaceParamToMark;
import com.sql.parser.ReplaceParamToNamedParam;
import com.sql.parser.SqlParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析SQL工具类
 */
public class SQLUtil {


    public static String replaceSQLParams(String sql, Map<String, Object> paramsValueMap) throws JSQLParserException {
        if (sql == null || sql.trim().length() < 1) {
            return null;
        }

        //使用正则表达式替换参数
        String sqlEp = ParamsUtil.replaceAllParams(sql, paramsValueMap, SQLConstant.PARAMS_NO_VALUE_FLAG);
        //处理SQL
        String finalSQL = SqlParser.deleteEmptyValueCondition(sqlEp);

        return finalSQL;

    }


    public static String replaceSQLParams2(String sql, Map<String, String> paramsValueMap) throws JSQLParserException {
        Map<String, Object> map = new HashMap<>();
        for (String key : paramsValueMap.keySet()) {
            map.put(key, paramsValueMap.get(key));
        }
        return replaceSQLParams(sql, map);
    }


    /**
     * 给SQL参数增加单引号，如果已有单引号则不增加单引号
     *
     * @param sql
     * @return
     */
    public static String addSQLParamsSingleQuoteMark(String sql) {
        List<String> list = ParamsUtil.findAllParams(sql, true, false);
        if (list != null) {
            for (String param : list) {
                String noSingleQuoteMark = "";
                String singleQuoteMark = "";
                if (param.startsWith("${")) {
                    noSingleQuoteMark = "\\$\\{" + param.substring(2, param.length() - 1) + "\\}";
                    singleQuoteMark = "'\\$\\{" + param.substring(2, param.length() - 1) + "\\}'";
                }
                if (param.startsWith("#{")) {
                    noSingleQuoteMark = "\\#\\{" + param.substring(2, param.length() - 1) + "\\}";
                    singleQuoteMark = "'\\#\\{" + param.substring(2, param.length() - 1) + "\\}'";
                }
                //先将带单引号的改为无单引号，再将其该我带单引号的，不能直接改是因为带单引号的会出现两个单引号
                sql = sql.replaceAll(singleQuoteMark, noSingleQuoteMark).replaceAll(noSingleQuoteMark, singleQuoteMark);
            }
        }

        return sql;
    }


    /**
     * 将SQL转化为Spring jdbcTemplate.queryForList(sql, args, argTypes);方法使用的参数
     *
     * @param sql
     * @param paramsValueMap
     * @return
     * @throws JSQLParserException
     */
    public static JdbcTemplateQueryParams sqlToJdbcTemplateQuery(String sql, Map<String, Object> paramsValueMap) throws JSQLParserException {
        JdbcTemplateQueryParams jdbcTemplateQueryParams = new JdbcTemplateQueryParams();

        StringBuilder buffer = new StringBuilder();
        ReplaceParamToMark replaceParamToMark = new ReplaceParamToMark(" ? ", paramsValueMap);


        SelectDeParser selectDeparser = new SelectDeParser(replaceParamToMark, buffer);
        replaceParamToMark.setSelectVisitor(selectDeparser);
        replaceParamToMark.setBuffer(buffer);
        StatementDeParser stmtDeparser = new StatementDeParser(replaceParamToMark, selectDeparser, buffer);


        String addSingleQuoteSql = addSQLParamsSingleQuoteMark(sql);
        Statement stmt = CCJSqlParserUtil.parse(addSingleQuoteSql);
        stmt.accept(stmtDeparser);

        String markSql = stmtDeparser.getBuffer().toString();
        String finalSQL = SqlParser.deleteEmptyValueCondition(markSql);

        if (replaceParamToMark.getParamList() != null) {
            String[] strings = new String[replaceParamToMark.getParamList().size()];
            jdbcTemplateQueryParams.setArgNames(replaceParamToMark.getParamList().toArray(strings));
        }
        jdbcTemplateQueryParams.setOriginalSql(sql);
        jdbcTemplateQueryParams.setSql(finalSQL);
        return jdbcTemplateQueryParams;

    }

    public static JdbcTemplateQueryParams sqlToJdbcTemplateQuery2(String sql, Map<String, String> paramsValueMap) throws JSQLParserException {
        Map<String, Object> map = new HashMap<>();
        for (String key : paramsValueMap.keySet()) {
            map.put(key, paramsValueMap.get(key));
        }
        return sqlToJdbcTemplateQuery(sql, map);
    }


    /**
     * 将SQL转化为Spring jdbcTemplate.queryForList(sql, args, argTypes);方法使用的参数
     *
     * @param sql
     * @param paramsValueMap
     * @return
     * @throws JSQLParserException
     */
    public static JdbcTemplateQueryParams sqlToNamedParameterJdbcTemplateQuery(String sql, Map<String, Object> paramsValueMap) throws JSQLParserException {
        JdbcTemplateQueryParams jdbcTemplateQueryParams = new JdbcTemplateQueryParams();

        StringBuilder buffer = new StringBuilder();
        ReplaceParamToNamedParam replaceParamToNamedParam = new ReplaceParamToNamedParam(paramsValueMap);


        SelectDeParser selectDeparser = new SelectDeParser(replaceParamToNamedParam, buffer);
        replaceParamToNamedParam.setSelectVisitor(selectDeparser);
        replaceParamToNamedParam.setBuffer(buffer);
        StatementDeParser stmtDeparser = new StatementDeParser(replaceParamToNamedParam, selectDeparser, buffer);


        String addSingleQuoteSql = addSQLParamsSingleQuoteMark(sql);
        Statement stmt = CCJSqlParserUtil.parse(addSingleQuoteSql);
        stmt.accept(stmtDeparser);

        String markSql = stmtDeparser.getBuffer().toString();
        String finalSQL = SqlParser.deleteEmptyValueCondition(markSql);

        if (replaceParamToNamedParam.getParamList() != null) {
            String[] strings = new String[replaceParamToNamedParam.getParamList().size()];
            jdbcTemplateQueryParams.setArgNames(replaceParamToNamedParam.getParamList().toArray(strings));
        }
        jdbcTemplateQueryParams.setOriginalSql(sql);
        jdbcTemplateQueryParams.setSql(finalSQL);
        return jdbcTemplateQueryParams;

    }


}
