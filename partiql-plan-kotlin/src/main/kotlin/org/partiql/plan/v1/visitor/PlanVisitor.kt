@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.visitor

import kotlin.OptIn
import org.partiql.`value`.PartiQLValueExperimental
import org.partiql.plan.v1.Catalog
import org.partiql.plan.v1.Identifier
import org.partiql.plan.v1.PartiQLPlan
import org.partiql.plan.v1.PlanNode
import org.partiql.plan.v1.Ref
import org.partiql.plan.v1.Rel
import org.partiql.plan.v1.Rex
import org.partiql.plan.v1.Statement

public interface PlanVisitor<R, C> {
  public fun visit(node: PlanNode, ctx: C): R

  public fun visitPartiQLPlan(node: PartiQLPlan, ctx: C): R

  public fun visitCatalog(node: Catalog, ctx: C): R

  public fun visitCatalogItem(node: Catalog.Item, ctx: C): R

  public fun visitCatalogItemValue(node: Catalog.Item.Value, ctx: C): R

  public fun visitCatalogItemFn(node: Catalog.Item.Fn, ctx: C): R

  public fun visitCatalogItemAgg(node: Catalog.Item.Agg, ctx: C): R

  public fun visitRef(node: Ref, ctx: C): R

  public fun visitRefCast(node: Ref.Cast, ctx: C): R

  public fun visitStatement(node: Statement, ctx: C): R

  public fun visitStatementQuery(node: Statement.Query, ctx: C): R

  public fun visitIdentifier(node: Identifier, ctx: C): R

  public fun visitIdentifierSymbol(node: Identifier.Symbol, ctx: C): R

  public fun visitIdentifierQualified(node: Identifier.Qualified, ctx: C): R

  public fun visitRex(node: Rex, ctx: C): R

  public fun visitRexOp(node: Rex.Op, ctx: C): R

  public fun visitRexOpLit(node: Rex.Op.Lit, ctx: C): R

  public fun visitRexOpVar(node: Rex.Op.Var, ctx: C): R

  public fun visitRexOpGlobal(node: Rex.Op.Global, ctx: C): R

  public fun visitRexOpPath(node: Rex.Op.Path, ctx: C): R

  public fun visitRexOpPathIndex(node: Rex.Op.Path.Index, ctx: C): R

  public fun visitRexOpPathKey(node: Rex.Op.Path.Key, ctx: C): R

  public fun visitRexOpPathSymbol(node: Rex.Op.Path.Symbol, ctx: C): R

  public fun visitRexOpCast(node: Rex.Op.Cast, ctx: C): R

  public fun visitRexOpCall(node: Rex.Op.Call, ctx: C): R

  public fun visitRexOpCallStatic(node: Rex.Op.Call.Static, ctx: C): R

  public fun visitRexOpCallDynamic(node: Rex.Op.Call.Dynamic, ctx: C): R

  public fun visitRexOpCallDynamicCandidate(node: Rex.Op.Call.Dynamic.Candidate, ctx: C): R

  public fun visitRexOpCase(node: Rex.Op.Case, ctx: C): R

  public fun visitRexOpCaseBranch(node: Rex.Op.Case.Branch, ctx: C): R

  public fun visitRexOpCollection(node: Rex.Op.Collection, ctx: C): R

  public fun visitRexOpStruct(node: Rex.Op.Struct, ctx: C): R

  public fun visitRexOpStructField(node: Rex.Op.Struct.Field, ctx: C): R

  public fun visitRexOpPivot(node: Rex.Op.Pivot, ctx: C): R

  public fun visitRexOpSubquery(node: Rex.Op.Subquery, ctx: C): R

  public fun visitRexOpSelect(node: Rex.Op.Select, ctx: C): R

  public fun visitRexOpTupleUnion(node: Rex.Op.TupleUnion, ctx: C): R

  public fun visitRexOpErr(node: Rex.Op.Err, ctx: C): R

  public fun visitRel(node: Rel, ctx: C): R

  public fun visitRelType(node: Rel.Type, ctx: C): R

  public fun visitRelOp(node: Rel.Op, ctx: C): R

  public fun visitRelOpScan(node: Rel.Op.Scan, ctx: C): R

  public fun visitRelOpScanIndexed(node: Rel.Op.ScanIndexed, ctx: C): R

  public fun visitRelOpUnpivot(node: Rel.Op.Unpivot, ctx: C): R

  public fun visitRelOpDistinct(node: Rel.Op.Distinct, ctx: C): R

  public fun visitRelOpFilter(node: Rel.Op.Filter, ctx: C): R

  public fun visitRelOpSort(node: Rel.Op.Sort, ctx: C): R

  public fun visitRelOpSortSpec(node: Rel.Op.Sort.Spec, ctx: C): R

  public fun visitRelOpUnion(node: Rel.Op.Union, ctx: C): R

  public fun visitRelOpIntersect(node: Rel.Op.Intersect, ctx: C): R

  public fun visitRelOpExcept(node: Rel.Op.Except, ctx: C): R

  public fun visitRelOpLimit(node: Rel.Op.Limit, ctx: C): R

  public fun visitRelOpOffset(node: Rel.Op.Offset, ctx: C): R

  public fun visitRelOpProject(node: Rel.Op.Project, ctx: C): R

  public fun visitRelOpJoin(node: Rel.Op.Join, ctx: C): R

  public fun visitRelOpAggregate(node: Rel.Op.Aggregate, ctx: C): R

  public fun visitRelOpAggregateCall(node: Rel.Op.Aggregate.Call, ctx: C): R

  public fun visitRelOpExclude(node: Rel.Op.Exclude, ctx: C): R

  public fun visitRelOpExcludePath(node: Rel.Op.Exclude.Path, ctx: C): R

  public fun visitRelOpExcludeStep(node: Rel.Op.Exclude.Step, ctx: C): R

  public fun visitRelOpExcludeType(node: Rel.Op.Exclude.Type, ctx: C): R

  public fun visitRelOpExcludeTypeStructSymbol(node: Rel.Op.Exclude.Type.StructSymbol, ctx: C): R

  public fun visitRelOpExcludeTypeStructKey(node: Rel.Op.Exclude.Type.StructKey, ctx: C): R

  public fun visitRelOpExcludeTypeCollIndex(node: Rel.Op.Exclude.Type.CollIndex, ctx: C): R

  public fun visitRelOpExcludeTypeStructWildcard(node: Rel.Op.Exclude.Type.StructWildcard, ctx: C):
      R

  public fun visitRelOpExcludeTypeCollWildcard(node: Rel.Op.Exclude.Type.CollWildcard, ctx: C): R

  public fun visitRelOpErr(node: Rel.Op.Err, ctx: C): R

  public fun visitRelBinding(node: Rel.Binding, ctx: C): R
}
