package com.beust.jcommander;


import java.lang.annotation.*;

/**
 * Mark a @Parameter as a @ParameterFile if the File referenced by it should be parsed as a property file,
 * and modify the Options Object in which it is referenced (for instance a scenario file modifies the Scenario options)
 *
 * @author sramage 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ParameterFile {

	
}
