# 🚀 DevFlow — AI驱动的软件项目管理系统

> Java 21 + Spring Boot 3.2 + Vue3 + DeepSeek AI  
> 2026年Java全栈实习面试级项目

---

## 📋 项目简介

DevFlow 是一个**AI增强的软件项目管理工具**，在传统 Issue 追踪系统基础上集成了 DeepSeek 大模型，实现 Issue 自动分类、相似问题检索、每日站报生成等智能功能。覆盖软件工程全流程：**需求管理 → Sprint规划 → Issue追踪 → AI分析 → 统计报表**。

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────┐
│                    Nginx :80                        │
│              Vue3 + Element Plus                    │
└────────────────────────┬────────────────────────────┘
                         │ REST API + JWT
┌────────────────────────▼────────────────────────────┐
│              Spring Boot 3.2 (Java 21)              │
│  ┌──────────┬──────────┬──────────┬──────────────┐  │
│  │ 用户认证 │ 项目管理 │ Issue追踪 │  Sprint管理  │  │
│  │ JWT+BCrypt│  CRUD   │ 状态流转  │  迭代规划    │  │
│  └──────────┴──────────┴──────────┴──────────────┘  │
│  ┌──────────────────────────────────────────────┐   │
│  │              🤖 AI 引擎                       │   │
│  │  DeepSeek API → 自动分类/相似检索/每日站报    │   │
│  └──────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│                    MySQL 8.0                         │
│   user | project | sprint | issue | comment          │
│   role | label | notification | project_member       │
└─────────────────────────────────────────────────────┘
```

---

## 🛠️ 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **后端** | Java 21 + Spring Boot 3.2.7 | 虚拟线程就绪 |
| **安全** | Spring Security + JWT + BCrypt | 无状态认证 |
| **ORM** | Spring Data JPA + Hibernate | 自动建表 + 迁移 |
| **数据库** | MySQL 8.0 | 11张核心业务表 |
| **AI** | DeepSeek API (OpenAI兼容) | 3个AI智能功能 |
| **前端** | Vue3 + Element Plus + Vite | SPA单页应用 |
| **文档** | Knife4j / Swagger | 接口文档自动生成 |
| **部署** | Docker Compose | 一键部署 |

---

## ✨ 核心功能

### 1. 用户认证
- 注册/登录（BCrypt 密码加密）
- JWT 无状态认证，24h过期
- Spring Security 拦截器自动校验

### 2. 项目管理
- 项目 CRUD + 成员管理（PM/Developer/Tester）
- Sprint 迭代规划（Plan → Active → Complete）
- 通知系统（Issue分配/状态变更/评论通知）

### 3. Issue 追踪
- Issue 创建/编辑/删除（BUG / FEATURE / TASK / IMPROVEMENT）
- **状态流转**：TODO → IN_PROGRESS → IN_REVIEW → TESTING → DONE
- 评论系统 + 实时通知
- 筛选：按状态/类型/负责人

### 4. 🤖 AI 智能功能
- **自动分类**：提交 Issue 时 AI 自动识别类型、优先级、标签
- **相似检索**：AI 判断新 Issue 是否与已有 Issue 重复或相关
- **每日站报**：根据当日 Issue 变更生成 Scrum 站会报告

### 5. 前端界面
- Vue3 + Element Plus 响应式设计
- 登录/项目列表/Issue看板/Sprint管理/AI面板
- Vite 构建，首屏加载 < 500ms

---

## 📡 API 接口一览

```
POST   /api/auth/register          注册
POST   /api/auth/login             登录（返回JWT）

GET    /api/projects               项目列表
POST   /api/projects               创建项目
GET    /api/projects/{id}          项目详情
PUT    /api/projects/{id}          更新项目
DELETE /api/projects/{id}          删除项目
POST   /api/projects/{id}/members  添加成员

POST   /api/projects/{pid}/sprints               创建Sprint
GET    /api/projects/{pid}/sprints               Sprint列表
PUT    /api/projects/{pid}/sprints/{sid}/start   开始Sprint

POST   /api/projects/{pid}/issues                 创建Issue
GET    /api/projects/{pid}/issues                  Issue列表（支持筛选）
POST   /api/projects/{pid}/issues/{iid}/transition 状态流转
POST   /api/projects/{pid}/issues/{iid}/comments   添加评论

POST   /api/ai/issues/{id}/classify     🤖 AI自动分类
GET    /api/ai/issues/{id}/similar      🤖 相似Issue检索
GET    /api/ai/projects/{pid}/standup   🤖 每日站报

GET    /api/notifications              通知列表
```

---

## 🚀 本地启动

### 环境要求
- JDK 21+
- MySQL 8.0
- Maven 3.9+
- Node.js 18+

### 启动步骤

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS devflow"

# 2. 启动后端（端口8080）
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd devflow && mvn spring-boot:run

# 3. 启动前端（端口3000，开发模式）
cd devflow/frontend && npm install && npm run dev

# 4. 访问
# 前端: http://localhost:3000
# API文档: http://localhost:8080/docs
# 测试账号: admin / admin123
```

### 一键打包（前端+后端合并）

```bash
cd devflow/frontend && npm run build
cp -r dist/* ../src/main/resources/static/
cd .. && mvn clean package -DskipTests
# 一个 devflow-1.0.0.jar 包含全部，java -jar 直接跑
```

---

## 🧪 测试数据

| 账户 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |
| dev01 | dev123 | 开发者 |

---

## 📂 项目结构

```
devflow/
├── src/main/java/com/devflow/
│   ├── ai/AiService.java           🤖 DeepSeek AI引擎
│   ├── config/SecurityConfig.java   🔐 Spring Security + JWT
│   ├── controller/                  🌐 REST接口层
│   ├── dto/                         📦 请求/响应对象
│   ├── entity/                      🗄️ JPA实体（11张表）
│   ├── repository/                  📂 数据访问层
│   ├── security/                    🔑 JWT工具类+过滤器
│   └── service/                     💼 业务逻辑层
├── frontend/                        🎨 Vue3前端
│   └── src/views/                   Login/Projects/ProjectDetail/IssueDetail
├── docker-compose.yml               🐳 Docker部署
└── README.md
```

---

## 🎯 面试亮点速查

| 技术点 | 对应代码 | 能聊多久 |
|--------|---------|---------|
| JWT 无状态认证 | `security/JwtUtils.java` | 5-8分钟 |
| BCrypt 密码加密 | `config/SecurityConfig.java` | 3-5分钟 |
| Issue 状态机 | `impl/IssueServiceImpl.java:26` | 5-7分钟 |
| RBAC 权限控制 | `entity/Role.java` + `SecurityConfig` | 5-8分钟 |
| DeepSeek API 集成 | `ai/AiService.java` | 8-10分钟 |
| Prompt 工程（JSON约束输出） | `ai/AiService.java:45-55` | 5-8分钟 |
| RAG 相似检索思路 | `ai/AiService.java:86-140` | 5-7分钟 |
| JPA 自动建表 | `application.yml:13` | 2-3分钟 |
| 全局异常处理 | `common/GlobalExceptionHandler.java` | 3-5分钟 |
| Vue3 SPA 前后端分离 | `frontend/src/` | 5-8分钟 |

---

## 📝 MIT License

Built with ❤️ for 2026 Java Internship Interview
