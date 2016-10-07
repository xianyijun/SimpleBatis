## **项目介绍** ##
> 一个阅读MyBatis源码而模仿实现的简单ORM框架，实现了个人对MyBatis的常用功能。

主要实现有：
-   映射器映射
-   动态SQL
-   嵌套ResultMap
-   延迟加载
-   一级缓存和二级缓存
-   事务管理
-   异常封装
-   数据源和连接池

## **项目结构** ##
```
└── cn
    └── xianyijun
        └── orm
            ├── annotation
            ├── binding
            ├── builder
            ├── cache
            ├── core
            ├── cursor
            ├── datasource
            ├── exception
            ├── executor
            ├── io
            ├── mapping
            ├── parse
            ├── plugins
            ├── reflection
            ├── script
            ├── session
            ├── transaction
            ├── type
            └── util

```

## **annotation** ##
注解包，由于个人不太习惯使用注解来声明对应的SQL,一般是使用XMl来指定对应SQL和@Param注解来指定对应的参数名。
所以此处只有一个@Param注解,在ParamNameResolver中会使用到。
## **binding** ##
这个包主要是用来创建MapperMethod的，MapperMethod是由SqlCommand和MethodSignature来唯一指定确认，此处使用来Builder设计模式。
由于在执行对应的MapperMethod之前需要进行参数的处理:参数映射和属性映射，和执行MapperMethod之后ResultMap的处理：结果映射，将SQL语句执行的结果集ResultSet转换为对应的结果
这里使用的动态代理来完成，MapperProxy为代理对象，MapperProxyFactory的动态代理工厂，来生产对应的代理对象。
MapperRegistry主要是用来持有Mapper->MapperProxyFactory的映射。
## **build和parse** ##
build和parse包主要是对框架需要用的XML配置文件：Mybatis-config.xml和Mapper.xml等文件进行解析然后创建对应的对象进行持有处理，使用Sax解析xml和xpath来处理xml文档。
## **Cache** ##
Cache主要使用管理一级缓存和二级缓存的，此处的Cache使用了装饰者模式，通过CacheBuilder（Builder模式）对Cache进行装饰，用户也可以实现Cache接口实现自定义的Cache。
一级缓存主要是通过PerpetualCache子类来实现，而二级缓存则通过其他子类进行装饰实现。
缓存其实可以看成是一个Map,最重要的一点是如何构建key，只要key相等就可以认为这两次的查询是相等的。
CacheKey是通过查询的特征值来确定的：statementId，查询结果集的范围即rowBounds.offset和rowBounds.limit、查询最终对应的的SQL和设置的参数值

## **datasource** ##
数据源主要通过工厂模式实现，主要分为UnpooledDataSource和pooledDataSource两种，是否有连接池实现。
pooledDataSource其实是对unpooledDataSource进行了一层封装，连接的创建还是通过unpooledDataSource创建的，不过获取连接和销毁连接的时候，并不是直接创建和直接销毁，而是
根据PoolState持有的池信息来管理，PoolState中有idleConnections和activeConnections两个队列，根据队列的状态来对连接的创建和销毁请求进行处理。
## **exception** ##
主要是异常进行封装
## **executor** ##
查询执行器，主要是对SQL执行语句的动态生成和对查询缓存进行维护。
## **io** ##
io包
## **mapping** ##
映射包
## **reflection** ##
反射包，为框架提供支持
## **script** ##
构建动态SQL
## **session** ##
为应用层提供服务，提供数据访问接口
## **transaction** ##
事务管理
## **type** ##
Java数据类型与JDBC数据类型的转换（还有别名），类型处理器
## **util** ##
工具类包


##  **联系我们** ##
- Email : xianyijun0@gmail.com

- Github : https://github.com/xianyijun

