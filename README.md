# 考勤管理系统

基于 Spring Boot 的课堂考勤管理系统，支持学生签到打卡、考勤统计、Excel 批量导入等功能。

**学号：42411142  姓名：薛磊**

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.2.0 |
| MyBatis | 3.0.4 |
| MySQL | 8.0+ |
| Spring Security | 6.x |
| Thymeleaf | 3.x |
| PageHelper | 1.4.7 |
| Apache POI | 5.2.3 |

## 功能模块

### 用户角色

- **ADMIN** — 管理员，拥有全部权限
- **TEACHER** — 教师，可管理学生、课程、课次、选课、考勤
- **STUDENT** — 学生，可打卡签到、查看个人考勤统计

### 功能列表

- 用户注册与登录（Spring Security + BCrypt 加密）
- 基于角色的权限控制（侧边栏 + API 双层拦截）
- **用户管理**：管理员增删改查用户（ADMIN / TEACHER / STUDENT）
- **学生管理**：增删改查学生，支持 Excel 批量导入
- **教室管理**：教室信息维护，可视化不可用座位展示
- **课程管理**：课程编排，关联教师与教室
- **课次管理**：课程节次安排，支持精确到分钟的日期时间
- **选课管理**：学生选课记录维护
- **考勤管理**：签到记录查看、状态编辑（正常/迟到/早退/缺勤）
- **打卡签到**：学生登录后查看今日课次，一键签到，10 分钟内签到为正常，超时记为迟到
- **考勤统计**：按学生维度统计出勤率、正常/迟到/缺勤次数

## 项目结构

```
src/main/java/com/example/attendancesystem/
├── AttendanceSystemApplication.java   # 启动类
├── controller/                        # 控制器层
│   ├── PageController.java            # 页面路由
│   ├── RegisterController.java        # 注册接口
│   ├── UserController.java            # 用户 CRUD
│   ├── StudentController.java         # 学生 CRUD + Excel 导入
│   ├── ClassroomController.java       # 教室 CRUD
│   ├── CourseController.java          # 课程 CRUD
│   ├── CourseSessionController.java   # 课次 CRUD
│   ├── CourseSelectionController.java # 选课 CRUD
│   └── AttendanceController.java      # 签到 + 考勤 CRUD + 统计
├── entity/                            # 实体 & 查询参数
│   ├── User.java / UserQueryParam.java
│   ├── Student.java / StudentQueryParam.java
│   ├── Classroom.java / ClassroomQueryParam.java
│   ├── Course.java / CourseQueryParam.java
│   ├── CourseSession.java / CourseSessionQueryParam.java
│   ├── CourseSelection.java / CourseSelectionQueryParam.java
│   ├── Attendance.java / AttendanceQueryParam.java
│   └── StatisticsDTO.java
├── mapper/                            # MyBatis Mapper 接口
├── service/                           # 服务接口 & 实现
│   └── impl/
└── util/                              # 工具类
    ├── Result.java                    # 统一响应格式
    ├── PageResult.java                # 分页结果封装
    ├── ImportResult.java              # Excel 导入结果
    └── SecurityConfig.java            # Spring Security 配置

src/main/resources/
├── application.yml                    # 应用配置
├── templates/                         # Thymeleaf 模板
│   ├── fragments/sidebar.html         # 侧边栏碎片
│   ├── login.html / register.html     # 登录 / 注册
│   ├── welcome.html                   # 首页（打卡页）
│   ├── user-list.html / user-form.html
│   ├── student-list.html / student-form.html / student-import.html
│   ├── classroom-list.html / classroom-form.html
│   ├── course-list.html / course-form.html
│   ├── course-session-list.html / course-session-form.html
│   ├── course-selection-list.html / course-selection-form.html
│   ├── attendance-list.html
│   └── attendance-statistics.html     # 考勤统计页
├── static/
│   ├── css/layout.css / student-list.css / auth.css
│   └── js/                            # 各页面 JS
└── com/example/attendancesystem/mapper/
    └── *.xml                          # MyBatis SQL 映射（与 Mapper 接口同目录）
```

## 数据库表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user` | 用户（登录账号） | id, username, password, real_name, role |
| `student` | 学生信息 | student_id, student_name, gender |
| `classroom` | 教室 | id, classroom_name, rows, cols, exclude_seats |
| `course` | 课程 | course_id, course_name, teacher_id, classroom_id |
| `course_session` | 课次 | session_id, course_id, session_date(datetime), status |
| `course_selection` | 选课记录 | id, student_id, course_id |
| `attendance` | 考勤记录 | id, session_id, student_id, check_in_time, status |

考勤状态枚举：`NORMAL`（正常）/ `LATE`（迟到）/ `EARLY`（早退）/ `ABSENT`（缺勤）

## 快速开始

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.6+

### 数据库初始化

```sql
CREATE DATABASE IF NOT EXISTS attendance_system DEFAULT CHARSET utf8mb4;
```

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/attendance_system
    username: root
    password: your_password
```

### 启动

```bash
mvn spring-boot:run
```

访问 `http://localhost:8080`，默认跳转登录页。

首次使用需通过注册创建学生账号，或使用已有管理员账号登录后创建用户。

### 默认管理员

如数据库中无管理员账号，可在 `user` 表手动插入：

```sql
INSERT INTO user(username, password, real_name, role, create_time)
VALUES('admin', '$2a$10$...', '管理员', 'ADMIN', NOW());
```

（密码为 BCrypt 加密后的 `swufe`）

## 打卡签到流程

1. 学生注册或由管理员创建 → `user` + `student` 表同步写入
2. 管理员/教师创建课程 → 创建课次（指定日期时间）→ 学生选课
3. 学生登录 → 首页展示当日课次 → 点击"签到"
4. 系统比较签到时间与课次时间：
   - ≤10 分钟 → `NORMAL`（正常）
   - >10 分钟 → `LATE`（迟到）
5. 学生可在"考勤统计"查看个人出勤率

## API 响应格式

```json
{
  "code": 1,        // 1=成功, 0=失败
  "msg": "...",
  "data": {}
}
```

分页响应：

```json
{
  "code": 1,
  "data": {
    "total": 100,
    "rows": [...]
  }
}
```

## 配置说明

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB      # Excel 导入文件大小限制
      max-request-size: 50MB

file:
  upload:
    path: D:/Code_JavaStudy/upload_file/   # 文件上传临时目录
```
