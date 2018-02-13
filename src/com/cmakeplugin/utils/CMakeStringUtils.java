package com.cmakeplugin.utils;

import com.intellij.openapi.util.TextRange;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMakeStringUtils {
  private static final String CMAKE_ESCAPE_SEQUENCE="(\\\\[^A-Za-z0-9;]|\\\\t|\\\\r|\\\\n|\\\\)";
  private static final String CMAKE_VAR_NAME="([A-Za-z0-9/_.+-]|"+CMAKE_ESCAPE_SEQUENCE+")+";
  private static final String CMAKE_VAR_BEGIN="(\\$\\{)";
  private static final String CMAKE_VAR_END="}";
  private static final String CMAKE_ENV_VAR_BEGIN="(^ENV\\{)";
  private static final String CMAKE_ENV_VAR_REF_BEGIN="(\\$ENV\\{)";

  /**
   * Parse giving text to find outer variables boundaries
   * @param text
   * @return list of outer variables boundaries including ${ or $ENV{ or ENV{ and }
   */
// TODO Should be more elegant way to implement that.
  public static List<TextRange> getOuterVarRefs(String text) {
    List<TextRange> result = new ArrayList<>();
    int varLevel = 0, maxVarLevel = Integer.MIN_VALUE;
    int outerVarBegin = 0;
    Pattern pattern = Pattern.compile(
            CMAKE_VAR_BEGIN+"|"+CMAKE_ENV_VAR_BEGIN+"|"+CMAKE_ENV_VAR_REF_BEGIN+"|"+CMAKE_VAR_END);
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      if (       matcher.group().matches(CMAKE_VAR_BEGIN)
              || matcher.group().matches(CMAKE_ENV_VAR_BEGIN)
              || matcher.group().matches(CMAKE_ENV_VAR_REF_BEGIN) ) {
        if (varLevel < 1) {
          varLevel = 0;
          outerVarBegin=matcher.start();
        }
        maxVarLevel = ++varLevel;
      } else if ( matcher.group().matches(CMAKE_VAR_END) ) {
        if (varLevel == maxVarLevel) {
          maxVarLevel = Integer.MIN_VALUE;
        }
        varLevel--;
      }
      if ((varLevel == 0) && text.substring(outerVarBegin, matcher.end()).matches(
              // check if allowed cmake variable symbols only used
              // https://cmake.org/cmake/help/latest/manual/cmake-language.7.html#variable-references
              "("+CMAKE_VAR_BEGIN+"|"+CMAKE_ENV_VAR_BEGIN+"|"+CMAKE_ENV_VAR_REF_BEGIN+"|"
              +CMAKE_VAR_NAME+"|"+CMAKE_VAR_END+")+")
              ) {
        result.add(new TextRange(outerVarBegin, matcher.end()));
      }
    }
    return result;
  }

  /**
   * Parse giving text to find inner variables boundaries
   * @param text
   * @return list of inner variables boundaries NOT including ${ and }
   */
  public static List<TextRange> getInnerVars(String text) {
    List<TextRange> result = new ArrayList<>();
    Pattern pattern = Pattern.compile("(?<="+ CMAKE_VAR_BEGIN +")"+ CMAKE_VAR_NAME +"(?="+CMAKE_VAR_END+")");
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      result.add(new TextRange(matcher.start(), matcher.end()));
      }
    return result;
  }

  /**
   * Parse giving text to find inner ENV variables boundaries
   * @param text
   * @return list of inner variables boundaries NOT including $ENV{ or ENV{} and }
   */
  public static List<TextRange> getInnerEnvVars(String text) {
    List<TextRange> result = new ArrayList<>();
    Pattern pattern = Pattern.compile(
            "(?<="+ CMAKE_ENV_VAR_REF_BEGIN+"|"+CMAKE_ENV_VAR_BEGIN+")"+ CMAKE_VAR_NAME +"(?="+CMAKE_VAR_END+")");
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      result.add(new TextRange(matcher.start(), matcher.end()));
    }
    return result;
  }

}
