## **CMake plugin for IntelliJ Idea CE and Android Studio**

This is a CMake support plugin for IntelliJ Idea IDE. It brings syntax highlight support for CMake build and run system.
It is my play project to study intellij language support. Please see [JetBrains manuals](http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support.html) for more details. Work is still in progress.

Plugin is inspired and based on [CMaker](https://github.com/dubrousky/CMaker) by [Aliaksandr Dubrouski](https://github.com/dubrousky)


## **License**

Plugin is open-source software and is licenced under GPL v3 licence.

## **Versions**

**v.0.0.1**
* Supports basic syntax highlight.

## **Build Instruction**

* To get started you need the IntelliJ Plugin development SDK, *Grammar-Kit* plugin (to build parser and lexer from bnf grammar)

* Download the code, set Plugin SDK and Java SDK

* In project properties set the `plugin.xml` location
* Hover the `grammar/cmake.bnf` and generate parser code
* Custom jflex lexer is used, so use existing lexer instead of generating from bnf. Custom lexer now contains state to distinguish between the command name and argument.
* In project properties mark gen as source folder
* Generate lexer class from `*.flex` file
* Build project and you are set to go
