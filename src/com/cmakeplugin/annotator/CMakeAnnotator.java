package com.cmakeplugin.annotator;

//import com.cmakeplugin.CMakeSyntaxHighlighter;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import com.jetbrains.cidr.cpp.cmake.psi.*;
//import static CMakeKeywords.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMakeAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof CMakeCommandName) {
      annotateCommand(element, holder);
    } else if (element instanceof CMakeArgument) {
      if (((CMakeArgument) element).getBracketArgStart()==null) {
        PsiElement cmakeLiteral = ((CMakeArgument) element).getCMakeLiteral();
        if (element.getFirstChild().getNode().getElementType() == CMakeTokenTypes.QUOTE) {
          // Annotate Quoted argument
          annotateVariables(cmakeLiteral, holder);
        } else if (!(annotateLegacy(cmakeLiteral, holder))){
          // Annotate Unquoted argument
          annotatePathURL(cmakeLiteral, holder);
          annotateVariables(cmakeLiteral, holder);
          annotateProperty(cmakeLiteral, holder);
          annotateOperator(cmakeLiteral, holder);
//          holder.createInfoAnnotation(cmakeLiteral, null)
//                  .setTextAttributes(DefaultLanguageHighlighterColors.IDENTIFIER);
        }
      }
    }
  }

  private boolean annotateLegacy(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().matches("(.*[^\\\\]\"(.*[^\\\\])?\".*)|(.*\\$\\(.*\\).*)")) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
      return true;
    } else return false;
  }

  private void annotatePathURL(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().contains("/")) {
//      TextRange range = new TextRange(element.getTextRange().getStartOffset(),
//              element.getTextRange().getStartOffset() + element.getTextRange().getLength());
//      holder.createInfoAnnotation(range, null)
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
//              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PATH_URL);
    }
  }

  private void annotateVariables(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String argtext = element.getText();
    int pos = element.getTextRange().getStartOffset();
    parseVarsIntoLists(argtext);
    for ( TextRange outerVarRange: outerVarsList) {
      holder.createInfoAnnotation( outerVarRange.shiftRight(pos), null)
              .setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
//              .setTextAttributes(CMakeSyntaxHighlighter.VARIABLE);
    }
    for ( TextRange innerVarRange: innerVarsList ) {
      for (String varRegexp: CMakeKeywords.variables_All ) {
        if (argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset())
                .matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
//                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VARIABLE);
        }
      }
    }
    for ( TextRange innerVarRange: innerENVvarsList ) {
      for (String varRegexp: CMakeKeywords.variables_ENV ) {
        if (argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset())
                .matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
//                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VARIABLE);
        }
      }
    }
  }

  private ArrayList<TextRange> outerVarsList;
  private ArrayList<TextRange> innerVarsList;
  private ArrayList<TextRange> innerENVvarsList;

/** Fill outerVarsList, innerVarsList and innerENVvarsList with indexes
 *  of outer and inner Variables inside given text
   * @param   text   the string to search in.
*/
// TODO Should be more elegant way to implement that.
  private void parseVarsIntoLists(String text) {
    outerVarsList = new ArrayList<>();
    innerVarsList = new ArrayList<>();
    innerENVvarsList = new ArrayList<>();
    int varLevel = 0, maxVarLevel = Integer.MIN_VALUE;
    int outerVarBegin = 0, innerVarBegin = 0;
    Pattern pattern = Pattern.compile("(\\$(ENV)?|^ENV)\\{|}");
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      if ( matcher.group().equals("${") || matcher.group().equals("$ENV{")
              || matcher.group().equals("ENV{") ) {
        if (varLevel < 1) {
          varLevel = 0;
          outerVarBegin=matcher.start();
        }
        maxVarLevel = ++varLevel;
        innerVarBegin = matcher.end();
      } else if ( matcher.group().equals("}") ) {
        if (varLevel == maxVarLevel) {
          if (text.substring(innerVarBegin-2,innerVarBegin).equals("${")) {
            innerVarsList.add(new TextRange(innerVarBegin, matcher.start()));
          } else {
            innerENVvarsList.add(new TextRange(innerVarBegin, matcher.start()));
          }
          maxVarLevel = Integer.MIN_VALUE;
        }
        varLevel--;
      }
      if (varLevel == 0) {
        outerVarsList.add(new TextRange(outerVarBegin, matcher.end()));
      }
    }
  }

  private void annotateCommand(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String commandName = element.getText().toLowerCase();
    if (CMakeKeywords.commands_Project.contains(commandName)
            || CMakeKeywords.commands_Scripting.contains(commandName)
            || CMakeKeywords.commands_Test.contains(commandName)
            ) {
      holder.createInfoAnnotation(element, null)
            .setTextAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
//              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_COMMAND);
    } else if (CMakeKeywords.commands_Deprecated.contains(commandName)){
      holder.createWarningAnnotation(element,"Deprecated command")
            .setHighlightType(ProblemHighlightType.LIKE_DEPRECATED);
    }
  }

  private void annotateProperty(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String propertyName = element.getText().toUpperCase();
    for (String varRegexp: CMakeKeywords.properties_All){
      if (propertyName.matches(varRegexp)){
        holder.createInfoAnnotation(element, null)
                .setTextAttributes(DefaultLanguageHighlighterColors.NUMBER);
//                .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PROPERTY);
      }
    }
    for (String varRegexp: CMakeKeywords.properties_Deprecated){
      if (propertyName.matches(varRegexp)) {
        holder.createWarningAnnotation(element, "Deprecated property")
                .setHighlightType(ProblemHighlightType.LIKE_DEPRECATED);
      }
    }
  }

  private void annotateOperator(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String operatorName = element.getText().toUpperCase();
    if (CMakeKeywords.operators.contains(operatorName) ) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(DefaultLanguageHighlighterColors.METADATA);
//              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_OPERATOR);
    }
  }

}
