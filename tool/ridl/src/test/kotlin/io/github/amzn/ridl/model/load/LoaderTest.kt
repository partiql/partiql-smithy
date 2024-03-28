package io.github.amzn.ridl.model.load

import org.junit.jupiter.api.Test
import io.github.amzn.ridl.codegen.language.kotlin.KotlinGenerator
import io.github.amzn.ridl.codegen.language.kotlin.KotlinOptions

import io.github.amzn.ridl.model.Document

class LoaderTest {

    @Test
    fun aliases() {
        val grammar = """
            type p_type int32;

            // alias without forward declaration
            type bar foo;
            type foo p_type;

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

    @Test
    fun unions() {
        val grammar = """
            type t_struct struct {
                x: int32,
                y: int64,
            };
            
            type t_union union {
                var_a: t_struct,
                var_b: t_struct,
            };
            
            type t_union_3 union {
                var_a: struct { x: int32, y: int64 },
                var_b: struct { x: float32, y: float64 },
            };

            type t_union_4 union {
                var_a: int32,
                var_b: t_struct,
                var_c: struct { x: float32, y: float64 },
            };

            type t_union_5 union {
                var_1: union {
                    var_a: int32,
                    var_b: int64,
                },
                var_2: union {
                    var_a: t_struct,
                    var_b: t_struct,
                },
                var_3: union {
                    var_a: struct { x: int32, y: int64 },
                    var_b: struct { x: float32, y: float64 },
                },
                var_4: union {
                    var_a: int32,
                    var_b: t_struct,
                    var_c: struct { x: float32, y: float64 },
                },
            };
        """.trimIndent()
        val document = Document.load(grammar)
        println(document)
    }
}
