package com.sql.util;

import com.sql.constant.Constant;
import com.sql.entity.ParamExp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数工具
 */
public class ParamsUtil {


    /**
     * 查找str值中指定字符串开头和结尾的所有内容
     *
     * @param str
     * @param regParamsPattern 参数的正则表达式
     * @param isDistinct       是否去重
     * @return
     */
    public static List<ParamExp> findAllParams(String str, String[] regParamsPattern, boolean isDistinct) {
        if (str == null) {
            return null;
        }

        List<ParamExp> paramExpList = new ArrayList<>();

        Set<String> paramNameSet = new LinkedHashSet<>();
        for (String s : regParamsPattern) {
            Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {

                String exp = str.substring(matcher.start(), matcher.end());
                String name = exp.substring(exp.indexOf("{")+1,exp.indexOf("}"));

                if (isDistinct && paramNameSet.contains(name)) {
                    continue;
                }
                paramNameSet.add(name);


                ParamExp paramExp = ParamExp.builder().name(name).exp(exp).build();
                paramExpList.add(paramExp);
            }
        }

        if (paramExpList.size() > 0) {
            return paramExpList;
        }
        return null;
    }


    /**
     * 根据参数paramsValueMap替换str中的参数值
     *
     * @param str
     * @param regParamsPattern 参数的正则表达式
     * @param paramsValueMap
     * @param noValReplaceStr  在paramsValueMap中没有str中的参数对应的值的代替字符
     * @return
     */
    public static String replaceAllParams(String str, String[] regParamsPattern, Map<String, Object> paramsValueMap, String noValReplaceStr) {
        if (str == null) {
            return null;
        }
        List<ParamExp> paramExpList = findAllParams(str, regParamsPattern, true);
        if (paramExpList != null && paramExpList.size() >0) {
            if (paramsValueMap == null) {
                paramsValueMap = new HashMap<>();
            }
            for (ParamExp paramExp : paramExpList) {
                String paramsName =paramExp.getName();
                String paramsExp="${"+paramsName+"}";
                Object v = paramsValueMap.get(paramsName);
                if (v != null) {
                    str = str.replace(paramsExp, String.valueOf(v));
                } else if (noValReplaceStr != null) {
                    str = str.replace(paramsExp, noValReplaceStr);
                } else {
                    str = str.replace(paramsExp, "");
                }
            }
        }
        return str;
    }


    public static void main(String[] args) {

        String s = "https://www.baidu.com/s?wd='${name}'&rsv_spt=${id}${1234}${ttt}'";
        Map<String, Object> params = new HashMap<>();
        params.put("name", "缪应江");
        params.put("id", "1098");
        params.put("1234", "AA");
        params.put("ttt", "BB");

        List<ParamExp> list = findAllParams(s, Constant.REG_PARAMS_PATTERN, false);
        String v = replaceAllParams(s, Constant.REG_PARAMS_PATTERN, params, "");
        System.out.println(list);
        System.out.println(v);
    }

}
