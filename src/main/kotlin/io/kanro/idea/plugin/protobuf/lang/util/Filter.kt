package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.elementType

private class AndFilter(private vararg val filters: PsiElementFilter) : PsiElementFilter {
    override fun isAccepted(element: PsiElement): Boolean {
        return filters.all { it.isAccepted(element) }
    }
}

private class OrFilter(private vararg val filters: PsiElementFilter) : PsiElementFilter {
    override fun isAccepted(element: PsiElement): Boolean {
        return filters.any { it.isAccepted(element) }
    }
}

private class NotFilter(private val filter: PsiElementFilter) : PsiElementFilter {
    override fun isAccepted(element: PsiElement): Boolean {
        return !filter.isAccepted(element)
    }
}

private class TokenSetFilter(private val tokenSet: TokenSet) : PsiElementFilter {
    override fun isAccepted(element: PsiElement): Boolean {
        return tokenSet.contains(element.elementType)
    }
}

object AnyElement : PsiElementFilter {
    override fun isAccepted(element: PsiElement): Boolean {
        return true
    }
}

infix fun PsiElementFilter.and(other: PsiElementFilter): PsiElementFilter {
    return AndFilter(this, other)
}

infix fun PsiElementFilter.or(other: PsiElementFilter): PsiElementFilter {
    return OrFilter(this, other)
}

fun PsiElementFilter.and(vararg others: PsiElementFilter): PsiElementFilter {
    return AndFilter(this, *others)
}

fun PsiElementFilter.or(vararg others: PsiElementFilter): PsiElementFilter {
    return OrFilter(this, *others)
}

operator fun PsiElementFilter.not(): PsiElementFilter {
    return NotFilter(this)
}

fun TokenSet.asFilter(): PsiElementFilter {
    return TokenSetFilter(this)
}
