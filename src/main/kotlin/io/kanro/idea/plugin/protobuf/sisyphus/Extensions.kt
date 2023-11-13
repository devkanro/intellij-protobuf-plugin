package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.java.findJavaClass
import io.kanro.idea.plugin.protobuf.java.fullClassName
import io.kanro.idea.plugin.protobuf.java.getterName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike

fun ProtobufMessageDefinition.toClass(): PsiClass? {
    return findJavaClass(fullClassName())
}

fun ProtobufMessageDefinition.toMutableClass(): PsiClass? {
    return findJavaClass(fullMutableClassName())
}

fun ProtobufEnumDefinition.toClass(): PsiClass? {
    return findJavaClass(fullClassName())
}

fun ProtobufEnumValueDefinition.toEnumConstant(): PsiEnumConstant? {
    val enum = owner()?.toClass() ?: return null
    return enum.findFieldByName(valueName(), false) as? PsiEnumConstant
}

fun ProtobufServiceDefinition.toClass(): PsiClass? {
    return findJavaClass(fullClassName())
}

fun ProtobufServiceDefinition.toClientClass(): PsiClass? {
    return findJavaClass(fullClientName())
}

fun ProtobufRpcDefinition.toMethod(): PsiMethod? {
    val service = owner()?.toClass() ?: return null
    return service.findMethodsByName(methodName(), false).firstOrNull()
}

fun ProtobufRpcDefinition.toClientMethod(): PsiMethod? {
    val service = owner()?.toClientClass() ?: return null
    return service.findMethodsByName(methodName(), false).firstOrNull()
}

fun ProtobufFieldLike.toGetters(): Array<PsiMethod> {
    return when (val owner = owner()) {
        is ProtobufMessageDefinition ->
            owner.toMutableClass()?.findMethodsByName(getterName(), true)
                ?: PsiMethod.EMPTY_ARRAY
        else -> PsiMethod.EMPTY_ARRAY
    }
}

fun ProtobufFieldLike.toSetters(): Array<PsiMethod> {
    return when (val owner = owner()) {
        is ProtobufMessageDefinition ->
            owner.toMutableClass()?.findMethodsByName(getterName(), true)
                ?: PsiMethod.EMPTY_ARRAY
        else -> PsiMethod.EMPTY_ARRAY
    }
}

/**
 * [com.bybutter.sisyphus.protobuf.Message] must be found in project.
 */
fun isSisyphus(element: PsiElement): Boolean {
    return JavaPsiFacade.getInstance(element.project)
        .findClass("com.bybutter.sisyphus.protobuf.Message", GlobalSearchScope.allScope(element.project)) != null
}
