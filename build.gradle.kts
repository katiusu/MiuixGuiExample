plugins {
    // 插件版本统一由 gradle/libs.versions.toml 提供，子模块按 id 应用即可
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

allprojects {
    repositories {
        // Miuix 自 0.9.x 起发布到 Maven Central（top.yukonga.miuix.kmp），
        // 不再需要 GitHub Packages 及其 gpr.user / gpr.key 凭据。
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }
}

// 产物统一落在本仓库根的 build/ 下（原 android/ 子目录布局的 ../../build 重定向已移除，
// 它会把 APK 写到仓库外一层，CI 无法上传）。
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}


