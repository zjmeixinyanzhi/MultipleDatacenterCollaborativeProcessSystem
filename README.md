
Multiple Datacenter Collaborative Process System（MDCPS）为多源、海量遥感数据的分布式处理平台，该平台采用主从架构，基于主数据中心（主中心）和分布式的数据中心（子中心）上的分布式任务执行代理框架，实现多数据中心上海量遥感数据的处理。
详细系统架构参考：http://link.springer.com/article/10.1007%2Fs10586-016-0577-6

1、源码目录结构

├─CommonProductRepositoryBySQL2.2              ### 最终数据产品入库程序源码
│  └─src
│      ├─DataNamerParser
│      ├─DBSystem
│      ├─domain
│      ├─FileOperation
│      └─MainSystem
├─DataBase                                     ### 数据库
├─MultiProcessSysWebService_Thread_Online_1.3  ### 主中心业务系统源码
│  ├─build-area
│  ├─src
│  │  ├─DataService
│  │  ├─DBManage
│  │  ├─Download
│  │  ├─FileOperation
│  │  ├─OrderManage
│  │  ├─org
│  │  │  ├─dom4j
│  │  │  └─kepler
│  │  │      └─moml
│  │  ├─RSDataManage
│  │  ├─ServiceInterface
│  │  ├─SystemManage
│  │  ├─TaskSchedular
│  │  └─Workflow
│  │      └─Kepler
│  └─WebContent
├─OrderSubmitRunnableJar_2.4                  ### 各类型业务订单提交源码
│  └─src
│      ├─DBSystem
│      ├─FileOperation
│      ├─LogSystem
│      ├─MainSystem
│      ├─OrderManage
│      ├─OrderSubmit
│      ├─org
│      │  └─tempuri
│      ├─ResourceManage
│      ├─RSDataManage
│      └─ServiceInterface
├─RSDataPrepareRunnableJar1.1                ### 数据请求与下载系统源码
│  └─src
│      ├─DBSystem
│      ├─FTPSystem
│      ├─LogSystem
│      ├─MainSystem
│      ├─OrderManage
│      ├─RSDataInfo
│      └─ServiceInterface
├─StandardProductRepositoryBySQL2.3          ### 中间数据产品入库程序源码
│  └─src
│      ├─DataNamerParser
│      ├─DBSystem
│      ├─domain
│      ├─FileOperation
│      ├─LogSystem
│      ├─MainSystem
│      ├─OrderManage
│      └─Storage
└─TaskExecutionAgent2.4                      ### 子中心业务系统源码
    └─src
        ├─DBManage
        ├─FileOperations
        ├─OrderManage
        ├─Pbs
        ├─RSDataManage
        ├─ServiceInterface
        ├─TaskExeAgent
        └─TaskSchedular

