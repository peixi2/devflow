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
│             Caddy :80/:443 + 自动 HTTPS             │
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
| **部署** | Docker Compose + Caddy | 一键部署 + 自动 HTTPS |

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
- Node.js 20.19+ 或 22.12+

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
# 首次使用: 在登录页注册账号
```

### 一键打包（前端+后端合并）

```bash
cd devflow/frontend && npm run build
cp -r dist/* ../src/main/resources/static/
cd .. && mvn clean package -DskipTests
# 一个 devflow-1.0.0.jar 包含全部，java -jar 直接跑
```

### Docker 本地验证

```bash
cp .env.example .env
# 修改 .env 中的数据库密码、JWT 密钥和演示账号密码
docker compose up -d --build
docker compose ps
```

默认访问 `http://localhost`。停止服务但保留数据库数据：

```bash
docker compose down
```

### 服务器 + 域名部署

服务器要求：Linux x86_64/arm64、Docker Engine 24+、Docker Compose v2，推荐 2 核 4 GB 内存。

1. 在域名服务商处添加一条 A 记录，将演示子域名指向服务器公网 IP。
2. 在服务器安全组中开放 TCP 80、TCP 443 和 UDP 443；不要开放 MySQL 3306。
3. 克隆仓库并创建生产环境变量：

```bash
git clone https://github.com/peixi2/devflow.git
cd devflow
cp .env.example .env
```

4. 编辑 `.env`：

```dotenv
SITE_ADDRESS=devflow.example.com
MYSQL_PASSWORD=<随机数据库密码>
MYSQL_ROOT_PASSWORD=<另一个随机数据库密码>
JWT_SECRET=<至少32字符的随机密钥>
DEMO_USERNAME=admin
DEMO_PASSWORD=<演示账号密码>
DEEPSEEK_API_KEY=<DeepSeek API Key>
```

5. 启动并检查：

```bash
chmod +x scripts/*.sh
./scripts/deploy.sh
curl https://devflow.example.com/health
```

Caddy 会在 DNS 生效且 80/443 可访问后自动申请和续签 HTTPS 证书。更新版本时执行：

```bash
git pull --ff-only
./scripts/deploy.sh
```

数据库备份：

```bash
./scripts/backup.sh
```

生产环境默认关闭公开注册，并在数据库为空时创建演示账号、示例项目、Sprint 和 Issue。所有密钥只保存在服务器 `.env` 中，该文件已被 Git 忽略。

### 免费 Render 演示部署

仓库根目录的 `render.yaml` 可以创建免费的 Render Web Service，并自动获得固定的
`*.onrender.com` HTTPS 地址。该部署使用 H2 内存数据库，实例休眠或重启后会自动恢复演示数据。

1. 登录 Render 并连接此 GitHub 仓库。
2. 选择 **New > Blueprint**，然后选择 `devflow` 仓库。
3. 确认使用 Free 计划并创建服务。

免费实例闲置 15 分钟后会休眠，首次访问的唤醒时间约为一分钟。演示账号默认为
`admin` / `admin123`。如需启用 AI，在 Render 环境变量中设置 `DEEPSEEK_API_KEY`。

---

## 🧪 测试数据

| 账户 | 密码 | 角色 |
|------|------|------|
| `.env` 中的 `DEMO_USERNAME` | `.env` 中的 `DEMO_PASSWORD` | 演示账号 |

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
