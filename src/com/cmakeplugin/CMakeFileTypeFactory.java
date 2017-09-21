package com.cmakeplugin;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class CMakeFileTypeFactory extends FileTypeFactory {
  @Override
  public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
    fileTypeConsumer.consume(
            CMakeFileType.INSTANCE,
            new FileNameMatcherEx() {
              @Override
              @NotNull
              public String getPresentableString() {
                return "CMakeLists";
              }

              @Override
              public boolean acceptsCharSequence(CharSequence fileName) {
                return fileName.toString().matches("CMakeLists.*");
//                return fileName.toString().matches("(CMakeLists[.]txt)|(.*[.]cmake)");
              }

            }
    );
  }
}