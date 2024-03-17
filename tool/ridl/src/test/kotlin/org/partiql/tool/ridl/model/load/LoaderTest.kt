package org.partiql.tool.ridl.model.load

import org.junit.jupiter.api.Test

import org.partiql.tool.ridl.model.Document

class LoaderTest {

    @Test
    fun load() {

        val grammar = """
            type p_type int32;
            type foo p_type;
            type bar foo;
            
            type point struct { x: foo, y: bar };

            namespace other {
            
                type p_type int64;
                type p_type_outer ::p_type; // alias outer type
                
                type point struct { 
                    x: ::p_type,        // int32
                    y: p_type_outer,    // int32
                    z: p_type,          // int64
                };
            }
        """.trimIndent()

        val document = Document.load(grammar)
        println(document)
    }
}