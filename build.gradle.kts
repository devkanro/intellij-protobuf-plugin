import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.5.2"
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "1.3.1"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "0.1.13"

    id("org.jetbrains.grammarkit") version "2021.2.2"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.commonmark:commonmark:0.18.2")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.18.2")
    implementation("org.commonmark:commonmark-ext-autolink:0.18.2")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

changelog {
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}

qodana {
    cachePath.set(projectDir.resolve(".qodana").canonicalPath)
    reportPath.set(projectDir.resolve("build/reports/inspections").canonicalPath)
    saveReport.set(true)
    showReport.set(System.getenv("QODANA_SHOW_REPORT")?.toBoolean() ?: false)
}

tasks {
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = it
            kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
            dependsOn(named("generateLexer"), named("generateParser"))
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            projectDir.resolve("README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider {
            changelog.run {
                getOrNull(properties("pluginVersion")) ?: getLatest()
            }.toHTML()
        })
    }

    generateLexer {
        source.set("src/main/grammar/protobuf.flex")
        targetDir.set(buildDir.resolve("generated/sources/grammar/io/kanro/idea/plugin/protobuf/lang/lexer").path)
        targetClass.set("_ProtobufLexer")
        purgeOldFiles.set(true)
    }

    generateParser {
        source.set("src/main/grammar/protobuf.bnf")
        targetRoot.set(buildDir.resolve("generated/sources/grammar").path)
        purgeOldFiles.set(true)
        pathToParser.set("io/kanro/idea/plugin/protobuf/lang/parser/ProtobufParser.java")
        pathToPsiRoot.set("io/kanro/idea/plugin/protobuf/lang/psi")
    }

    prepareSandbox {
        doLast {
            val file = file(buildDir.resolve("idea-sandbox/config/disabled_plugins.txt"))
            file.ensureParentDirsCreated()
            file.writeText(
                buildString {
                    appendLine("idea.plugin.protoeditor")
                    appendLine("com.intellij.grpc")
                }
            )
        }
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}

sourceSets {
    named("main") {
        java {
            srcDir(buildDir.resolve("generated/sources/grammar"))
        }
    }
}
