## **CMake plugin for IntelliJ Idea CE and Android Studio**

This is a CMake support plugin for IntelliJ Idea IDE. It brings syntax highlight support for CMake build and run system. The main goal was to make `CMakeLists.txt` files more readable in Android Studio.
It is my play project to study intellij language support.  Please see [JetBrains manual](http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support.html) for more details. Work is still in progress.

Plugin is inspired and based on [CMaker](https://github.com/dubrousky/CMaker) by [Aliaksandr Dubrouski](https://github.com/dubrousky)

CMake Syntax from [cmake.org](https://cmake.org/cmake/help/latest/manual/cmake-language.7.html) is mostly implemented.

Commands, Variables, Property and Operator was taken from [vim CMake support pluging](https://raw.githubusercontent.com/nickhutchinson/vim-cmake-syntax/master/syntax/cmake.vim)

There are few known syntax highlights bugs, mostly around `$` symbol, because it's used to recognise Variable references in arguments. But those bugs could be reproduced mostly on synthetic tests while real `CMakeLists.txt` files shown correctly.  

## **Binary**
If you wish to use compiled version of pluging, please take `CMake.jar` from the root dir of that project and place file to the `<YOUR_IntelliJ_IDE>\config\plugins`  folder [(where to find it)](http://www.jetbrains.org/intellij/sdk/docs/basics/settings_caches_logs.html), and then restart your IDE so the changes may take effect. More details at [JetBrains manual](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/deploying_plugin.html).

Plugin was already submitted into [JetBrains Plugins Repository](https://plugins.jetbrains.com/) and hopefully will be available in search results withing few days. In the meantime [direct link](https://plugins.jetbrains.com/plugin/10089-cmake-simple-highlighter) could be used.

## **License**

Plugin is open-source software and is licenced under GPL v3 licence.

## **Versions**

**v.0.0.1**
* Supports basic syntax highlight.

## **Build Instruction**

* To get started you need the IntelliJ Plugin development SDK, *Grammar-Kit* plugin (to build parser and lexer from bnf grammar)

* Download the code, set Plugin SDK and Java SDK

* In project properties set the `plugin.xml` location (`resources/META-INF/plugin.xml`)
* Hover the `cmake_v3.bnf` and generate parser code
* In project properties mark gen as source folder
* Generate lexer class from `CMake_org_v3.flex` file
* Build project and you are set to go
