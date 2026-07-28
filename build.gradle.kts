plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "kr.amcg"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // IC = IntelliJ IDEA Community. 플러그인은 platform 모듈만 쓰므로
        // PyCharm·WebStorm·CLion 등 모든 JetBrains IDE 에 설치된다.
        create("IC", "2024.3")
    }
}

kotlin {
    jvmToolchain(21)
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"
            untilBuild = "260.*"
        }
    }
}

tasks {
    // 플러그인 검증(verifyPlugin)은 네트워크로 IDE 를 추가 다운로드하므로 기본 빌드에서 제외
    buildSearchableOptions {
        enabled = false
    }
}
