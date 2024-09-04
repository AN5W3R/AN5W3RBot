

### 该项目基于[LLOneBot](https://github.com/LLOneBot/LLOneBot)开发

是一款qq聊天机器人

### 一切开发旨在学习，请勿用于非法用途

- 是完全免费且开放源代码的软件，仅供学习和娱乐用途使用
- 不会通过任何方式强制收取费用，或对使用者提出物质条件

### 使用方式

1.安装NTQQ

2.安装LLONEBOT[快速开始 | LLOneBot](https://llonebot.github.io/zh-CN/guide/getting-started)

3.在要作为机器人的QQ的LLOneBot中设置正向WebSocket监听端口,并确保和setting.json中的port保持一致

4.运行该项目中的exe或者自行编译运行项目

### 功能调用符

目前功能调用符号为"."

可在"setting.json"文件中，"identifier"后进行修改

### 功能列表

#### 功能管理

@机器人 .功能管理-功能名-开启/关闭(仅群主和管理员拥有权限)

@机器人 .功能查询(可查询机器人已有功能)-(表暂未完成)

#### 连接GoogleAI的聊天功能

在私聊或群聊中@机器人可以和机器人聊天(需配置美国加速节点的代理,代理端口需要在setting.json中设置proxyPort,默认为7890)

可自己调教语言模型，调教好后，保存为配置文件，在"googleModel"后修改即可

#### 关键词检测禁言功能



#### 自定义文本聊天功能

在"AtMessage.json"文件中进行配置，含有关键词即会触发

#### 翻译功能

识别文本并翻译为中文:@机器人 .翻译-待翻译文本

识别文本并翻译为目标语言:@机器人 .翻译-待翻译文本-目标语言

将指定语言翻译为目标语言:@机器人 .翻译-待翻译文本-源语言-目标语言

其中语言可以使用中,日,英汉字,也可以使用字母缩写详细见:[Supported languages | DeepL API Docs](https://developers.deepl.com/docs/resources/supported-languages)

#### 开黑小队功能

@机器人 .创建-队伍名称(例如：lol1，lol2)-人数-备注(任意)

@机器人 .加入-lol1

@机器人 .退出-lol1

@机器人 .解散-lol1(每个人都有权限)

@机器人 .开了-lol1

创建队伍后，每隔一段时间会发一遍组队消息(时间设置暂未独立出来)

人数达到设定的数字后，机器人会自动@队内全体成员，通知可以进行游戏，也可以直接执行"开了"命令，同样的效果

#### 猜角色功能

截取图片的部分来进行猜角色游戏，可在"setting.json"文件中，"guessRatio"和"guessChance"分别设置截取"图片大小"和"可猜次数"

开启猜角色:@机器人 .猜角色-图库名称(图库和图库名称可在"ImageSrc.json"中进行配置)

提交答案:@机器人 角色的名称

答案错误时不会提示,答案正确时会发送完整图片

揭晓答案:@机器人 .答案(答案可在图库中的"text.json"进行调整)

随机查看图片和对应答案:@机器人 .图库名称


#### 图片功能

随机发送一张图库内的图片

@机器人 .美图/涩图

图库名称在"ImageSrc.json"中进行配置，图库在"ImageSrc.json"中将本地路径配置在对应图库名后即可

##### 抽签功能

@机器人 .抽签

抽签图库可在"setting.json"文件中，"fortuneSrc"后进行配置

抽签文本可在图库中"text.json"中进行修改

文本位置调整可在"font.json"中进行修改

##### 塔罗牌功能

@机器人 .塔罗牌

图库和图库名称可在"ImageSrc.json"中进行配置

文本可在图库中的"text.json"进行调整

#### 哔哩哔哩直播间监控功能

在setting.json设置BiliBiliStreamRoom参数定位直播间配置信息文件

如果有直播间开播或下播会在群聊中提醒

