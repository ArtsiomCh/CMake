package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMakeFoldingBuilderForSubsequentSingleLineComments implements FoldingBuilder {

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
    if (!CMakeComponent.isCMakePlusActive) return FoldingDescriptor.EMPTY;
    List<FoldingDescriptor> result = new ArrayList<>();
    final Collection<PsiComment> comments =
        PsiTreeUtil.findChildrenOfType(node.getPsi(), PsiComment.class);
    Set<PsiComment> processedComments = new HashSet<>();

    for (PsiComment comment : comments) {
      if (processedComments.add(comment) && CMakePlusPDC.isLineComment(comment)) {
        final FoldingDescriptor commentDescriptor =
            getCommentDescriptor(comment, processedComments);
        if (commentDescriptor != null) result.add(commentDescriptor);
      }
    }
    return result.toArray(new FoldingDescriptor[0]);
  }

  @Nullable
  private FoldingDescriptor getCommentDescriptor(
      @NotNull PsiComment comment, @NotNull Set<PsiComment> processedComments) {
    final TextRange commentRange = getSubsequentSingleLineCommentsRange(comment, processedComments);
    return (commentRange == null)
        ? null
        : new NamedFoldingDescriptor(
            comment.getNode(), commentRange, null, getPlaceholderText(comment));
  }

  /** see {@link com.intellij.codeInsight.folding.impl.CommentFoldingUtil} */
  @Nullable
  private TextRange getSubsequentSingleLineCommentsRange(
      PsiComment comment, Set<PsiComment> processedComments) {
    PsiElement end = null;
    for (PsiElement current = comment.getNextSibling();
        current != null;
        current = current.getNextSibling()) {
      if (current instanceof PsiComment
          && CMakePlusPDC.isLineComment((PsiComment) current)
          && !processedComments.contains(current)) {
        end = current;
        processedComments.add((PsiComment) current);
        continue;
      }
      if (CMakePlusPDC.isSubsequentLineCommentsGlueElement(current)) continue;
      break;
    }
    return (end == null)
        ? null
        : new TextRange(comment.getTextRange().getStartOffset(), end.getTextRange().getEndOffset());
  }

  @NotNull
  private String getPlaceholderText(PsiElement element) {
    String text = element.getText();
    if (text.length() > 50) text = text.substring(0, 50);
    return text + " ... ";
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
