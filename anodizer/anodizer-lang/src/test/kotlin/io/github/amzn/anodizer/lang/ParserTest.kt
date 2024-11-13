package io.github.amzn.anodizer.lang

import org.junit.jupiter.api.Test

/**
 * I haven't invested much time into assertions after compiler passes, so I'm just inspecting.
 */
class ParserTest {

    @Test
    fun aliases() {
        val grammar = this::class.java.getResource("/kitchen.ridl")!!.readText()
        val document = AnodizerParser.parse(grammar, "grammar")
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
        val document = AnodizerParser.parse(grammar, "grammar")
        println(document)
    }

    @Test
    fun lifetimes() {
        val grammar = """
            
            // no lifetime, fields are primitives
            type t_struct struct {
                x: int32,
                y: int64,
            };
            
            // lifetime, field has a reference
            type t_pair struct {
               x: int32,
               y: t_struct,
            };
            
            // no lifetime, alias to non-lifetime struct
            type t_alias_no t_struct;
            
            // lifetime, alias to lifetime struct
            type t_alias_yes t_pair;
            
            // no lifetime, variants are primitives
            type t_union_1 union {
                x: int32,
                y: bool,
            };
            
            // no lifetime, variants are structs of primitives
            type t_union_2 union {
                x: t_struct,
                y: t_struct,
            };
            
            // no lifetime, variants are structs of primitives
            type t_union_3 union {
                x: struct { x: int32, y: int64 },
                y: struct { u: string, v: bool },
            };
            
            // lifetime, variant has a lifetime
            type t_union_4 union {
                x: int32,
                y: int64,
                z: t_pair,
            };
            
            // lifetime, inline variant has a lifetime
            type t_union_5 union {
                x: struct {  pair: t_pair },
                y: t_alias_no,
            };
            
            // lifetime, variant has a lifetime
            type t_union_6 union {
                x: bool,
                y: t_alias_yes,
            };
            
        """.trimIndent()
        val document = AnodizerParser.parse(grammar, "grammar")
        // val lowered = document.lower()
        // val lifetimes = document.lifetimes()
        // println(lifetimes)
        println(document)
    }
}
