package io.kanro.idea.plugin.protobuf.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufKeywordToken;
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens;

public class ProtobufParserUtil extends GeneratedParserUtilBase {
    static boolean parseKeyword(PsiBuilder builder, int level) {
        if (builder.eof()) {
            return false;
        }
        if (builder.getTokenType() instanceof ProtobufKeywordToken) {
            builder.remapCurrentToken(ProtobufTokens.IDENTIFIER_LITERAL);
            builder.advanceLexer();
            return true;
        }
        return false;
    }

    public static boolean parseTextMessage(PsiBuilder builder, int level) {
        return ProtoTextParser.Message(builder, level);
    }

    public static boolean parseTextFieldName(PsiBuilder builder, int level) {
        return ProtoTextParser.FieldName(builder, level);
    }
}
