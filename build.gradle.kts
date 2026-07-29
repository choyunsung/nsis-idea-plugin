import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.4.10"
    id("org.jetbrains.intellij.platform") version "2.18.1"
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
        // IntelliJ IDEA 를 컴파일 기준 플랫폼으로 쓴다. 실제 의존은 platform 모듈뿐이라
        // PyCharm·WebStorm·CLion 등 모든 JetBrains IDE 에 설치된다.
        // (IC 별도 배포는 2025.3(253) 부터 중단돼 intellijIdea() 를 쓴다)
        intellijIdea("2026.2")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            // 2024.3 이상이면 설치된다. 상한을 넓게 둬 IDE 를 올려도 막히지 않게 한다.
            sinceBuild = "243"
            untilBuild = "299.*"
        }
    }
    // 설정 검색 인덱스는 빌드마다 IDE 를 띄워서 느리다 — 로컬 설치본에는 불필요
    buildSearchableOptions = false
}
