## Changelog

### Current
2017-03-05

* Fixed: '--' handling, #296
* Added: Add `getProgramName` to `JCommander`, #247
* Added: Documentation for `listConverter` and `splitter`, #253, (@jeremysolarz)
* Fixed: Return right parameter name in exception, #227, (@jeremysolarz)
* Fixed: `JCommander#getParameters` returning nothing, #315, (@simon04)
* Fixed: Allow empty string (e.g. `java -jar jcommander-program.jar param1 ""`) as part of main parameter, #306 (@jeremysolarz)
* Fixed: Default value for `@Parameter(help=true)` parameter is not displayed in output of `JCommander.usage()`, #305 (@jeremysolarz) 
* Fixed: When providing two names in `@Parameter` always first name is given to `IValueValidator`, #309 (@jeremysolarz) 

### 1.58
2016-09-29

* Added: `IStringConverterInstanceFactory` to create converter instances, #279 (@simon04)
* Added: Allow to specify the `@file` charset, #286 (@simon04)
* Added: Converters `InetAddressConverter` (#288), `CharArrayConverter` (#289; @garydgregory) 
* Fixed: When using `parseWithoutValidation()`, JCommander uses 'parse()' on child commanders, #267 (@simon04)
* Fixed: Share all options (such as column size, allow abbreviated options, case sensitivity) with sub commands, see `JCommander.Options` class, #280 (fixes #155, #244, #261, #274; @simon04)
* Fixed: Thread-safe and non-shared converter factories, #284 (@simon04)
* Fixed: Skip `Path` converter when class is not available (Android), #287 (@JesusFreke)
* Added: JCommander now requires Java 8

### 1.56
2016-08-05

* Added: Allow user to retrieve unknown command, #275

### 1.55
2016-02-28

* Added: Support for disabling the `@file` expansion, #156
* Fixed: Wrap usage for commands and main parameters, #258
* Added: Read parameters from interfaces, #252
* Added: Refuse to write to final fields, #243
* Added: Access private fields/methods, #236
* Fixed: Fix description line wrapping, #239 
* Fixed: Prioritize registered converters for enums over generic enum conversion, #179
* Added: Travis CI support, https://travis-ci.org/cbeust/jcommander
* Added: Gradle build support
* Fixed: Better error message when there's a visibility problem.
* Require Java 7

### 1.48
2015-04-11

* Added: Added support for URL, URI, Java NIO paths parameters, #189, #219
* Fixed: Incorrect usage formatting with single long options, #200

### 1.37
2014-10-05

* Added: Support for `hidden` commands (`Parameters`), #191 
* Added: parameter overwriting (and even disallowing it for certain parameters)
* Added: `#` mark comments in a `@file`, #199 
* Added: Support for "--"
* Fixed: Bug in enum parsing, #184 

### 1.34
2014-02-22

* Fixed problem whereby Parameters returning Lists and with alternate names were being reset on the first use of an alternate name, #182

### 1.32
2013-09-09

* Fixed: Main parameters with a default value should be overridden if a main parameter is specified, #137
* Fixed: Allow enum values without converting them to uppercase, #107

### 1.30
2012/10/27

* Added: JCommander#acceptUnknownOption and JCommander#getUnknownArgs
* Added: JCommander#allowAbbreviatedOptions (default: false)
* Added: JCommander#setCaseSensitiveOptions (default: true)
* Added: Support for enums (Scott M Stark)
* Fixed: Missing new lines in usage (styurin)
* Fixed: The description of commands is now displayed on the next line and indented.

### 1.29
2012/07/28

* Fixed: Empty string defaults now displayed as "<empty string>" in the usage
* Fixed: Bugs with the PositiveInteger validator
* Fixed: Parameters with a single double quote were not working properly

### 1.27
2012/07/05

* Added: IValueValidator to validate parameter values (typed) as opposed to IParameterValidator which validates strings
* Added: echoInput, used when password=true to echo the characters (Jason Wheeler)
* Added: @Parameter(help = true)
* Fixed: wasn't handling parameters that start with " but don't end with one correctly
* Fixed: if using a different option prefix, unknown option are mistakenly reported as "no main parameter defined" (kurmasz)
* Fixed: 113: getCommandDescription() returns the description of the main parameter instead of that of the command
* Fixed: bug with several multiple arity parameters (VariableArityTest)
* Fixed: variable arities not working when same parameter appears multiple times.

### 1.25
2012/04/26

* Added: Default passwords are no longer displayed in the usage (Paul Mendelson)
* Added: Variable arities now work magically, no need for IVariableArity any more
* Fixed: Commands using @Parameters(resourceBundle) were not i18n'ed properly in the usage()
* Fixed: StringIndexOutOfBoundsException if passing an empty parameter (bomanz)
* Fixed: #105: If no description is given for an enum, use that enum's value (Adrian Muraru)
* Fixed: #108: Dynamic parameters with "=" in them are not parsed correctly (szhem)
* Fixed: Commands with same prefix as options were not working properly.
* Fixed: #97: Required password always complains that it is not specified (timoteoponce)

### 1.23
2012/01/12

* Added: @DynamicParameter
* Fixed: Use JDK 6 Console() when available to improve support of non ascii chars (Julien Henry)

### 1.20
2011/11/24

* Added: Support for delegating parameter definitions to child classes (rodionmoiseev)
* Added: @Parameter(commandNames) so that command names can be specified with annotations
* Added: Support for enums (Adrian Muraru)
* Fixed: Throw if an unknown option is found
* Fixed: Main parameters are now validated as well (Connor Mullen)

### 1.19
2011/10/10

* Added: commandDescriptionKey to @Parameters, to allow internationalized command descriptions
* Added: JCommander#setParameterDescriptionComparator for better control over usage()
* Fixed: Fields of type Set (HashSet and SortedSet) are now supported
* Fixed: defaults for commands were not properly applied (Stevo Slavic)
* Fixed: "-args=a=b,b=c" was not being parsed correctly (Michael Lancaster)
* Fixed: #73: descriptionKey was being ignored on main parameters

### 1.18
2011/07/20

* Added: Default converter factories can be overridden (Scott Clasen)
* Added: IParameterValidator
* Added: Don't display "Options:" if none were defined
* Added: Enforce that the type of the main parameter is a List
* Added: usage() now displays the options for each command as well
* Fixed: Default values with a validator were being validate at parse() time instead of creation time.
* Fixed: Exception when using an @ file with empty lines between options
* Fixed: OOM when parsing certain descriptions with long URL's in them

### 1.15
2011/01/24

* Added: Added a constructor that takes a Bundle only, #47 (Russell Egan)
* Fixed: NPE with calling getCommandDescription() of an unknown command

### 1.13
2010/12/15

* Added: Boolean parameters with arity 0 (e.g. "foo -debug")
* Fixed: JCommander would sometimes just print a stack trace and continue, now rethrowing.

### 1.7
2010/09/06

* Added: Command usages are now shown in the order they were added to the JCommander object
* Fixed: JCommander now compatible with Java 5
* Fixed: Minor bug in the command display (Marc Ende)

### 1.6
2010/08/28

* Added: @Parameters(commandDescription = "command description")
* Added: now throwing an exception if required main parameters are not supplied
* Fixed: usage() was changing default values after two runs (jstrachan)

### 1.5
2010/08/15

* Added: overloaded versions of usage() with StringBuilders
* Added: inheritance support (Guillaume Sauthier)
* Added: support for commands (e.g. "main add --author=cbeust Foo.java")
* Added: support for converters for main parameters (e.g. List<HostPort>).

### 1.4
2010/07/28

* Added: string converter factories
* Added: IDefaultProvider
* Added: PropertyFileDefaultProvider
* Added: Usage is now showing required parameters and default value
* Added: Support for values that look like parameters ("-integer -3", "/file /tmp/a")
* Added: @Parameters(optionPrefixes) to allow for different prefixes than "-"

### 1.2
2010/07/25

* Usage is now aligned and alphabetically sorted
* Added the hidden attribute
* Added support for different separators than " " (e.g. "=").
* Deprecated @ResourceBundle, replaced with @Parameters

### 1.1
2010/08/15

* Better internationalization
* Password support
* Type converters

