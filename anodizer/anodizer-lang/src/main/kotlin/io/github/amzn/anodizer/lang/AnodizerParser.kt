package io.github.amzn.anodizer.lang

import io.github.amzn.anodizer.antlr.RIDLLexer
import io.github.amzn.anodizer.antlr.RIDLParser
import io.github.amzn.anodizer.AnodizerModel
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
import java.io.InputStream
import java.nio.file.Path

/**
 * Type definitions don't require forward declarations and can be recursive / mutually recursive.
 */
public object AnodizerParser {

    @JvmStatic
    @JvmOverloads
    public fun parse(input: String, domain: String, include: Path? = null): AnodizerModel {
        return parse(input.byteInputStream(), domain, include)
    }

    @JvmStatic
    @JvmOverloads
    public fun parse(input: InputStream, domain: String, include: Path? = null): AnodizerModel {
        val stream = CharStreams.fromStream(input)
        val lexer = RIDLLexer(stream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(LexerErrorListener)
        val tokens = CommonTokenStream(lexer)
        val parser = RIDLParser(tokens)
        parser.reset()
        parser.removeErrorListeners()
        parser.addErrorListener(ParseErrorListener)
        val tree = parser.document()!!
        // 1st pass, build the symbol tree for name resolution.
        val root = Symbols.load(tree, domain)
        // 2nd pass, populate type definitions.
        val definitions = Definitions.load(tree, root)
        // Done.
        return AnodizerModel(domain, definitions)
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
}
