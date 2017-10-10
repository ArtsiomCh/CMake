package com.cmakeplugin;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.Map;

public class CMakeColorSettingsPage implements ColorSettingsPage {
  private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
          new AttributesDescriptor("Line or Brackets comment", CMakeSyntaxHighlighter.COMMENT),
          new AttributesDescriptor("String literal", CMakeSyntaxHighlighter.STRING),
          new AttributesDescriptor("Braces \"(\"", CMakeSyntaxHighlighter.BRACES),
          new AttributesDescriptor("Commands and arguments separator", CMakeSyntaxHighlighter.SEPARATOR),
          new AttributesDescriptor("Bad char", CMakeSyntaxHighlighter.BADCHAR),
          new AttributesDescriptor("Keyword", CMakeSyntaxHighlighter.KEYWORD),
          new AttributesDescriptor("CMake command", CMakeSyntaxHighlighter.CMAKE_COMMAND),
          new AttributesDescriptor("Unquoted legacy argument", CMakeSyntaxHighlighter.UNQUOTED_LEGACY),
          new AttributesDescriptor("CMake variable", CMakeSyntaxHighlighter.CMAKE_VARIABLE),
          new AttributesDescriptor("Local variable", CMakeSyntaxHighlighter.VARIABLE),
          new AttributesDescriptor("Variable boundaries", CMakeSyntaxHighlighter.VAR_REF),
          new AttributesDescriptor("Bracket argument", CMakeSyntaxHighlighter.BRACKET_ARGUMENT),
          new AttributesDescriptor("CMake operator", CMakeSyntaxHighlighter.CMAKE_OPERATOR),
          new AttributesDescriptor("Path or URL reference", CMakeSyntaxHighlighter.CMAKE_PATH_URL),
          new AttributesDescriptor("CMake property", CMakeSyntaxHighlighter.CMAKE_PROPERTY),
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
            "unknown_command(\n" +
            "\tunquoted_argument=${outer_${inner_variable}_variable}/followed/by/path\n" +
            "\tENV{environmental_variable_reference} BadChar=\\d\\g\\j)\n" +
            "set( <-this_is_known_CMake_Command arg1;arg2;arg3\n" +
            "\tPUBLIC with_known_CMake_Property\n" +
            "\t#[=[with Bracket Comment]=] AND with_known_CMake_Operator\n" +
            "\t${CMAKE_CXX_FLAGS} with_known_CMake_Variable\n" +
            "\tUnquotedLegacy\"fff\"ghg -Da=\"b c\"\" \" -Da=$(v) a\" \"b\"c\"f$$$ )\n" +
            "message( \"This is a quoted argument containing multiple lines.\n" +
            "This is always one argument even though it contains a ; character.\n" +
            "Both \\\\-escape sequences and ${variable} references are evaluated.\n" +
            "The text does not end on an escaped double-quote like \\\".\n" +
            "It does end in an unescaped double quote.\")\n" +
            "message( [=[\n" +
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
    return null;
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "CMake";
  }
}