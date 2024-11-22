package io.github.amzn.anodizer.codegen.context

import net.pearx.kasechange.CaseFormat
import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toSnakeCase

/**
 * CtxPath
 * -----------------------
 * steps    -> ["path", "to", "my_name"]
 * snake    -> path_to_my_name
 * camel    -> pathToMyName
 * pascal   -> PathToMyName
 */
public class CtxPath(
    @JvmField public val steps: Array<String>,
    @JvmField public val snake: String,
    @JvmField public val camel: String,
    @JvmField public val pascal: String,
) {

    /**
     * NAME: [a-z][a-z0-9_]*;
     */
    private val case = CaseFormat.LOWER_UNDERSCORE

    /**
     * snake("")    -> pathtomy_name
     * snake(".")   -> path.to.my_name
     * snake("_")   -> path_to_my_name
     * snake("::")  -> path::to::my_name
     */
    public fun snake(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toSnakeCase(case) }

    /**
     * pascal("")   -> PathToMyName
     * pascal(".")  -> Path.To.MyName
     * pascal("_")  -> Path_To_My_Name
     * pascal("::") -> Path::To::My::Name
     */
    public fun pascal(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toPascalCase(case) }

    /**
     * camel("")    -> pathtomyName
     * camel(".")   -> path.to.myName
     * camel("_")   -> path_to_myName
     * camel("::")  -> path::to::myName
     */
    public fun camel(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toCamelCase(case) }

    /**
     * upper("")    -> PATHTOMY_NAME
     * upper(".")   -> PATH.TO.MY_NAME
     * upper("_")   -> PATH_TO_MY_NAME
     * upper("::")  -> PATH::TO::MY_NAME
     */
    public fun upper(delimiter: String = ""): String = steps.joinToString(delimiter) { it.uppercase() }

    /**
     * lower("")    -> pathtomy_name
     * lower(".")   -> path.to.my_name
     * lower("_")   -> path_to_my_name
     * lower("::")  -> path::to::my_name
     */
    public fun lower(delimiter: String = ""): String = steps.joinToString(delimiter) { it.lowercase() }
}