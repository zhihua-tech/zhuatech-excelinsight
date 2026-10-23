# ZhuaTech Excel Insight｜知华科技Excel 智能筛选系统

## 本次新增：表格字段质量画像（2026-10-23）

对已解析表格逐列核算缺失、无效、去重和数值统计，给出具体行号及字段错误，不再返回固定统计数字。

从工作台进入“专项业务工具”（`/practical.html`）。本次功能执行真实后端计算、支持错误提示、表格预览和完整结果导出；不把旧演示结果冒充成新功能实现。

轻量H5项目没有登录体系，本新增接口默认关闭（503）。部署时以环境变量ZHUATECH_PRACTICAL_TOKEN配置独立32–256位可打印ASCII令牌，然后在专项页面填写该令牌；令牌只保留在本页内存。Compose已透传该变量。不要把真实令牌写入前端、示例文件或Git。旧演示接口不因此变成生产授权体系。

接口：`POST /api/practical/typed-table-profile`。算法、样例、校验及测试命令见[专项升级说明](docs/AI_MULTIMODAL_UPGRADE_20261023.md)。不调用外部模型、不自动写业务单据。

[简体中文](README.md) | [English](README.en.md)

ZhuaTech Excel Insight 是上海如静知华信息科技有限公司面向“表格数据分析”场景推出的社区源码项目。面向业务人员的 Excel 数据筛选、异常发现与结果导出系统。用自然语言配置筛选口径，并保留字段、规则和结果快照。

[知华科技官网](https://www.zhuatech.cn/) · Java 包名 `cn.zhuatech.excelinsight` · API `POST /api/excelinsight/run`

> 工程默认执行本地确定性演示逻辑，不内置模型、密钥和第三方数据。已预留 DeepSeek 兼容配置，使用者可自行接入并承担数据授权、输出复核和业务合规责任。

![知华科技Excel 智能筛选工作台](docs/images/excelinsight-dashboard.png)

界面围绕真实业务队列组织：上方展示核心运营指标，中间并列呈现处理队列与辅助分析，关键结论提供置信度、依据和人工确认入口，避免把模型输出直接写入正式业务。

## 从业务队列开始，而不是从聊天框开始

| 模块 | 社区源码版能力 |
| --- | --- |
| 数据工作台 | 命中总金额 1,286 万元 |
| 筛选规则 | 18 条单笔金额超过 10 万元 |
| 异常记录 | 补齐缺失客户编码 |
| 导出任务 | 由财务复核高金额记录 |
| AI 扩展 | 本地演示管线、DeepSeek 兼容请求载荷、置信度阈值与人工复核状态 |
| 工程能力 | Java 21、Spring Boot 4、H5、MySQL 8、Docker Compose、JUnit 自动化测试 |

## 工程结构

`backend/` 提供可验证的 Java API，`frontend/` 是可直接运行的响应式 H5 工作台，`database/schema.sql` 给出 MySQL 业务表与审计表，`docs/images/` 保存实际页面截图。

## 本地运行

```bash
cd backend
mvn spring-boot:run
```

直接打开 `frontend/index.html` 即可使用前端演示；也可以运行完整容器：

```bash
docker compose up --build
```

访问 `http://localhost:8088`。即使后端未启动，页面仍会返回相同结构的本地演示结果。

## DeepSeek 接入预留

```dotenv
ZHUATECH_LLM_PROVIDER=local
ZHUATECH_LLM_BASE_URL=https://api.deepseek.com
ZHUATECH_LLM_MODEL=deepseek-chat
ZHUATECH_LLM_API_KEY=
```

建议在 `WorkspaceService` 外增加 Provider 接口，将外部调用放入独立适配器；API Key 只通过环境变量或密钥管理服务注入，不提交到源码仓库。生产环境还应补充组织权限、数据脱敏、调用审计、限流、失败重试和人工确认。

## 负责任使用

- 只处理已获授权的数据，不得上传无权访问的客户、员工或个人信息。
- AI 结果属于辅助信息，不能替代业务负责人、专业人员或法定审批人的决定。
- 高风险结论、对外承诺和正式业务写入必须经过人工复核。
- 本仓库不包含真实业务数据、生产账号、模型密钥或第三方受版权保护的素材。

## 许可、咨询与商业服务

本工程仅限个人学习、研究和非商业技术交流，**不得商用**。企业内部生产使用、SaaS 部署、软件实施、模型接入、品牌定制与项目交付，均须获得上海如静知华信息科技有限公司书面授权，详见 [LICENSE](LICENSE)。

| 微信咨询一 | 微信咨询二 |
| --- | --- |
| ![知华科技微信咨询二维码一](docs/images/zhuatech-wechat-consulting.png) | ![知华科技微信咨询二维码二](docs/images/zhuatech-wechat-consulting-2.png) |

深度开发、中小企业 AI 转型和软件项目外包请访问：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)

SEO：Excel智能筛选,自然语言筛选,表格数据分析,数据质量,DeepSeek Java、企业 AI 转型、知华科技、上海软件外包、中小企业信息化。


## 2026 企业级热度项目升级

本次根据公开仓库访问热度补充 **数据质量与发布治理**：对缺失、重复、模式漂移、敏感字段、责任人和血缘进行质量评级及发布阻断。

- 企业 API：`POST /api/enterprise/data-quality/assess`
- 决策输出：执行许可、量化指标、阻断/升级路线、控制清单
- 可审计性：规则确定、输入输出可留痕，并附正常与阻断场景测试
- AI 接入：预留 DeepSeek-compatible 建议层配置，AI 不直接绕过审批或改变正式业务状态

详细设计见 [企业级升级说明](docs/ENTERPRISE_UPGRADE.md)。深度开发、企业部署和系统集成请联系[知华科技（上海如静知华信息科技有限公司）](https://www.zhuatech.cn/)。
