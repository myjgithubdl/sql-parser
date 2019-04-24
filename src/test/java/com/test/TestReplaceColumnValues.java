package com.test;

import com.sql.entity.JdbcTemplateQueryParams;
import com.sql.parser.ReplaceColumnValues;
import com.sql.parser.ReplaceParamToMark;
import com.sql.parser.SqlParser;
import com.sql.util.ParamsUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sql.util.SQLUtil.addSQLParamsSingleQuoteMark;

public class TestReplaceColumnValues {

    static class ReplaceColumnAndLongValues extends ExpressionDeParser {

        @Override
        public void visit(StringValue stringValue) {
            System.out.println(stringValue.getValue());
            this.getBuffer().append("?");
        }


    }


    public static void main(String[] args) throws JSQLParserException {

        String sql="SELECT 'abc', 5 FROM mytable WHERE col='${test}' and stbS in (${in}) and addr like '%{addrLike}%' and name=#{name} and name2=#{name} and id=12 and age between ${age1} and ${age2}  and sex like concat('%',${sex},'%') ";


        JdbcTemplateQueryParams jdbcTemplateQueryParams = new JdbcTemplateQueryParams();

        StringBuilder buffer = new StringBuilder();
        ReplaceColumnAndLongValues replaceParamToMark = new ReplaceColumnAndLongValues( );


        SelectDeParser selectDeparser = new SelectDeParser(replaceParamToMark, buffer);
        replaceParamToMark.setSelectVisitor(selectDeparser);
        replaceParamToMark.setBuffer(buffer);
        StatementDeParser stmtDeparser = new StatementDeParser(replaceParamToMark, selectDeparser, buffer);


        String addSingleQuoteSql = addSQLParamsSingleQuoteMark(sql);
        Statement stmt = CCJSqlParserUtil.parse(addSingleQuoteSql);
        stmt.accept(stmtDeparser);

        String markSql = stmtDeparser.getBuffer().toString();

        System.out.println(sql);
        System.out.println(markSql);



    }
}