package com.sql.parser;

import com.sql.constant.Constant;
import com.sql.entity.ParamExp;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 替换参数到指定的标记
 */
public class ReplaceParamToMark extends ExpressionDeParser {

    /**
     * SQL中所有的参数  {@link ParamExp }
     */
    List<ParamExp> paramExpList;

    /**
     * 解析出的参数列表
     */
    private List<String> paramList = new ArrayList<>();

    /**
     * 参数对应的值
     */
    Map<String, Object> paramsValueMap;

    /**
     * 参数转化为的标志
     */
    private String mark = " ? ";

    public ReplaceParamToMark() {
    }

    public ReplaceParamToMark(String mark, List<ParamExp> paramExpList, Map<String, Object> paramsValueMap) {
        this.mark = mark;
        this.paramExpList = paramExpList;
        this.paramsValueMap = paramsValueMap;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }

    @Override
    public void visit(StringValue stringValue) {
        String strValue = stringValue.toString();
        if (paramExpList != null && paramExpList.size() > 0) {
            String name = null;
            Object value = null;

            for (ParamExp paramExp : paramExpList) {
                if (strValue.equals(paramExp.getExp())) {
                    name = paramExp.getName();
                    value = paramsValueMap.get(name);

                    if (value != null && paramExp.getExp().contains("%") && paramExp.getExp().contains("${")) {//处理like查询
                        value = paramExp.getExp().replace("'", "").replace("${" + name + "}", value.toString());
                        paramsValueMap.put(name, value);
                    }
                    break;
                }
            }

            if (name != null && value != null) {
                //设置了替换为的标记
                this.getBuffer().append(" " + this.mark);
                this.paramList.add(name);

            } else {
                this.getBuffer().append(Constant.PARAMS_NO_VALUE_FLAG);
            }
        } else {
            this.getBuffer().append(Constant.PARAMS_NO_VALUE_FLAG);
        }
    }

}