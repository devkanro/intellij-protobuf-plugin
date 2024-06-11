package io.kanro.idea.plugin.protobuf.aip.reference.contributor

// class ProtobufTypeNameInStringProvider : ProtobufSymbolReferenceProvider {
//    override fun hovers(element: ProtobufSymbolReferenceHost): ProtobufSymbolReferenceHover? {
//        if (element !is ProtobufStringValue) return null
//        return CachedValuesManager.getCachedValue(element) {
//            val assign = element.parentOfType<ValueAssign>() ?: return@getCachedValue null
//            val targetField = assign.field()?.qualifiedName()
//            return@getCachedValue when {
//                targetField == AipOptions.lroMetadataName -> {
//                    CachedValueProvider.Result.create(
//                        StringProtobufSymbolReferenceHover(
//                            element,
//                            ProtobufSymbolFilters.messageTypeName,
//                        ),
//                        PsiModificationTracker.MODIFICATION_COUNT,
//                    )
//                }
//
//                targetField == AipOptions.lroResponseName -> {
//                    CachedValueProvider.Result.create(
//                        StringProtobufSymbolReferenceHover(
//                            element,
//                            ProtobufSymbolFilters.messageTypeName,
//                        ),
//                        PsiModificationTracker.MODIFICATION_COUNT,
//                    )
//                }
//
//                else -> null
//            }
//        }
//    }
// }
//
// class StringProtobufSymbolReferenceHover(
//    val element: ProtobufStringValue,
//    private val filter: PsiElementFilter,
// ) : ProtobufSymbolReferenceHover {
//    private var text = element.text
//    private val range: TextRange
//    private val parts: List<ProtobufSymbolReferenceHover.SymbolPart>
//    private val absolutely: Boolean
//
//    init {
//        var range = element.textRange
//        var offset = 0
//        if (text.startsWith('"')) {
//            text = text.substring(1)
//            offset += 1
//            range = TextRange.create(range.startOffset + 1, range.endOffset)
//        }
//        if (text.startsWith('.')) {
//            absolutely = true
//            text = text.substring(1)
//            offset += 1
//            range = TextRange.create(range.startOffset + 1, range.endOffset)
//        } else {
//            absolutely = false
//        }
//        if (text.endsWith('"')) {
//            text = text.substring(0, text.length - 1)
//            range = TextRange.create(range.startOffset, range.endOffset - 1)
//        }
//        this.range = range
//
//        parts =
//            text.splitToRange('.').map {
//                val realRange = it.shiftRight(offset)
//                ProtobufSymbolReferenceHover.SymbolPart(realRange.startOffset, realRange.substring(element.text))
//            }
//    }
//
//    override fun textRange(): TextRange {
//        return range
//    }
//
//    override fun symbolParts(): List<ProtobufSymbolReferenceHover.SymbolPart> {
//        return parts
//    }
//
//    override fun renamePart(
//        index: Int,
//        newName: String,
//    ) {
//        val value = element.value()
//        val part = symbolParts()[index]
//        val start = value.substring(0, part.startOffset)
//        val end = value.substring(part.startOffset + part.value.length, value.length)
//        element.replace(ProtobufPsiFactory.createStringValue(element.project, "$start$newName$end"))
//    }
//
//    override fun rename(newName: String) {
//        element.replace(ProtobufPsiFactory.createStringValue(element.project, newName))
//    }
//
//    override fun absolutely(): Boolean {
//        return absolutely
//    }
//
//    override fun variantFilter(): PsiElementFilter {
//        return filter
//    }
// }
