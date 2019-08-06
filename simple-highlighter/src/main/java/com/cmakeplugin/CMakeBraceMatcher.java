package com.cmakeplugin;

import static com.cmakeplugin.psi.CMakeTypes.ENDFOREACH;
import static com.cmakeplugin.psi.CMakeTypes.ENDFUNCTION;
import static com.cmakeplugin.psi.CMakeTypes.ENDIF;
import static com.cmakeplugin.psi.CMakeTypes.ENDMACRO;
import static com.cmakeplugin.psi.CMakeTypes.ENDWHILE;
import static com.cmakeplugin.psi.CMakeTypes.FOREACH;
import static com.cmakeplugin.psi.CMakeTypes.FUNCTION;
import static com.cmakeplugin.psi.CMakeTypes.IF;
import static com.cmakeplugin.psi.CMakeTypes.LPAR;
import static com.cmakeplugin.psi.CMakeTypes.MACRO;
import static com.cmakeplugin.psi.CMakeTypes.RPAR;
import static com.cmakeplugin.psi.CMakeTypes.WHILE;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMakeBraceMatcher implements PairedBraceMatcher {

  private static final BracePair[] PAIRS = new BracePair[]{
          new BracePair(LPAR, RPAR, false),
          new BracePair(IF, ENDIF, false),
          new BracePair(FOREACH, ENDFOREACH, false),
          new BracePair(WHILE, ENDWHILE, false),
          new BracePair(FUNCTION, ENDFUNCTION, false),
          new BracePair(MACRO, ENDMACRO, false),
//          new BracePair(, , false),
  };

  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
    return true;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
