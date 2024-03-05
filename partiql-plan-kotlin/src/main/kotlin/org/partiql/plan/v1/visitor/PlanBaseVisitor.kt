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

public abstract class PlanBaseVisitor<R, C> : PlanVisitor<R, C> {
  public override fun visit(node: PlanNode, ctx: C): R = node.accept(this, ctx)

  public override fun visitPartiQLPlan(node: PartiQLPlan, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitCatalog(node: Catalog, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitCatalogItem(node: Catalog.Item, ctx: C): R = when (node) {
    is Catalog.Item.Value -> visitCatalogItemValue(node, ctx)
    is Catalog.Item.Fn -> visitCatalogItemFn(node, ctx)
    is Catalog.Item.Agg -> visitCatalogItemAgg(node, ctx)
  }

  public override fun visitCatalogItemValue(node: Catalog.Item.Value, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitCatalogItemFn(node: Catalog.Item.Fn, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitCatalogItemAgg(node: Catalog.Item.Agg, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRef(node: Ref, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRefCast(node: Ref.Cast, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitStatement(node: Statement, ctx: C): R = when (node) {
    is Statement.Query -> visitStatementQuery(node, ctx)
  }

  public override fun visitStatementQuery(node: Statement.Query, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitIdentifier(node: Identifier, ctx: C): R = when (node) {
    is Identifier.Symbol -> visitIdentifierSymbol(node, ctx)
    is Identifier.Qualified -> visitIdentifierQualified(node, ctx)
  }

  public override fun visitIdentifierSymbol(node: Identifier.Symbol, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitIdentifierQualified(node: Identifier.Qualified, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitRex(node: Rex, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOp(node: Rex.Op, ctx: C): R = when (node) {
    is Rex.Op.Lit -> visitRexOpLit(node, ctx)
    is Rex.Op.Var -> visitRexOpVar(node, ctx)
    is Rex.Op.Global -> visitRexOpGlobal(node, ctx)
    is Rex.Op.Path -> visitRexOpPath(node, ctx)
    is Rex.Op.Cast -> visitRexOpCast(node, ctx)
    is Rex.Op.Call -> visitRexOpCall(node, ctx)
    is Rex.Op.Case -> visitRexOpCase(node, ctx)
    is Rex.Op.Collection -> visitRexOpCollection(node, ctx)
    is Rex.Op.Struct -> visitRexOpStruct(node, ctx)
    is Rex.Op.Pivot -> visitRexOpPivot(node, ctx)
    is Rex.Op.Subquery -> visitRexOpSubquery(node, ctx)
    is Rex.Op.Select -> visitRexOpSelect(node, ctx)
    is Rex.Op.TupleUnion -> visitRexOpTupleUnion(node, ctx)
    is Rex.Op.Err -> visitRexOpErr(node, ctx)
  }

  public override fun visitRexOpLit(node: Rex.Op.Lit, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpVar(node: Rex.Op.Var, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpGlobal(node: Rex.Op.Global, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpPath(node: Rex.Op.Path, ctx: C): R = when (node) {
    is Rex.Op.Path.Index -> visitRexOpPathIndex(node, ctx)
    is Rex.Op.Path.Key -> visitRexOpPathKey(node, ctx)
    is Rex.Op.Path.Symbol -> visitRexOpPathSymbol(node, ctx)
  }

  public override fun visitRexOpPathIndex(node: Rex.Op.Path.Index, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRexOpPathKey(node: Rex.Op.Path.Key, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpPathSymbol(node: Rex.Op.Path.Symbol, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRexOpCast(node: Rex.Op.Cast, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpCall(node: Rex.Op.Call, ctx: C): R = when (node) {
    is Rex.Op.Call.Static -> visitRexOpCallStatic(node, ctx)
    is Rex.Op.Call.Dynamic -> visitRexOpCallDynamic(node, ctx)
  }

  public override fun visitRexOpCallStatic(node: Rex.Op.Call.Static, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRexOpCallDynamic(node: Rex.Op.Call.Dynamic, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitRexOpCallDynamicCandidate(node: Rex.Op.Call.Dynamic.Candidate, ctx: C): R
      = defaultVisit(node, ctx)

  public override fun visitRexOpCase(node: Rex.Op.Case, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpCaseBranch(node: Rex.Op.Case.Branch, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRexOpCollection(node: Rex.Op.Collection, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRexOpStruct(node: Rex.Op.Struct, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpStructField(node: Rex.Op.Struct.Field, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitRexOpPivot(node: Rex.Op.Pivot, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpSubquery(node: Rex.Op.Subquery, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpSelect(node: Rex.Op.Select, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRexOpTupleUnion(node: Rex.Op.TupleUnion, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRexOpErr(node: Rex.Op.Err, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRel(node: Rel, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelType(node: Rel.Type, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOp(node: Rel.Op, ctx: C): R = when (node) {
    is Rel.Op.Scan -> visitRelOpScan(node, ctx)
    is Rel.Op.ScanIndexed -> visitRelOpScanIndexed(node, ctx)
    is Rel.Op.Unpivot -> visitRelOpUnpivot(node, ctx)
    is Rel.Op.Distinct -> visitRelOpDistinct(node, ctx)
    is Rel.Op.Filter -> visitRelOpFilter(node, ctx)
    is Rel.Op.Sort -> visitRelOpSort(node, ctx)
    is Rel.Op.Union -> visitRelOpUnion(node, ctx)
    is Rel.Op.Intersect -> visitRelOpIntersect(node, ctx)
    is Rel.Op.Except -> visitRelOpExcept(node, ctx)
    is Rel.Op.Limit -> visitRelOpLimit(node, ctx)
    is Rel.Op.Offset -> visitRelOpOffset(node, ctx)
    is Rel.Op.Project -> visitRelOpProject(node, ctx)
    is Rel.Op.Join -> visitRelOpJoin(node, ctx)
    is Rel.Op.Aggregate -> visitRelOpAggregate(node, ctx)
    is Rel.Op.Exclude -> visitRelOpExclude(node, ctx)
    is Rel.Op.Err -> visitRelOpErr(node, ctx)
  }

  public override fun visitRelOpScan(node: Rel.Op.Scan, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpScanIndexed(node: Rel.Op.ScanIndexed, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitRelOpUnpivot(node: Rel.Op.Unpivot, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpDistinct(node: Rel.Op.Distinct, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpFilter(node: Rel.Op.Filter, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpSort(node: Rel.Op.Sort, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpSortSpec(node: Rel.Op.Sort.Spec, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRelOpUnion(node: Rel.Op.Union, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpIntersect(node: Rel.Op.Intersect, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRelOpExcept(node: Rel.Op.Except, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpLimit(node: Rel.Op.Limit, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpOffset(node: Rel.Op.Offset, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpProject(node: Rel.Op.Project, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpJoin(node: Rel.Op.Join, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpAggregate(node: Rel.Op.Aggregate, ctx: C): R = defaultVisit(node,
      ctx)

  public override fun visitRelOpAggregateCall(node: Rel.Op.Aggregate.Call, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitRelOpExclude(node: Rel.Op.Exclude, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpExcludePath(node: Rel.Op.Exclude.Path, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitRelOpExcludeStep(node: Rel.Op.Exclude.Step, ctx: C): R =
      defaultVisit(node, ctx)

  public override fun visitRelOpExcludeType(node: Rel.Op.Exclude.Type, ctx: C): R = when (node) {
    is Rel.Op.Exclude.Type.StructSymbol -> visitRelOpExcludeTypeStructSymbol(node, ctx)
    is Rel.Op.Exclude.Type.StructKey -> visitRelOpExcludeTypeStructKey(node, ctx)
    is Rel.Op.Exclude.Type.CollIndex -> visitRelOpExcludeTypeCollIndex(node, ctx)
    is Rel.Op.Exclude.Type.StructWildcard -> visitRelOpExcludeTypeStructWildcard(node, ctx)
    is Rel.Op.Exclude.Type.CollWildcard -> visitRelOpExcludeTypeCollWildcard(node, ctx)
  }

  public override fun visitRelOpExcludeTypeStructSymbol(node: Rel.Op.Exclude.Type.StructSymbol,
      ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpExcludeTypeStructKey(node: Rel.Op.Exclude.Type.StructKey, ctx: C): R
      = defaultVisit(node, ctx)

  public override fun visitRelOpExcludeTypeCollIndex(node: Rel.Op.Exclude.Type.CollIndex, ctx: C): R
      = defaultVisit(node, ctx)

  public override fun visitRelOpExcludeTypeStructWildcard(node: Rel.Op.Exclude.Type.StructWildcard,
      ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpExcludeTypeCollWildcard(node: Rel.Op.Exclude.Type.CollWildcard,
      ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelOpErr(node: Rel.Op.Err, ctx: C): R = defaultVisit(node, ctx)

  public override fun visitRelBinding(node: Rel.Binding, ctx: C): R = defaultVisit(node, ctx)

  public open fun defaultVisit(node: PlanNode, ctx: C): R {
    for (child in node.children) {
      child.accept(this, ctx)
    }
    return defaultReturn(node, ctx)
  }

  public abstract fun defaultReturn(node: PlanNode, ctx: C): R
}
