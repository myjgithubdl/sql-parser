package com.sql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数表达式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamExp {

    /**
     * 参数名
     * 如表达式${id} 的name为id
     */
    private String name;

    /**
     * 表达式
     * 如表达式${id} 的exp为${id}
     */
    private String exp;

}
