/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.partiql.tool.ridl.parser

import org.partiql.tool.ridl.antlr.RIDLBaseVisitor
import org.partiql.tool.ridl.antlr.RIDLParser

/**
 * Simple wrapper over the set, but can add detailed error messaging / source locations later.
 *
 * @property names
 */
internal class Names private constructor(private val names: Set<String>) {

    fun contains(name: String): Boolean = names.contains(name)

    companion object {

        @JvmStatic
        fun build(tree: RIDLParser.DocumentContext): Names {
            val names = mutableSetOf<String>()
            Visitor(names).visit(tree)
            return Names(names)
        }
    }

    private class Visitor(
        private val names: MutableSet<String>,
    ) : RIDLBaseVisitor<Unit>() {

        override fun visitDefinition(ctx: RIDLParser.DefinitionContext) {
            val name = ctx.IDENTIFIER().text
            if (names.contains(name)) {
                error("Duplicate name `$name` found at ${Location.of(ctx)}")
            }
            names.add(name)
        }
    }
}
