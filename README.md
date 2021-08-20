# 数据库链式操作类
### 参数设置
```
配置文件：jntoodb.properties
参数：
jntoo.db.debug=true  是否输出调试信息
jntoo.db.connection  设置获取数据库链接类名称继承自 ConnectionConfig 类,必须设置
jntoo.db.prefix      设置表前缀
```

## DB 静态类 创建QueryWrapper 类
```
DB.name(“表名称”)       创建 QueryWrapper 类，该类操作返回得数据为：QMap 类型数据
DB.name(Pojo类.class)  设置实体类，按实体类返回相关数据
DB.name(Pojo 实例)     设置实例，相关数据在查询时会以非null 得值写入查询条件，使用insert、update 插入即可插入
```

## CURD 操作
### `Select`查询
#### where 操作
###### where(自定义条件) 不限制where 条件写法 等同于SQL语句： where 自定义条件 如下：

````
DB.name("table").where("1=1 AND field='12'").select();
select * from table WHERE 1=1 AND field='12'
````

#### where(String field , Object)  等于条件 如：
````
DB.name("table").where("field",12).select();
select * from table where field=?  条件 ?=12  自定写入绑定
````
