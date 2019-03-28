package com.sql.entity;

/**
 * 封装Spring JdbcTemplate 查询参数
 * 该方式的问题在于不能解决in查询问题
 *
 * @since 0.0.1
 */
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


    public JdbcTemplateQueryParams() {
    }

    public JdbcTemplateQueryParams(String originalSql, String sql, String[] argNames, Object[] argValues, int[] argTypes) {
        this.originalSql = originalSql;
        this.sql = sql;
        this.argNames = argNames;
        this.argValues = argValues;
        this.argTypes = argTypes;
    }

    public String getOriginalSql() {
        return originalSql;
    }

    public void setOriginalSql(String originalSql) {
        this.originalSql = originalSql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String[] getArgNames() {
        return argNames;
    }

    public void setArgNames(String[] argNames) {
        this.argNames = argNames;
    }

    public int[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(int[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getArgValues() {
        return argValues;
    }

    public void setArgValues(Object[] argValues) {
        this.argValues = argValues;
    }
}
