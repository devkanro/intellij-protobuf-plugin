package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizableOptions
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage

class ProtobufLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage(): Language {
        return ProtobufLanguage
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor()
    }

    override fun getCodeSample(settingsType: SettingsType): String {
        return """syntax = "proto3";

package foo.bar.baz;

import "google/api/annotations.proto";
import "google/api/resource.proto";
import "google/api/field_behavior.proto";

option java_package = "foo.bar.baz";
option java_multiple_files = true;
option java_outer_classname = "ShowcaseProto";

// The showcase api for protobuf file grammar.
service ShowcaseApi {
    rpc GetMessage(GetMessageRequest) returns (Message) {
        option (google.api.http) = {
            get: "/v1/echo"
        };
    }
}

/**
 * The message resource for showcase api.
 */
message Message {
    option (google.api.resource) = {
        type: "foo.bar/Message"
        pattern: "messages/{message}"
    };
    string name = 1 [(google.api.field_behavior) = REQUIRED];
    string body = 2;
    Type type = 3;

    reserved 10 to 30;

    enum Type {
        MESSAGE_TYPE_UNSPECIFIED = 0;
        MESSAGE_TYPE_FOO = 1;
        MESSAGE_TYPE_BAR = 2;
        MESSAGE_TYPE_BAZ = 3;
    }
}

message GetMessageRequest {
    string name = 1[
        (google.api.resource_reference).type = "foo.bar/Message",
        (google.api.field_behavior) = REQUIRED
    ];
}"""
    }

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions,
    ) {
        commonSettings.SPACE_BEFORE_COLON = false
        commonSettings.KEEP_BLANK_LINES_BETWEEN_PACKAGE_DECLARATION_AND_HEADER = 1
    }

    override fun customizeSettings(
        consumer: CodeStyleSettingsCustomizable,
        settingsType: SettingsType,
    ) {
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> {
                consumer.showStandardOptions("SPACE_WITHIN_BRACES")
                consumer.showStandardOptions("SPACE_WITHIN_BRACKETS")
                consumer.showStandardOptions("SPACE_WITHIN_PARENTHESES")
                consumer.showStandardOptions("SPACE_BEFORE_COMMA")
                consumer.showStandardOptions("SPACE_AFTER_COMMA")
                consumer.showStandardOptions("SPACE_BEFORE_COLON")
                consumer.moveStandardOption("SPACE_BEFORE_COLON", "Assignment")
                consumer.showStandardOptions("SPACE_AFTER_COLON")
                consumer.moveStandardOption("SPACE_AFTER_COLON", "Assignment")
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS")
                consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Around '='")
                consumer.moveStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Assignment")
                consumer.showStandardOptions("SPACE_BEFORE_CLASS_LBRACE")
                consumer.renameStandardOption("SPACE_BEFORE_CLASS_LBRACE", "Body left brace")
                consumer.moveStandardOption("SPACE_BEFORE_CLASS_LBRACE", "Before block")
                consumer.showStandardOptions("SPACE_BEFORE_METHOD_LBRACE")
                consumer.renameStandardOption("SPACE_BEFORE_METHOD_LBRACE", "Option left bracket")
                consumer.moveStandardOption("SPACE_BEFORE_METHOD_LBRACE", "Before block")
            }
            SettingsType.BLANK_LINES_SETTINGS -> {
                val blankLines = CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES
                consumer.showCustomOption(
                    ProtobufCodeStyleSettings::class.java,
                    ProtobufCodeStyleSettings::BLANK_LINES_AFTER_SYNTAX.name,
                    "After syntax statement",
                    blankLines,
                )
                consumer.showStandardOptions("BLANK_LINES_AFTER_PACKAGE")
                consumer.showStandardOptions("BLANK_LINES_AFTER_IMPORTS")
                consumer.showCustomOption(
                    ProtobufCodeStyleSettings::class.java,
                    ProtobufCodeStyleSettings::BLANK_LINES_AFTER_FILE_OPTIONS.name,
                    "After file options",
                    blankLines,
                )

                val blankLinesKeep = CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES_KEEP
                consumer.showCustomOption(
                    ProtobufCodeStyleSettings::class.java,
                    ProtobufCodeStyleSettings::KEEP_BLANK_LINES_BETWEEN_IMPORTS.name,
                    "Between imports",
                    blankLinesKeep,
                )
                consumer.showCustomOption(
                    ProtobufCodeStyleSettings::class.java,
                    ProtobufCodeStyleSettings::KEEP_BLANK_LINES_BETWEEN_FILE_OPTIONS.name,
                    "Between file options",
                    blankLinesKeep,
                )
            }

            else -> {}
        }
    }
}
