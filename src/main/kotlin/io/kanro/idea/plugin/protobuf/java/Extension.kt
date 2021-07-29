package io.kanro.idea.plugin.protobuf.java

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.sisyphus.isSisyphus

fun PsiElement.findJavaClass(name: QualifiedName?): PsiClass? {
    val className = name?.toString() ?: return null
    val scope = FileResolver.searchScope(this)
    return JavaPsiFacade.getInstance(project).findClass(className, scope)
}

fun ProtobufMessageDefinition.toClass(): PsiClass? {
    return findJavaClass(fullClassName())
}

fun ProtobufMessageDefinition.toMessageOrBuilderClass(): PsiClass? {
    return findJavaClass(fullMessageOrBuilderName())
}

fun ProtobufMessageDefinition.toBuilderClass(): PsiClass? {
    return findJavaClass(fullBuilderName())
}

fun ProtobufFieldLike.toGetters(): Array<PsiMethod> {
    return when (val owner = owner()) {
        is ProtobufMessageDefinition -> owner.toBuilderClass()?.findMethodsByName(getterName(), true) ?: arrayOf()
        else -> arrayOf()
    }
}

fun ProtobufFieldLike.toSetters(): Array<PsiMethod> {
    return when (val owner = owner()) {
        is ProtobufMessageDefinition -> owner.toBuilderClass()?.findMethodsByName(setterName(), true) ?: arrayOf()
        else -> arrayOf()
    }
}

fun ProtobufEnumDefinition.toClass(): PsiClass? {
    return findJavaClass(fullClassName())
}

fun ProtobufEnumValueDefinition.toEnumConstant(): PsiEnumConstant? {
    val enum = owner()?.toClass() ?: return null
    return enum.findFieldByName(valueName(), false) as? PsiEnumConstant
}

fun ProtobufServiceDefinition.toImplBaseClass(): PsiClass? {
    return findJavaClass(fullImplBaseName())
}

fun ProtobufServiceDefinition.toStubClass(): PsiClass? {
    return findJavaClass(fullStubName())
}

fun ProtobufServiceDefinition.toBlockingStubClass(): PsiClass? {
    return findJavaClass(fullBlockingStubName())
}

fun ProtobufServiceDefinition.toFutureStubClass(): PsiClass? {
    return findJavaClass(fullFutureStubName())
}

fun ProtobufRpcDefinition.toImplBaseMethod(): PsiMethod? {
    return owner()?.toImplBaseClass()?.findMethodsByName(methodName(), false)?.firstOrNull()
}

fun ProtobufRpcDefinition.toStubMethod(): PsiMethod? {
    return owner()?.toStubClass()?.findMethodsByName(methodName(), false)?.firstOrNull()
}

fun ProtobufRpcDefinition.toBlockingStubMethod(): PsiMethod? {
    return owner()?.toBlockingStubClass()?.findMethodsByName(methodName(), false)?.firstOrNull()
}

fun ProtobufRpcDefinition.toFutureStubMethod(): PsiMethod? {
    return owner()?.toFutureStubClass()?.findMethodsByName(methodName(), false)?.firstOrNull()
}

/**
 * [com.google.protobuf.MessageOrBuilder] must be found in project.
 */
fun isJava(element: PsiElement): Boolean {
    return !isSisyphus(element) && JavaPsiFacade.getInstance(element.project)
        .findClass("com.google.protobuf.MessageOrBuilder", GlobalSearchScope.allScope(element.project)) != null
}
