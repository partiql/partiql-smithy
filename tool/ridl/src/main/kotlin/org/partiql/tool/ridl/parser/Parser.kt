package org.partiql.tool.ridl.parser

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
import org.partiql.tool.ridl.antlr.RIDLBaseVisitor
import org.partiql.tool.ridl.antlr.RIDLLexer
import org.partiql.tool.ridl.antlr.RIDLParser
import org.partiql.tool.ridl.model.Definition
import org.partiql.tool.ridl.model.Document
import org.partiql.tool.ridl.model.RType
import org.partiql.tool.ridl.model.RTypeArray
import org.partiql.tool.ridl.model.RTypeEnum
import org.partiql.tool.ridl.model.RTypeIon
import org.partiql.tool.ridl.model.RTypeList
import org.partiql.tool.ridl.model.RTypeMap
import org.partiql.tool.ridl.model.RTypeNamed
import org.partiql.tool.ridl.model.RTypePrimitive
import org.partiql.tool.ridl.model.RTypeStruct
import org.partiql.tool.ridl.model.RTypeTuple
import org.partiql.tool.ridl.model.RTypeUnion
import org.partiql.tool.ridl.model.RTypeUnit
import java.io.ByteArrayInputStream
import java.nio.file.Path

/**
 * Type definitions don't require forward declarations and can be recursive / mutually recursive.
 */
internal object Parser {

    @JvmStatic
    fun load(input: String, include: Path?): Document {
        val source = ByteArrayInputStream(input.toByteArray(Charsets.UTF_8))
        val lexer = RIDLLexer(CharStreams.fromStream(source))
        lexer.removeErrorListeners()
        lexer.addErrorListener(LexerErrorListener)
        val tokens = CommonTokenStream(lexer)
        val parser = RIDLParser(tokens)
        parser.reset()
        parser.removeErrorListeners()
        parser.addErrorListener(ParseErrorListener)
        val tree = parser.document()!!
        // 1st pass, build the symbol tree for name resolution.
        val names = Names.build(tree)
        // 2nd pass, populate definitions using the symbol tree.
        val definitions = mutableListOf<Definition>()
        DefinitionVisitor(names, definitions).visit(tree)
        return Document(definitions)
    }

    /**
     * Catches Lexical errors (unidentified tokens).
     */
    private object LexerErrorListener : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?,
        ) {
            if (offendingSymbol is Token) {
                val token = offendingSymbol.text
                // val tokenType = RIDLParser.VOCABULARY.getSymbolicName(offendingSymbol.type)
                val offset = charPositionInLine + 1
                val length = token.length
                throw IllegalArgumentException("$msg. Location $line:$offset:$length")
            } else {
                throw IllegalArgumentException("Offending symbol is not a Token.")
            }
        }
    }

    /**
     * Catches Parser errors (malformed syntax).
     */
    private object ParseErrorListener : BaseErrorListener() {

        private val rules = RIDLParser.ruleNames.asList()

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?,
        ) {
            if (offendingSymbol is Token) {
                val rule = e?.ctx?.toString(rules) ?: "UNKNOWN"
                val token = offendingSymbol.text
                // val tokenType = RIDLParser.VOCABULARY.getSymbolicName(offendingSymbol.type)
                val offset = charPositionInLine + 1
                val length = token.length
                throw IllegalArgumentException("$msg. Rule: $rule, Location $line:$offset:$length")
            } else {
                throw IllegalArgumentException("Offending symbol is not a Token.")
            }
        }
    }

    private class DefinitionVisitor(
        private val names: Names,
        private val definitions: MutableList<Definition>,
    ) : RIDLBaseVisitor<RType>() {

        override fun visitDefinition(ctx: RIDLParser.DefinitionContext): RType {
            val name = ctx.IDENTIFIER().text
            val type = visitType(ctx.type())
            val definition = Definition(name, type)
            definitions.add(definition)
            return type
        }

        override fun visitTypeNamed(ctx: RIDLParser.TypeNamedContext): RTypeNamed {
            val name = ctx.IDENTIFIER().text
            if (!names.contains(name)) {
                error("Undefined type `$name` at ${Location.of(ctx)}")
            }
            return RTypeNamed(name)
        }

        override fun visitTypePrimitive(ctx: RIDLParser.TypePrimitiveContext): RTypePrimitive {
            val type = when (ctx.text) {
                "bool" -> RTypePrimitive.T.BOOL
                "i32" -> RTypePrimitive.T.I32
                "i64" -> RTypePrimitive.T.I64
                "f32" -> RTypePrimitive.T.F32
                "f64" -> RTypePrimitive.T.F64
                "decimal" -> RTypePrimitive.T.DECIMAL
                "char" -> RTypePrimitive.T.CHAR
                "string" -> RTypePrimitive.T.STRING
                "byte" -> RTypePrimitive.T.BYTE
                "bytes" -> RTypePrimitive.T.BYTES
                else -> error("Unknown primitive type `${ctx.text}` at ${Location.of(ctx)}")
            }
            return RTypePrimitive(type)
        }

        override fun visitTypeIon(ctx: RIDLParser.TypeIonContext): RTypeIon {
            val type = when (ctx.text) {
                "ion" -> RTypeIon.T.ANY
                "ion.bool" -> RTypeIon.T.BOOL
                "ion.int" -> RTypeIon.T.INT
                "ion.float" -> RTypeIon.T.FLOAT
                "ion.decimal" -> RTypeIon.T.DECIMAL
                "ion.timestamp" -> RTypeIon.T.TIMESTAMP
                "ion.string" -> RTypeIon.T.STRING
                "ion.symbol" -> RTypeIon.T.SYMBOL
                "ion.blob" -> RTypeIon.T.BLOB
                "ion.clob" -> RTypeIon.T.CLOB
                "ion.struct" -> RTypeIon.T.STRUCT
                "ion.list" -> RTypeIon.T.LIST
                "ion.sexp" -> RTypeIon.T.SEXP
                else -> error("Unknown Ion type ${ctx.text} at ${Location.of(ctx)}")
            }
            return RTypeIon(type)
        }

        override fun visitTypeList(ctx: RIDLParser.TypeListContext): RTypeList {
            val items = visitType(ctx.type())
            return RTypeList(items)
        }

        override fun visitTypeMap(ctx: RIDLParser.TypeMapContext): RTypeMap {
            val key = visitTypePrimitive(ctx.typePrimitive())
            val value = visitType(ctx.type())
            return RTypeMap(key, value)
        }

        override fun visitTypeTuple(ctx: RIDLParser.TypeTupleContext): RTypeTuple {
            val operands = ctx.type().map { visitType(it) }
            return RTypeTuple(operands)
        }

        override fun visitTypeArray(ctx: RIDLParser.TypeArrayContext): RTypeArray {
            val type = visitTypePrimitive(ctx.typePrimitive())
            val size = ctx.INTEGER().text.toInt()
            return RTypeArray(type, size)
        }

        override fun visitTypeStruct(ctx: RIDLParser.TypeStructContext): RTypeStruct {
            val fields = ctx.typeStructField().map {
                val name = it.IDENTIFIER().text
                val type = visitType(it.type())
                RTypeStruct.Field(name, type)
            }
            return RTypeStruct(fields)
        }

        override fun visitTypeUnion(ctx: RIDLParser.TypeUnionContext): RTypeUnion {
            val variants = ctx.typeUnionVariant().map {
                val name = it.IDENTIFIER().text
                val type = visitType(it.type())
                RTypeUnion.Variant(name, type)
            }
            return RTypeUnion(variants)
        }

        override fun visitTypeEnum(ctx: RIDLParser.TypeEnumContext): RTypeEnum {
            val values = ctx.ENUMERATOR().map { it.text }
            return RTypeEnum(values)
        }

        override fun visitTypeUnit(ctx: RIDLParser.TypeUnitContext): RTypeUnit = RTypeUnit
    }
}
