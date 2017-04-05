# Multiple Datacenter Collaborative Process System
## 0、简介
Multiple Datacenter Collaborative Process System（MDCPS）为多源、海量遥感数据的分布式处理平台，该平台采用主从架构，基于主数据中心（主中心）和分布式的数据中心（子中心）上的分布式任务执行代理框架，实现多数据中心上海量遥感数据的处理。
## 1、系统架构
* 主数据中心 ：生产流程库+多级任务订单库+Kepler工作流+多目标优化工作流调度模型+Webservice协议+元数据管理+GridTFP数据传输+多级产品库
* 子数据中心任务执行代理：任务队列+PBS批量作业系统+任务状态更新引擎
 
## 2、详细说明 
详细系统架构参考：<http://link.springer.com/article/10.1007%2Fs10586-016-0577-6>
## 3、源码目录结构

</br>├─CommonProductRepositoryBySQL2.2              ### 最终数据产品入库程序源码
</br>│  └─src
</br>│      ├─DataNamerParser
</br>│      ├─DBSystem
</br>│      ├─domain
</br>│      ├─FileOperation
</br>│      └─MainSystem
</br>├─DataBase                                     ### 数据库
</br>├─MultiProcessSysWebService_Thread_Online_1.3  ### 主中心业务系统源码
</br>│  ├─build-area
</br>│  ├─src
</br>│  │  ├─DataService
</br>│  │  ├─DBManage
</br>│  │  ├─Download
</br>│  │  ├─FileOperation
</br>│  │  ├─OrderManage
</br>│  │  ├─org
</br>│  │  │  ├─dom4j
</br>│  │  │  └─kepler
</br>│  │  │      └─moml
</br>│  │  ├─RSDataManage
</br>│  │  ├─ServiceInterface
</br>│  │  ├─SystemManage
</br>│  │  ├─TaskSchedular
</br>│  │  └─Workflow
</br>│  │      └─Kepler
</br>│  └─WebContent
</br>├─OrderSubmitRunnableJar_2.4                  ### 各类型业务订单提交源码
</br>│  └─src
</br>│      ├─DBSystem
</br>│      ├─FileOperation
</br>│      ├─LogSystem
</br>│      ├─MainSystem
</br>│      ├─OrderManage
</br>│      ├─OrderSubmit
</br>│      ├─org
</br>│      │  └─tempuri
</br>│      ├─ResourceManage
</br>│      ├─RSDataManage
</br>│      └─ServiceInterface
</br>├─RSDataPrepareRunnableJar1.1                ### 数据请求与下载系统源码
</br>│  └─src
</br>│      ├─DBSystem
</br>│      ├─FTPSystem
</br>│      ├─LogSystem
</br>│      ├─MainSystem
</br>│      ├─OrderManage
</br>│      ├─RSDataInfo
</br>│      └─ServiceInterface
</br>├─StandardProductRepositoryBySQL2.3          ### 中间数据产品入库程序源码
</br>│  └─src
</br>│      ├─DataNamerParser
</br>│      ├─DBSystem
</br>│      ├─domain
</br>│      ├─FileOperation
</br>│      ├─LogSystem
</br>│      ├─MainSystem
</br>│      ├─OrderManage
</br>│      └─Storage
</br>└─TaskExecutionAgent2.4                      ### 子中心业务系统源码
</br>    └─src
</br>        ├─DBManage
</br>        ├─FileOperations
</br>        ├─OrderManage
</br>        ├─Pbs
</br>        ├─RSDataManage
</br>        ├─ServiceInterface
</br>        ├─TaskExeAgent
</br>        └─TaskSchedular

