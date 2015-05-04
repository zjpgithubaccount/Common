var ioc = {
    dataSource: {
        type: "com.alibaba.druid.pool.DruidDataSource",
        events: {
            depose: "close"
        },
        fields: {
//            driverClassName: '${database.driverClassName}',
            url: '${database.url}',
            username: '${database.username}',
            password: '${database.password}',
            initialSize: 10,
            maxActive: 100,
            minIdle: 10,
            defaultAutoCommit: false,
            testWhileIdle: true,
            filters: "stat",
            //validationQueryTimeout : 5,
            validationQuery: "${database.validationQuery}"
        }
    },
    dao: {
        type: "org.simple.base.nutz.dao.CommonDao",
        fields: {
            dataSource: {
                refer: 'dataSource'
            }
        }
    },
    configDao: {
        type: "org.simple.base.nutz.dao.CommonDao",
        fields: {
            dataSource: {
                refer: 'dataSource'
            }
        }
    }
};
