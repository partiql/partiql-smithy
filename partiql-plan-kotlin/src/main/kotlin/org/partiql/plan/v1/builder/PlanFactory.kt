@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.builder

import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.jvm.JvmStatic
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

public interface PlanFactory {
  public val _id: () -> String

  public fun partiQLPlan(catalogs: List<Catalog>, statement: Statement): PartiQLPlan

  public fun catalog(name: String, items: List<Catalog.Item>): Catalog

  public fun catalogItemValue(path: List<String>, type: StaticType): Catalog.Item.Value

  public fun catalogItemFn(path: List<String>, specific: String): Catalog.Item.Fn

  public fun catalogItemAgg(path: List<String>, specific: String): Catalog.Item.Agg

  public fun ref(catalog: Int, symbol: Int): Ref

  public fun refCast(input: PartiQLValueType, target: PartiQLValueType): Ref.Cast

  public fun statementQuery(root: Rex): Statement.Query

  public fun identifierSymbol(symbol: String, caseSensitivity: Identifier.CaseSensitivity):
      Identifier.Symbol

  public fun identifierQualified(root: Identifier.Symbol, steps: List<Identifier.Symbol>):
      Identifier.Qualified

  public fun rex(type: StaticType, op: Rex.Op): Rex

  public fun rexOpLit(`value`: PartiQLValue): Rex.Op.Lit

  public fun rexOpVar(depth: Int, ref: Int): Rex.Op.Var

  public fun rexOpGlobal(ref: Ref): Rex.Op.Global

  public fun rexOpPathIndex(root: Rex, key: Rex): Rex.Op.Path.Index

  public fun rexOpPathKey(root: Rex, key: Rex): Rex.Op.Path.Key

  public fun rexOpPathSymbol(root: Rex, key: String): Rex.Op.Path.Symbol

  public fun rexOpCast(cast: Ref.Cast, arg: Rex): Rex.Op.Cast

  public fun rexOpCallStatic(fn: Ref, args: List<Rex>): Rex.Op.Call.Static

  public fun rexOpCallDynamic(args: List<Rex>, candidates: List<Rex.Op.Call.Dynamic.Candidate>):
      Rex.Op.Call.Dynamic

  public fun rexOpCallDynamicCandidate(
    fn: Ref,
    parameters: List<PartiQLValueType>,
    coercions: List<Ref.Cast?>,
  ): Rex.Op.Call.Dynamic.Candidate

  public fun rexOpCase(branches: List<Rex.Op.Case.Branch>, default: Rex): Rex.Op.Case

  public fun rexOpCaseBranch(condition: Rex, rex: Rex): Rex.Op.Case.Branch

  public fun rexOpCollection(values: List<Rex>): Rex.Op.Collection

  public fun rexOpStruct(fields: List<Rex.Op.Struct.Field>): Rex.Op.Struct

  public fun rexOpStructField(k: Rex, v: Rex): Rex.Op.Struct.Field

  public fun rexOpPivot(
    key: Rex,
    `value`: Rex,
    rel: Rel,
  ): Rex.Op.Pivot

  public fun rexOpSubquery(
    `constructor`: Rex,
    rel: Rel,
    coercion: Rex.Op.Subquery.Coercion,
  ): Rex.Op.Subquery

  public fun rexOpSelect(`constructor`: Rex, rel: Rel): Rex.Op.Select

  public fun rexOpTupleUnion(args: List<Rex>): Rex.Op.TupleUnion

  public fun rexOpErr(message: String): Rex.Op.Err

  public fun rel(type: Rel.Type, op: Rel.Op): Rel

  public fun relType(schema: List<Rel.Binding>, props: Set<Rel.Prop>): Rel.Type

  public fun relOpScan(rex: Rex): Rel.Op.Scan

  public fun relOpScanIndexed(rex: Rex): Rel.Op.ScanIndexed

  public fun relOpUnpivot(rex: Rex): Rel.Op.Unpivot

  public fun relOpDistinct(input: Rel): Rel.Op.Distinct

  public fun relOpFilter(input: Rel, predicate: Rex): Rel.Op.Filter

  public fun relOpSort(input: Rel, specs: List<Rel.Op.Sort.Spec>): Rel.Op.Sort

  public fun relOpSortSpec(rex: Rex, order: Rel.Op.Sort.Order): Rel.Op.Sort.Spec

  public fun relOpUnion(lhs: Rel, rhs: Rel): Rel.Op.Union

  public fun relOpIntersect(lhs: Rel, rhs: Rel): Rel.Op.Intersect

  public fun relOpExcept(lhs: Rel, rhs: Rel): Rel.Op.Except

  public fun relOpLimit(input: Rel, limit: Rex): Rel.Op.Limit

  public fun relOpOffset(input: Rel, offset: Rex): Rel.Op.Offset

  public fun relOpProject(input: Rel, projections: List<Rex>): Rel.Op.Project

  public fun relOpJoin(
    lhs: Rel,
    rhs: Rel,
    rex: Rex,
    type: Rel.Op.Join.Type,
  ): Rel.Op.Join

  public fun relOpAggregate(
    input: Rel,
    strategy: Rel.Op.Aggregate.Strategy,
    calls: List<Rel.Op.Aggregate.Call>,
    groups: List<Rex>,
  ): Rel.Op.Aggregate

  public fun relOpAggregateCall(
    agg: Ref,
    setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier,
    args: List<Rex>,
  ): Rel.Op.Aggregate.Call

  public fun relOpExclude(input: Rel, paths: List<Rel.Op.Exclude.Path>): Rel.Op.Exclude

  public fun relOpExcludePath(root: Rex.Op.Var, steps: List<Rel.Op.Exclude.Step>):
      Rel.Op.Exclude.Path

  public fun relOpExcludeStep(type: Rel.Op.Exclude.Type, substeps: List<Rel.Op.Exclude.Step>):
      Rel.Op.Exclude.Step

  public fun relOpExcludeTypeStructSymbol(symbol: String): Rel.Op.Exclude.Type.StructSymbol

  public fun relOpExcludeTypeStructKey(key: String): Rel.Op.Exclude.Type.StructKey

  public fun relOpExcludeTypeCollIndex(index: Int): Rel.Op.Exclude.Type.CollIndex

  public fun relOpExcludeTypeStructWildcard(): Rel.Op.Exclude.Type.StructWildcard

  public fun relOpExcludeTypeCollWildcard(): Rel.Op.Exclude.Type.CollWildcard

  public fun relOpErr(message: String): Rel.Op.Err

  public fun relBinding(name: String, type: StaticType): Rel.Binding

  public companion object {
    public val DEFAULT: PlanFactory = PlanFactoryImpl()

    @JvmStatic
    public fun <T : PlanNode> create(block: PlanFactory.() -> T) = PlanFactory.DEFAULT.block()
  }
}
