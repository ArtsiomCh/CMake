package com.cmakeplugin;

import com.cmakeplugin.utils.CmakePlusPDC;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMakeFoldingBuilderForMultiLineBodies implements FoldingBuilder {

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
    return PsiTreeUtil.findChildrenOfAnyType(node.getPsi(), CmakePlusPDC.foldableBodies).stream()
        .filter(it -> isMultiLine(document, it))
        .map(this::createFoldingDescriptor)
        .toArray(FoldingDescriptor[]::new);
  }

  private boolean isMultiLine(@NotNull Document document, @NotNull PsiElement element) {
    final TextRange range = element.getTextRange();
    if (range.isEmpty()) return false;
    final int firstLineNumber = document.getLineNumber(range.getStartOffset());
    // exclude EOL with '\n' that belongs to BodyBlock
    final int lastLineNumber = document.getLineNumber(range.getEndOffset() - 1);
    return firstLineNumber != lastLineNumber;
  }

  @NotNull
  private FoldingDescriptor createFoldingDescriptor(PsiElement element) {
    return new NamedFoldingDescriptor(
        element.getNode(),
        CmakePlusPDC.getBodyBlockRangeToFold(element),
        null,
        getPlaceholderText(element));
  }

  @NotNull
  private String getPlaceholderText(PsiElement element) {
    String text = element.getText();
    if (text.length() < 50) return text;
    String prefix = text.substring(0, 35);
    String suffix = text.substring(text.length() - 10);
    return prefix + " ... " + suffix;
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return false;
  }
}
