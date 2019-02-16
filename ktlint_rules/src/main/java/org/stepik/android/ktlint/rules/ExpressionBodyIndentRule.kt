package org.stepik.android.ktlint.rules

import com.github.shyiko.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtNamedFunction

class ExpressionBodyIndentRule : Rule("expression-body-indent") {
    companion object {
        private const val MAX_EXPRESSION_BODY_LEN = 10
    }

    override fun visit(node: ASTNode, autoCorrect: Boolean, emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit) {
        val element = node.psi ?: return
        when (element) {
            is KtNamedFunction -> {
                val eq = element.equalsToken ?: return
                val nextEq = eq.nextSibling

                if (nextEq is PsiWhiteSpace) {
                    if (!nextEq.text.startsWith('\n')) {
                        if (nextEq.nextSibling.textLength > MAX_EXPRESSION_BODY_LEN) {
                            emit(node.startOffset, "Expression body should be written on the next line after method declaration", false)
                        }
                    } else {
                        val funPrev = element.prevSibling
                        if (funPrev is PsiWhiteSpace) {
                            val funIndent = funPrev.text.split('\n').last().length
                            val eqIndent = nextEq.text.split('\n').last().length

                            if (funIndent + 4 != eqIndent) {
                                emit(node.startOffset, "Unexpected indentation of expression body (expected ${funIndent + 4}, actual $eqIndent) ", false)
                            }
                        }
                    }
                }
            }
        }
    }
}