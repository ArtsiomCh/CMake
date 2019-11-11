package com.cmakeplugin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class CMakeKeywords {
  private CMakeKeywords() {}

/*
  private static String varNumberRegexp = "[0-9]";
  private static final Set<String> commands_Scripting = new HashSet<>();
  private static final Set<String> commands_Project = new HashSet<>();
  private static final Set<String> commands_Test = new HashSet<>();

  private static final Set<String> variables_All = new HashSet<>();
  private static List<Pattern> variables_regexp;

  private static final Set<String> properties_All = new HashSet<>();
  private static List<Pattern> properties_regexp;
*/

  private static final Logger LOGGER = LoggerFactory.getLogger(CMakeKeywords.class);
  private static final String varRegexp = "[A-Za-z0-9/_.+-]+";

  private static Map<String, String> commands;
  private static final Set<String> commands_Deprecated = new HashSet<>();

  private static Map<String, String> modules;
  private static Map<String, String> policies;

  private static Map<String, String> properties = new HashMap<>();
  private static Map<Pattern, String> properties_regexp = new HashMap<>();
  private static final Set<String> properties_Deprecated = new HashSet<>();
  private static List<Pattern> properties_Deprecated_regexp;

  private static Map<String, String> variables = new HashMap<>();
  private static Map<Pattern, String> variables_regexp = new HashMap<>();

  private static final Set<String> variables_ENV = new HashSet<>();
  private static List<Pattern> variables_ENV_regexp;

  private static final Set<String> operators = new HashSet<>();

  private static final Set<String> boolValues = new HashSet<>();
  private static List<Pattern> boolValues_regexp;

  public static boolean isCommand(String text){
    return commands.containsKey(text);
  }

  public static boolean isCommandDeprecated(String text){
    return commands_Deprecated.contains(text);
  }

  public static boolean isVariableENV(String text){
    return variables_ENV.contains(text)
        || variables_ENV_regexp.stream().anyMatch(p -> p.matcher(text).matches());
  }

  public static boolean isVariable(String text){
    return variables.containsKey(text)
        || variables_regexp.keySet().stream().anyMatch(p -> p.matcher(text).matches());
  }

  public static boolean isProperty(String text){
    return properties.containsKey(text)
        || properties_regexp.keySet().stream().anyMatch(p -> p.matcher(text).matches());
  }

  public static boolean isPropertyDeprecated(String text){
    return properties_Deprecated.contains(text)
        || properties_Deprecated_regexp.stream().anyMatch(p -> p.matcher(text).matches());
  }

  public static boolean isOperator(String text){
    return operators.contains(text);
  }

  public static boolean isModule(String text){
    return modules.containsKey(text);
  }

  public static boolean isBoolValue(String text){
    return boolValues.contains(text)
        || boolValues_regexp.stream().anyMatch(p -> p.matcher(text).matches());
  }

  public static Set<String> getAllVariables(){
    return variables.keySet();
  }


  public static String getCommandHelp(String commandName) {
    return commands.get(commandName);
  }

  public static String getModuleHelp(String moduleName) {
    return modules.get(moduleName);
  }

  public static String getPolicyHelp(String policyNum) {
    return policies.get(policyNum);
  }

  public static String getPropertyHelp(String propName) {
    return getHelpForItem(propName, properties, properties_regexp);
  }

  public static String getVariableHelp(String varName) {
    return getHelpForItem(varName, variables, variables_regexp);
  }

  private static String getHelpForItem(String itemName, Map<String, String> itemsMap, Map<Pattern, String> itemsMap_regexp) {
    String varHelp = itemsMap.get(itemName);
    if (varHelp != null) return varHelp;

    Pattern foundPattern = null;
    for (Pattern pattern : itemsMap_regexp.keySet()) {
      if (pattern.matcher(itemName).matches()) {
        if (foundPattern == null) foundPattern = pattern;
        else {
          final String oldRegexp = foundPattern.toString();
          final String newRegexp = pattern.toString();
          final int oldRegexpCount = oldRegexp.split("\\[").length;
          final int newRegexpCount = newRegexp.split("\\[").length;
          if (
            // CMAKE_<LANG>_FLAGS_<CONFIG> vs
            // CMAKE_<LANG>_FLAGS_INIT        <----------
              oldRegexpCount > newRegexpCount
                  // CMAKE_<LANG>_FLAGS_<CONFIG> vs
                  // CMAKE_<LANG>_FLAGS_<CONFIG>_INIT <----------
                  || (oldRegexpCount == newRegexpCount && oldRegexp.length() < newRegexp.length()))
            foundPattern = pattern;
        }
      }
    }
    return (foundPattern != null) ? itemsMap_regexp.get(foundPattern) : null;
  }

  private static Map<String, String> deSerializeMap(String filePathInResources) {
    Map<String, String> result = Collections.emptyMap();
    try {
      ObjectInputStream ois =
          new ObjectInputStream(
              CMakeKeywords.class.getClassLoader().getResourceAsStream(filePathInResources));
      result = (HashMap) ois.readObject();
      ois.close();
    } catch (IOException | ClassNotFoundException e) {
      LOGGER.warn("Can't deserialize Map: " + filePathInResources + " \n", e);
    }
    return result;
  }

  private static void deSerializeMapWithRegexp (String filePathInResources, Map<String, String> itemsMap, Map<Pattern, String> itemsMap_regexp){
    final Map<String, String> items_temp = deSerializeMap(filePathInResources);
    for (Entry<String, String> entry : items_temp.entrySet()) {
      String itemName = entry.getKey();
      if (itemName.contains("<")) {
        itemName = itemName.replaceAll("<" + varRegexp + ">", varRegexp);
        itemsMap_regexp.put(Pattern.compile(itemName), entry.getValue());
      } else itemsMap.put(itemName, entry.getValue());
    }
  }

  static {
    commands = deSerializeMap("/HashMaps/command2helptext.hashmap");
    Collections.addAll(commands_Deprecated,"build_name","exec_program","export_library_dependencies","install_files","install_programs","install_targets","load_command","make_directory","output_required_files","remove","subdir_depends","subdirs","use_mangled_mesa","utility_source","variable_requires","write_file" );

    modules = deSerializeMap("/HashMaps/module2helptext.hashmap");
    policies = deSerializeMap("/HashMaps/policy2helptext.hashmap");
    deSerializeMapWithRegexp("/HashMaps/property2helptext.hashmap", properties, properties_regexp);
    deSerializeMapWithRegexp("/HashMaps/variable2helptext.hashmap", variables, variables_regexp);

    // fixme: Macro Argument Caveats https://cmake.org/cmake/help/latest/command/macro.html
    variables_regexp.put(
        Pattern.compile("ARG(C|N|V[0-9]+)"),
        "In addition to referencing the formal parameters you can reference the values ${ARGC} which will be set to the number of arguments passed into the function as well as ${ARGV0}, ${ARGV1}, ${ARGV2}, … which will have the actual values of the arguments passed in. This facilitates creating macros with optional arguments.\n"
            + "\n"
            + "Furthermore, ${ARGV} holds the list of all arguments given to the macro and ${ARGN} holds the list of arguments past the last expected argument. Referencing to ${ARGV#} arguments beyond ${ARGC} have undefined behavior. Checking that ${ARGC} is greater than # is the only way to ensure that ${ARGV#} was passed to the function as an extra argument.");

    // https://cmake.org/cmake/help/latest/manual/cmake-env-variables.7.html
    Collections.addAll(variables_ENV,
// Environment Variables that Control the Build
            "CMAKE_CONFIG_TYPE","CMAKE_MSVCIDE_RUN_PATH","CMAKE_OSX_ARCHITECTURES","LDFLAGS","MACOSX_DEPLOYMENT_TARGET",
// Environment Variables for Languages
            "CC","CFLAGS","CSFLAGS","CUDACXX","CUDAFLAGS","CUDAHOSTCXX","CXX","CXXFLAGS","FC","FFLAGS","RC","RCFLAGS",
// Environment Variables for CTest
            "CMAKE_CONFIG_TYPE","CTEST_INTERACTIVE_DEBUG_MODE","CTEST_OUTPUT_ON_FAILURE","CTEST_PARALLEL_LEVEL","CTEST_USE_LAUNCHERS_DEFAULT","DASHBOARD_TEST_FROM_CTEST"
    );
    variables_ENV_regexp = Stream.of(
// Environment Variables for Languages
            "ASM"+ varRegexp +"","ASM"+ varRegexp +"FLAGS"
    ).map(Pattern::compile).collect(Collectors.toList());

    Collections.addAll(properties_Deprecated,
//DeprecatedPropertiesonDirectories
"TEST_INCLUDE_FILE",
//DeprecatedPropertiesonTargets
"POST_INSTALL_SCRIPT","PRE_INSTALL_SCRIPT"
//DeprecatedPropertiesonSourceFiles
            );

    properties_Deprecated_regexp = Stream.of(
//DeprecatedPropertiesonDirectories
"COMPILE_DEFINITIONS_"+ varRegexp,
//DeprecatedPropertiesonTargets
"COMPILE_DEFINITIONS_"+ varRegexp,
//DeprecatedPropertiesonSourceFiles
"COMPILE_DEFINITIONS_"+ varRegexp
            ).map(Pattern::compile).collect(Collectors.toList());

//CMAKE_Operators
    Collections.addAll(operators,
"ABSOLUTE","BOOL","CACHE","DOC","EXT","INTERNAL","MATCHES","NAME","NAMES","NAME_WE","PATH","PATHS","PROGRAM","STRING"
// IF expressions tests https://cmake.org/cmake/help/latest/command/if.html
            ,"EXISTS","COMMAND","DEFINED","EQUAL","LESS","LESS_EQUAL","GREATER","GREATER_EQUAL","STREQUAL","STRLESS","STRLESS_EQUAL","STRGREATER","STRGREATER_EQUAL","VERSION_EQUAL","VERSION_LESS","VERSION_LESS_EQUAL","VERSION_GREATER","VERSION_GREATER_EQUAL","MATCHES","NOT","AND","OR"
// string command https://cmake.org/cmake/help/latest/command/string.html
            ,"FIND","REVERSE","REPLACE"
            ,"REGEX","MATCH","MATCHALL","REPLACE"
            ,"APPEND","PREPEND","CONCAT","TOLOWER","TOUPPER","LENGTH","SUBSTRING","STRIP","GENEX_STRIP"
            ,"COMPARE","LESS","GREATER","EQUAL","NOTEQUAL","LESS_EQUAL","GREATER_EQUAL"
            ,"MD5","SHA1","SHA224","SHA256","SHA384","SHA512","SHA3_224","SHA3_256","SHA3_384","SHA3_512"
            ,"ASCII","CONFIGURE","RANDOM","TIMESTAMP","MAKE_C_IDENTIFIER","UUID"
//list command https://cmake.org/cmake/help/latest/command/list.html
            ,"LENGTH","GET","APPEND","FILTER","FIND","INSERT","REMOVE_AT","REMOVE_ITEM","REMOVE_DUPLICATES","REVERSE","SORT"
//file command https://cmake.org/cmake/help/latest/command/file.html
            ,"WRITE","APPEND","READ","STRINGS","GLOB","GLOB_RECURSE","RENAME","REMOVE","REMOVE_RECURSE","MAKE_DIRECTORY","RELATIVE_PATH","TO_CMAKE_PATH","TO_NATIVE_PATH","DOWNLOAD","UPLOAD","TIMESTAMP","COPY","INSTALL","LOCK",
// My addition
        "SHARED","STATIC","MODULE","PRIVATE","PUBLIC","INTERFACE","TARGET","TARGETS","PROPERTY","PROPERTIES","REQUIRED","EXPORT","NAMESPACE","DESTINATION","FILES","CONFIG","REQUIRED","COMPONENTS","FATAL_ERROR","STATUS","WARNING","AUTHOR_WARNING","SEND_ERROR","DEPRECATION",
        "POLICY", "SET", "NEW", "OLD"
    );

//Boolean values in CMake https://cmake.org/Wiki/CMake:VariablesListsStrings
    Collections.addAll(boolValues,
// false
            "NO","N","OFF","FALSE","NOTFOUND",
//true
            "TRUE","ON","Y","YE","YES"
    );
    boolValues_regexp = Stream.of(
        varRegexp +"-NOTFOUND"
    ).map(Pattern::compile).collect(Collectors.toList());
  }
}
