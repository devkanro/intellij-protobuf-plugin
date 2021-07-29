package io.kanro.idea.plugin.protobuf.jvm

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.sisyphus.isSisyphus


/**
 * [com.google.protobuf.MessageOrBuilder] must be found in project.
 */
fun isJava(element: PsiElement): Boolean {
    return !isSisyphus(element) && JavaPsiFacade.getInstance(element.project)
        .findClass("com.google.protobuf.MessageOrBuilder", GlobalSearchScope.allScope(element.project)) != null
}