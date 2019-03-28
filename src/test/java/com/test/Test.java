package com.test;

import com.sql.constant.SQLConstant;
import com.sql.entity.JdbcTemplateQueryParams;
import com.sql.util.ParamsUtil;
import com.sql.util.SQLUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test {


    public static void main(String[] args) throws JSQLParserException {
        //test2();
        test4();
    }

    public static void test1() throws JSQLParserException {


        //String sql="select  * from fact_air_cn fac where  dt>${dt} and area in (select DISTINCT  city from dim_area_cn where quality like ${quality} ) or area LIKE '${areaLike}'  and id in (${idIn})   ";
        //String sql = "select * from fact_air_cn fac where  dt>${dt} and  id in (${idIn})  or a like ${like}  ";
        //String sql = "select * from rpt_old_cus_apply_d where apply_dt between ${startTime} and ${endTime} ";
        //String sql = "select * from  bi_app_activate_apply_d where s_dt between ${startTime} and ${endTime} limit 10";
        //String sql = "select ttt.* , case when sms_fail_count > 0 then concat(round((sms_fail_count/sms_count)*100,2) , '%')  else null  end  sms_fail_count_rate from (select send_time , send_type , sum(sms_count) sms_count , sum(sms_success_count) sms_success_count , sum(sms_fail_count) sms_fail_count  from (select s.sms_count , s.sms_success_count , s.sms_fail_count , substring(s.send_time , 1,11) send_time  , sc.send_type from sms s , sms_content sc  where s.sms_content_id=sc.id  and s.is_send='Y' AND s.send_time>'2018-01-01'  and sc.send_type='${sendType}') as cc group by send_time  , send_type ) as ttt order by send_time desc ";
        String sql = " select group_concat(distinct name separator '||') from sms where  id=${smsId} and tel_upload_id=${tel_upload_id} ";

        sql = "SELECT CONCAT(tb1.classify,tb1.id) id_num , tb1.* ,scc.send_type, \n" +
                "packNumberCount , sendNumberCount , noSendNumberCount , \n" +
                "packSuccessCount , packFailCount\n" +
                "FROM (\n" +
                "select tu.id , 'A' classify , tu.telCount totalNumberCount , tu.uploadTelTime  createTime, tu.queryFlag\n" +
                "from tel_upload tu\n" +
                "UNION ALL \n" +
                "SELECT lqs.id, 'B' classify , lqs.sms_count totalNumberCount, lqs.create_time createTime, lqs.query_flag  queryFlag \n" +
                "FROM label_query_his lqs  \n" +
                "WHERE lqs.app_state='审批通过'   \n" +
                ") tb1 LEFT JOIN \n" +
                "\n" +
                "(\n" +
                "SELECT id , classify , packNumberCount , \n" +
                "sendNumberCount , noSendNumberCount , packSuccessCount , packFailCount  FROM (\n" +
                "SELECT id ,  classify , \n" +
                "sum(packNumberCount) packNumberCount , sum(sendNumberCount) sendNumberCount ,\n" +
                "sum(noSendNumberCount) noSendNumberCount , sum(packSuccessCount) packSuccessCount , \n" +
                "sum(packFailCount) packFailCount   \n" +
                "FROM ( \n" +
                "SELECT  \n" +
                "case when sms.tel_upload_id>0 then sms.tel_upload_id when sms.label_query_id >0 then sms.label_query_id ELSE NULL END id , \n" +
                "case when sms.tel_upload_id>0 then 'A' when sms.label_query_id >0 then 'B' ELSE '' END classify ,  \n" +
                "sms.is_send isSend , sms.sms_count packNumberCount ,\n" +
                "case WHEN sms.is_send='Y' THEN sms.sms_count ELSE 0 END sendNumberCount , \n" +
                "case WHEN sms.is_send='N' THEN sms.sms_count ELSE 0 END noSendNumberCount , \n" +
                "sms.sms_success_count packSuccessCount , sms.sms_fail_count packFailCount \n" +
                "from   sms  \n" +
                ") ts group by id , classify\n" +
                ") sm\n" +
                ") tb2 on tb1.id=tb2.id and tb1.classify=tb2.classify \n" +
                "LEFT JOIN (\n" +
                "select\n" +
                "case when tel_upload_id>0 then tel_upload_id when label_query_id >0 then label_query_id ELSE NULL END id , \n" +
                "case when tel_upload_id>0 then 'A' when label_query_id >0 then 'B' ELSE '' END classify , \n" +
                "send_type\n" +
                "from (\n" +
                "select DISTINCT scc.tel_upload_id , scc.label_query_id , scc.send_type  from sms_content scc\n" +
                ") scc\n" +
                ") scc  on tb1.id=scc.id and tb1.classify=scc.classify \n" +
                "where 1=1 \n" +
                "and scc.send_type='${sendType}'\n" +
                "and CONCAT(tb1.classify,tb1.id)='${idNum}'\n" +
                "\n" +
                "order by tb1.createTime desc , id desc , classify ";


        Map<String, Object> params = new HashMap<>();
        params.put("dt", 123);

        String realSql = null;
        try {
            realSql = SQLUtil.replaceSQLParams(sql, params);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

        //realSql =StringEscapeUtils.
        System.out.println("替换前SQL：" + CCJSqlParserUtil.parse(sql));
        System.out.println("替换后SQL：" + realSql);


    }


    public static void test2() throws JSQLParserException {
        //需要解析的SQL语句
        String sql = "select * from user where id=${id} and name='${name}'";
        //参数列表
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Myron");
        //解析SQL
        String realSql = SQLUtil.replaceSQLParams(sql, params);
        System.out.println("原SQL：        " + sql);
        System.out.println("正则替换参数SQL：" + ParamsUtil.replaceAllParams(sql, params, SQLConstant.PARAMS_NO_VALUE_FLAG));
        System.out.println("解析后SQL：     " + realSql);


        Select select = (Select) CCJSqlParserUtil.parse("select * from a where 1=1");
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

        System.out.println(select);

    }

    public static void test3() throws JSQLParserException {
        String sql = "SELECT NAME, ADDRESS, COL1 FROM USER WHERE name=${name} and age=12 and  SSN IN ('11111111111111', '22222222222222');";
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        //Start of value modification
        StringBuilder buffer = new StringBuilder();
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {

            @Override
            public void visit(StringValue stringValue) {
                System.out.println(stringValue);
                this.getBuffer().append("XXXX");
            }


            @Override
            public void visit(EqualsTo equalsTo) {
                System.out.println("EqualsTo******");

                equalsTo.setASTNode(null);

                // equalsTo.accept(null);

                super.visit(equalsTo);
            }
        };
        SelectDeParser deparser = new SelectDeParser(expressionDeParser, buffer);
        expressionDeParser.setSelectVisitor(deparser);
        expressionDeParser.setBuffer(buffer);
        select.getSelectBody().accept(deparser);
//End of value modification


        System.out.println(buffer.toString());
//Result is: SELECT NAME, ADDRESS, COL1 FROM USER WHERE SSN IN (XXXX, XXXX)
    }


    public static void test4() throws JSQLParserException {
        //需要解析的SQL语句
        String sql = "select * from user where id=${id} and name='${name}' " +
                "and age=${age} and address like ${address} ";
        //参数列表
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Myron");
        params.put("age", "12");

        JdbcTemplateQueryParams jdbcTemplateQueryParams = SQLUtil.sqlToJdbcTemplateQuery(sql, params);

        System.out.println("解析前SQL:"+sql);
        System.out.println("解析后SQL:"+jdbcTemplateQueryParams.getSql());
    }

    public static void test5() throws JSQLParserException {
        //   Select select = (Select) CCJSqlParserUtil.parse("select * from a where 1=1 and name='123' and 1=1 or 1=1 ; ");
    }

    public static void test6() throws JSQLParserException {

        //需要解析的SQL语句
        String sql = "select * from user where id=${id} and name='${name}' and age=${age} and address like ${address}  and age in (${inAge})";
        //参数列表
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Myron");
        params.put("age", "12");

        List<String> inAge = new ArrayList<>();
        inAge.add("1");
        inAge.add("2");
        params.put("inAge", inAge);

        SQLUtil.sqlToJdbcTemplateQuery(sql, params);
        JdbcTemplateQueryParams jdbcTemplateQueryParams = SQLUtil.sqlToNamedParameterJdbcTemplateQuery(sql, params);

        System.out.println(sql);
        System.out.println(jdbcTemplateQueryParams.getSql());

    }
}
