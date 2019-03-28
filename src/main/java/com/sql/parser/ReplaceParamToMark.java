package com.sql.parser;

import com.sql.constant.SQLConstant;
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

    public ReplaceParamToMark(String mark) {
        this.mark = mark;
    }

    public ReplaceParamToMark(String mark,Map<String, Object> paramsValueMap) {
        this.mark = mark;
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
        boolean isParamEL = isParamEL(strValue);

        //值是参数
        if (isParamEL) {
            String paramName = getParamName(strValue);
            //有参数值
            if (paramsValueMap != null && paramsValueMap.get(paramName) != null) {
                //设置了替换为的标记
                if (this.mark != null) {
                    this.getBuffer().append(this.mark);
                    this.paramList.add(paramName);
                } else {
                    String mapValue = paramsValueMap.get(paramName).toString();
                    this.getBuffer().append(strValue.replace(strValue, mapValue));
                }
            } else {
                this.getBuffer().append(SQLConstant.PARAMS_NO_VALUE_FLAG);
            }
        } else {
            this.getBuffer().append(stringValue);
        }
    }

    /**
     * @return
     */
    public static boolean isParamEL(String sqlVal) {
        if ((sqlVal.startsWith("${") || sqlVal.startsWith("'${")
                || sqlVal.startsWith("#{") || sqlVal.startsWith("'#{"))
                && (sqlVal.endsWith("}") || sqlVal.endsWith("}'"))) {
            return true;
        }
        return false;
    }

    public static String getParamName(String param) {
        return param.replaceAll("'", "")
                .replaceAll("#", "")
                .replace("$", "").replace("{", "").replace("}", "");

    }

    public static String getParamEL(String sqlVal) {
        if ((sqlVal.startsWith("${") || sqlVal.startsWith("'${")
                || sqlVal.startsWith("#{") || sqlVal.startsWith("'#{"))
                && sqlVal.endsWith("}")) {
            return sqlVal.replaceAll("'", "");
        }
        return null;
    }


}