import org.jetbrains.intellij.platform.gradle.TestFrameworkType

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
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    // BasePlatformTestCase 는 JUnit3 스타일이라 헤드리스로 돌린다
    systemProperty("java.awt.headless", "true")
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
    }
}

// Java/Kotlin 타깃은 일부러 지정하지 않는다.
// IntelliJ Platform 플러그인이 대상 플랫폼(2026.2)에 맞는 값을 afterEvaluate 에서 설정하며,
// 여기서 21 로 못박아도 덮어써진다(verifyPluginProjectConfiguration 이 25 로 보고).
// sinceBuild 를 대상 플랫폼과 맞춰 두면 바이트코드 호환 경고도 함께 사라진다.

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            // 대상 플랫폼과 동일하게. 낮추면 그보다 낮은 IDE 에는 없는 API 를 쓸 위험이 생긴다.
            sinceBuild = "262"
            // until-build 는 두지 않는다 — 두면 IDE 를 올리는 순간 설치가 막힌다.
            untilBuild = provider { null }
        }
    }
    // 설정 검색 인덱스는 빌드마다 IDE 를 띄워서 느리다 — 로컬 설치본에는 불필요
    buildSearchableOptions = false
}

tasks.runIde {
    // ./gradlew runIde -PideProject=/경로  로 특정 폴더를 연 채 샌드박스 IDE 를 띄운다.
    (project.findProperty("ideProject") as String?)?.let { args(it) }
    // 샌드박스에서 신뢰 확인 대화상자를 건너뛴다 (테스트용 프로젝트를 바로 열기 위함)
    jvmArgumentProviders.add(
        CommandLineArgumentProvider {
            listOf(
                "-Didea.trust.all.projects=true",
                "-Dide.show.tips.on.startup.default.value=false",
            )
        },
    )
}
