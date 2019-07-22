package com.cmakeplugin.utils;

import com.intellij.openapi.util.TextRange;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMakeVarStringUtil {
  private static final String ESCAPE_SEQUENCE = "(\\\\[^A-Za-z0-9;]|\\\\t|\\\\r|\\\\n|\\\\;)";
  private static final String VAR_NAME = "([A-Za-z0-9/_.+-]|" + ESCAPE_SEQUENCE + ")+";
  private static final String VAR_BEGIN = "((^\\$|(?<=[^\\\\])\\$)\\{)"; // Escaped \$ excluded
  private static final String VAR_END = "}";
  private static final String ENV_VAR_BEGIN = "(^ENV\\{)";
  private static final String ENV_VAR_REF_BEGIN =
      "((^\\$|(?<=[^\\\\])\\$)ENV\\{)"; // Escaped \$ excluded

  private static Map<String, Boolean> cacheCouldBeVarName = new ConcurrentHashMap<>();
  private static Map<String, List<TextRange>> cacheOuterVarRefs = new ConcurrentHashMap<>();
  private static Map<String, List<TextRange>> cacheInnerVars = new ConcurrentHashMap<>();

  private static final Pattern patternCouldBeVarName = Pattern.compile(VAR_NAME);

  @Contract(pure = true)
  static boolean couldBeVarName(@NotNull String text) {
    return cacheCouldBeVarName.computeIfAbsent(
        text, key -> patternCouldBeVarName.matcher(key).matches());
  }

  private static final List<TextRange> EMPTY_RANGES_LIST = Collections.emptyList();

  private static final Pattern patternOuterVarRefs =
      Pattern.compile(VAR_BEGIN + "|" + ENV_VAR_BEGIN + "|" + ENV_VAR_REF_BEGIN + "|" + VAR_END);
  /**
   * Parse giving text to find outer variables boundaries
   *
   * @param text
   * @return list of outer variables boundaries including ${ or $ENV{ or ENV{ and }
   */
  @NotNull
  static List<TextRange> getOuterVarRefs(String text) {
    return cacheOuterVarRefs.computeIfAbsent(text, CMakeVarStringUtil::doGetOuterVarRefs);
  }

  @NotNull
  private static List<TextRange> doGetOuterVarRefs(String text) {
    Matcher matcher = patternOuterVarRefs.matcher(text);
    if (!matcher.find()) return EMPTY_RANGES_LIST;

    List<TextRange> result = new ArrayList<>();
    int varLevel = 0;
    int maxVarLevel = Integer.MIN_VALUE;
    int outerVarBegin = 0;
    do {
      if (matcher.group().matches(VAR_BEGIN)
          || matcher.group().matches(ENV_VAR_BEGIN)
          || matcher.group().matches(ENV_VAR_REF_BEGIN)) {
        if (varLevel < 1) {
          varLevel = 0;
          outerVarBegin = matcher.start();
        }
        maxVarLevel = ++varLevel;
      } else if (matcher.group().matches(VAR_END)) {
        if (varLevel == maxVarLevel) {
          maxVarLevel = Integer.MIN_VALUE;
        }
        varLevel--;
      }
      if ((varLevel == 0)
          && text.substring(outerVarBegin, matcher.end())
              .matches(
                  // check if allowed cmake variable symbols only used
                  // https://cmake.org/cmake/help/latest/manual/cmake-language.7.html#variable-references
                  "("
                      + VAR_BEGIN
                      + "|"
                      + ENV_VAR_BEGIN
                      + "|"
                      + ENV_VAR_REF_BEGIN
                      + "|"
                      + VAR_NAME
                      + "|"
                      + VAR_END
                      + ")+")) {
        result.add(new TextRange(outerVarBegin, matcher.end()));
      }
    } while (matcher.find());
    return result;
  }

  private static final Pattern patternInnerVars =
      Pattern.compile("(?<=" + VAR_BEGIN + ")" + VAR_NAME + "(?=" + VAR_END + ")");
  /**
   * Parse giving text to find inner variables boundaries
   *
   * @param text
   * @return list of inner variables boundaries NOT including ${ and }
   */
  @NotNull
  static List<TextRange> getInnerVars(String text) {
    return cacheInnerVars.computeIfAbsent(
        text, key -> getMatchedRanges(patternInnerVars.matcher(key)));
  }

  private static final Pattern patternInnerEnvVars =
      Pattern.compile(
          "(?<="
              + ENV_VAR_REF_BEGIN
              + "|"
              + ENV_VAR_BEGIN
              + ")"
              + VAR_NAME
              + "(?="
              + VAR_END
              + ")");
  /**
   * Parse giving text to find inner ENV variables boundaries
   *
   * @param text
   * @return list of inner variables boundaries NOT including $ENV{ or ENV{ and }
   */
  @NotNull
  public static List<TextRange> getInnerEnvVars(String text) {
    return getMatchedRanges(patternInnerEnvVars.matcher(text));
  }

  private static List<TextRange> getMatchedRanges(@NotNull Matcher matcher) {
    if (!matcher.find()) return EMPTY_RANGES_LIST;
    List<TextRange> result = new ArrayList<>();
    do {
      result.add(new TextRange(matcher.start(), matcher.end()));
    } while (matcher.find());
    return result;
  }
}
