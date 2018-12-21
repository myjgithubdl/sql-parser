package com.sql.util;

import com.sql.constant.SQLConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * @param isContainRegExp：是否包含正则表达式匹配开始和结束字符内容
     * @return
     */
    public static List<String> findAllParams(String str, boolean isContainRegExp) {
        if (str == null) {
            return null;
        }

        List<String> params = new ArrayList<>();
        for (String s : SQLConstant.REG_PARAMS_PATTERN) {
            Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                if (isContainRegExp) {
                    params.add(str.substring(matcher.start(), matcher.end()));
                } else {
                    params.add(matcher.group(1));
                }
            }
        }
        if (params.size() > 0) {
            return params;
        }
        return null;
    }


    /**
     * 根据参数paramsValueMap替换str中的参数值
     *
     * @param str
     * @param paramsValueMap
     * @param noValReplaceStr 在paramsValueMap中没有str中的参数对应的值的代替字符
     * @return
     */
    public static String replaceAllParams(String str, Map<String, Object> paramsValueMap , String noValReplaceStr) {
        if (str == null) {
            return null;
        }
        List<String> allParamsExp = findAllParams(str, true);
        if (allParamsExp != null) {
            if (paramsValueMap == null) {
                paramsValueMap = new HashMap<>();
            }
            for (String paramsExp : allParamsExp) {
                String paramsName = paramsExp.substring(2, paramsExp.length() - 1);
                Object v = paramsValueMap.get(paramsName);
                if (v != null) {
                    str = str.replace(paramsExp, String.valueOf(v));
                } else if(noValReplaceStr != null ) {
                    str = str.replace(paramsExp, noValReplaceStr);
                }else{
                    str = str.replace(paramsExp, "");
                }
            }
        }
        return str;
    }


    public static void main(String[] args) {
        String s = "https://www.baidu.com/s?wd='${name}'&rsv_spt=#{id}${1234}#{ttt}'";
        Map<String, Object> params = new HashMap<>();
        params.put("name", "缪应江");
        params.put("id", "1098");
        params.put("1234", "AA");
        params.put("ttt", "BB");

        List<String> list = findAllParams(s, true);
        String v = replaceAllParams(s, params,"");
        System.out.println(list);
        System.out.println(v);
    }

}
