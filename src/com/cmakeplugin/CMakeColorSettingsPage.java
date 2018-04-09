package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePDC;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

public class CMakeColorSettingsPage implements ColorSettingsPage {
//  private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
  private static final  AttributesDescriptor[] LEXER_DESCRIPTORS = new  AttributesDescriptor[]{
        new AttributesDescriptor("Line or Brackets comment", CMakeSyntaxHighlighter.COMMENT),
        new AttributesDescriptor("Braces \"(\"", CMakeSyntaxHighlighter.BRACES),
        new AttributesDescriptor("Commands and arguments separator", CMakeSyntaxHighlighter.SEPARATOR),
        new AttributesDescriptor("Bad char", CMakeSyntaxHighlighter.BADCHAR),
        new AttributesDescriptor("Keyword", CMakeSyntaxHighlighter.KEYWORD),
        new AttributesDescriptor("Bracket argument", CMakeSyntaxHighlighter.BRACKET_ARGUMENT),
        new AttributesDescriptor("Quoted argument", CMakeSyntaxHighlighter.STRING)
  };

  private static final  AttributesDescriptor[] ANNOTATOR_DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("CMake command", CMakeSyntaxHighlighter.CMAKE_COMMAND),
            new AttributesDescriptor("Unquoted legacy argument", CMakeSyntaxHighlighter.UNQUOTED_LEGACY),
            new AttributesDescriptor("CMake variable reference", CMakeSyntaxHighlighter.CMAKE_VAR_REF),
            new AttributesDescriptor("Local variable reference", CMakeSyntaxHighlighter.VAR_REF),
            new AttributesDescriptor("CMake variable definition", CMakeSyntaxHighlighter.CMAKE_VAR_DEF),
            new AttributesDescriptor("Local variable definition", CMakeSyntaxHighlighter.VAR_DEF),

            new AttributesDescriptor("CMake operator", CMakeSyntaxHighlighter.CMAKE_OPERATOR),
            new AttributesDescriptor("Path or URL reference", CMakeSyntaxHighlighter.CMAKE_PATH_URL),
            new AttributesDescriptor("CMake property", CMakeSyntaxHighlighter.CMAKE_PROPERTY)
  };

  @Nullable
  @Override
  public Icon getIcon() {
    return CMakeIcons.FILE;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return new CMakeSyntaxHighlighter();
  }

  @NotNull
  @Override
  public String getDemoText() {
    return "# Line Comment\n" +
            "#[[This is a bracket comment.\n" +
            "It runs until the close bracket.]]\n" +
            "if(<p>TRUE</p>)\n" +
            "\tunknown_command(\n" +
            "\t\tunquoted_argument=<vr>${outer_${inner_variable}_variable}</vr><u>/followed/by/path</u>\n" +
            "\t\t<cvd>${CMAKE_CXX_FLAGS}</cvd> BadChar=\\d\\g\\j)\n" +
            "endif()\n" +
            "<c>set</c>(<vd>variable_definition</vd> arg1;arg2;arg3\n" +
            "\t<p>PUBLIC</p> with_known_CMake_Property\n" +
            "\t#[=[with Bracket Comment]=] <o>AND</o> with_known_CMake_Operator\n" +
            "\t<cvr>${CMAKE_CXX_FLAGS}</cvr> with_known_CMake_Variable_reference\n" +
            "\t<l>UnquotedLegacy\"fff\"ghg -Da=\"b c\"\" \" -Da=$(v) a\" \"b\"c\"f$$$ </l>)\n" +
            "<c>message</c>( <-this_is_known_CMake_Command \"This is a quoted argument containing multiple lines.\n" +
            "This is always one argument even though it contains a ; character.\n" +
            "Both \\\\-escape sequences and <vr>${variable}</vr> references are evaluated.\n" +
            "The text does not end on an escaped double-quote like \\\".\n" +
            "It does end in an unescaped double quote.\")\n" +
            "<c>message</c>( [=[\n" +
            "This is the first line in a bracket argument with bracket length 1.\n" +
            "No \\-escape sequences or ${variable} references are evaluated.\n" +
            "This is always one argument even though it contains a ; character.\n" +
            "The text does not end on a closing bracket of length 0 like ]].\n" +
            "It does end in a closing bracket of length 1.\n" +
            "]=])";

  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return ContainerUtil.newHashMap(
            Arrays.asList("c", "o", "u", "p", "l", "cvr", "vr", "cvd", "vd"), Arrays.asList(
                    CMakeSyntaxHighlighter.CMAKE_COMMAND,
                    CMakeSyntaxHighlighter.CMAKE_OPERATOR,
                    CMakeSyntaxHighlighter.CMAKE_PATH_URL,
                    CMakeSyntaxHighlighter.CMAKE_PROPERTY,
                    CMakeSyntaxHighlighter.UNQUOTED_LEGACY,
                    CMakeSyntaxHighlighter.CMAKE_VAR_REF,
                    CMakeSyntaxHighlighter.VAR_REF,
                    CMakeSyntaxHighlighter.CMAKE_VAR_DEF,
                    CMakeSyntaxHighlighter.VAR_DEF
            ));
  }
//  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() { return null; }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return CMakePDC.isCLION
            ? ANNOTATOR_DESCRIPTORS
            : Stream.concat(Arrays.stream(LEXER_DESCRIPTORS), Arrays.stream(ANNOTATOR_DESCRIPTORS))
                    .toArray(AttributesDescriptor[]::new); // LEXER_DESCRIPTORS + ANNOTATOR_DESCRIPTORS
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return CMakePDC.isCLION ? "CMake additional syntax" : "CMake";
  }
}