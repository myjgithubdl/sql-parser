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
 * <p>
 * 替换了Spring NamedParameterJdbcTemplate 查询
 * <p>
 * 将没有值的参数使用没值标志标记下，将有值的参数替换为  :参数名
 */
public class ReplaceParamToNamedParam extends ExpressionDeParser {

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

    public ReplaceParamToNamedParam() {
    }

    public ReplaceParamToNamedParam(Map<String, Object> paramsValueMap) {
        this.paramsValueMap = paramsValueMap;
    }

    public ReplaceParamToNamedParam(List<ParamExp> paramExpList, Map<String, Object> paramsValueMap) {
        this.paramsValueMap = paramsValueMap;
        this.paramExpList = paramExpList;
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
                this.getBuffer().append(" :" + name);
                this.paramList.add(name);

            } else {
                this.getBuffer().append(Constant.PARAMS_NO_VALUE_FLAG);
            }
        } else {
            this.getBuffer().append(Constant.PARAMS_NO_VALUE_FLAG);
        }

    }


}