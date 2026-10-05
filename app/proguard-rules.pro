# 本工程是纯 GUI 应用（没有 Xposed / hook 模块），release 构建启用 R8：
# 见 app/build.gradle.kts 的 isMinifyEnabled = true + isShrinkResources = true。
#
# 当前代码没有依赖反射的入口（配置读写用 org.json 手工处理，页面全部由 Compose 直接引用），
# 因此不需要额外的 -keep：依赖库自带的 consumer rules 会被 R8 自动合并。
# 若后续引入反射 / 序列化 / JNI 相关库，请在此补充对应的 keep 规则。
