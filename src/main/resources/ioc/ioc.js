var ioc = {
    tx: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [2]
    },
    txNONE: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [0]
    },
    txREAD_UNCOMMITTED: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [1]
    },
    txREAD_COMMITTED: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [2]
    },
    txREPEATABLE_READ: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [4]
    },
    txSERIALIZABLE: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [8]
    },
    //声明一个log进行日志记录
    log: {
        type: 'org.nutz.aop.interceptor.LoggingMethodInterceptor'
    },
    tmpFilePool: {
        type: 'org.nutz.filepool.NutFilePool',
        // 临时文件最大个数为 1000 个
        args: [
            {
                java: '$iocUtil.getPath("/upload/tmp")'
            },
            100
        ]
    },
    uploadFileContext: {
        type: 'org.nutz.mvc.upload.UploadingContext',
        singleton: false,
        args: [
            {
                refer: 'tmpFilePool'
            }
        ],
        fields: {
            // 是否忽略空文件, 默认为 false
            ignoreNull: true,
            // 单个文件最大尺寸(大约的值，单位为字节，即 1048576 为 1M)
            maxFileSize: 10240000,
            // 正则表达式匹配可以支持的文件名
            nameFilter: '^(.+[.])(jpeg|jpg|png|xls)$'
        }
    },
    uploadAdaptor: {
        type: 'org.nutz.mvc.upload.UploadAdaptor',
        singleton: false,
        args: [
            {
                refer: 'uploadFileContext'
            }
        ]
    }
};