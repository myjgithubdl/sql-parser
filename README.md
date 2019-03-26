# sql-parser

sql-parser是一个依赖于[JSqlParser](https://github.com/JSQLParser/JSqlParser)做参数替换的工具，SQL语句中的参数是通过${}或#{}标识的，如有SQL语句：select * from user where id=${id} and name='${name}'，则参数即为id和name，解析的原则是先将SQL中的参数替换为指定的标识后再做值替换。



![例子](https://raw.githubusercontent.com/myjgithubdl/sql-parser/master/docs/assets/imgs/image1.png)





# 替换步骤

下面就根据SQL语句select * from user where id=${id} and name='${name}'做介绍。

### 1、正则替换

​	该步骤是解析出SQL中所有参数，如果在传入的Map参数列表中有对应的值则作替换，如果没有则使用特定的值代替（目的是使用JSqlParser解析出表达式后做条件剔除）。在上图中因为只传入了name参数的值为Myron，所以解析出的SQL为：

```sql
select * from user where id=-00000000 and name='Myron'
```



### 2、解析SQL中的表达式

​	在使用正则对SQL中的参数做替换以后，再用CCJSqlParserUtil解析出SQL中的表达式。

![表达式列表](https://raw.githubusercontent.com/myjgithubdl/sql-parser/master/docs/assets/imgs/expression-list.png)

### 3、表达式替换

​	根据正则替换后的SQL和解析出的SQL表达式再次做替换。



# 使用方法



```java
//需要解析的SQL语句
String sql="select * from user where id=${id} and name='${name}'";
//参数列表
Map<String, Object> params = new HashMap<>();
params.put("name", "Myron");
//解析SQL
String realSql =SQLUtil.replaceSQLParams(sql, params);
```



