package io.github.amzn.anodizer.codegen.rust

import org.junit.jupiter.api.Test

class LifetimeTest {

    @Test
    fun lifetimes() {
        val grammar = """
            
            type struct_a struct {
                x: bool,
                y: int,
                z: string,
            };

            type struct_b struct {
                x: bool[],
                y: int[],
                z: string[],
            };

            type struct_c struct {
                x: decimal(1,2),
                y: blob(10),
                z: clob(10),
            };

            type struct_d struct {
                a: struct_a,
                b: struct_b,
            };
            
            type union_d union {
                a: struct_a,
                b: struct_b,
                c: struct_c,
                d: struct_d,
            };
            
        """.trimIndent()
        // val document = AnodizerParser.parse(grammar, "grammar")
        // val domain = Model.contextualize(document)
        // val index = Index.of(domain)
        // val lifetimes = Lifetimes.of(domain, index)
        // assert ??
    }
}
