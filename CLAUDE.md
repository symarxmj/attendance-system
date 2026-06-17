# CLAUDE.md

## 项目概述
基于 Spring Boot 3.2.0 + MyBatis 3.0.4 + Thymeleaf + Spring Security 的课堂考勤管理系统。

## 重要工作原则
- 对于用户的任何功能需求或优化建议，有任何细节不确定或存在歧义时，**务必主动询问用户澄清**，不要自行猜测或假设。涉及修改范围、数据格式、权限边界、UI 交互等问题时必须确认。

## 技术栈
- Java 21, Spring Boot 3.2.0, MyBatis 3.0.4, MySQL 8.0+
- Spring Security 6.x, Thymeleaf, PageHelper 1.4.7, Apache POI 5.2.3

## 项目结构
```
src/main/java/com/example/attendancesystem/
├── controller/     # PageController(页面路由) + REST 控制器
├── entity/         # 实体类 + QueryParam
├── mapper/         # MyBatis Mapper 接口
├── service/        # 服务接口 & impl
└── util/           # Result, PageResult, ImportResult, SecurityConfig
```

## 关键约定
- 统一响应格式 `Result` (code=1 成功, code=0 失败)
- 分页使用 PageHelper + `PageResult<T>`
- `@PreAuthorize` 方法级权限控制
- 密码 BCrypt 加密，默认密码 "swufe"
- User-Student 双表通过 username=student_id 关联
