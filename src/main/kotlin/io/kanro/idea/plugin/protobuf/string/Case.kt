package io.kanro.idea.plugin.protobuf.string

import io.kanro.idea.plugin.protobuf.string.case.CamelCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.CaseFormat
import io.kanro.idea.plugin.protobuf.string.case.CaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.CommonWordSplitter
import io.kanro.idea.plugin.protobuf.string.case.DotCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.KebabCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.PascalCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.ScreamingSnakeCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.SnakeCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.SpaceCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.TitleCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.TrainCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.UpperDotCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.UpperSpaceCaseFormatter
import io.kanro.idea.plugin.protobuf.string.case.WordSplitter

fun String.toCase(format: CaseFormat) = format.format(this)

fun String.toCase(
    formatter: CaseFormatter,
    splitter: WordSplitter = CommonWordSplitter,
) = formatter.format(splitter.split(this))

/** Converts a string to 'SCREAMING_SNAKE_CASE'. */
fun String.toScreamingSnakeCase() = toCase(ScreamingSnakeCaseFormatter)

/** Converts a string to 'snake_case.' */
fun String.toSnakeCase() = toCase(SnakeCaseFormatter)

/** Converts a string to 'PascalCase'. */
fun String.toPascalCase() = toCase(PascalCaseFormatter)

/** Converts a string to 'camelCase'. */
fun String.toCamelCase() = toCase(CamelCaseFormatter)

/** Converts a string to 'TRAIN-CASE'. */
fun String.toTrainCase() = toCase(TrainCaseFormatter)

/** Converts a string to 'kebab-case'. */
fun String.toKebabCase() = toCase(KebabCaseFormatter)

/** Converts a string to 'UPPER SPACE CASE'. */
fun String.toUpperSpaceCase() = toCase(UpperSpaceCaseFormatter)

/** Converts a string to 'Title Case'. */
fun String.toTitleCase() = toCase(TitleCaseFormatter)

/** Converts a string to 'lower space case'. */
fun String.toLowerSpaceCase() = toCase(SpaceCaseFormatter)

/** Converts a string to 'UPPER.DOT.CASE'. */
fun String.toUpperDotCase() = toCase(UpperDotCaseFormatter)

/** Converts a string to 'dot.case'. */
fun String.toDotCase() = toCase(DotCaseFormatter)
