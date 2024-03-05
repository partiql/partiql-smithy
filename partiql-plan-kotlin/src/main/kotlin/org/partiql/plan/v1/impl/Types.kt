@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.impl

import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import org.partiql.`value`.PartiQLValue
import org.partiql.`value`.PartiQLValueExperimental
import org.partiql.`value`.PartiQLValueType
import org.partiql.plan.v1.Catalog
import org.partiql.plan.v1.Identifier
import org.partiql.plan.v1.PartiQLPlan
import org.partiql.plan.v1.PlanNode
import org.partiql.plan.v1.Ref
import org.partiql.plan.v1.Rel
import org.partiql.plan.v1.Rex
import org.partiql.plan.v1.Statement
import org.partiql.plan.v1.visitor.PlanVisitor
import org.partiql.types.StaticType

internal open class PartiQLPlanImpl(
  public override val _id: String,
  public override val catalogs: List<Catalog>,
  public override val statement: Statement,
) : PartiQLPlan() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(catalogs)
    kids.add(statement)
    kids.filterNotNull()
  }


  public override fun copy(catalogs: List<Catalog>, statement: Statement): PartiQLPlan =
      PartiQLPlanImpl(_id, catalogs, statement)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitPartiQLPlan(this, ctx)
}

internal open class CatalogImpl(
  public override val _id: String,
  public override val name: String,
  public override val items: List<Catalog.Item>,
) : Catalog() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(items)
    kids.filterNotNull()
  }


  public override fun copy(name: String, items: List<Catalog.Item>): Catalog = CatalogImpl(_id,
      name, items)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitCatalog(this, ctx)
}

internal open class CatalogItemValueImpl(
  public override val _id: String,
  public override val path: List<String>,
  public override val type: StaticType,
) : Catalog.Item.Value() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(path: List<String>, type: StaticType): Catalog.Item.Value =
      CatalogItemValueImpl(_id, path, type)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitCatalogItemValue(this, ctx)
}

internal open class CatalogItemFnImpl(
  public override val _id: String,
  public override val path: List<String>,
  public override val specific: String,
) : Catalog.Item.Fn() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(path: List<String>, specific: String): Catalog.Item.Fn =
      CatalogItemFnImpl(_id, path, specific)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitCatalogItemFn(this, ctx)
}

internal open class CatalogItemAggImpl(
  public override val _id: String,
  public override val path: List<String>,
  public override val specific: String,
) : Catalog.Item.Agg() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(path: List<String>, specific: String): Catalog.Item.Agg =
      CatalogItemAggImpl(_id, path, specific)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitCatalogItemAgg(this, ctx)
}

internal open class RefImpl(
  public override val _id: String,
  public override val catalog: Int,
  public override val symbol: Int,
) : Ref() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(catalog: Int, symbol: Int): Ref = RefImpl(_id, catalog, symbol)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = visitor.visitRef(this,
      ctx)
}

internal open class RefCastImpl(
  public override val _id: String,
  public override val input: PartiQLValueType,
  public override val target: PartiQLValueType,
) : Ref.Cast() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(input: PartiQLValueType, target: PartiQLValueType): Ref.Cast =
      RefCastImpl(_id, input, target)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRefCast(this, ctx)
}

internal open class StatementQueryImpl(
  public override val _id: String,
  public override val root: Rex,
) : Statement.Query() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(root)
    kids.filterNotNull()
  }


  public override fun copy(root: Rex): Statement.Query = StatementQueryImpl(_id, root)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitStatementQuery(this, ctx)
}

internal open class IdentifierSymbolImpl(
  public override val _id: String,
  public override val symbol: String,
  public override val caseSensitivity: Identifier.CaseSensitivity,
) : Identifier.Symbol() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(symbol: String, caseSensitivity: Identifier.CaseSensitivity):
      Identifier.Symbol = IdentifierSymbolImpl(_id, symbol, caseSensitivity)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitIdentifierSymbol(this, ctx)
}

internal open class IdentifierQualifiedImpl(
  public override val _id: String,
  public override val root: Identifier.Symbol,
  public override val steps: List<Identifier.Symbol>,
) : Identifier.Qualified() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(root)
    kids.addAll(steps)
    kids.filterNotNull()
  }


  public override fun copy(root: Identifier.Symbol, steps: List<Identifier.Symbol>):
      Identifier.Qualified = IdentifierQualifiedImpl(_id, root, steps)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitIdentifierQualified(this, ctx)
}

internal open class RexImpl(
  public override val _id: String,
  public override val type: StaticType,
  public override val op: Rex.Op,
) : Rex() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(op)
    kids.filterNotNull()
  }


  public override fun copy(type: StaticType, op: Rex.Op): Rex = RexImpl(_id, type, op)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = visitor.visitRex(this,
      ctx)
}

internal open class RexOpLitImpl(
  public override val _id: String,
  public override val `value`: PartiQLValue,
) : Rex.Op.Lit() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(`value`: PartiQLValue): Rex.Op.Lit = RexOpLitImpl(_id, value)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpLit(this, ctx)
}

internal open class RexOpVarImpl(
  public override val _id: String,
  public override val depth: Int,
  public override val ref: Int,
) : Rex.Op.Var() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(depth: Int, ref: Int): Rex.Op.Var = RexOpVarImpl(_id, depth, ref)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpVar(this, ctx)
}

internal open class RexOpGlobalImpl(
  public override val _id: String,
  public override val ref: Ref,
) : Rex.Op.Global() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(ref)
    kids.filterNotNull()
  }


  public override fun copy(ref: Ref): Rex.Op.Global = RexOpGlobalImpl(_id, ref)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpGlobal(this, ctx)
}

internal open class RexOpPathIndexImpl(
  public override val _id: String,
  public override val root: Rex,
  public override val key: Rex,
) : Rex.Op.Path.Index() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(root)
    kids.add(key)
    kids.filterNotNull()
  }


  public override fun copy(root: Rex, key: Rex): Rex.Op.Path.Index = RexOpPathIndexImpl(_id, root,
      key)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpPathIndex(this, ctx)
}

internal open class RexOpPathKeyImpl(
  public override val _id: String,
  public override val root: Rex,
  public override val key: Rex,
) : Rex.Op.Path.Key() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(root)
    kids.add(key)
    kids.filterNotNull()
  }


  public override fun copy(root: Rex, key: Rex): Rex.Op.Path.Key = RexOpPathKeyImpl(_id, root, key)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpPathKey(this, ctx)
}

internal open class RexOpPathSymbolImpl(
  public override val _id: String,
  public override val root: Rex,
  public override val key: String,
) : Rex.Op.Path.Symbol() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(root)
    kids.filterNotNull()
  }


  public override fun copy(root: Rex, key: String): Rex.Op.Path.Symbol = RexOpPathSymbolImpl(_id,
      root, key)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpPathSymbol(this, ctx)
}

internal open class RexOpCastImpl(
  public override val _id: String,
  public override val cast: Ref.Cast,
  public override val arg: Rex,
) : Rex.Op.Cast() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(cast)
    kids.add(arg)
    kids.filterNotNull()
  }


  public override fun copy(cast: Ref.Cast, arg: Rex): Rex.Op.Cast = RexOpCastImpl(_id, cast, arg)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpCast(this, ctx)
}

internal open class RexOpCallStaticImpl(
  public override val _id: String,
  public override val fn: Ref,
  public override val args: List<Rex>,
) : Rex.Op.Call.Static() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(fn)
    kids.addAll(args)
    kids.filterNotNull()
  }


  public override fun copy(fn: Ref, args: List<Rex>): Rex.Op.Call.Static = RexOpCallStaticImpl(_id,
      fn, args)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpCallStatic(this, ctx)
}

internal open class RexOpCallDynamicImpl(
  public override val _id: String,
  public override val args: List<Rex>,
  public override val candidates: List<Rex.Op.Call.Dynamic.Candidate>,
) : Rex.Op.Call.Dynamic() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(args)
    kids.addAll(candidates)
    kids.filterNotNull()
  }


  public override fun copy(args: List<Rex>, candidates: List<Rex.Op.Call.Dynamic.Candidate>):
      Rex.Op.Call.Dynamic = RexOpCallDynamicImpl(_id, args, candidates)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpCallDynamic(this, ctx)
}

internal open class RexOpCallDynamicCandidateImpl(
  public override val _id: String,
  public override val fn: Ref,
  public override val parameters: List<PartiQLValueType>,
  public override val coercions: List<Ref.Cast?>,
) : Rex.Op.Call.Dynamic.Candidate() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(fn)
    kids.addAll(coercions)
    kids.filterNotNull()
  }


  public override fun copy(
    fn: Ref,
    parameters: List<PartiQLValueType>,
    coercions: List<Ref.Cast?>,
  ): Rex.Op.Call.Dynamic.Candidate = RexOpCallDynamicCandidateImpl(_id, fn, parameters, coercions)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpCallDynamicCandidate(this, ctx)
}

internal open class RexOpCaseImpl(
  public override val _id: String,
  public override val branches: List<Rex.Op.Case.Branch>,
  public override val default: Rex,
) : Rex.Op.Case() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(branches)
    kids.add(default)
    kids.filterNotNull()
  }


  public override fun copy(branches: List<Rex.Op.Case.Branch>, default: Rex): Rex.Op.Case =
      RexOpCaseImpl(_id, branches, default)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpCase(this, ctx)
}

internal open class RexOpCaseBranchImpl(
  public override val _id: String,
  public override val condition: Rex,
  public override val rex: Rex,
) : Rex.Op.Case.Branch() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(condition)
    kids.add(rex)
    kids.filterNotNull()
  }


  public override fun copy(condition: Rex, rex: Rex): Rex.Op.Case.Branch = RexOpCaseBranchImpl(_id,
      condition, rex)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpCaseBranch(this, ctx)
}

internal open class RexOpCollectionImpl(
  public override val _id: String,
  public override val values: List<Rex>,
) : Rex.Op.Collection() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(values)
    kids.filterNotNull()
  }


  public override fun copy(values: List<Rex>): Rex.Op.Collection = RexOpCollectionImpl(_id, values)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpCollection(this, ctx)
}

internal open class RexOpStructImpl(
  public override val _id: String,
  public override val fields: List<Rex.Op.Struct.Field>,
) : Rex.Op.Struct() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(fields)
    kids.filterNotNull()
  }


  public override fun copy(fields: List<Rex.Op.Struct.Field>): Rex.Op.Struct = RexOpStructImpl(_id,
      fields)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpStruct(this, ctx)
}

internal open class RexOpStructFieldImpl(
  public override val _id: String,
  public override val k: Rex,
  public override val v: Rex,
) : Rex.Op.Struct.Field() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(k)
    kids.add(v)
    kids.filterNotNull()
  }


  public override fun copy(k: Rex, v: Rex): Rex.Op.Struct.Field = RexOpStructFieldImpl(_id, k, v)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpStructField(this, ctx)
}

internal open class RexOpPivotImpl(
  public override val _id: String,
  public override val key: Rex,
  public override val `value`: Rex,
  public override val rel: Rel,
) : Rex.Op.Pivot() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(key)
    kids.add(value)
    kids.add(rel)
    kids.filterNotNull()
  }


  public override fun copy(
    key: Rex,
    `value`: Rex,
    rel: Rel,
  ): Rex.Op.Pivot = RexOpPivotImpl(_id, key, value, rel)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpPivot(this, ctx)
}

internal open class RexOpSubqueryImpl(
  public override val _id: String,
  public override val `constructor`: Rex,
  public override val rel: Rel,
  public override val coercion: Rex.Op.Subquery.Coercion,
) : Rex.Op.Subquery() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(constructor)
    kids.add(rel)
    kids.filterNotNull()
  }


  public override fun copy(
    `constructor`: Rex,
    rel: Rel,
    coercion: Rex.Op.Subquery.Coercion,
  ): Rex.Op.Subquery = RexOpSubqueryImpl(_id, constructor, rel, coercion)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpSubquery(this, ctx)
}

internal open class RexOpSelectImpl(
  public override val _id: String,
  public override val `constructor`: Rex,
  public override val rel: Rel,
) : Rex.Op.Select() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(constructor)
    kids.add(rel)
    kids.filterNotNull()
  }


  public override fun copy(`constructor`: Rex, rel: Rel): Rex.Op.Select = RexOpSelectImpl(_id,
      constructor, rel)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpSelect(this, ctx)
}

internal open class RexOpTupleUnionImpl(
  public override val _id: String,
  public override val args: List<Rex>,
) : Rex.Op.TupleUnion() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(args)
    kids.filterNotNull()
  }


  public override fun copy(args: List<Rex>): Rex.Op.TupleUnion = RexOpTupleUnionImpl(_id, args)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpTupleUnion(this, ctx)
}

internal open class RexOpErrImpl(
  public override val _id: String,
  public override val message: String,
) : Rex.Op.Err() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(message: String): Rex.Op.Err = RexOpErrImpl(_id, message)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRexOpErr(this, ctx)
}

internal open class RelImpl(
  public override val _id: String,
  public override val type: Rel.Type,
  public override val op: Rel.Op,
) : Rel() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(type)
    kids.add(op)
    kids.filterNotNull()
  }


  public override fun copy(type: Rel.Type, op: Rel.Op): Rel = RelImpl(_id, type, op)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R = visitor.visitRel(this,
      ctx)
}

internal open class RelTypeImpl(
  public override val _id: String,
  public override val schema: List<Rel.Binding>,
  public override val props: Set<Rel.Prop>,
) : Rel.Type() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.addAll(schema)
    kids.filterNotNull()
  }


  public override fun copy(schema: List<Rel.Binding>, props: Set<Rel.Prop>): Rel.Type =
      RelTypeImpl(_id, schema, props)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelType(this, ctx)
}

internal open class RelOpScanImpl(
  public override val _id: String,
  public override val rex: Rex,
) : Rel.Op.Scan() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(rex)
    kids.filterNotNull()
  }


  public override fun copy(rex: Rex): Rel.Op.Scan = RelOpScanImpl(_id, rex)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpScan(this, ctx)
}

internal open class RelOpScanIndexedImpl(
  public override val _id: String,
  public override val rex: Rex,
) : Rel.Op.ScanIndexed() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(rex)
    kids.filterNotNull()
  }


  public override fun copy(rex: Rex): Rel.Op.ScanIndexed = RelOpScanIndexedImpl(_id, rex)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpScanIndexed(this, ctx)
}

internal open class RelOpUnpivotImpl(
  public override val _id: String,
  public override val rex: Rex,
) : Rel.Op.Unpivot() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(rex)
    kids.filterNotNull()
  }


  public override fun copy(rex: Rex): Rel.Op.Unpivot = RelOpUnpivotImpl(_id, rex)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpUnpivot(this, ctx)
}

internal open class RelOpDistinctImpl(
  public override val _id: String,
  public override val input: Rel,
) : Rel.Op.Distinct() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.filterNotNull()
  }


  public override fun copy(input: Rel): Rel.Op.Distinct = RelOpDistinctImpl(_id, input)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpDistinct(this, ctx)
}

internal open class RelOpFilterImpl(
  public override val _id: String,
  public override val input: Rel,
  public override val predicate: Rex,
) : Rel.Op.Filter() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.add(predicate)
    kids.filterNotNull()
  }


  public override fun copy(input: Rel, predicate: Rex): Rel.Op.Filter = RelOpFilterImpl(_id, input,
      predicate)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpFilter(this, ctx)
}

internal open class RelOpSortImpl(
  public override val _id: String,
  public override val input: Rel,
  public override val specs: List<Rel.Op.Sort.Spec>,
) : Rel.Op.Sort() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.addAll(specs)
    kids.filterNotNull()
  }


  public override fun copy(input: Rel, specs: List<Rel.Op.Sort.Spec>): Rel.Op.Sort =
      RelOpSortImpl(_id, input, specs)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpSort(this, ctx)
}

internal open class RelOpSortSpecImpl(
  public override val _id: String,
  public override val rex: Rex,
  public override val order: Rel.Op.Sort.Order,
) : Rel.Op.Sort.Spec() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(rex)
    kids.filterNotNull()
  }


  public override fun copy(rex: Rex, order: Rel.Op.Sort.Order): Rel.Op.Sort.Spec =
      RelOpSortSpecImpl(_id, rex, order)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpSortSpec(this, ctx)
}

internal open class RelOpUnionImpl(
  public override val _id: String,
  public override val lhs: Rel,
  public override val rhs: Rel,
) : Rel.Op.Union() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(lhs)
    kids.add(rhs)
    kids.filterNotNull()
  }


  public override fun copy(lhs: Rel, rhs: Rel): Rel.Op.Union = RelOpUnionImpl(_id, lhs, rhs)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpUnion(this, ctx)
}

internal open class RelOpIntersectImpl(
  public override val _id: String,
  public override val lhs: Rel,
  public override val rhs: Rel,
) : Rel.Op.Intersect() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(lhs)
    kids.add(rhs)
    kids.filterNotNull()
  }


  public override fun copy(lhs: Rel, rhs: Rel): Rel.Op.Intersect = RelOpIntersectImpl(_id, lhs, rhs)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpIntersect(this, ctx)
}

internal open class RelOpExceptImpl(
  public override val _id: String,
  public override val lhs: Rel,
  public override val rhs: Rel,
) : Rel.Op.Except() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(lhs)
    kids.add(rhs)
    kids.filterNotNull()
  }


  public override fun copy(lhs: Rel, rhs: Rel): Rel.Op.Except = RelOpExceptImpl(_id, lhs, rhs)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcept(this, ctx)
}

internal open class RelOpLimitImpl(
  public override val _id: String,
  public override val input: Rel,
  public override val limit: Rex,
) : Rel.Op.Limit() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.add(limit)
    kids.filterNotNull()
  }


  public override fun copy(input: Rel, limit: Rex): Rel.Op.Limit = RelOpLimitImpl(_id, input, limit)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpLimit(this, ctx)
}

internal open class RelOpOffsetImpl(
  public override val _id: String,
  public override val input: Rel,
  public override val offset: Rex,
) : Rel.Op.Offset() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.add(offset)
    kids.filterNotNull()
  }


  public override fun copy(input: Rel, offset: Rex): Rel.Op.Offset = RelOpOffsetImpl(_id, input,
      offset)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpOffset(this, ctx)
}

internal open class RelOpProjectImpl(
  public override val _id: String,
  public override val input: Rel,
  public override val projections: List<Rex>,
) : Rel.Op.Project() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.addAll(projections)
    kids.filterNotNull()
  }


  public override fun copy(input: Rel, projections: List<Rex>): Rel.Op.Project =
      RelOpProjectImpl(_id, input, projections)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpProject(this, ctx)
}

internal open class RelOpJoinImpl(
  public override val _id: String,
  public override val lhs: Rel,
  public override val rhs: Rel,
  public override val rex: Rex,
  public override val type: Rel.Op.Join.Type,
) : Rel.Op.Join() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(lhs)
    kids.add(rhs)
    kids.add(rex)
    kids.filterNotNull()
  }


  public override fun copy(
    lhs: Rel,
    rhs: Rel,
    rex: Rex,
    type: Rel.Op.Join.Type,
  ): Rel.Op.Join = RelOpJoinImpl(_id, lhs, rhs, rex, type)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpJoin(this, ctx)
}

internal open class RelOpAggregateImpl(
  public override val _id: String,
  public override val input: Rel,
  public override val strategy: Rel.Op.Aggregate.Strategy,
  public override val calls: List<Rel.Op.Aggregate.Call>,
  public override val groups: List<Rex>,
) : Rel.Op.Aggregate() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.addAll(calls)
    kids.addAll(groups)
    kids.filterNotNull()
  }


  public override fun copy(
    input: Rel,
    strategy: Rel.Op.Aggregate.Strategy,
    calls: List<Rel.Op.Aggregate.Call>,
    groups: List<Rex>,
  ): Rel.Op.Aggregate = RelOpAggregateImpl(_id, input, strategy, calls, groups)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpAggregate(this, ctx)
}

internal open class RelOpAggregateCallImpl(
  public override val _id: String,
  public override val agg: Ref,
  public override val setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier,
  public override val args: List<Rex>,
) : Rel.Op.Aggregate.Call() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(agg)
    kids.addAll(args)
    kids.filterNotNull()
  }


  public override fun copy(
    agg: Ref,
    setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier,
    args: List<Rex>,
  ): Rel.Op.Aggregate.Call = RelOpAggregateCallImpl(_id, agg, setQuantifier, args)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpAggregateCall(this, ctx)
}

internal open class RelOpExcludeImpl(
  public override val _id: String,
  public override val input: Rel,
  public override val paths: List<Rel.Op.Exclude.Path>,
) : Rel.Op.Exclude() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(input)
    kids.addAll(paths)
    kids.filterNotNull()
  }


  public override fun copy(input: Rel, paths: List<Rel.Op.Exclude.Path>): Rel.Op.Exclude =
      RelOpExcludeImpl(_id, input, paths)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExclude(this, ctx)
}

internal open class RelOpExcludePathImpl(
  public override val _id: String,
  public override val root: Rex.Op.Var,
  public override val steps: List<Rel.Op.Exclude.Step>,
) : Rel.Op.Exclude.Path() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(root)
    kids.addAll(steps)
    kids.filterNotNull()
  }


  public override fun copy(root: Rex.Op.Var, steps: List<Rel.Op.Exclude.Step>): Rel.Op.Exclude.Path
      = RelOpExcludePathImpl(_id, root, steps)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcludePath(this, ctx)
}

internal open class RelOpExcludeStepImpl(
  public override val _id: String,
  public override val type: Rel.Op.Exclude.Type,
  public override val substeps: List<Rel.Op.Exclude.Step>,
) : Rel.Op.Exclude.Step() {
  public override val children: List<PlanNode> by lazy {
    val kids = mutableListOf<PlanNode?>()
    kids.add(type)
    kids.addAll(substeps)
    kids.filterNotNull()
  }


  public override fun copy(type: Rel.Op.Exclude.Type, substeps: List<Rel.Op.Exclude.Step>):
      Rel.Op.Exclude.Step = RelOpExcludeStepImpl(_id, type, substeps)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcludeStep(this, ctx)
}

internal open class RelOpExcludeTypeStructSymbolImpl(
  public override val _id: String,
  public override val symbol: String,
) : Rel.Op.Exclude.Type.StructSymbol() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(symbol: String): Rel.Op.Exclude.Type.StructSymbol =
      RelOpExcludeTypeStructSymbolImpl(_id, symbol)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcludeTypeStructSymbol(this, ctx)
}

internal open class RelOpExcludeTypeStructKeyImpl(
  public override val _id: String,
  public override val key: String,
) : Rel.Op.Exclude.Type.StructKey() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(key: String): Rel.Op.Exclude.Type.StructKey =
      RelOpExcludeTypeStructKeyImpl(_id, key)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcludeTypeStructKey(this, ctx)
}

internal open class RelOpExcludeTypeCollIndexImpl(
  public override val _id: String,
  public override val index: Int,
) : Rel.Op.Exclude.Type.CollIndex() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(index: Int): Rel.Op.Exclude.Type.CollIndex =
      RelOpExcludeTypeCollIndexImpl(_id, index)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcludeTypeCollIndex(this, ctx)
}

internal open class RelOpExcludeTypeStructWildcardImpl(
  public override val _id: String,
) : Rel.Op.Exclude.Type.StructWildcard() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(): Rel.Op.Exclude.Type.StructWildcard =
      RelOpExcludeTypeStructWildcardImpl(_id)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcludeTypeStructWildcard(this, ctx)
}

internal open class RelOpExcludeTypeCollWildcardImpl(
  public override val _id: String,
) : Rel.Op.Exclude.Type.CollWildcard() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(): Rel.Op.Exclude.Type.CollWildcard =
      RelOpExcludeTypeCollWildcardImpl(_id)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpExcludeTypeCollWildcard(this, ctx)
}

internal open class RelOpErrImpl(
  public override val _id: String,
  public override val message: String,
) : Rel.Op.Err() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(message: String): Rel.Op.Err = RelOpErrImpl(_id, message)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelOpErr(this, ctx)
}

internal open class RelBindingImpl(
  public override val _id: String,
  public override val name: String,
  public override val type: StaticType,
) : Rel.Binding() {
  public override val children: List<PlanNode> = emptyList()

  public override fun copy(name: String, type: StaticType): Rel.Binding = RelBindingImpl(_id, name,
      type)

  public override fun <R, C> accept(visitor: PlanVisitor<R, C>, ctx: C): R =
      visitor.visitRelBinding(this, ctx)
}
