package org.partiql.tool.ridl.model.load

import org.junit.jupiter.api.Test
import org.partiql.tool.ridl.model.Document

class LoaderTest {

    @Test
    fun load() {
        val input = """
            type my_bool bool;
            type my_i32 int32;
            type my_i64 int64;
            type my_f32 float32;
            type my_f64 float64;
            type my_str string;
            type my_byte byte;
            type my_byte_string bytes;
            
            type my_variable_array bool[];
            type my_fixed_array bool[8];
            
            // check no need for forward declarations and basic name reference.
            type my_items my_item[];
            type my_item string;
           
            type my_enum enum { ENUMERATOR_X, ENUMERATOR_Y, ENUMERATOR_Z };
           
            // struct basic
            type my_struct struct {
                x: int32,
                y: int64,
            };
            
            // struct referencing named types with inlines
            type my_other_struct struct {
                a: my_i32,
                b: my_i64,
                c: my_struct,
                // d: struct { x: int32, y: int64 },
            };

            type my_union union {
                 var_a int32;
                 var_b int64;
                 var_c struct { x: int32, y: int32 };
            };
            
            namespace foo {
                type my_byte byte;
                namespace bar {
                    type my_byte byte;
                }
            }
        """.trimIndent()
        val document = Document.load(input)
        println(document)
    }
}
