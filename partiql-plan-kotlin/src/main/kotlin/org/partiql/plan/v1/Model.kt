@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1

import kotlin.Char
import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.random.Random
import org.partiql.`value`.PartiQLValue
import org.partiql.`value`.PartiQLValueExperimental
import org.partiql.`value`.PartiQLValueType
import org.partiql.plan.v1.builder.CatalogBuilder
import org.partiql.plan.v1.builder.CatalogItemAggBuilder
import org.partiql.plan.v1.builder.CatalogItemFnBuilder
import org.partiql.plan.v1.builder.CatalogItemValueBuilder
import org.partiql.plan.v1.builder.IdentifierQualifiedBuilder
import org.partiql.plan.v1.builder.IdentifierSymbolBuilder
import org.partiql.plan.v1.builder.PartiQlPlanBuilder
import org.partiql.plan.v1.builder.RefBuilder
import org.partiql.plan.v1.builder.RefCastBuilder
import org.partiql.plan.v1.builder.RelBindingBuilder
import org.partiql.plan.v1.builder.RelBuilder
import org.partiql.plan.v1.builder.RelOpAggregateBuilder
import org.partiql.plan.v1.builder.RelOpAggregateCallBuilder
import org.partiql.plan.v1.builder.RelOpDistinctBuilder
import org.partiql.plan.v1.builder.RelOpErrBuilder
import org.partiql.plan.v1.builder.RelOpExceptBuilder
import org.partiql.plan.v1.builder.RelOpExcludeBuilder
import org.partiql.plan.v1.builder.RelOpExcludePathBuilder
import org.partiql.plan.v1.builder.RelOpExcludeStepBuilder
import org.partiql.plan.v1.builder.RelOpExcludeTypeCollIndexBuilder
import org.partiql.plan.v1.builder.RelOpExcludeTypeCollWildcardBuilder
import org.partiql.plan.v1.builder.RelOpExcludeTypeStructKeyBuilder
import org.partiql.plan.v1.builder.RelOpExcludeTypeStructSymbolBuilder
import org.partiql.plan.v1.builder.RelOpExcludeTypeStructWildcardBuilder
import org.partiql.plan.v1.builder.RelOpFilterBuilder
import org.partiql.plan.v1.builder.RelOpIntersectBuilder
import org.partiql.plan.v1.builder.RelOpJoinBuilder
import org.partiql.plan.v1.builder.RelOpLimitBuilder
import org.partiql.plan.v1.builder.RelOpOffsetBuilder
import org.partiql.plan.v1.builder.RelOpProjectBuilder
import org.partiql.plan.v1.builder.RelOpScanBuilder
import org.partiql.plan.v1.builder.RelOpScanIndexedBuilder
import org.partiql.plan.v1.builder.RelOpSortBuilder
import org.partiql.plan.v1.builder.RelOpSortSpecBuilder
import org.partiql.plan.v1.builder.RelOpUnionBuilder
import org.partiql.plan.v1.builder.RelOpUnpivotBuilder
import org.partiql.plan.v1.builder.RelTypeBuilder
import org.partiql.plan.v1.builder.RexBuilder
import org.partiql.plan.v1.builder.RexOpCallDynamicBuilder
import org.partiql.plan.v1.builder.RexOpCallDynamicCandidateBuilder
import org.partiql.plan.v1.builder.RexOpCallStaticBuilder
import org.partiql.plan.v1.builder.RexOpCaseBranchBuilder
import org.partiql.plan.v1.builder.RexOpCaseBuilder
import org.partiql.plan.v1.builder.RexOpCastBuilder
import org.partiql.plan.v1.builder.RexOpCollectionBuilder
import org.partiql.plan.v1.builder.RexOpErrBuilder
import org.partiql.plan.v1.builder.RexOpGlobalBuilder
import org.partiql.plan.v1.builder.RexOpLitBuilder
import org.partiql.plan.v1.builder.RexOpPathIndexBuilder
import org.partiql.plan.v1.builder.RexOpPathKeyBuilder
import org.partiql.plan.v1.builder.RexOpPathSymbolBuilder
import org.partiql.plan.v1.builder.RexOpPivotBuilder
import org.partiql.plan.v1.builder.RexOpSelectBuilder
import org.partiql.plan.v1.builder.RexOpStructBuilder
import org.partiql.plan.v1.builder.RexOpStructFieldBuilder
import org.partiql.plan.v1.builder.RexOpSubqueryBuilder
import org.partiql.plan.v1.builder.RexOpTupleUnionBuilder
import org.partiql.plan.v1.builder.RexOpVarBuilder
import org.partiql.plan.v1.builder.StatementQueryBuilder
import org.partiql.plan.v1.visitor.PlanVisitor
import org.partiql.types.StaticType

public abstract class PlanNode {
  @JvmField
  public var tag: String = "Plan-${"%06x".format(Random.nextInt())}"

  public abstract val children: List<PlanNode>

  public abstract fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R
}

public data class PartiQLPlan(
  @JvmField
  public val catalogs: List<Catalog>,
  @JvmField
  public val statement: Statement,
) : PlanNode() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(catalogs)
    kids.add(statement)
    kids.filterNotNull()
  }


  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitPartiQLPlan(this, ctx)

  public companion object {
    @JvmStatic
    public fun builder(): PartiQlPlanBuilder = PartiQlPlanBuilder()
  }
}

public data class Catalog(
  @JvmField
  public val name: String,
  @JvmField
  public val items: List<Item>,
) : PlanNode() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(items)
    kids.filterNotNull()
  }


  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitCatalog(this, ctx)

  public sealed class Item : PlanNode() {
    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
      is Value -> visitor.visitCatalogItemValue(this, ctx)
      is Fn -> visitor.visitCatalogItemFn(this, ctx)
      is Agg -> visitor.visitCatalogItemAgg(this, ctx)
    }

    public data class Value(
      @JvmField
      public val path: List<String>,
      @JvmField
      public val type: StaticType,
    ) : Item() {
      public override val children: List<PlanNode> = emptyList()

      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitCatalogItemValue(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): CatalogItemValueBuilder = CatalogItemValueBuilder()
      }
    }

    public data class Fn(
      @JvmField
      public val path: List<String>,
      @JvmField
      public val specific: String,
    ) : Item() {
      public override val children: List<PlanNode> = emptyList()

      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitCatalogItemFn(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): CatalogItemFnBuilder = CatalogItemFnBuilder()
      }
    }

    public data class Agg(
      @JvmField
      public val path: List<String>,
      @JvmField
      public val specific: String,
    ) : Item() {
      public override val children: List<PlanNode> = emptyList()

      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitCatalogItemAgg(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): CatalogItemAggBuilder = CatalogItemAggBuilder()
      }
    }
  }

  public companion object {
    @JvmStatic
    public fun builder(): CatalogBuilder = CatalogBuilder()
  }
}

public data class Ref(
  @JvmField
  public val catalog: Int,
  @JvmField
  public val symbol: Int,
) : PlanNode() {
  public override val children: List<PlanNode> = emptyList()

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = visitor.visitRef(this,
      ctx)

  public data class Cast(
    @JvmField
    public val input: PartiQLValueType,
    @JvmField
    public val target: PartiQLValueType,
  ) : PlanNode() {
    public override val children: List<PlanNode> = emptyList()

    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
        visitor.visitRefCast(this, ctx)

    public companion object {
      @JvmStatic
      public fun builder(): RefCastBuilder = RefCastBuilder()
    }
  }

  public companion object {
    @JvmStatic
    public fun builder(): RefBuilder = RefBuilder()
  }
}

public sealed class Statement : PlanNode() {
  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
    is Query -> visitor.visitStatementQuery(this, ctx)
  }

  public data class Query(
    @JvmField
    public val root: Rex,
  ) : Statement() {
    public override val children: List<PlanNode> by lazy {
      val kids = mutableListOf<PlanNode?>()
      kids.add(root)
      kids.filterNotNull()
    }


    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
        visitor.visitStatementQuery(this, ctx)

    public companion object {
      @JvmStatic
      public fun builder(): StatementQueryBuilder = StatementQueryBuilder()
    }
  }
}

public sealed class Identifier : PlanNode() {
  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
    is Symbol -> visitor.visitIdentifierSymbol(this, ctx)
    is Qualified -> visitor.visitIdentifierQualified(this, ctx)
  }

  public enum class CaseSensitivity {
    SENSITIVE,
    INSENSITIVE,
  }

  public data class Symbol(
    @JvmField
    public val symbol: String,
    @JvmField
    public val caseSensitivity: CaseSensitivity,
  ) : Identifier() {
    public override val children: List<PlanNode> = emptyList()

    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
        visitor.visitIdentifierSymbol(this, ctx)

    public companion object {
      @JvmStatic
      public fun builder(): IdentifierSymbolBuilder = IdentifierSymbolBuilder()
    }
  }

  public data class Qualified(
    @JvmField
    public val root: Symbol,
    @JvmField
    public val steps: List<Symbol>,
  ) : Identifier() {
    public override val children: List<PlanNode> by lazy {
      val kids = mutableListOf<PlanNode?>()
      kids.add(root)
      kids.addAll(steps)
      kids.filterNotNull()
    }


    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
        visitor.visitIdentifierQualified(this, ctx)

    public companion object {
      @JvmStatic
      public fun builder(): IdentifierQualifiedBuilder = IdentifierQualifiedBuilder()
    }
  }
}

public data class Rex(
  @JvmField
  public val type: StaticType,
  @JvmField
  public val op: Op,
) : PlanNode() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(op)
    kids.filterNotNull()
  }


  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = visitor.visitRex(this,
      ctx)

  public sealed class Op : PlanNode() {
    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
      is Lit -> visitor.visitRexOpLit(this, ctx)
      is Var -> visitor.visitRexOpVar(this, ctx)
      is Global -> visitor.visitRexOpGlobal(this, ctx)
      is Path -> visitor.visitRexOpPath(this, ctx)
      is Cast -> visitor.visitRexOpCast(this, ctx)
      is Call -> visitor.visitRexOpCall(this, ctx)
      is Case -> visitor.visitRexOpCase(this, ctx)
      is Collection -> visitor.visitRexOpCollection(this, ctx)
      is Struct -> visitor.visitRexOpStruct(this, ctx)
      is Pivot -> visitor.visitRexOpPivot(this, ctx)
      is Subquery -> visitor.visitRexOpSubquery(this, ctx)
      is Select -> visitor.visitRexOpSelect(this, ctx)
      is TupleUnion -> visitor.visitRexOpTupleUnion(this, ctx)
      is Err -> visitor.visitRexOpErr(this, ctx)
    }

    public data class Lit(
      @JvmField
      public val `value`: PartiQLValue,
    ) : Op() {
      public override val children: List<PlanNode> = emptyList()

      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpLit(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpLitBuilder = RexOpLitBuilder()
      }
    }

    public data class Var(
      @JvmField
      public val depth: Int,
      @JvmField
      public val ref: Int,
    ) : Op() {
      public override val children: List<PlanNode> = emptyList()

      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpVar(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpVarBuilder = RexOpVarBuilder()
      }
    }

    public data class Global(
      @JvmField
      public val ref: Ref,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(ref)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpGlobal(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpGlobalBuilder = RexOpGlobalBuilder()
      }
    }

    public sealed class Path : Op() {
      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
        is Index -> visitor.visitRexOpPathIndex(this, ctx)
        is Key -> visitor.visitRexOpPathKey(this, ctx)
        is Symbol -> visitor.visitRexOpPathSymbol(this, ctx)
      }

      public data class Index(
        @JvmField
        public val root: Rex,
        @JvmField
        public val key: Rex,
      ) : Path() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(root)
          kids.add(key)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRexOpPathIndex(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RexOpPathIndexBuilder = RexOpPathIndexBuilder()
        }
      }

      public data class Key(
        @JvmField
        public val root: Rex,
        @JvmField
        public val key: Rex,
      ) : Path() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(root)
          kids.add(key)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRexOpPathKey(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RexOpPathKeyBuilder = RexOpPathKeyBuilder()
        }
      }

      public data class Symbol(
        @JvmField
        public val root: Rex,
        @JvmField
        public val key: String,
      ) : Path() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(root)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRexOpPathSymbol(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RexOpPathSymbolBuilder = RexOpPathSymbolBuilder()
        }
      }
    }

    public data class Cast(
      @JvmField
      public val cast: Ref.Cast,
      @JvmField
      public val arg: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(cast)
        kids.add(arg)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpCast(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpCastBuilder = RexOpCastBuilder()
      }
    }

    public sealed class Call : Op() {
      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
        is Static -> visitor.visitRexOpCallStatic(this, ctx)
        is Dynamic -> visitor.visitRexOpCallDynamic(this, ctx)
      }

      public data class Static(
        @JvmField
        public val fn: Ref,
        @JvmField
        public val args: List<Rex>,
      ) : Call() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(fn)
          kids.addAll(args)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRexOpCallStatic(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RexOpCallStaticBuilder = RexOpCallStaticBuilder()
        }
      }

      public data class Dynamic(
        @JvmField
        public val args: List<Rex>,
        @JvmField
        public val candidates: List<Candidate>,
      ) : Call() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.addAll(args)
          kids.addAll(candidates)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRexOpCallDynamic(this, ctx)

        public data class Candidate(
          @JvmField
          public val fn: Ref,
          @JvmField
          public val parameters: List<PartiQLValueType>,
          @JvmField
          public val coercions: List<Ref.Cast?>,
        ) : PlanNode() {
          public override val children: List<PlanNode> by lazy {
            val kids = mutableListOf<PlanNode?>()
            kids.add(fn)
            kids.addAll(coercions)
            kids.filterNotNull()
          }


          public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
              visitor.visitRexOpCallDynamicCandidate(this, ctx)

          public companion object {
            @JvmStatic
            public fun builder(): RexOpCallDynamicCandidateBuilder =
                RexOpCallDynamicCandidateBuilder()
          }
        }

        public companion object {
          @JvmStatic
          public fun builder(): RexOpCallDynamicBuilder = RexOpCallDynamicBuilder()
        }
      }
    }

    public data class Case(
      @JvmField
      public val branches: List<Branch>,
      @JvmField
      public val default: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.addAll(branches)
        kids.add(default)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpCase(this, ctx)

      public data class Branch(
        @JvmField
        public val condition: Rex,
        @JvmField
        public val rex: Rex,
      ) : PlanNode() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(condition)
          kids.add(rex)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRexOpCaseBranch(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RexOpCaseBranchBuilder = RexOpCaseBranchBuilder()
        }
      }

      public companion object {
        @JvmStatic
        public fun builder(): RexOpCaseBuilder = RexOpCaseBuilder()
      }
    }

    public data class Collection(
      @JvmField
      public val values: List<Rex>,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.addAll(values)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpCollection(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpCollectionBuilder = RexOpCollectionBuilder()
      }
    }

    public data class Struct(
      @JvmField
      public val fields: List<Field>,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.addAll(fields)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpStruct(this, ctx)

      public data class Field(
        @JvmField
        public val k: Rex,
        @JvmField
        public val v: Rex,
      ) : PlanNode() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(k)
          kids.add(v)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRexOpStructField(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RexOpStructFieldBuilder = RexOpStructFieldBuilder()
        }
      }

      public companion object {
        @JvmStatic
        public fun builder(): RexOpStructBuilder = RexOpStructBuilder()
      }
    }

    public data class Pivot(
      @JvmField
      public val key: Rex,
      @JvmField
      public val `value`: Rex,
      @JvmField
      public val rel: Rel,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(key)
        kids.add(value)
        kids.add(rel)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpPivot(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpPivotBuilder = RexOpPivotBuilder()
      }
    }

    public data class Subquery(
      @JvmField
      public val `constructor`: Rex,
      @JvmField
      public val rel: Rel,
      @JvmField
      public val coercion: Coercion,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(constructor)
        kids.add(rel)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpSubquery(this, ctx)

      public enum class Coercion {
        SCALAR,
        ROW,
      }

      public companion object {
        @JvmStatic
        public fun builder(): RexOpSubqueryBuilder = RexOpSubqueryBuilder()
      }
    }

    public data class Select(
      @JvmField
      public val `constructor`: Rex,
      @JvmField
      public val rel: Rel,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(constructor)
        kids.add(rel)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpSelect(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpSelectBuilder = RexOpSelectBuilder()
      }
    }

    public data class TupleUnion(
      @JvmField
      public val args: List<Rex>,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.addAll(args)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpTupleUnion(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpTupleUnionBuilder = RexOpTupleUnionBuilder()
      }
    }

    public data class Err(
      @JvmField
      public val message: String,
    ) : Op() {
      public override val children: List<PlanNode> = emptyList()

      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRexOpErr(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RexOpErrBuilder = RexOpErrBuilder()
      }
    }
  }

  public companion object {
    @JvmStatic
    public fun builder(): RexBuilder = RexBuilder()
  }
}

public data class Rel(
  @JvmField
  public val type: Type,
  @JvmField
  public val op: Op,
) : PlanNode() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(type)
    kids.add(op)
    kids.filterNotNull()
  }


  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = visitor.visitRel(this,
      ctx)

  public enum class Prop {
    ORDERED,
  }

  public data class Type(
    @JvmField
    public val schema: List<Binding>,
    @JvmField
    public val props: Set<Prop>,
  ) : PlanNode() {
    public override val children: List<PlanNode> by lazy {
      val kids = mutableListOf<PlanNode?>()
      kids.addAll(schema)
      kids.filterNotNull()
    }


    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
        visitor.visitRelType(this, ctx)

    public companion object {
      @JvmStatic
      public fun builder(): RelTypeBuilder = RelTypeBuilder()
    }
  }

  public sealed class Op : PlanNode() {
    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
      is Scan -> visitor.visitRelOpScan(this, ctx)
      is ScanIndexed -> visitor.visitRelOpScanIndexed(this, ctx)
      is Unpivot -> visitor.visitRelOpUnpivot(this, ctx)
      is Distinct -> visitor.visitRelOpDistinct(this, ctx)
      is Filter -> visitor.visitRelOpFilter(this, ctx)
      is Sort -> visitor.visitRelOpSort(this, ctx)
      is Union -> visitor.visitRelOpUnion(this, ctx)
      is Intersect -> visitor.visitRelOpIntersect(this, ctx)
      is Except -> visitor.visitRelOpExcept(this, ctx)
      is Limit -> visitor.visitRelOpLimit(this, ctx)
      is Offset -> visitor.visitRelOpOffset(this, ctx)
      is Project -> visitor.visitRelOpProject(this, ctx)
      is Join -> visitor.visitRelOpJoin(this, ctx)
      is Aggregate -> visitor.visitRelOpAggregate(this, ctx)
      is Exclude -> visitor.visitRelOpExclude(this, ctx)
      is Err -> visitor.visitRelOpErr(this, ctx)
    }

    public data class Scan(
      @JvmField
      public val rex: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(rex)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpScan(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpScanBuilder = RelOpScanBuilder()
      }
    }

    public data class ScanIndexed(
      @JvmField
      public val rex: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(rex)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpScanIndexed(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpScanIndexedBuilder = RelOpScanIndexedBuilder()
      }
    }

    public data class Unpivot(
      @JvmField
      public val rex: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(rex)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpUnpivot(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpUnpivotBuilder = RelOpUnpivotBuilder()
      }
    }

    public data class Distinct(
      @JvmField
      public val input: Rel,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpDistinct(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpDistinctBuilder = RelOpDistinctBuilder()
      }
    }

    public data class Filter(
      @JvmField
      public val input: Rel,
      @JvmField
      public val predicate: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.add(predicate)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpFilter(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpFilterBuilder = RelOpFilterBuilder()
      }
    }

    public data class Sort(
      @JvmField
      public val input: Rel,
      @JvmField
      public val specs: List<Spec>,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.addAll(specs)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpSort(this, ctx)

      public enum class Order {
        ASC_NULLS_LAST,
        ASC_NULLS_FIRST,
        DESC_NULLS_LAST,
        DESC_NULLS_FIRST,
      }

      public data class Spec(
        @JvmField
        public val rex: Rex,
        @JvmField
        public val order: Order,
      ) : PlanNode() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(rex)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRelOpSortSpec(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RelOpSortSpecBuilder = RelOpSortSpecBuilder()
        }
      }

      public companion object {
        @JvmStatic
        public fun builder(): RelOpSortBuilder = RelOpSortBuilder()
      }
    }

    public data class Union(
      @JvmField
      public val lhs: Rel,
      @JvmField
      public val rhs: Rel,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(lhs)
        kids.add(rhs)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpUnion(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpUnionBuilder = RelOpUnionBuilder()
      }
    }

    public data class Intersect(
      @JvmField
      public val lhs: Rel,
      @JvmField
      public val rhs: Rel,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(lhs)
        kids.add(rhs)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpIntersect(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpIntersectBuilder = RelOpIntersectBuilder()
      }
    }

    public data class Except(
      @JvmField
      public val lhs: Rel,
      @JvmField
      public val rhs: Rel,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(lhs)
        kids.add(rhs)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpExcept(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpExceptBuilder = RelOpExceptBuilder()
      }
    }

    public data class Limit(
      @JvmField
      public val input: Rel,
      @JvmField
      public val limit: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.add(limit)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpLimit(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpLimitBuilder = RelOpLimitBuilder()
      }
    }

    public data class Offset(
      @JvmField
      public val input: Rel,
      @JvmField
      public val offset: Rex,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.add(offset)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpOffset(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpOffsetBuilder = RelOpOffsetBuilder()
      }
    }

    public data class Project(
      @JvmField
      public val input: Rel,
      @JvmField
      public val projections: List<Rex>,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.addAll(projections)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpProject(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpProjectBuilder = RelOpProjectBuilder()
      }
    }

    public data class Join(
      @JvmField
      public val lhs: Rel,
      @JvmField
      public val rhs: Rel,
      @JvmField
      public val rex: Rex,
      @JvmField
      public val type: Type,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(lhs)
        kids.add(rhs)
        kids.add(rex)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpJoin(this, ctx)

      public enum class Type {
        INNER,
        LEFT,
        RIGHT,
        FULL,
      }

      public companion object {
        @JvmStatic
        public fun builder(): RelOpJoinBuilder = RelOpJoinBuilder()
      }
    }

    public data class Aggregate(
      @JvmField
      public val input: Rel,
      @JvmField
      public val strategy: Strategy,
      @JvmField
      public val calls: List<Call>,
      @JvmField
      public val groups: List<Rex>,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.addAll(calls)
        kids.addAll(groups)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpAggregate(this, ctx)

      public enum class Strategy {
        FULL,
        PARTIAL,
      }

      public data class Call(
        @JvmField
        public val agg: Ref,
        @JvmField
        public val setQuantifier: SetQuantifier,
        @JvmField
        public val args: List<Rex>,
      ) : PlanNode() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(agg)
          kids.addAll(args)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRelOpAggregateCall(this, ctx)

        public enum class SetQuantifier {
          ALL,
          DISTINCT,
        }

        public companion object {
          @JvmStatic
          public fun builder(): RelOpAggregateCallBuilder = RelOpAggregateCallBuilder()
        }
      }

      public companion object {
        @JvmStatic
        public fun builder(): RelOpAggregateBuilder = RelOpAggregateBuilder()
      }
    }

    public data class Exclude(
      @JvmField
      public val input: Rel,
      @JvmField
      public val paths: List<Path>,
    ) : Op() {
      public override val children: List<PlanNode> by lazy {
        val kids = mutableListOf<PlanNode?>()
        kids.add(input)
        kids.addAll(paths)
        kids.filterNotNull()
      }


      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpExclude(this, ctx)

      public data class Path(
        @JvmField
        public val root: Rex.Op.Var,
        @JvmField
        public val steps: List<Step>,
      ) : PlanNode() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(root)
          kids.addAll(steps)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRelOpExcludePath(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RelOpExcludePathBuilder = RelOpExcludePathBuilder()
        }
      }

      public data class Step(
        @JvmField
        public val type: Type,
        @JvmField
        public val substeps: List<Step>,
      ) : PlanNode() {
        public override val children: List<PlanNode> by lazy {
          val kids = mutableListOf<PlanNode?>()
          kids.add(type)
          kids.addAll(substeps)
          kids.filterNotNull()
        }


        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
            visitor.visitRelOpExcludeStep(this, ctx)

        public companion object {
          @JvmStatic
          public fun builder(): RelOpExcludeStepBuilder = RelOpExcludeStepBuilder()
        }
      }

      public sealed class Type : PlanNode() {
        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
          is StructSymbol -> visitor.visitRelOpExcludeTypeStructSymbol(this, ctx)
          is StructKey -> visitor.visitRelOpExcludeTypeStructKey(this, ctx)
          is CollIndex -> visitor.visitRelOpExcludeTypeCollIndex(this, ctx)
          is StructWildcard -> visitor.visitRelOpExcludeTypeStructWildcard(this, ctx)
          is CollWildcard -> visitor.visitRelOpExcludeTypeCollWildcard(this, ctx)
        }

        public data class StructSymbol(
          @JvmField
          public val symbol: String,
        ) : Type() {
          public override val children: List<PlanNode> = emptyList()

          public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
              visitor.visitRelOpExcludeTypeStructSymbol(this, ctx)

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeStructSymbolBuilder =
                RelOpExcludeTypeStructSymbolBuilder()
          }
        }

        public data class StructKey(
          @JvmField
          public val key: String,
        ) : Type() {
          public override val children: List<PlanNode> = emptyList()

          public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
              visitor.visitRelOpExcludeTypeStructKey(this, ctx)

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeStructKeyBuilder =
                RelOpExcludeTypeStructKeyBuilder()
          }
        }

        public data class CollIndex(
          @JvmField
          public val index: Int,
        ) : Type() {
          public override val children: List<PlanNode> = emptyList()

          public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
              visitor.visitRelOpExcludeTypeCollIndex(this, ctx)

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeCollIndexBuilder =
                RelOpExcludeTypeCollIndexBuilder()
          }
        }

        public data class StructWildcard(
          @JvmField
          public val ` `: Char = ' ',
        ) : Type() {
          public override val children: List<PlanNode> = emptyList()

          public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
              visitor.visitRelOpExcludeTypeStructWildcard(this, ctx)

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeStructWildcardBuilder =
                RelOpExcludeTypeStructWildcardBuilder()
          }
        }

        public data class CollWildcard(
          @JvmField
          public val ` `: Char = ' ',
        ) : Type() {
          public override val children: List<PlanNode> = emptyList()

          public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
              visitor.visitRelOpExcludeTypeCollWildcard(this, ctx)

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeCollWildcardBuilder =
                RelOpExcludeTypeCollWildcardBuilder()
          }
        }
      }

      public companion object {
        @JvmStatic
        public fun builder(): RelOpExcludeBuilder = RelOpExcludeBuilder()
      }
    }

    public data class Err(
      @JvmField
      public val message: String,
    ) : Op() {
      public override val children: List<PlanNode> = emptyList()

      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
          visitor.visitRelOpErr(this, ctx)

      public companion object {
        @JvmStatic
        public fun builder(): RelOpErrBuilder = RelOpErrBuilder()
      }
    }
  }

  public data class Binding(
    @JvmField
    public val name: String,
    @JvmField
    public val type: StaticType,
  ) : PlanNode() {
    public override val children: List<PlanNode> = emptyList()

    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
        visitor.visitRelBinding(this, ctx)

    public companion object {
      @JvmStatic
      public fun builder(): RelBindingBuilder = RelBindingBuilder()
    }
  }

  public companion object {
    @JvmStatic
    public fun builder(): RelBuilder = RelBuilder()
  }
}
