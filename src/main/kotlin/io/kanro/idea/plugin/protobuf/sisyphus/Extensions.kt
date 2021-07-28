package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiMethod
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.element.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.element.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.element.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.element.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.element.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike

fun ProtobufMessageDefinition.toClass(): PsiClass? {
    val scope = FileResolver.searchScope(this)
    val className = SisyphusNamespace.scope(this)?.toString() ?: return null
    return JavaPsiFacade.getInstance(project).findClass(className, scope)
}

fun ProtobufMessageDefinition.toMutableClass(): PsiClass? {
    val scope = FileResolver.searchScope(this)
    val className = SisyphusMutableMessageNamespace.scope(this)?.toString() ?: return null
    return JavaPsiFacade.getInstance(project).findClass(className, scope)
}

fun ProtobufEnumDefinition.toClass(): PsiClass? {
    val scope = FileResolver.searchScope(this)
    val className = SisyphusNamespace.scope(this)?.toString() ?: return null
    return JavaPsiFacade.getInstance(project).findClass(className, scope)
}

fun ProtobufServiceDefinition.toClass(): PsiClass? {
    val scope = FileResolver.searchScope(this)
    val className = SisyphusNamespace.scope(this)?.toString() ?: return null
    return JavaPsiFacade.getInstance(project).findClass(className, scope)
}

fun ProtobufServiceDefinition.toClientClass(): PsiClass? {
    val scope = FileResolver.searchScope(this)
    val className = SisyphusClientNamespace.scope(this)?.toString() ?: return null
    return JavaPsiFacade.getInstance(project).findClass(className, scope)
}

fun ProtobufEnumValueDefinition.toEnumConstant(): PsiEnumConstant? {
    val enum = owner()?.toClass() ?: return null
    return enum.findFieldByName(SisyphusNamespace.name(this), false) as? PsiEnumConstant
}

fun ProtobufRpcDefinition.toMethod(): PsiMethod? {
    val service = owner()?.toClass() ?: return null
    return service.findMethodsByName(SisyphusNamespace.name(this), false).firstOrNull()
}

fun ProtobufRpcDefinition.toClientMethod(): PsiMethod? {
    val service = owner()?.toClientClass() ?: return null
    return service.findMethodsByName(SisyphusClientNamespace.name(this), false).firstOrNull()
}

fun ProtobufFieldLike.toGetter(): PsiMethod? {
    return when (val owner = owner()) {
        is ProtobufMessageDefinition -> {
            val clazz = owner.toClass() ?: return null
            clazz.findMethodsByName(SisyphusFieldGetterNamespace.name(this), false).firstOrNull()
        }
        else -> null
    }
}

fun ProtobufFieldLike.toSetter(): PsiMethod? {
    return when (val owner = owner()) {
        is ProtobufMessageDefinition -> {
            val clazz = owner.toMutableClass() ?: return null
            clazz.findMethodsByName(SisyphusFieldSetterNamespace.name(this), false).firstOrNull()
                ?: clazz.findMethodsByName(SisyphusFieldGetterNamespace.name(this), false).firstOrNull()
        }
        else -> null
    }
}
