package com.sql.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.List;

public class SqlParser {

    /**
     * 删除sql的条件中有指定标志flag的查询条件
     *
     * @param sql
     * @return
     */
    public static String deleteEmptyValueCondition(String sql) throws JSQLParserException {
        List<Expression> expressionList = new ArrayList<>();
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Select) {
            Select select = (Select) statement;
            parseSelectExpression(select.getSelectBody(), expressionList);

        }
        String realSql = statement.toString();
        String finalSQL = SQLConditionHandle.deleteSQLEmptyFlagCondition(realSql, expressionList);
        return finalSQL;
    }


    /**
     * 解析SelectBody中的所有表达式
     *
     * @param selectBody
     * @param expressionList
     */
    public static void parseSelectExpression(SelectBody selectBody, List<Expression> expressionList) {
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;

            //处理where条件
            if (plainSelect.getWhere() != null) {
                Expression expression = plainSelect.getWhere();
                parseExpression(expression, expressionList);
            }

            //处理子查询
            if (plainSelect.getFromItem() != null) {
                FromItem fromItem = plainSelect.getFromItem();
                if (fromItem instanceof SubSelect) {
                    SubSelect subSelect = (SubSelect) fromItem;
                    SelectBody subSelectBody = subSelect.getSelectBody();
                    parseSelectExpression(subSelectBody, expressionList);
                } else if (fromItem instanceof SubJoin) {
                    SubJoin subJoin = (SubJoin) fromItem;
                    List<Join> joins = subJoin.getJoinList();
                    parseJoinExpression(joins, expressionList);
                }
            }

            //处理join操作
            if (plainSelect.getJoins() != null) {
                List<Join> joins = plainSelect.getJoins();
                parseJoinExpression(joins, expressionList);
            }
        }else if(selectBody instanceof SetOperationList){
            List<SelectBody> selectBodyList = ((SetOperationList) selectBody).getSelects();
            for(SelectBody selectBody1 : selectBodyList){
                parseSelectExpression(selectBody1, expressionList);
            }

        }else {

        }
    }

    /**
     * 查找最小表达式
     *
     * @param expression
     * @param expressionList
     */
    public static void parseExpression(Expression expression, List<Expression> expressionList) {
        //如果是简单的查询表达式
        if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;

            //左边表达式
            Expression leftExpression = binaryExpression.getLeftExpression();
            if (leftExpression instanceof Column) {
                expressionList.add(expression);
                return;
            }

            //右边表达式
            Expression rightExpression = binaryExpression.getRightExpression();
            if (rightExpression instanceof StringValue) {
                expressionList.add(expression);
                return;
            }

            parseExpression(leftExpression, expressionList);
            parseExpression(rightExpression, expressionList);
        } else if (expression instanceof InExpression) {
            //处理in
            InExpression inExpression = (InExpression) expression;
            ItemsList itemsList = inExpression.getRightItemsList();

            //in是列表
            if (itemsList instanceof ExpressionList) {
                expressionList.add(expression);
            } else if (itemsList instanceof MultiExpressionList) {
                MultiExpressionList multiExpressionList= (MultiExpressionList) itemsList;

                List<ExpressionList> exprList = multiExpressionList.getExprList();

                for(ExpressionList expressionList1 : exprList){
                    List<Expression> expressions = expressionList1.getExpressions();

                    for(Expression expression1 : expressions){
                        parseExpression(expression1 ,expressionList);
                    }
                }


            } else if (itemsList instanceof SubSelect) {//in是子查询
                SubSelect subSelect = (SubSelect) itemsList;
                SelectBody selectBody = subSelect.getSelectBody();
                parseSelectExpression(selectBody, expressionList);
            }

        } else if (expression instanceof Between) {
            //Between between= (Between) expression;

            expressionList.add(expression);

        }else if(expression instanceof Parenthesis){
            Parenthesis parenthesis= (Parenthesis) expression;
            parseExpression(parenthesis.getExpression() ,expressionList);
        }
    }


    /**
     * 解析JOIN表达式
     *
     * @param joins
     * @param expressionList
     */
    public static void parseJoinExpression(List<Join> joins, List<Expression> expressionList) {
        if (joins == null) {
            return;
        }
        for (Join join : joins) {
            FromItem rightItem = join.getRightItem();
            if (rightItem instanceof SubJoin) {

            } else if (rightItem instanceof SubSelect) {

            }

            Expression onExpression = join.getOnExpression();
            if (onExpression != null) {
                parseExpression(onExpression, expressionList);
            }
        }
    }


}
