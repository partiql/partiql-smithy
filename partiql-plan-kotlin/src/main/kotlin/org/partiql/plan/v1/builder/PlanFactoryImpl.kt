@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.builder

import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.random.Random
import org.partiql.`value`.PartiQLValue
import org.partiql.`value`.PartiQLValueExperimental
import org.partiql.`value`.PartiQLValueType
import org.partiql.plan.v1.Catalog
import org.partiql.plan.v1.Identifier
import org.partiql.plan.v1.PartiQLPlan
import org.partiql.plan.v1.Ref
import org.partiql.plan.v1.Rel
import org.partiql.plan.v1.Rex
import org.partiql.plan.v1.Statement
import org.partiql.plan.v1.impl.CatalogImpl
import org.partiql.plan.v1.impl.CatalogItemAggImpl
import org.partiql.plan.v1.impl.CatalogItemFnImpl
import org.partiql.plan.v1.impl.CatalogItemValueImpl
import org.partiql.plan.v1.impl.IdentifierQualifiedImpl
import org.partiql.plan.v1.impl.IdentifierSymbolImpl
import org.partiql.plan.v1.impl.PartiQLPlanImpl
import org.partiql.plan.v1.impl.RefCastImpl
import org.partiql.plan.v1.impl.RefImpl
import org.partiql.plan.v1.impl.RelBindingImpl
import org.partiql.plan.v1.impl.RelImpl
import org.partiql.plan.v1.impl.RelOpAggregateCallImpl
import org.partiql.plan.v1.impl.RelOpAggregateImpl
import org.partiql.plan.v1.impl.RelOpDistinctImpl
import org.partiql.plan.v1.impl.RelOpErrImpl
import org.partiql.plan.v1.impl.RelOpExceptImpl
import org.partiql.plan.v1.impl.RelOpExcludeImpl
import org.partiql.plan.v1.impl.RelOpExcludePathImpl
import org.partiql.plan.v1.impl.RelOpExcludeStepImpl
import org.partiql.plan.v1.impl.RelOpExcludeTypeCollIndexImpl
import org.partiql.plan.v1.impl.RelOpExcludeTypeCollWildcardImpl
import org.partiql.plan.v1.impl.RelOpExcludeTypeStructKeyImpl
import org.partiql.plan.v1.impl.RelOpExcludeTypeStructSymbolImpl
import org.partiql.plan.v1.impl.RelOpExcludeTypeStructWildcardImpl
import org.partiql.plan.v1.impl.RelOpFilterImpl
import org.partiql.plan.v1.impl.RelOpIntersectImpl
import org.partiql.plan.v1.impl.RelOpJoinImpl
import org.partiql.plan.v1.impl.RelOpLimitImpl
import org.partiql.plan.v1.impl.RelOpOffsetImpl
import org.partiql.plan.v1.impl.RelOpProjectImpl
import org.partiql.plan.v1.impl.RelOpScanImpl
import org.partiql.plan.v1.impl.RelOpScanIndexedImpl
import org.partiql.plan.v1.impl.RelOpSortImpl
import org.partiql.plan.v1.impl.RelOpSortSpecImpl
import org.partiql.plan.v1.impl.RelOpUnionImpl
import org.partiql.plan.v1.impl.RelOpUnpivotImpl
import org.partiql.plan.v1.impl.RelTypeImpl
import org.partiql.plan.v1.impl.RexImpl
import org.partiql.plan.v1.impl.RexOpCallDynamicCandidateImpl
import org.partiql.plan.v1.impl.RexOpCallDynamicImpl
import org.partiql.plan.v1.impl.RexOpCallStaticImpl
import org.partiql.plan.v1.impl.RexOpCaseBranchImpl
import org.partiql.plan.v1.impl.RexOpCaseImpl
import org.partiql.plan.v1.impl.RexOpCastImpl
import org.partiql.plan.v1.impl.RexOpCollectionImpl
import org.partiql.plan.v1.impl.RexOpErrImpl
import org.partiql.plan.v1.impl.RexOpGlobalImpl
import org.partiql.plan.v1.impl.RexOpLitImpl
import org.partiql.plan.v1.impl.RexOpPathIndexImpl
import org.partiql.plan.v1.impl.RexOpPathKeyImpl
import org.partiql.plan.v1.impl.RexOpPathSymbolImpl
import org.partiql.plan.v1.impl.RexOpPivotImpl
import org.partiql.plan.v1.impl.RexOpSelectImpl
import org.partiql.plan.v1.impl.RexOpStructFieldImpl
import org.partiql.plan.v1.impl.RexOpStructImpl
import org.partiql.plan.v1.impl.RexOpSubqueryImpl
import org.partiql.plan.v1.impl.RexOpTupleUnionImpl
import org.partiql.plan.v1.impl.RexOpVarImpl
import org.partiql.plan.v1.impl.StatementQueryImpl
import org.partiql.types.StaticType

public open class PlanFactoryImpl : PlanFactory {
  public override val _id: () -> String = { """Plan-${"%08x".format(Random.nextInt())}""" }

  public override fun partiQLPlan(catalogs: List<Catalog>, statement: Statement): PartiQLPlan =
      PartiQLPlanImpl(_id(), catalogs, statement)

  public override fun catalog(name: String, items: List<Catalog.Item>): Catalog = CatalogImpl(_id(),
      name, items)

  public override fun catalogItemValue(path: List<String>, type: StaticType): Catalog.Item.Value =
      CatalogItemValueImpl(_id(), path, type)

  public override fun catalogItemFn(path: List<String>, specific: String): Catalog.Item.Fn =
      CatalogItemFnImpl(_id(), path, specific)

  public override fun catalogItemAgg(path: List<String>, specific: String): Catalog.Item.Agg =
      CatalogItemAggImpl(_id(), path, specific)

  public override fun ref(catalog: Int, symbol: Int): Ref = RefImpl(_id(), catalog, symbol)

  public override fun refCast(input: PartiQLValueType, target: PartiQLValueType): Ref.Cast =
      RefCastImpl(_id(), input, target)

  public override fun statementQuery(root: Rex): Statement.Query = StatementQueryImpl(_id(), root)

  public override fun identifierSymbol(symbol: String, caseSensitivity: Identifier.CaseSensitivity):
      Identifier.Symbol = IdentifierSymbolImpl(_id(), symbol, caseSensitivity)

  public override fun identifierQualified(root: Identifier.Symbol, steps: List<Identifier.Symbol>):
      Identifier.Qualified = IdentifierQualifiedImpl(_id(), root, steps)

  public override fun rex(type: StaticType, op: Rex.Op): Rex = RexImpl(_id(), type, op)

  public override fun rexOpLit(`value`: PartiQLValue): Rex.Op.Lit = RexOpLitImpl(_id(), value)

  public override fun rexOpVar(depth: Int, ref: Int): Rex.Op.Var = RexOpVarImpl(_id(), depth, ref)

  public override fun rexOpGlobal(ref: Ref): Rex.Op.Global = RexOpGlobalImpl(_id(), ref)

  public override fun rexOpPathIndex(root: Rex, key: Rex): Rex.Op.Path.Index =
      RexOpPathIndexImpl(_id(), root, key)

  public override fun rexOpPathKey(root: Rex, key: Rex): Rex.Op.Path.Key = RexOpPathKeyImpl(_id(),
      root, key)

  public override fun rexOpPathSymbol(root: Rex, key: String): Rex.Op.Path.Symbol =
      RexOpPathSymbolImpl(_id(), root, key)

  public override fun rexOpCast(cast: Ref.Cast, arg: Rex): Rex.Op.Cast = RexOpCastImpl(_id(), cast,
      arg)

  public override fun rexOpCallStatic(fn: Ref, args: List<Rex>): Rex.Op.Call.Static =
      RexOpCallStaticImpl(_id(), fn, args)

  public override fun rexOpCallDynamic(args: List<Rex>,
      candidates: List<Rex.Op.Call.Dynamic.Candidate>): Rex.Op.Call.Dynamic =
      RexOpCallDynamicImpl(_id(), args, candidates)

  public override fun rexOpCallDynamicCandidate(
    fn: Ref,
    parameters: List<PartiQLValueType>,
    coercions: List<Ref.Cast?>,
  ): Rex.Op.Call.Dynamic.Candidate = RexOpCallDynamicCandidateImpl(_id(), fn, parameters, coercions)

  public override fun rexOpCase(branches: List<Rex.Op.Case.Branch>, default: Rex): Rex.Op.Case =
      RexOpCaseImpl(_id(), branches, default)

  public override fun rexOpCaseBranch(condition: Rex, rex: Rex): Rex.Op.Case.Branch =
      RexOpCaseBranchImpl(_id(), condition, rex)

  public override fun rexOpCollection(values: List<Rex>): Rex.Op.Collection =
      RexOpCollectionImpl(_id(), values)

  public override fun rexOpStruct(fields: List<Rex.Op.Struct.Field>): Rex.Op.Struct =
      RexOpStructImpl(_id(), fields)

  public override fun rexOpStructField(k: Rex, v: Rex): Rex.Op.Struct.Field =
      RexOpStructFieldImpl(_id(), k, v)

  public override fun rexOpPivot(
    key: Rex,
    `value`: Rex,
    rel: Rel,
  ): Rex.Op.Pivot = RexOpPivotImpl(_id(), key, value, rel)

  public override fun rexOpSubquery(
    `constructor`: Rex,
    rel: Rel,
    coercion: Rex.Op.Subquery.Coercion,
  ): Rex.Op.Subquery = RexOpSubqueryImpl(_id(), constructor, rel, coercion)

  public override fun rexOpSelect(`constructor`: Rex, rel: Rel): Rex.Op.Select =
      RexOpSelectImpl(_id(), constructor, rel)

  public override fun rexOpTupleUnion(args: List<Rex>): Rex.Op.TupleUnion =
      RexOpTupleUnionImpl(_id(), args)

  public override fun rexOpErr(message: String): Rex.Op.Err = RexOpErrImpl(_id(), message)

  public override fun rel(type: Rel.Type, op: Rel.Op): Rel = RelImpl(_id(), type, op)

  public override fun relType(schema: List<Rel.Binding>, props: Set<Rel.Prop>): Rel.Type =
      RelTypeImpl(_id(), schema, props)

  public override fun relOpScan(rex: Rex): Rel.Op.Scan = RelOpScanImpl(_id(), rex)

  public override fun relOpScanIndexed(rex: Rex): Rel.Op.ScanIndexed = RelOpScanIndexedImpl(_id(),
      rex)

  public override fun relOpUnpivot(rex: Rex): Rel.Op.Unpivot = RelOpUnpivotImpl(_id(), rex)

  public override fun relOpDistinct(input: Rel): Rel.Op.Distinct = RelOpDistinctImpl(_id(), input)

  public override fun relOpFilter(input: Rel, predicate: Rex): Rel.Op.Filter =
      RelOpFilterImpl(_id(), input, predicate)

  public override fun relOpSort(input: Rel, specs: List<Rel.Op.Sort.Spec>): Rel.Op.Sort =
      RelOpSortImpl(_id(), input, specs)

  public override fun relOpSortSpec(rex: Rex, order: Rel.Op.Sort.Order): Rel.Op.Sort.Spec =
      RelOpSortSpecImpl(_id(), rex, order)

  public override fun relOpUnion(lhs: Rel, rhs: Rel): Rel.Op.Union = RelOpUnionImpl(_id(), lhs, rhs)

  public override fun relOpIntersect(lhs: Rel, rhs: Rel): Rel.Op.Intersect =
      RelOpIntersectImpl(_id(), lhs, rhs)

  public override fun relOpExcept(lhs: Rel, rhs: Rel): Rel.Op.Except = RelOpExceptImpl(_id(), lhs,
      rhs)

  public override fun relOpLimit(input: Rel, limit: Rex): Rel.Op.Limit = RelOpLimitImpl(_id(),
      input, limit)

  public override fun relOpOffset(input: Rel, offset: Rex): Rel.Op.Offset = RelOpOffsetImpl(_id(),
      input, offset)

  public override fun relOpProject(input: Rel, projections: List<Rex>): Rel.Op.Project =
      RelOpProjectImpl(_id(), input, projections)

  public override fun relOpJoin(
    lhs: Rel,
    rhs: Rel,
    rex: Rex,
    type: Rel.Op.Join.Type,
  ): Rel.Op.Join = RelOpJoinImpl(_id(), lhs, rhs, rex, type)

  public override fun relOpAggregate(
    input: Rel,
    strategy: Rel.Op.Aggregate.Strategy,
    calls: List<Rel.Op.Aggregate.Call>,
    groups: List<Rex>,
  ): Rel.Op.Aggregate = RelOpAggregateImpl(_id(), input, strategy, calls, groups)

  public override fun relOpAggregateCall(
    agg: Ref,
    setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier,
    args: List<Rex>,
  ): Rel.Op.Aggregate.Call = RelOpAggregateCallImpl(_id(), agg, setQuantifier, args)

  public override fun relOpExclude(input: Rel, paths: List<Rel.Op.Exclude.Path>): Rel.Op.Exclude =
      RelOpExcludeImpl(_id(), input, paths)

  public override fun relOpExcludePath(root: Rex.Op.Var, steps: List<Rel.Op.Exclude.Step>):
      Rel.Op.Exclude.Path = RelOpExcludePathImpl(_id(), root, steps)

  public override fun relOpExcludeStep(type: Rel.Op.Exclude.Type,
      substeps: List<Rel.Op.Exclude.Step>): Rel.Op.Exclude.Step = RelOpExcludeStepImpl(_id(), type,
      substeps)

  public override fun relOpExcludeTypeStructSymbol(symbol: String): Rel.Op.Exclude.Type.StructSymbol
      = RelOpExcludeTypeStructSymbolImpl(_id(), symbol)

  public override fun relOpExcludeTypeStructKey(key: String): Rel.Op.Exclude.Type.StructKey =
      RelOpExcludeTypeStructKeyImpl(_id(), key)

  public override fun relOpExcludeTypeCollIndex(index: Int): Rel.Op.Exclude.Type.CollIndex =
      RelOpExcludeTypeCollIndexImpl(_id(), index)

  public override fun relOpExcludeTypeStructWildcard(): Rel.Op.Exclude.Type.StructWildcard =
      RelOpExcludeTypeStructWildcardImpl(_id())

  public override fun relOpExcludeTypeCollWildcard(): Rel.Op.Exclude.Type.CollWildcard =
      RelOpExcludeTypeCollWildcardImpl(_id())

  public override fun relOpErr(message: String): Rel.Op.Err = RelOpErrImpl(_id(), message)

  public override fun relBinding(name: String, type: StaticType): Rel.Binding =
      RelBindingImpl(_id(), name, type)
}
