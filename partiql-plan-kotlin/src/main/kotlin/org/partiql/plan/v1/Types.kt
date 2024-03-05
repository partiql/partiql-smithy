@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.jvm.JvmStatic
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

public interface PlanNode {
  public val _id: String

  public val children: List<PlanNode>

  public fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R
}

public abstract class PartiQLPlan : PlanNode {
  public abstract val catalogs: List<Catalog>

  public abstract val statement: Statement

  public override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PartiQLPlan) return false
    if (catalogs != other.catalogs) return false
    if (statement != other.statement) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = catalogs.hashCode()
    result = 31 * result + statement.hashCode()
    return result
  }

  public abstract fun copy(catalogs: List<Catalog> = this.catalogs, statement: Statement =
      this.statement): PartiQLPlan

  public companion object {
    @JvmStatic
    public fun builder(): PartiQlPlanBuilder = PartiQlPlanBuilder()
  }
}

public abstract class Catalog : PlanNode {
  public abstract val name: String

  public abstract val items: List<Item>

  public override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Catalog) return false
    if (name != other.name) return false
    if (items != other.items) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + items.hashCode()
    return result
  }

  public abstract fun copy(name: String = this.name, items: List<Item> = this.items): Catalog

  public sealed interface Item : PlanNode {
    public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
      is Value -> visitor.visitCatalogItemValue(this, ctx)
      is Fn -> visitor.visitCatalogItemFn(this, ctx)
      is Agg -> visitor.visitCatalogItemAgg(this, ctx)
    }

    public abstract class Value : Item {
      public abstract val path: List<String>

      public abstract val type: StaticType

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Value) return false
        if (path != other.path) return false
        if (type != other.type) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + type.hashCode()
        return result
      }

      public abstract fun copy(path: List<String> = this.path, type: StaticType = this.type): Value

      public companion object {
        @JvmStatic
        public fun builder(): CatalogItemValueBuilder = CatalogItemValueBuilder()
      }
    }

    public abstract class Fn : Item {
      public abstract val path: List<String>

      public abstract val specific: String

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Fn) return false
        if (path != other.path) return false
        if (specific != other.specific) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + specific.hashCode()
        return result
      }

      public abstract fun copy(path: List<String> = this.path, specific: String = this.specific): Fn

      public companion object {
        @JvmStatic
        public fun builder(): CatalogItemFnBuilder = CatalogItemFnBuilder()
      }
    }

    public abstract class Agg : Item {
      public abstract val path: List<String>

      public abstract val specific: String

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Agg) return false
        if (path != other.path) return false
        if (specific != other.specific) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + specific.hashCode()
        return result
      }

      public abstract fun copy(path: List<String> = this.path, specific: String = this.specific):
          Agg

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

public abstract class Ref : PlanNode {
  public abstract val catalog: Int

  public abstract val symbol: Int

  public override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Ref) return false
    if (catalog != other.catalog) return false
    if (symbol != other.symbol) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = catalog.hashCode()
    result = 31 * result + symbol.hashCode()
    return result
  }

  public abstract fun copy(catalog: Int = this.catalog, symbol: Int = this.symbol): Ref

  public abstract class Cast : PlanNode {
    public abstract val input: PartiQLValueType

    public abstract val target: PartiQLValueType

    public override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Cast) return false
      if (input != other.input) return false
      if (target != other.target) return false
      return true
    }

    public override fun hashCode(): Int {
      var result = input.hashCode()
      result = 31 * result + target.hashCode()
      return result
    }

    public abstract fun copy(input: PartiQLValueType = this.input, target: PartiQLValueType =
        this.target): Cast

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

public sealed interface Statement : PlanNode {
  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
    is Query -> visitor.visitStatementQuery(this, ctx)
  }

  public abstract class Query : Statement {
    public abstract val root: Rex

    public override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Query) return false
      if (root != other.root) return false
      return true
    }

    public override fun hashCode(): Int = root.hashCode()

    public abstract fun copy(root: Rex = this.root): Query

    public companion object {
      @JvmStatic
      public fun builder(): StatementQueryBuilder = StatementQueryBuilder()
    }
  }
}

public sealed interface Identifier : PlanNode {
  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
    is Symbol -> visitor.visitIdentifierSymbol(this, ctx)
    is Qualified -> visitor.visitIdentifierQualified(this, ctx)
  }

  public enum class CaseSensitivity {
    SENSITIVE,
    INSENSITIVE,
  }

  public abstract class Symbol : Identifier {
    public abstract val symbol: String

    public abstract val caseSensitivity: CaseSensitivity

    public override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Symbol) return false
      if (symbol != other.symbol) return false
      if (caseSensitivity != other.caseSensitivity) return false
      return true
    }

    public override fun hashCode(): Int {
      var result = symbol.hashCode()
      result = 31 * result + caseSensitivity.hashCode()
      return result
    }

    public abstract fun copy(symbol: String = this.symbol, caseSensitivity: CaseSensitivity =
        this.caseSensitivity): Symbol

    public companion object {
      @JvmStatic
      public fun builder(): IdentifierSymbolBuilder = IdentifierSymbolBuilder()
    }
  }

  public abstract class Qualified : Identifier {
    public abstract val root: Symbol

    public abstract val steps: List<Symbol>

    public override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Qualified) return false
      if (root != other.root) return false
      if (steps != other.steps) return false
      return true
    }

    public override fun hashCode(): Int {
      var result = root.hashCode()
      result = 31 * result + steps.hashCode()
      return result
    }

    public abstract fun copy(root: Symbol = this.root, steps: List<Symbol> = this.steps): Qualified

    public companion object {
      @JvmStatic
      public fun builder(): IdentifierQualifiedBuilder = IdentifierQualifiedBuilder()
    }
  }
}

public abstract class Rex : PlanNode {
  public abstract val type: StaticType

  public abstract val op: Op

  public override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Rex) return false
    if (type != other.type) return false
    if (op != other.op) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = type.hashCode()
    result = 31 * result + op.hashCode()
    return result
  }

  public abstract fun copy(type: StaticType = this.type, op: Op = this.op): Rex

  public sealed interface Op : PlanNode {
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

    public abstract class Lit : Op {
      public abstract val `value`: PartiQLValue

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Lit) return false
        if (`value` != other.`value`) return false
        return true
      }

      public override fun hashCode(): Int = `value`.hashCode()

      public abstract fun copy(`value`: PartiQLValue = this.value): Lit

      public companion object {
        @JvmStatic
        public fun builder(): RexOpLitBuilder = RexOpLitBuilder()
      }
    }

    public abstract class Var : Op {
      public abstract val depth: Int

      public abstract val ref: Int

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Var) return false
        if (depth != other.depth) return false
        if (ref != other.ref) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = depth.hashCode()
        result = 31 * result + ref.hashCode()
        return result
      }

      public abstract fun copy(depth: Int = this.depth, ref: Int = this.ref): Var

      public companion object {
        @JvmStatic
        public fun builder(): RexOpVarBuilder = RexOpVarBuilder()
      }
    }

    public abstract class Global : Op {
      public abstract val ref: Ref

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Global) return false
        if (ref != other.ref) return false
        return true
      }

      public override fun hashCode(): Int = ref.hashCode()

      public abstract fun copy(ref: Ref = this.ref): Global

      public companion object {
        @JvmStatic
        public fun builder(): RexOpGlobalBuilder = RexOpGlobalBuilder()
      }
    }

    public sealed interface Path : Op {
      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
        is Index -> visitor.visitRexOpPathIndex(this, ctx)
        is Key -> visitor.visitRexOpPathKey(this, ctx)
        is Symbol -> visitor.visitRexOpPathSymbol(this, ctx)
      }

      public abstract class Index : Path {
        public abstract val root: Rex

        public abstract val key: Rex

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Index) return false
          if (root != other.root) return false
          if (key != other.key) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = root.hashCode()
          result = 31 * result + key.hashCode()
          return result
        }

        public abstract fun copy(root: Rex = this.root, key: Rex = this.key): Index

        public companion object {
          @JvmStatic
          public fun builder(): RexOpPathIndexBuilder = RexOpPathIndexBuilder()
        }
      }

      public abstract class Key : Path {
        public abstract val root: Rex

        public abstract val key: Rex

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Key) return false
          if (root != other.root) return false
          if (key != other.key) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = root.hashCode()
          result = 31 * result + key.hashCode()
          return result
        }

        public abstract fun copy(root: Rex = this.root, key: Rex = this.key): Key

        public companion object {
          @JvmStatic
          public fun builder(): RexOpPathKeyBuilder = RexOpPathKeyBuilder()
        }
      }

      public abstract class Symbol : Path {
        public abstract val root: Rex

        public abstract val key: String

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Symbol) return false
          if (root != other.root) return false
          if (key != other.key) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = root.hashCode()
          result = 31 * result + key.hashCode()
          return result
        }

        public abstract fun copy(root: Rex = this.root, key: String = this.key): Symbol

        public companion object {
          @JvmStatic
          public fun builder(): RexOpPathSymbolBuilder = RexOpPathSymbolBuilder()
        }
      }
    }

    public abstract class Cast : Op {
      public abstract val cast: Ref.Cast

      public abstract val arg: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cast) return false
        if (cast != other.cast) return false
        if (arg != other.arg) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = cast.hashCode()
        result = 31 * result + arg.hashCode()
        return result
      }

      public abstract fun copy(cast: Ref.Cast = this.cast, arg: Rex = this.arg): Cast

      public companion object {
        @JvmStatic
        public fun builder(): RexOpCastBuilder = RexOpCastBuilder()
      }
    }

    public sealed interface Call : Op {
      public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
        is Static -> visitor.visitRexOpCallStatic(this, ctx)
        is Dynamic -> visitor.visitRexOpCallDynamic(this, ctx)
      }

      public abstract class Static : Call {
        public abstract val fn: Ref

        public abstract val args: List<Rex>

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Static) return false
          if (fn != other.fn) return false
          if (args != other.args) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = fn.hashCode()
          result = 31 * result + args.hashCode()
          return result
        }

        public abstract fun copy(fn: Ref = this.fn, args: List<Rex> = this.args): Static

        public companion object {
          @JvmStatic
          public fun builder(): RexOpCallStaticBuilder = RexOpCallStaticBuilder()
        }
      }

      public abstract class Dynamic : Call {
        public abstract val args: List<Rex>

        public abstract val candidates: List<Candidate>

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Dynamic) return false
          if (args != other.args) return false
          if (candidates != other.candidates) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = args.hashCode()
          result = 31 * result + candidates.hashCode()
          return result
        }

        public abstract fun copy(args: List<Rex> = this.args, candidates: List<Candidate> =
            this.candidates): Dynamic

        public abstract class Candidate : PlanNode {
          public abstract val fn: Ref

          public abstract val parameters: List<PartiQLValueType>

          public abstract val coercions: List<Ref.Cast?>

          public override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Candidate) return false
            if (fn != other.fn) return false
            if (parameters != other.parameters) return false
            if (coercions != other.coercions) return false
            return true
          }

          public override fun hashCode(): Int {
            var result = fn.hashCode()
            result = 31 * result + parameters.hashCode()
            result = 31 * result + coercions.hashCode()
            return result
          }

          public abstract fun copy(
            fn: Ref = this.fn,
            parameters: List<PartiQLValueType> = this.parameters,
            coercions: List<Ref.Cast?> = this.coercions,
          ): Candidate

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

    public abstract class Case : Op {
      public abstract val branches: List<Branch>

      public abstract val default: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Case) return false
        if (branches != other.branches) return false
        if (default != other.default) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = branches.hashCode()
        result = 31 * result + default.hashCode()
        return result
      }

      public abstract fun copy(branches: List<Branch> = this.branches, default: Rex = this.default):
          Case

      public abstract class Branch : PlanNode {
        public abstract val condition: Rex

        public abstract val rex: Rex

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Branch) return false
          if (condition != other.condition) return false
          if (rex != other.rex) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = condition.hashCode()
          result = 31 * result + rex.hashCode()
          return result
        }

        public abstract fun copy(condition: Rex = this.condition, rex: Rex = this.rex): Branch

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

    public abstract class Collection : Op {
      public abstract val values: List<Rex>

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Collection) return false
        if (values != other.values) return false
        return true
      }

      public override fun hashCode(): Int = values.hashCode()

      public abstract fun copy(values: List<Rex> = this.values): Collection

      public companion object {
        @JvmStatic
        public fun builder(): RexOpCollectionBuilder = RexOpCollectionBuilder()
      }
    }

    public abstract class Struct : Op {
      public abstract val fields: List<Field>

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Struct) return false
        if (fields != other.fields) return false
        return true
      }

      public override fun hashCode(): Int = fields.hashCode()

      public abstract fun copy(fields: List<Field> = this.fields): Struct

      public abstract class Field : PlanNode {
        public abstract val k: Rex

        public abstract val v: Rex

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Field) return false
          if (k != other.k) return false
          if (v != other.v) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = k.hashCode()
          result = 31 * result + v.hashCode()
          return result
        }

        public abstract fun copy(k: Rex = this.k, v: Rex = this.v): Field

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

    public abstract class Pivot : Op {
      public abstract val key: Rex

      public abstract val `value`: Rex

      public abstract val rel: Rel

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pivot) return false
        if (key != other.key) return false
        if (`value` != other.`value`) return false
        if (rel != other.rel) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + `value`.hashCode()
        result = 31 * result + rel.hashCode()
        return result
      }

      public abstract fun copy(
        key: Rex = this.key,
        `value`: Rex = this.value,
        rel: Rel = this.rel,
      ): Pivot

      public companion object {
        @JvmStatic
        public fun builder(): RexOpPivotBuilder = RexOpPivotBuilder()
      }
    }

    public abstract class Subquery : Op {
      public abstract val `constructor`: Rex

      public abstract val rel: Rel

      public abstract val coercion: Coercion

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Subquery) return false
        if (`constructor` != other.`constructor`) return false
        if (rel != other.rel) return false
        if (coercion != other.coercion) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = `constructor`.hashCode()
        result = 31 * result + rel.hashCode()
        result = 31 * result + coercion.hashCode()
        return result
      }

      public abstract fun copy(
        `constructor`: Rex = this.constructor,
        rel: Rel = this.rel,
        coercion: Coercion = this.coercion,
      ): Subquery

      public enum class Coercion {
        SCALAR,
        ROW,
      }

      public companion object {
        @JvmStatic
        public fun builder(): RexOpSubqueryBuilder = RexOpSubqueryBuilder()
      }
    }

    public abstract class Select : Op {
      public abstract val `constructor`: Rex

      public abstract val rel: Rel

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Select) return false
        if (`constructor` != other.`constructor`) return false
        if (rel != other.rel) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = `constructor`.hashCode()
        result = 31 * result + rel.hashCode()
        return result
      }

      public abstract fun copy(`constructor`: Rex = this.constructor, rel: Rel = this.rel): Select

      public companion object {
        @JvmStatic
        public fun builder(): RexOpSelectBuilder = RexOpSelectBuilder()
      }
    }

    public abstract class TupleUnion : Op {
      public abstract val args: List<Rex>

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TupleUnion) return false
        if (args != other.args) return false
        return true
      }

      public override fun hashCode(): Int = args.hashCode()

      public abstract fun copy(args: List<Rex> = this.args): TupleUnion

      public companion object {
        @JvmStatic
        public fun builder(): RexOpTupleUnionBuilder = RexOpTupleUnionBuilder()
      }
    }

    public abstract class Err : Op {
      public abstract val message: String

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Err) return false
        if (message != other.message) return false
        return true
      }

      public override fun hashCode(): Int = message.hashCode()

      public abstract fun copy(message: String = this.message): Err

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

public abstract class Rel : PlanNode {
  public abstract val type: Type

  public abstract val op: Op

  public override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Rel) return false
    if (type != other.type) return false
    if (op != other.op) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = type.hashCode()
    result = 31 * result + op.hashCode()
    return result
  }

  public abstract fun copy(type: Type = this.type, op: Op = this.op): Rel

  public enum class Prop {
    ORDERED,
  }

  public abstract class Type : PlanNode {
    public abstract val schema: List<Binding>

    public abstract val props: Set<Prop>

    public override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Type) return false
      if (schema != other.schema) return false
      if (props != other.props) return false
      return true
    }

    public override fun hashCode(): Int {
      var result = schema.hashCode()
      result = 31 * result + props.hashCode()
      return result
    }

    public abstract fun copy(schema: List<Binding> = this.schema, props: Set<Prop> = this.props):
        Type

    public companion object {
      @JvmStatic
      public fun builder(): RelTypeBuilder = RelTypeBuilder()
    }
  }

  public sealed interface Op : PlanNode {
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

    public abstract class Scan : Op {
      public abstract val rex: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Scan) return false
        if (rex != other.rex) return false
        return true
      }

      public override fun hashCode(): Int = rex.hashCode()

      public abstract fun copy(rex: Rex = this.rex): Scan

      public companion object {
        @JvmStatic
        public fun builder(): RelOpScanBuilder = RelOpScanBuilder()
      }
    }

    public abstract class ScanIndexed : Op {
      public abstract val rex: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScanIndexed) return false
        if (rex != other.rex) return false
        return true
      }

      public override fun hashCode(): Int = rex.hashCode()

      public abstract fun copy(rex: Rex = this.rex): ScanIndexed

      public companion object {
        @JvmStatic
        public fun builder(): RelOpScanIndexedBuilder = RelOpScanIndexedBuilder()
      }
    }

    public abstract class Unpivot : Op {
      public abstract val rex: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Unpivot) return false
        if (rex != other.rex) return false
        return true
      }

      public override fun hashCode(): Int = rex.hashCode()

      public abstract fun copy(rex: Rex = this.rex): Unpivot

      public companion object {
        @JvmStatic
        public fun builder(): RelOpUnpivotBuilder = RelOpUnpivotBuilder()
      }
    }

    public abstract class Distinct : Op {
      public abstract val input: Rel

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Distinct) return false
        if (input != other.input) return false
        return true
      }

      public override fun hashCode(): Int = input.hashCode()

      public abstract fun copy(input: Rel = this.input): Distinct

      public companion object {
        @JvmStatic
        public fun builder(): RelOpDistinctBuilder = RelOpDistinctBuilder()
      }
    }

    public abstract class Filter : Op {
      public abstract val input: Rel

      public abstract val predicate: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Filter) return false
        if (input != other.input) return false
        if (predicate != other.predicate) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + predicate.hashCode()
        return result
      }

      public abstract fun copy(input: Rel = this.input, predicate: Rex = this.predicate): Filter

      public companion object {
        @JvmStatic
        public fun builder(): RelOpFilterBuilder = RelOpFilterBuilder()
      }
    }

    public abstract class Sort : Op {
      public abstract val input: Rel

      public abstract val specs: List<Spec>

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sort) return false
        if (input != other.input) return false
        if (specs != other.specs) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + specs.hashCode()
        return result
      }

      public abstract fun copy(input: Rel = this.input, specs: List<Spec> = this.specs): Sort

      public enum class Order {
        ASC_NULLS_LAST,
        ASC_NULLS_FIRST,
        DESC_NULLS_LAST,
        DESC_NULLS_FIRST,
      }

      public abstract class Spec : PlanNode {
        public abstract val rex: Rex

        public abstract val order: Order

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Spec) return false
          if (rex != other.rex) return false
          if (order != other.order) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = rex.hashCode()
          result = 31 * result + order.hashCode()
          return result
        }

        public abstract fun copy(rex: Rex = this.rex, order: Order = this.order): Spec

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

    public abstract class Union : Op {
      public abstract val lhs: Rel

      public abstract val rhs: Rel

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Union) return false
        if (lhs != other.lhs) return false
        if (rhs != other.rhs) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = lhs.hashCode()
        result = 31 * result + rhs.hashCode()
        return result
      }

      public abstract fun copy(lhs: Rel = this.lhs, rhs: Rel = this.rhs): Union

      public companion object {
        @JvmStatic
        public fun builder(): RelOpUnionBuilder = RelOpUnionBuilder()
      }
    }

    public abstract class Intersect : Op {
      public abstract val lhs: Rel

      public abstract val rhs: Rel

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Intersect) return false
        if (lhs != other.lhs) return false
        if (rhs != other.rhs) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = lhs.hashCode()
        result = 31 * result + rhs.hashCode()
        return result
      }

      public abstract fun copy(lhs: Rel = this.lhs, rhs: Rel = this.rhs): Intersect

      public companion object {
        @JvmStatic
        public fun builder(): RelOpIntersectBuilder = RelOpIntersectBuilder()
      }
    }

    public abstract class Except : Op {
      public abstract val lhs: Rel

      public abstract val rhs: Rel

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Except) return false
        if (lhs != other.lhs) return false
        if (rhs != other.rhs) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = lhs.hashCode()
        result = 31 * result + rhs.hashCode()
        return result
      }

      public abstract fun copy(lhs: Rel = this.lhs, rhs: Rel = this.rhs): Except

      public companion object {
        @JvmStatic
        public fun builder(): RelOpExceptBuilder = RelOpExceptBuilder()
      }
    }

    public abstract class Limit : Op {
      public abstract val input: Rel

      public abstract val limit: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Limit) return false
        if (input != other.input) return false
        if (limit != other.limit) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + limit.hashCode()
        return result
      }

      public abstract fun copy(input: Rel = this.input, limit: Rex = this.limit): Limit

      public companion object {
        @JvmStatic
        public fun builder(): RelOpLimitBuilder = RelOpLimitBuilder()
      }
    }

    public abstract class Offset : Op {
      public abstract val input: Rel

      public abstract val offset: Rex

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Offset) return false
        if (input != other.input) return false
        if (offset != other.offset) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + offset.hashCode()
        return result
      }

      public abstract fun copy(input: Rel = this.input, offset: Rex = this.offset): Offset

      public companion object {
        @JvmStatic
        public fun builder(): RelOpOffsetBuilder = RelOpOffsetBuilder()
      }
    }

    public abstract class Project : Op {
      public abstract val input: Rel

      public abstract val projections: List<Rex>

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Project) return false
        if (input != other.input) return false
        if (projections != other.projections) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + projections.hashCode()
        return result
      }

      public abstract fun copy(input: Rel = this.input, projections: List<Rex> = this.projections):
          Project

      public companion object {
        @JvmStatic
        public fun builder(): RelOpProjectBuilder = RelOpProjectBuilder()
      }
    }

    public abstract class Join : Op {
      public abstract val lhs: Rel

      public abstract val rhs: Rel

      public abstract val rex: Rex

      public abstract val type: Type

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Join) return false
        if (lhs != other.lhs) return false
        if (rhs != other.rhs) return false
        if (rex != other.rex) return false
        if (type != other.type) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = lhs.hashCode()
        result = 31 * result + rhs.hashCode()
        result = 31 * result + rex.hashCode()
        result = 31 * result + type.hashCode()
        return result
      }

      public abstract fun copy(
        lhs: Rel = this.lhs,
        rhs: Rel = this.rhs,
        rex: Rex = this.rex,
        type: Type = this.type,
      ): Join

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

    public abstract class Aggregate : Op {
      public abstract val input: Rel

      public abstract val strategy: Strategy

      public abstract val calls: List<Call>

      public abstract val groups: List<Rex>

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Aggregate) return false
        if (input != other.input) return false
        if (strategy != other.strategy) return false
        if (calls != other.calls) return false
        if (groups != other.groups) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + strategy.hashCode()
        result = 31 * result + calls.hashCode()
        result = 31 * result + groups.hashCode()
        return result
      }

      public abstract fun copy(
        input: Rel = this.input,
        strategy: Strategy = this.strategy,
        calls: List<Call> = this.calls,
        groups: List<Rex> = this.groups,
      ): Aggregate

      public enum class Strategy {
        FULL,
        PARTIAL,
      }

      public abstract class Call : PlanNode {
        public abstract val agg: Ref

        public abstract val setQuantifier: SetQuantifier

        public abstract val args: List<Rex>

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Call) return false
          if (agg != other.agg) return false
          if (setQuantifier != other.setQuantifier) return false
          if (args != other.args) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = agg.hashCode()
          result = 31 * result + setQuantifier.hashCode()
          result = 31 * result + args.hashCode()
          return result
        }

        public abstract fun copy(
          agg: Ref = this.agg,
          setQuantifier: SetQuantifier = this.setQuantifier,
          args: List<Rex> = this.args,
        ): Call

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

    public abstract class Exclude : Op {
      public abstract val input: Rel

      public abstract val paths: List<Path>

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Exclude) return false
        if (input != other.input) return false
        if (paths != other.paths) return false
        return true
      }

      public override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + paths.hashCode()
        return result
      }

      public abstract fun copy(input: Rel = this.input, paths: List<Path> = this.paths): Exclude

      public abstract class Path : PlanNode {
        public abstract val root: Rex.Op.Var

        public abstract val steps: List<Step>

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Path) return false
          if (root != other.root) return false
          if (steps != other.steps) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = root.hashCode()
          result = 31 * result + steps.hashCode()
          return result
        }

        public abstract fun copy(root: Rex.Op.Var = this.root, steps: List<Step> = this.steps): Path

        public companion object {
          @JvmStatic
          public fun builder(): RelOpExcludePathBuilder = RelOpExcludePathBuilder()
        }
      }

      public abstract class Step : PlanNode {
        public abstract val type: Type

        public abstract val substeps: List<Step>

        public override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is Step) return false
          if (type != other.type) return false
          if (substeps != other.substeps) return false
          return true
        }

        public override fun hashCode(): Int {
          var result = type.hashCode()
          result = 31 * result + substeps.hashCode()
          return result
        }

        public abstract fun copy(type: Type = this.type, substeps: List<Step> = this.substeps): Step

        public companion object {
          @JvmStatic
          public fun builder(): RelOpExcludeStepBuilder = RelOpExcludeStepBuilder()
        }
      }

      public sealed interface Type : PlanNode {
        public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = when (this) {
          is StructSymbol -> visitor.visitRelOpExcludeTypeStructSymbol(this, ctx)
          is StructKey -> visitor.visitRelOpExcludeTypeStructKey(this, ctx)
          is CollIndex -> visitor.visitRelOpExcludeTypeCollIndex(this, ctx)
          is StructWildcard -> visitor.visitRelOpExcludeTypeStructWildcard(this, ctx)
          is CollWildcard -> visitor.visitRelOpExcludeTypeCollWildcard(this, ctx)
        }

        public abstract class StructSymbol : Type {
          public abstract val symbol: String

          public override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StructSymbol) return false
            if (symbol != other.symbol) return false
            return true
          }

          public override fun hashCode(): Int = symbol.hashCode()

          public abstract fun copy(symbol: String = this.symbol): StructSymbol

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeStructSymbolBuilder =
                RelOpExcludeTypeStructSymbolBuilder()
          }
        }

        public abstract class StructKey : Type {
          public abstract val key: String

          public override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StructKey) return false
            if (key != other.key) return false
            return true
          }

          public override fun hashCode(): Int = key.hashCode()

          public abstract fun copy(key: String = this.key): StructKey

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeStructKeyBuilder =
                RelOpExcludeTypeStructKeyBuilder()
          }
        }

        public abstract class CollIndex : Type {
          public abstract val index: Int

          public override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is CollIndex) return false
            if (index != other.index) return false
            return true
          }

          public override fun hashCode(): Int = index.hashCode()

          public abstract fun copy(index: Int = this.index): CollIndex

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeCollIndexBuilder =
                RelOpExcludeTypeCollIndexBuilder()
          }
        }

        public abstract class StructWildcard : Type {
          public override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StructWildcard) return false
            return true
          }

          public override fun hashCode(): Int = 0

          public abstract fun copy(): StructWildcard

          public companion object {
            @JvmStatic
            public fun builder(): RelOpExcludeTypeStructWildcardBuilder =
                RelOpExcludeTypeStructWildcardBuilder()
          }
        }

        public abstract class CollWildcard : Type {
          public override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is CollWildcard) return false
            return true
          }

          public override fun hashCode(): Int = 0

          public abstract fun copy(): CollWildcard

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

    public abstract class Err : Op {
      public abstract val message: String

      public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Err) return false
        if (message != other.message) return false
        return true
      }

      public override fun hashCode(): Int = message.hashCode()

      public abstract fun copy(message: String = this.message): Err

      public companion object {
        @JvmStatic
        public fun builder(): RelOpErrBuilder = RelOpErrBuilder()
      }
    }
  }

  public abstract class Binding : PlanNode {
    public abstract val name: String

    public abstract val type: StaticType

    public override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Binding) return false
      if (name != other.name) return false
      if (type != other.type) return false
      return true
    }

    public override fun hashCode(): Int {
      var result = name.hashCode()
      result = 31 * result + type.hashCode()
      return result
    }

    public abstract fun copy(name: String = this.name, type: StaticType = this.type): Binding

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
