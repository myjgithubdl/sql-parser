package com.sql.entity;

import lombok.*;

import java.util.List;

/**
 * 封装Spring JdbcTemplate 查询参数
 * 该方式的问题在于不能解决in查询问题
 *
 * @since 0.0.1
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JdbcTemplateQueryParams {

    /**
     * 原始SQL,根据SQL中${}标识的参数解析出sql
     * 如：select * from tb where name=${name}
     */
    private String originalSql;

    /**
     * List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes)
     * queryForList执行的SQL,支持?占位符
     * select * from tb where name=?
     */
    private String sql;

    /**
     * 对应于sql中?占位符的参数名
     * {"name"}
     */
    private String[] argNames;

    /**
     * 对应于sql中?占位符的值
     * queryForList(String sql, Object[] args, int[] argTypes) 中的args
     */
    private Object[] argValues;

    /**
     * 对应于sql中?占位符的数据类型
     * queryForList(String sql, Object[] args, int[] argTypes) 中的argTypes
     *
     * @see java.sql.Types
     */
    private int[] argTypes;

    /**
     * SQL中的表达解析出的所有有参数表达式
     */
    private List<ParamExp> paramExpList;

}
