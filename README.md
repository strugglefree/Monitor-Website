## 运维监控系统

本系统分为服务端和客户端，客户端需要向服务端进行注册，注册完成后即可向服务端进行数据上报，由服务端实时处理客户端上报的监控数据，
并以图表形式在前端展示，同时用户也可以使用前端页面进行快捷SSH登录操作，便于对服务端的远程管理。（本项目仅作参考和学习）

* * *

### 客户端

客户端是安装在需要监控主机上的软件程序，开启后自动进行数据收集并完成上报。  
1. 客户端需要先向服务端进行注册，注册完成后才能开始使用。
2. 客户端通过定时任务不断收集当前机器运行数据，并上报给服务端。
3. Web界面可以配合服务端实现SSH远程控制交互。


客户端技术栈：
* 采用SpringBoot 3最新版作为基础框架  
* 采用oshi框架实现跨平台硬件实时运行数据读取  
* 采用SpringQuartz实现定时任务调度  
* 采用JSON存储服务端连接信息  

### 服务端

服务端需要对客户端提供的监控数据进行整理，生成一个可供前端折线图展示的时间段数据集，并实时进行更新，利用缓存技术对数据获取进行优化等等。

1. 服务端需要保存所有客户端信息，让客户端可以注册。
2. 服务端需要接收并处理客户端发来的监控数据，方便前端快捷查看。
3. 客户端支持多账户，可以进行权限配置，不同服务器可以由不同账户进行管理。

服务端技术栈：

* 采用JSCH框架实现远程SSH连接
* 采用WebSocket与前端对接实现前端Shell操作
* 采用InfluxDB实现服务器监控历史信息存储效率更高
* 采用Mybatis-Plus作为持久层框架，使用更便捷
* 采用Redis存储注册/重置操作验证码，带过期时间控制
* 采用RabbitMQ积压短信发送任务，再由监听器统一处理
* 采用SpringSecurity作为权限校验框架，手动整合Jwt校验方案
* 采用Redis进行IP地址限流处理，防刷接口
* 视图层对象和数据层对象分离，编写工具方法利用反射快速互相转换
* 错误和异常页面统一采用JSON格式返回，前端处理响应更统一
* 手动处理跨域，采用过滤器实现
* 使用Swagger作为接口文档自动生成，已自动配置登录相关接口
* 采用过滤器实现对所有请求自动生成雪花ID方便线上定位问题
* 针对于多环境进行处理，开发环境和生产环境采用不同的配置
* 日志中包含单次请求完整信息以及对应的雪花ID，支持文件记录

### 网页端
网页端用于展示所有服务器数据，以及实时更新服务器运行时状态，配合后端完成前端伪SSH终端，快捷方便对服务器进行管理，支持子账户分权限管理等。

前端技术栈：

* 采用最新Vue3构建
* 采用ElementUI作为UI框架
* 采用Fontawsome作为图标库
* 采用Xterm.js作为前端伪终端实现
* 子账户权限控制
* 暗黑模式适配
