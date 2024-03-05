@file:Suppress("UNUSED_PARAMETER")
@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.builder

import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.collections.MutableSet
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
import org.partiql.types.StaticType

public fun <T : PlanNode> plan(factory: PlanFactory = PlanFactory.DEFAULT,
    block: PlanBuilder.() -> T) = PlanBuilder(factory).block()

public class PlanBuilder(
  private val factory: PlanFactory = PlanFactory.DEFAULT,
) {
  public fun partiQLPlan(
    catalogs: MutableList<Catalog> = mutableListOf(),
    statement: Statement? = null,
    block: PartiQlPlanBuilder.() -> Unit = {},
  ): PartiQLPlan {
    val builder = PartiQlPlanBuilder(catalogs, statement)
    builder.block()
    return builder.build(factory)
  }

  public fun catalog(
    name: String? = null,
    items: MutableList<Catalog.Item> = mutableListOf(),
    block: CatalogBuilder.() -> Unit = {},
  ): Catalog {
    val builder = CatalogBuilder(name, items)
    builder.block()
    return builder.build(factory)
  }

  public fun catalogItemValue(
    path: MutableList<String> = mutableListOf(),
    type: StaticType? = null,
    block: CatalogItemValueBuilder.() -> Unit = {},
  ): Catalog.Item.Value {
    val builder = CatalogItemValueBuilder(path, type)
    builder.block()
    return builder.build(factory)
  }

  public fun catalogItemFn(
    path: MutableList<String> = mutableListOf(),
    specific: String? = null,
    block: CatalogItemFnBuilder.() -> Unit = {},
  ): Catalog.Item.Fn {
    val builder = CatalogItemFnBuilder(path, specific)
    builder.block()
    return builder.build(factory)
  }

  public fun catalogItemAgg(
    path: MutableList<String> = mutableListOf(),
    specific: String? = null,
    block: CatalogItemAggBuilder.() -> Unit = {},
  ): Catalog.Item.Agg {
    val builder = CatalogItemAggBuilder(path, specific)
    builder.block()
    return builder.build(factory)
  }

  public fun ref(
    catalog: Int? = null,
    symbol: Int? = null,
    block: RefBuilder.() -> Unit = {},
  ): Ref {
    val builder = RefBuilder(catalog, symbol)
    builder.block()
    return builder.build(factory)
  }

  public fun refCast(
    input: PartiQLValueType? = null,
    target: PartiQLValueType? = null,
    block: RefCastBuilder.() -> Unit = {},
  ): Ref.Cast {
    val builder = RefCastBuilder(input, target)
    builder.block()
    return builder.build(factory)
  }

  public fun statementQuery(root: Rex? = null, block: StatementQueryBuilder.() -> Unit = {}):
      Statement.Query {
    val builder = StatementQueryBuilder(root)
    builder.block()
    return builder.build(factory)
  }

  public fun identifierSymbol(
    symbol: String? = null,
    caseSensitivity: Identifier.CaseSensitivity? = null,
    block: IdentifierSymbolBuilder.() -> Unit = {},
  ): Identifier.Symbol {
    val builder = IdentifierSymbolBuilder(symbol, caseSensitivity)
    builder.block()
    return builder.build(factory)
  }

  public fun identifierQualified(
    root: Identifier.Symbol? = null,
    steps: MutableList<Identifier.Symbol> = mutableListOf(),
    block: IdentifierQualifiedBuilder.() -> Unit = {},
  ): Identifier.Qualified {
    val builder = IdentifierQualifiedBuilder(root, steps)
    builder.block()
    return builder.build(factory)
  }

  public fun rex(
    type: StaticType? = null,
    op: Rex.Op? = null,
    block: RexBuilder.() -> Unit = {},
  ): Rex {
    val builder = RexBuilder(type, op)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpLit(`value`: PartiQLValue? = null, block: RexOpLitBuilder.() -> Unit = {}):
      Rex.Op.Lit {
    val builder = RexOpLitBuilder(value)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpVar(
    depth: Int? = null,
    ref: Int? = null,
    block: RexOpVarBuilder.() -> Unit = {},
  ): Rex.Op.Var {
    val builder = RexOpVarBuilder(depth, ref)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpGlobal(ref: Ref? = null, block: RexOpGlobalBuilder.() -> Unit = {}):
      Rex.Op.Global {
    val builder = RexOpGlobalBuilder(ref)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpPathIndex(
    root: Rex? = null,
    key: Rex? = null,
    block: RexOpPathIndexBuilder.() -> Unit = {},
  ): Rex.Op.Path.Index {
    val builder = RexOpPathIndexBuilder(root, key)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpPathKey(
    root: Rex? = null,
    key: Rex? = null,
    block: RexOpPathKeyBuilder.() -> Unit = {},
  ): Rex.Op.Path.Key {
    val builder = RexOpPathKeyBuilder(root, key)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpPathSymbol(
    root: Rex? = null,
    key: String? = null,
    block: RexOpPathSymbolBuilder.() -> Unit = {},
  ): Rex.Op.Path.Symbol {
    val builder = RexOpPathSymbolBuilder(root, key)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpCast(
    cast: Ref.Cast? = null,
    arg: Rex? = null,
    block: RexOpCastBuilder.() -> Unit = {},
  ): Rex.Op.Cast {
    val builder = RexOpCastBuilder(cast, arg)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpCallStatic(
    fn: Ref? = null,
    args: MutableList<Rex> = mutableListOf(),
    block: RexOpCallStaticBuilder.() -> Unit = {},
  ): Rex.Op.Call.Static {
    val builder = RexOpCallStaticBuilder(fn, args)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpCallDynamic(
    args: MutableList<Rex> = mutableListOf(),
    candidates: MutableList<Rex.Op.Call.Dynamic.Candidate> = mutableListOf(),
    block: RexOpCallDynamicBuilder.() -> Unit = {},
  ): Rex.Op.Call.Dynamic {
    val builder = RexOpCallDynamicBuilder(args, candidates)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpCallDynamicCandidate(
    fn: Ref? = null,
    parameters: MutableList<PartiQLValueType> = mutableListOf(),
    coercions: MutableList<Ref.Cast?> = mutableListOf(),
    block: RexOpCallDynamicCandidateBuilder.() -> Unit = {},
  ): Rex.Op.Call.Dynamic.Candidate {
    val builder = RexOpCallDynamicCandidateBuilder(fn, parameters, coercions)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpCase(
    branches: MutableList<Rex.Op.Case.Branch> = mutableListOf(),
    default: Rex? = null,
    block: RexOpCaseBuilder.() -> Unit = {},
  ): Rex.Op.Case {
    val builder = RexOpCaseBuilder(branches, default)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpCaseBranch(
    condition: Rex? = null,
    rex: Rex? = null,
    block: RexOpCaseBranchBuilder.() -> Unit = {},
  ): Rex.Op.Case.Branch {
    val builder = RexOpCaseBranchBuilder(condition, rex)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpCollection(values: MutableList<Rex> = mutableListOf(),
      block: RexOpCollectionBuilder.() -> Unit = {}): Rex.Op.Collection {
    val builder = RexOpCollectionBuilder(values)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpStruct(fields: MutableList<Rex.Op.Struct.Field> = mutableListOf(),
      block: RexOpStructBuilder.() -> Unit = {}): Rex.Op.Struct {
    val builder = RexOpStructBuilder(fields)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpStructField(
    k: Rex? = null,
    v: Rex? = null,
    block: RexOpStructFieldBuilder.() -> Unit = {},
  ): Rex.Op.Struct.Field {
    val builder = RexOpStructFieldBuilder(k, v)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpPivot(
    key: Rex? = null,
    `value`: Rex? = null,
    rel: Rel? = null,
    block: RexOpPivotBuilder.() -> Unit = {},
  ): Rex.Op.Pivot {
    val builder = RexOpPivotBuilder(key, value, rel)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpSubquery(
    `constructor`: Rex? = null,
    rel: Rel? = null,
    coercion: Rex.Op.Subquery.Coercion? = null,
    block: RexOpSubqueryBuilder.() -> Unit = {},
  ): Rex.Op.Subquery {
    val builder = RexOpSubqueryBuilder(constructor, rel, coercion)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpSelect(
    `constructor`: Rex? = null,
    rel: Rel? = null,
    block: RexOpSelectBuilder.() -> Unit = {},
  ): Rex.Op.Select {
    val builder = RexOpSelectBuilder(constructor, rel)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpTupleUnion(args: MutableList<Rex> = mutableListOf(),
      block: RexOpTupleUnionBuilder.() -> Unit = {}): Rex.Op.TupleUnion {
    val builder = RexOpTupleUnionBuilder(args)
    builder.block()
    return builder.build(factory)
  }

  public fun rexOpErr(message: String? = null, block: RexOpErrBuilder.() -> Unit = {}): Rex.Op.Err {
    val builder = RexOpErrBuilder(message)
    builder.block()
    return builder.build(factory)
  }

  public fun rel(
    type: Rel.Type? = null,
    op: Rel.Op? = null,
    block: RelBuilder.() -> Unit = {},
  ): Rel {
    val builder = RelBuilder(type, op)
    builder.block()
    return builder.build(factory)
  }

  public fun relType(
    schema: MutableList<Rel.Binding> = mutableListOf(),
    props: MutableSet<Rel.Prop> = mutableSetOf(),
    block: RelTypeBuilder.() -> Unit = {},
  ): Rel.Type {
    val builder = RelTypeBuilder(schema, props)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpScan(rex: Rex? = null, block: RelOpScanBuilder.() -> Unit = {}): Rel.Op.Scan {
    val builder = RelOpScanBuilder(rex)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpScanIndexed(rex: Rex? = null, block: RelOpScanIndexedBuilder.() -> Unit = {}):
      Rel.Op.ScanIndexed {
    val builder = RelOpScanIndexedBuilder(rex)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpUnpivot(rex: Rex? = null, block: RelOpUnpivotBuilder.() -> Unit = {}):
      Rel.Op.Unpivot {
    val builder = RelOpUnpivotBuilder(rex)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpDistinct(input: Rel? = null, block: RelOpDistinctBuilder.() -> Unit = {}):
      Rel.Op.Distinct {
    val builder = RelOpDistinctBuilder(input)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpFilter(
    input: Rel? = null,
    predicate: Rex? = null,
    block: RelOpFilterBuilder.() -> Unit = {},
  ): Rel.Op.Filter {
    val builder = RelOpFilterBuilder(input, predicate)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpSort(
    input: Rel? = null,
    specs: MutableList<Rel.Op.Sort.Spec> = mutableListOf(),
    block: RelOpSortBuilder.() -> Unit = {},
  ): Rel.Op.Sort {
    val builder = RelOpSortBuilder(input, specs)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpSortSpec(
    rex: Rex? = null,
    order: Rel.Op.Sort.Order? = null,
    block: RelOpSortSpecBuilder.() -> Unit = {},
  ): Rel.Op.Sort.Spec {
    val builder = RelOpSortSpecBuilder(rex, order)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpUnion(
    lhs: Rel? = null,
    rhs: Rel? = null,
    block: RelOpUnionBuilder.() -> Unit = {},
  ): Rel.Op.Union {
    val builder = RelOpUnionBuilder(lhs, rhs)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpIntersect(
    lhs: Rel? = null,
    rhs: Rel? = null,
    block: RelOpIntersectBuilder.() -> Unit = {},
  ): Rel.Op.Intersect {
    val builder = RelOpIntersectBuilder(lhs, rhs)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcept(
    lhs: Rel? = null,
    rhs: Rel? = null,
    block: RelOpExceptBuilder.() -> Unit = {},
  ): Rel.Op.Except {
    val builder = RelOpExceptBuilder(lhs, rhs)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpLimit(
    input: Rel? = null,
    limit: Rex? = null,
    block: RelOpLimitBuilder.() -> Unit = {},
  ): Rel.Op.Limit {
    val builder = RelOpLimitBuilder(input, limit)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpOffset(
    input: Rel? = null,
    offset: Rex? = null,
    block: RelOpOffsetBuilder.() -> Unit = {},
  ): Rel.Op.Offset {
    val builder = RelOpOffsetBuilder(input, offset)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpProject(
    input: Rel? = null,
    projections: MutableList<Rex> = mutableListOf(),
    block: RelOpProjectBuilder.() -> Unit = {},
  ): Rel.Op.Project {
    val builder = RelOpProjectBuilder(input, projections)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpJoin(
    lhs: Rel? = null,
    rhs: Rel? = null,
    rex: Rex? = null,
    type: Rel.Op.Join.Type? = null,
    block: RelOpJoinBuilder.() -> Unit = {},
  ): Rel.Op.Join {
    val builder = RelOpJoinBuilder(lhs, rhs, rex, type)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpAggregate(
    input: Rel? = null,
    strategy: Rel.Op.Aggregate.Strategy? = null,
    calls: MutableList<Rel.Op.Aggregate.Call> = mutableListOf(),
    groups: MutableList<Rex> = mutableListOf(),
    block: RelOpAggregateBuilder.() -> Unit = {},
  ): Rel.Op.Aggregate {
    val builder = RelOpAggregateBuilder(input, strategy, calls, groups)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpAggregateCall(
    agg: Ref? = null,
    setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier? = null,
    args: MutableList<Rex> = mutableListOf(),
    block: RelOpAggregateCallBuilder.() -> Unit = {},
  ): Rel.Op.Aggregate.Call {
    val builder = RelOpAggregateCallBuilder(agg, setQuantifier, args)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExclude(
    input: Rel? = null,
    paths: MutableList<Rel.Op.Exclude.Path> = mutableListOf(),
    block: RelOpExcludeBuilder.() -> Unit = {},
  ): Rel.Op.Exclude {
    val builder = RelOpExcludeBuilder(input, paths)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcludePath(
    root: Rex.Op.Var? = null,
    steps: MutableList<Rel.Op.Exclude.Step> = mutableListOf(),
    block: RelOpExcludePathBuilder.() -> Unit = {},
  ): Rel.Op.Exclude.Path {
    val builder = RelOpExcludePathBuilder(root, steps)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcludeStep(
    type: Rel.Op.Exclude.Type? = null,
    substeps: MutableList<Rel.Op.Exclude.Step> = mutableListOf(),
    block: RelOpExcludeStepBuilder.() -> Unit = {},
  ): Rel.Op.Exclude.Step {
    val builder = RelOpExcludeStepBuilder(type, substeps)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcludeTypeStructSymbol(symbol: String? = null,
      block: RelOpExcludeTypeStructSymbolBuilder.() -> Unit = {}):
      Rel.Op.Exclude.Type.StructSymbol {
    val builder = RelOpExcludeTypeStructSymbolBuilder(symbol)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcludeTypeStructKey(key: String? = null,
      block: RelOpExcludeTypeStructKeyBuilder.() -> Unit = {}): Rel.Op.Exclude.Type.StructKey {
    val builder = RelOpExcludeTypeStructKeyBuilder(key)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcludeTypeCollIndex(index: Int? = null,
      block: RelOpExcludeTypeCollIndexBuilder.() -> Unit = {}): Rel.Op.Exclude.Type.CollIndex {
    val builder = RelOpExcludeTypeCollIndexBuilder(index)
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcludeTypeStructWildcard(block: RelOpExcludeTypeStructWildcardBuilder.() -> Unit
      = {}): Rel.Op.Exclude.Type.StructWildcard {
    val builder = RelOpExcludeTypeStructWildcardBuilder()
    builder.block()
    return builder.build(factory)
  }

  public fun relOpExcludeTypeCollWildcard(block: RelOpExcludeTypeCollWildcardBuilder.() -> Unit =
      {}): Rel.Op.Exclude.Type.CollWildcard {
    val builder = RelOpExcludeTypeCollWildcardBuilder()
    builder.block()
    return builder.build(factory)
  }

  public fun relOpErr(message: String? = null, block: RelOpErrBuilder.() -> Unit = {}): Rel.Op.Err {
    val builder = RelOpErrBuilder(message)
    builder.block()
    return builder.build(factory)
  }

  public fun relBinding(
    name: String? = null,
    type: StaticType? = null,
    block: RelBindingBuilder.() -> Unit = {},
  ): Rel.Binding {
    val builder = RelBindingBuilder(name, type)
    builder.block()
    return builder.build(factory)
  }
}
