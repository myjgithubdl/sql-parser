# sql-parser

sql-parser是一个依赖于[JSqlParser](https://github.com/JSQLParser/JSqlParser)做参数替换的工具，SQL语句中的参数是通过${}或#{}标识的，如有SQL语句：select * from user where id=${id} and name='${name}'，则参数即为id和name，解析的原则是先将SQL中的参数替换为指定的标识后再做值替换。目前支持替换为三张类型的SQL语句。

1. 替换为SQL语句
2. 替换为Spring JdbcTemplate执行的SQL语句（能防止SQL注入，但是不能解决in查询）
3. 替换为Spring NamedParameterJdbcTemplate执行的SQL语句（能防止SQL注入，能解决in查询）



（此方法会存在SQL注入问题）、



![例子](https://raw.githubusercontent.com/myjgithubdl/sql-parser/master/docs/assets/imgs/image1.png)





# 一、替换为SQL语句

下面就根据SQL语句select * from user where id=${id} and name='${name}'做介绍。

## 1、正则替换

​	该步骤是解析出SQL中所有参数，如果在传入的Map参数列表中有对应的值则作替换，如果没有则使用特定的值代替（目的是使用JSqlParser解析出表达式后做条件剔除）。在上图中因为只传入了name参数的值为Myron，所以解析出的SQL为：

```sql
select * from user where id=-00000000 and name='Myron'
```



## 2、解析SQL表达式

​	在使用正则对SQL中的参数做替换以后，再用CCJSqlParserUtil解析出SQL中的表达式。

![表达式列表](https://raw.githubusercontent.com/myjgithubdl/sql-parser/master/docs/assets/imgs/expression-list.png)

## 3、表达式替换

​	根据正则替换后的SQL和解析出的SQL表达式再次做替换。



## 使用方法



```java
//需要解析的SQL语句
String sql="select * from user where id=${id} and name='${name}'";
//参数列表
Map<String, Object> params = new HashMap<>();
params.put("name", "Myron");
//解析SQL
String realSql =SQLUtil.replaceSQLParams(sql, params);
```



# 二、替换为Spring JdbcTemplate SQL

下面就根据SQL语句select * from user where id=${id} and name='${name}'做介绍。

## 1、替换查询条件为字符串

如上面的SQL语句中的条件id=${id} 如果在SQL编译器看来${id}是数值，需要将其转化为'${id}'。然后在使用CCJSqlParserUtil.parse(addSingleQuoteSql)解析SQL语句

## 2、解析SQL表达式

​	在使用正则对SQL中的参数做替换以后，再用CCJSqlParserUtil解析出SQL中的表达式，同时对查询语句中的字符串(''标识的条件)做处理，检查如果是查询参数则判断是否有改参数的值，如果有则使用?代替，没有值的话则打上一个没有值的标记，后期会把该条件清除。

​	需要解析SQL语句中的字符串内容则要继承ExpressionDeParser类并重写其中的visit(StringValue stringValue)方法。

![表达式列表](https://raw.githubusercontent.com/myjgithubdl/sql-parser/master/docs/assets/imgs/extend-ExpressionDeParser.png)

## 3、表达式替换

​	根据替换后的SQL中被标记没有值的查询条件删除。



## 使用方法	

```java
//需要解析的SQL语句
String sql = "select * from user where id=${id} and name='${name}' " +
    "and age=${age} and address like ${address} ";
//参数列表
Map<String, Object> params = new HashMap<>();
params.put("name", "Myron");
params.put("age", "12");

JdbcTemplateQueryParams jdbcTemplateQueryParams = SQLUtil.sqlToJdbcTemplateQuery(sql, params);

System.out.println("解析前SQL:"+sql);
System.out.println("解析后SQL:"+jdbcTemplateQueryParams.getSql());
```



![表达式列表](https://raw.githubusercontent.com/myjgithubdl/sql-parser/master/docs/assets/imgs/extend-ExpressionDeParser-result.png)