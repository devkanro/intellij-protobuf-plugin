import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.2.1"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.3.1"

    id("org.jetbrains.grammarkit") version "2021.2.1"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
    maven { setUrl("https://www.jitpack.io") }
}

grammarKit {
    grammarKitRelease.set("2021.1.2")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

dependencies {
    implementation("org.commonmark:commonmark:0.18.1")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.18.1")
    implementation("org.commonmark:commonmark-ext-autolink:0.18.2")
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}

sourceSets {
    named("main") {
        java {
            srcDir(buildDir.resolve("generated/sources/grammar"))
        }
    }
}

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
        dependsOn(named("generateLexer"), named("generateParser"))
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
                    appendln("idea.plugin.protoeditor")
                    appendln("com.intellij.grpc")
                }
            )
        }
    }

    runIde {
        jvmArgs("-Xmx8196m")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            provider {
                projectDir.resolve("README.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"

                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
            }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
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
