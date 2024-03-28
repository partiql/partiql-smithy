package io.github.amzn.ridl.model.load

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
import io.github.amzn.ridl.antlr.RIDLLexer
import io.github.amzn.ridl.antlr.RIDLParser
import io.github.amzn.ridl.model.Document
import java.io.ByteArrayInputStream
import java.nio.file.Path

/**
 * Type definitions don't require forward declarations and can be recursive / mutually recursive.
 */
internal object Loader {

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
        val namespace = Symbols.build(tree)
        // 2nd and 3rd passes, populate definitions using the symbol tree and rebase(?) aliases.
        val definitions = Definitions.build(tree, namespace).rebase()
        // Done.
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
}
