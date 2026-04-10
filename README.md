# 中医方剂——趣味方歌（记忆卡片应用）

## 项目简介

这是一个基于Kotlin开发的Android记忆卡片应用，主要用于帮助用户记忆和复习方剂学方歌等内容。应用采用SM2记忆算法，通过智能调度复习时间，提高记忆效率。

## 功能特点

- 📚 **方剂学方歌记忆**：支持方剂学方歌的学习和复习
- 🧠 **智能记忆算法**：采用SM2算法，根据记忆效果自动调整复习时间
- 📱 **直观的用户界面**：现代化的Material Design风格
- 📊 **学习进度追踪**：记录学习历史和复习效果
- 🔄 **数据同步**：支持数据的备份和同步
- 🎨 **自定义卡片**：允许用户编辑和管理卡片内容
- 📅 **复习计划**：智能生成复习计划，提醒用户按时复习

## 项目结构

```
app/src/main/
├── java/com/example/memorycard/    # 核心代码
│   ├── algorithm/                  # 记忆算法
│   ├── data/                       # 数据处理
│   ├── database/                   # 数据库模型
│   ├── util/                       # 工具类
│   ├── view/                       # 自定义视图
│   ├── widget/                     # 小部件
│   └── *.kt                        # 各种Activity
├── res/                            # 资源文件
│   ├── layout/                     # 布局文件
│   ├── drawable/                   # 图片资源
│   ├── values/                     # 配置资源
│   └── anim/                       # 动画资源
└── AndroidManifest.xml             # 应用配置
```

## 核心功能模块

### 1. 记忆算法模块
- **SM2Algorithm.kt**：实现SM2记忆算法，计算最佳复习时间
- **MemoryLevel.kt**：定义记忆难度等级和相关逻辑

### 2. 数据管理模块
- **DataInitializer.kt**：初始化应用数据
- **DataSyncManager.kt**：管理数据同步
- **AppDatabase.kt**：数据库操作
- **FanggeCard.kt**：方剂卡片数据模型
- **StudyHistory.kt**：学习历史记录

### 3. 界面模块
- **MainActivity.kt**：应用主入口
- **HomeActivity.kt**：首页
- **FormulaListActivity.kt**：方剂列表
- **ReviewScheduleActivity.kt**：复习计划
- **StudyHistoryActivity.kt**：学习历史
- **SettingsActivity.kt**：设置页面

### 4. 自定义组件
- **BreathingGlowView.kt**：呼吸灯效果视图
- **TrailView.kt**：轨迹视图
- **FanggeWidget.kt**：桌面小部件

## 技术栈

- **开发语言**：Kotlin
- **开发框架**：Android SDK
- **UI设计**：Material Design
- **数据库**：Room
- **构建工具**：Gradle

## 安装和使用

### 安装
1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 构建并运行应用到Android设备或模拟器

### 使用方法
1. **首次启动**：应用会自动初始化方剂学方歌数据
2. **学习模式**：浏览方剂列表，点击卡片开始学习
3. **复习模式**：根据应用提醒的复习计划进行复习
4. **管理卡片**：在设置页面可以编辑和管理卡片
5. **查看历史**：在学习历史页面查看学习记录和进度

## 贡献

欢迎提交Issue和Pull Request来改进这个项目！

## 许可证

MIT License
