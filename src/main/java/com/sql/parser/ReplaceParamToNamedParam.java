package com.sql.parser;

import com.sql.constant.SQLConstant;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 替换参数到指定的标记
 * <p>
 * 替换了Spring NamedParameterJdbcTemplate 查询
 * <p>
 * 将没有值的参数使用没值标志标记下，将有值的参数替换为  :参数名
 */
public class ReplaceParamToNamedParam extends ExpressionDeParser {

    /**
     * 解析出的参数列表
     */
    private List<String> paramList = new ArrayList<>();

    /**
     * 参数对应的值
     */
    Map<String, Object> paramsValueMap;

    public ReplaceParamToNamedParam() {
    }

    public ReplaceParamToNamedParam(Map<String, Object> paramsValueMap) {
        this.paramsValueMap = paramsValueMap;
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

        if (isParamEL) {//值是参数
            String paramName = getParamName(strValue);
            if (paramsValueMap != null && paramsValueMap.get(paramName) != null) {//有参数值
                //设置了替换为的标记
                this.getBuffer().append(" :" + paramName);
                this.paramList.add(paramName);

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