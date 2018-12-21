package com.test;

import com.sql.util.SQLUtil;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.text.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

public class Test {


    public static void main(String[] args) {


        //String sql="select  * from fact_air_cn fac where  dt>${dt} and area in (select DISTINCT  city from dim_area_cn where quality like ${quality} ) or area LIKE '${areaLike}'  and id in (${idIn})   ";
        //String sql = "select * from fact_air_cn fac where  dt>${dt} and  id in (${idIn})  or a like ${like}  ";
        //String sql = "select * from rpt_old_cus_apply_d where apply_dt between ${startTime} and ${endTime} ";
        //String sql = "select * from  bi_app_activate_apply_d where s_dt between ${startTime} and ${endTime} limit 10";
        //String sql = "select ttt.* , case when sms_fail_count > 0 then concat(round((sms_fail_count/sms_count)*100,2) , '%')  else null  end  sms_fail_count_rate from (select send_time , send_type , sum(sms_count) sms_count , sum(sms_success_count) sms_success_count , sum(sms_fail_count) sms_fail_count  from (select s.sms_count , s.sms_success_count , s.sms_fail_count , substring(s.send_time , 1,11) send_time  , sc.send_type from sms s , sms_content sc  where s.sms_content_id=sc.id  and s.is_send='Y' AND s.send_time>'2018-01-01'  and sc.send_type='${sendType}') as cc group by send_time  , send_type ) as ttt order by send_time desc ";
        String sql="select * from t_mdrymrmx_rymrls where (1=1 or ry_name='${domp_param_user_id}')  or 1=1 limit 10";

        Map<String, Object> params = new HashMap<>();
        params.put("dt", 123);


        String realSql = null;
        try {
            realSql = SQLUtil.replaceSQLParams(sql, params);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

        //realSql =StringEscapeUtils.
        System.out.println("替换前SQL：" + sql);
        System.out.println("替换后SQL：" + realSql);


    }


}
