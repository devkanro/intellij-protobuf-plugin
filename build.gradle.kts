import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

fun properties(key: String) = providers.gradleProperty(key)

fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.gradleIntelliJPlugin) // Gradle IntelliJ Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
    alias(libs.plugins.grammarkit) // IntelliJ Grammark kit Plugin
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

// Configure project's dependencies
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.commonmark:commonmark:0.22.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.22.0")
    implementation("org.commonmark:commonmark-ext-autolink:0.22.0")
    implementation("com.bybutter.sisyphus:sisyphus-grpc:2.1.22")
    implementation("com.bybutter.sisyphus:sisyphus-jackson-protobuf:2.1.22")
    implementation("io.grpc:grpc-netty:1.62.2")
}

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins = properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild = properties("pluginSinceBuild")
        untilBuild = properties("pluginUntilBuild")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription =
            providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                with(it.lines()) {
                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
                }
            }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes =
            properties("pluginVersion").map { pluginVersion ->
                with(changelog) {
                    renderItem(
                        (getOrNull(pluginVersion) ?: getUnreleased())
                            .withHeader(false)
                            .withEmptySections(false),
                        Changelog.OutputType.HTML,
                    )
                }
            }
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    runIde {
        jvmArguments.add("-Didea.ProcessCanceledException=disabled")
    }

    generateLexer {
        sourceFile = layout.projectDirectory.file("src/main/grammar/protobuf.flex")
        targetOutputDir =
            layout.buildDirectory.dir("generated/sources/grammar/io/kanro/idea/plugin/protobuf/lang/lexer/proto")
        purgeOldFiles = true
    }

    generateParser {
        sourceFile = layout.projectDirectory.file("src/main/grammar/protobuf.bnf")
        targetRootOutputDir = layout.buildDirectory.dir("generated/sources/grammar")
        purgeOldFiles = true
        pathToParser = "io/kanro/idea/plugin/protobuf/lang/psi/proto/parser/ProtobufParser.java"
        pathToPsiRoot = "io/kanro/idea/plugin/protobuf/lang/psi/proto"
    }

    create<GenerateParserTask>("generateTextParser") {
        sourceFile = layout.projectDirectory.file("src/main/grammar/prototext.bnf")
        targetRootOutputDir = layout.buildDirectory.dir("generated/sources/grammar")
        purgeOldFiles = true
        pathToParser = "io/kanro/idea/plugin/protobuf/lang/psi/text/parser/ProtoTextParser.java"
        pathToPsiRoot = "io/kanro/idea/plugin/protobuf/lang/psi/text"
    }

    create<GenerateLexerTask>("generateTextLexer") {
        sourceFile = layout.projectDirectory.file("src/main/grammar/prototext.flex")
        targetOutputDir =
            layout.buildDirectory.dir("generated/sources/grammar/io/kanro/idea/plugin/protobuf/lang/lexer/text")
        purgeOldFiles = true
    }

    prepareSandbox {
        val file = layout.buildDirectory.file("idea-sandbox/config/disabled_plugins.txt").get().asFile
        doLast {
            file.ensureParentDirsCreated()
            file.writeText(
                buildString {
                    appendLine("idea.plugin.protoeditor")
                    appendLine("com.intellij.grpc")
                },
            )
        }
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token = environment("PUBLISH_TOKEN")
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels =
            properties("pluginVersion").map {
                listOf(
                    it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" },
                )
            }
    }

    compileKotlin {
        dependsOn(generateParser, named("generateTextParser"), generateLexer, named("generateTextLexer"))
    }
}

sourceSets {
    named("main") {
        java {
            srcDir(layout.buildDirectory.dir("generated/sources/grammar"))
        }
    }
}
