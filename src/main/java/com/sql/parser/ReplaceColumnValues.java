package com.sql.parser;

import com.sql.constant.SQLConstant;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import java.util.Map;

public class ReplaceColumnValues extends ExpressionDeParser {

    Map<String, Object> paramsValueMap;

    public void setParamsValueMap(Map<String, Object> paramsValueMap) {
        this.paramsValueMap = paramsValueMap;
    }

    @Override
    public void visit(StringValue stringValue) {
        String strValue = stringValue.toString();
        String paramEL = getParamEL(strValue);
        if (paramEL == null) {
            this.getBuffer().append(stringValue);
        } else {
            if (paramsValueMap != null && paramsValueMap.get(paramEL.substring(1, paramEL.length() - 1)) != null) {
                String mapValue = paramsValueMap.get(paramEL.substring(1, paramEL.length() - 1)).toString();
                this.getBuffer().append(strValue.replace(paramEL, mapValue));
            } else {
                this.getBuffer().append(SQLConstant.PARAMS_NO_VALUE_FLAG);
            }
        }
    }

  /*  @Override
    public void visit(LongValue longValue) {
        this.getBuffer().append(longValue);
    }*/

    /**
     * @return
     */
    public static String getParamEL(String sqlVal) {
        if ((sqlVal.startsWith("${") || sqlVal.startsWith("'${")
                || sqlVal.startsWith("#{") || sqlVal.startsWith("'#{"))
                && sqlVal.endsWith("}")) {
            return sqlVal.replaceAll("'", "");
        }
        return null;
    }


}