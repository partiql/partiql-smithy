@file:Suppress(
  "UNUSED_PARAMETER",
  "UNUSED_VARIABLE",
)
@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.util

import kotlin.OptIn
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Set
import org.partiql.`value`.PartiQLValueExperimental
import org.partiql.plan.v1.Catalog
import org.partiql.plan.v1.Identifier
import org.partiql.plan.v1.PartiQLPlan
import org.partiql.plan.v1.PlanNode
import org.partiql.plan.v1.Ref
import org.partiql.plan.v1.Rel
import org.partiql.plan.v1.Rex
import org.partiql.plan.v1.Statement
import org.partiql.plan.v1.builder.PlanFactory
import org.partiql.plan.v1.visitor.PlanBaseVisitor

public abstract class PlanRewriter<C> : PlanBaseVisitor<PlanNode, C>() {
  public open val plan: PlanFactory = PlanFactory.DEFAULT

  public override fun defaultReturn(node: PlanNode, ctx: C): PlanNode = node

  private inline fun <reified T> _visitList(
    nodes: List<T>,
    ctx: C,
    method: (node: T, ctx: C) -> PlanNode,
  ): List<T> {
    if (nodes.isEmpty()) return nodes
    var diff = false
    val transformed = ArrayList<T>(nodes.size)
    nodes.forEach {
      val n = method(it, ctx) as T
      if (it !== n) diff = true
      transformed.add(n)
    }
    return if (diff) transformed else nodes
  }

  private inline fun <reified T> _visitListNull(
    nodes: List<T?>,
    ctx: C,
    method: (node: T, ctx: C) -> PlanNode,
  ): List<T?> {
    if (nodes.isEmpty()) return nodes
    var diff = false
    val transformed = ArrayList<T?>(nodes.size)
    nodes.forEach {
      val n = if (it == null) null else method(it, ctx) as T
      if (it !== n) diff = true
      transformed.add(n)
    }
    return if (diff) transformed else nodes
  }

  private inline fun <reified T> _visitSet(
    nodes: Set<T>,
    ctx: C,
    method: (node: T, ctx: C) -> PlanNode,
  ): Set<T> {
    if (nodes.isEmpty()) return nodes
    var diff = false
    val transformed = HashSet<T>(nodes.size)
    nodes.forEach {
      val n = method(it, ctx) as T
      if (it !== n) diff = true
      transformed.add(n)
    }
    return if (diff) transformed else nodes
  }

  private inline fun <reified T> _visitSetNull(
    nodes: Set<T?>,
    ctx: C,
    method: (node: T, ctx: C) -> PlanNode,
  ): Set<T?> {
    if (nodes.isEmpty()) return nodes
    var diff = false
    val transformed = HashSet<T?>(nodes.size)
    nodes.forEach {
      val n = if (it == null) null else method(it, ctx) as T
      if (it !== n) diff = true
      transformed.add(n)
    }
    return if (diff) transformed else nodes
  }

  public override fun visitPartiQLPlan(node: PartiQLPlan, ctx: C): PlanNode {
    val catalogs = _visitList(node.catalogs, ctx, ::visitCatalog)
    val statement = visitStatement(node.statement, ctx) as Statement
    return if (catalogs !== node.catalogs || statement !== node.statement) {
      plan.partiQLPlan(catalogs, statement)
    } else {
      node
    }
  }

  public override fun visitCatalog(node: Catalog, ctx: C): PlanNode {
    val name = node.name
    val items = _visitList(node.items, ctx, ::visitCatalogItem)
    return if (name !== node.name || items !== node.items) {
      plan.catalog(name, items)
    } else {
      node
    }
  }

  public override fun visitCatalogItemValue(node: Catalog.Item.Value, ctx: C): PlanNode {
    val path = node.path
    val type = node.type
    return node
  }

  public override fun visitCatalogItemFn(node: Catalog.Item.Fn, ctx: C): PlanNode {
    val path = node.path
    val specific = node.specific
    return node
  }

  public override fun visitCatalogItemAgg(node: Catalog.Item.Agg, ctx: C): PlanNode {
    val path = node.path
    val specific = node.specific
    return node
  }

  public override fun visitRef(node: Ref, ctx: C): PlanNode {
    val catalog = node.catalog
    val symbol = node.symbol
    return node
  }

  public override fun visitRefCast(node: Ref.Cast, ctx: C): PlanNode {
    val input = node.input
    val target = node.target
    return node
  }

  public override fun visitStatementQuery(node: Statement.Query, ctx: C): PlanNode {
    val root = visitRex(node.root, ctx) as Rex
    return if (root !== node.root) {
      plan.statementQuery(root)
    } else {
      node
    }
  }

  public override fun visitIdentifierSymbol(node: Identifier.Symbol, ctx: C): PlanNode {
    val symbol = node.symbol
    val caseSensitivity = node.caseSensitivity
    return node
  }

  public override fun visitIdentifierQualified(node: Identifier.Qualified, ctx: C): PlanNode {
    val root = visitIdentifierSymbol(node.root, ctx) as Identifier.Symbol
    val steps = _visitList(node.steps, ctx, ::visitIdentifierSymbol)
    return if (root !== node.root || steps !== node.steps) {
      plan.identifierQualified(root, steps)
    } else {
      node
    }
  }

  public override fun visitRex(node: Rex, ctx: C): PlanNode {
    val type = node.type
    val op = visitRexOp(node.op, ctx) as Rex.Op
    return if (type !== node.type || op !== node.op) {
      plan.rex(type, op)
    } else {
      node
    }
  }

  public override fun visitRexOpLit(node: Rex.Op.Lit, ctx: C): PlanNode {
    val value = node.value
    return node
  }

  public override fun visitRexOpVar(node: Rex.Op.Var, ctx: C): PlanNode {
    val depth = node.depth
    val ref = node.ref
    return node
  }

  public override fun visitRexOpGlobal(node: Rex.Op.Global, ctx: C): PlanNode {
    val ref = visitRef(node.ref, ctx) as Ref
    return if (ref !== node.ref) {
      plan.rexOpGlobal(ref)
    } else {
      node
    }
  }

  public override fun visitRexOpPathIndex(node: Rex.Op.Path.Index, ctx: C): PlanNode {
    val root = visitRex(node.root, ctx) as Rex
    val key = visitRex(node.key, ctx) as Rex
    return if (root !== node.root || key !== node.key) {
      plan.rexOpPathIndex(root, key)
    } else {
      node
    }
  }

  public override fun visitRexOpPathKey(node: Rex.Op.Path.Key, ctx: C): PlanNode {
    val root = visitRex(node.root, ctx) as Rex
    val key = visitRex(node.key, ctx) as Rex
    return if (root !== node.root || key !== node.key) {
      plan.rexOpPathKey(root, key)
    } else {
      node
    }
  }

  public override fun visitRexOpPathSymbol(node: Rex.Op.Path.Symbol, ctx: C): PlanNode {
    val root = visitRex(node.root, ctx) as Rex
    val key = node.key
    return if (root !== node.root || key !== node.key) {
      plan.rexOpPathSymbol(root, key)
    } else {
      node
    }
  }

  public override fun visitRexOpCast(node: Rex.Op.Cast, ctx: C): PlanNode {
    val cast = visitRefCast(node.cast, ctx) as Ref.Cast
    val arg = visitRex(node.arg, ctx) as Rex
    return if (cast !== node.cast || arg !== node.arg) {
      plan.rexOpCast(cast, arg)
    } else {
      node
    }
  }

  public override fun visitRexOpCallStatic(node: Rex.Op.Call.Static, ctx: C): PlanNode {
    val fn = visitRef(node.fn, ctx) as Ref
    val args = _visitList(node.args, ctx, ::visitRex)
    return if (fn !== node.fn || args !== node.args) {
      plan.rexOpCallStatic(fn, args)
    } else {
      node
    }
  }

  public override fun visitRexOpCallDynamic(node: Rex.Op.Call.Dynamic, ctx: C): PlanNode {
    val args = _visitList(node.args, ctx, ::visitRex)
    val candidates = _visitList(node.candidates, ctx, ::visitRexOpCallDynamicCandidate)
    return if (args !== node.args || candidates !== node.candidates) {
      plan.rexOpCallDynamic(args, candidates)
    } else {
      node
    }
  }

  public override fun visitRexOpCallDynamicCandidate(node: Rex.Op.Call.Dynamic.Candidate, ctx: C):
      PlanNode {
    val fn = visitRef(node.fn, ctx) as Ref
    val parameters = node.parameters
    val coercions = _visitListNull(node.coercions, ctx, ::visitRefCast)
    return if (fn !== node.fn || parameters !== node.parameters || coercions !== node.coercions) {
      plan.rexOpCallDynamicCandidate(fn, parameters, coercions)
    } else {
      node
    }
  }

  public override fun visitRexOpCase(node: Rex.Op.Case, ctx: C): PlanNode {
    val branches = _visitList(node.branches, ctx, ::visitRexOpCaseBranch)
    val default = visitRex(node.default, ctx) as Rex
    return if (branches !== node.branches || default !== node.default) {
      plan.rexOpCase(branches, default)
    } else {
      node
    }
  }

  public override fun visitRexOpCaseBranch(node: Rex.Op.Case.Branch, ctx: C): PlanNode {
    val condition = visitRex(node.condition, ctx) as Rex
    val rex = visitRex(node.rex, ctx) as Rex
    return if (condition !== node.condition || rex !== node.rex) {
      plan.rexOpCaseBranch(condition, rex)
    } else {
      node
    }
  }

  public override fun visitRexOpCollection(node: Rex.Op.Collection, ctx: C): PlanNode {
    val values = _visitList(node.values, ctx, ::visitRex)
    return if (values !== node.values) {
      plan.rexOpCollection(values)
    } else {
      node
    }
  }

  public override fun visitRexOpStruct(node: Rex.Op.Struct, ctx: C): PlanNode {
    val fields = _visitList(node.fields, ctx, ::visitRexOpStructField)
    return if (fields !== node.fields) {
      plan.rexOpStruct(fields)
    } else {
      node
    }
  }

  public override fun visitRexOpStructField(node: Rex.Op.Struct.Field, ctx: C): PlanNode {
    val k = visitRex(node.k, ctx) as Rex
    val v = visitRex(node.v, ctx) as Rex
    return if (k !== node.k || v !== node.v) {
      plan.rexOpStructField(k, v)
    } else {
      node
    }
  }

  public override fun visitRexOpPivot(node: Rex.Op.Pivot, ctx: C): PlanNode {
    val key = visitRex(node.key, ctx) as Rex
    val value = visitRex(node.value, ctx) as Rex
    val rel = visitRel(node.rel, ctx) as Rel
    return if (key !== node.key || value !== node.value || rel !== node.rel) {
      plan.rexOpPivot(key, value, rel)
    } else {
      node
    }
  }

  public override fun visitRexOpSubquery(node: Rex.Op.Subquery, ctx: C): PlanNode {
    val constructor = visitRex(node.constructor, ctx) as Rex
    val rel = visitRel(node.rel, ctx) as Rel
    val coercion = node.coercion
    return if (constructor !== node.constructor || rel !== node.rel || coercion !== node.coercion) {
      plan.rexOpSubquery(constructor, rel, coercion)
    } else {
      node
    }
  }

  public override fun visitRexOpSelect(node: Rex.Op.Select, ctx: C): PlanNode {
    val constructor = visitRex(node.constructor, ctx) as Rex
    val rel = visitRel(node.rel, ctx) as Rel
    return if (constructor !== node.constructor || rel !== node.rel) {
      plan.rexOpSelect(constructor, rel)
    } else {
      node
    }
  }

  public override fun visitRexOpTupleUnion(node: Rex.Op.TupleUnion, ctx: C): PlanNode {
    val args = _visitList(node.args, ctx, ::visitRex)
    return if (args !== node.args) {
      plan.rexOpTupleUnion(args)
    } else {
      node
    }
  }

  public override fun visitRexOpErr(node: Rex.Op.Err, ctx: C): PlanNode {
    val message = node.message
    return node
  }

  public override fun visitRel(node: Rel, ctx: C): PlanNode {
    val type = visitRelType(node.type, ctx) as Rel.Type
    val op = visitRelOp(node.op, ctx) as Rel.Op
    return if (type !== node.type || op !== node.op) {
      plan.rel(type, op)
    } else {
      node
    }
  }

  public override fun visitRelType(node: Rel.Type, ctx: C): PlanNode {
    val schema = _visitList(node.schema, ctx, ::visitRelBinding)
    val props = node.props
    return if (schema !== node.schema || props !== node.props) {
      plan.relType(schema, props)
    } else {
      node
    }
  }

  public override fun visitRelOpScan(node: Rel.Op.Scan, ctx: C): PlanNode {
    val rex = visitRex(node.rex, ctx) as Rex
    return if (rex !== node.rex) {
      plan.relOpScan(rex)
    } else {
      node
    }
  }

  public override fun visitRelOpScanIndexed(node: Rel.Op.ScanIndexed, ctx: C): PlanNode {
    val rex = visitRex(node.rex, ctx) as Rex
    return if (rex !== node.rex) {
      plan.relOpScanIndexed(rex)
    } else {
      node
    }
  }

  public override fun visitRelOpUnpivot(node: Rel.Op.Unpivot, ctx: C): PlanNode {
    val rex = visitRex(node.rex, ctx) as Rex
    return if (rex !== node.rex) {
      plan.relOpUnpivot(rex)
    } else {
      node
    }
  }

  public override fun visitRelOpDistinct(node: Rel.Op.Distinct, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    return if (input !== node.input) {
      plan.relOpDistinct(input)
    } else {
      node
    }
  }

  public override fun visitRelOpFilter(node: Rel.Op.Filter, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    val predicate = visitRex(node.predicate, ctx) as Rex
    return if (input !== node.input || predicate !== node.predicate) {
      plan.relOpFilter(input, predicate)
    } else {
      node
    }
  }

  public override fun visitRelOpSort(node: Rel.Op.Sort, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    val specs = _visitList(node.specs, ctx, ::visitRelOpSortSpec)
    return if (input !== node.input || specs !== node.specs) {
      plan.relOpSort(input, specs)
    } else {
      node
    }
  }

  public override fun visitRelOpSortSpec(node: Rel.Op.Sort.Spec, ctx: C): PlanNode {
    val rex = visitRex(node.rex, ctx) as Rex
    val order = node.order
    return if (rex !== node.rex || order !== node.order) {
      plan.relOpSortSpec(rex, order)
    } else {
      node
    }
  }

  public override fun visitRelOpUnion(node: Rel.Op.Union, ctx: C): PlanNode {
    val lhs = visitRel(node.lhs, ctx) as Rel
    val rhs = visitRel(node.rhs, ctx) as Rel
    return if (lhs !== node.lhs || rhs !== node.rhs) {
      plan.relOpUnion(lhs, rhs)
    } else {
      node
    }
  }

  public override fun visitRelOpIntersect(node: Rel.Op.Intersect, ctx: C): PlanNode {
    val lhs = visitRel(node.lhs, ctx) as Rel
    val rhs = visitRel(node.rhs, ctx) as Rel
    return if (lhs !== node.lhs || rhs !== node.rhs) {
      plan.relOpIntersect(lhs, rhs)
    } else {
      node
    }
  }

  public override fun visitRelOpExcept(node: Rel.Op.Except, ctx: C): PlanNode {
    val lhs = visitRel(node.lhs, ctx) as Rel
    val rhs = visitRel(node.rhs, ctx) as Rel
    return if (lhs !== node.lhs || rhs !== node.rhs) {
      plan.relOpExcept(lhs, rhs)
    } else {
      node
    }
  }

  public override fun visitRelOpLimit(node: Rel.Op.Limit, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    val limit = visitRex(node.limit, ctx) as Rex
    return if (input !== node.input || limit !== node.limit) {
      plan.relOpLimit(input, limit)
    } else {
      node
    }
  }

  public override fun visitRelOpOffset(node: Rel.Op.Offset, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    val offset = visitRex(node.offset, ctx) as Rex
    return if (input !== node.input || offset !== node.offset) {
      plan.relOpOffset(input, offset)
    } else {
      node
    }
  }

  public override fun visitRelOpProject(node: Rel.Op.Project, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    val projections = _visitList(node.projections, ctx, ::visitRex)
    return if (input !== node.input || projections !== node.projections) {
      plan.relOpProject(input, projections)
    } else {
      node
    }
  }

  public override fun visitRelOpJoin(node: Rel.Op.Join, ctx: C): PlanNode {
    val lhs = visitRel(node.lhs, ctx) as Rel
    val rhs = visitRel(node.rhs, ctx) as Rel
    val rex = visitRex(node.rex, ctx) as Rex
    val type = node.type
    return if (lhs !== node.lhs || rhs !== node.rhs || rex !== node.rex || type !== node.type) {
      plan.relOpJoin(lhs, rhs, rex, type)
    } else {
      node
    }
  }

  public override fun visitRelOpAggregate(node: Rel.Op.Aggregate, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    val strategy = node.strategy
    val calls = _visitList(node.calls, ctx, ::visitRelOpAggregateCall)
    val groups = _visitList(node.groups, ctx, ::visitRex)
    return if (input !== node.input || strategy !== node.strategy || calls !== node.calls || groups
        !== node.groups) {
      plan.relOpAggregate(input, strategy, calls, groups)
    } else {
      node
    }
  }

  public override fun visitRelOpAggregateCall(node: Rel.Op.Aggregate.Call, ctx: C): PlanNode {
    val agg = visitRef(node.agg, ctx) as Ref
    val setQuantifier = node.setQuantifier
    val args = _visitList(node.args, ctx, ::visitRex)
    return if (agg !== node.agg || setQuantifier !== node.setQuantifier || args !== node.args) {
      plan.relOpAggregateCall(agg, setQuantifier, args)
    } else {
      node
    }
  }

  public override fun visitRelOpExclude(node: Rel.Op.Exclude, ctx: C): PlanNode {
    val input = visitRel(node.input, ctx) as Rel
    val paths = _visitList(node.paths, ctx, ::visitRelOpExcludePath)
    return if (input !== node.input || paths !== node.paths) {
      plan.relOpExclude(input, paths)
    } else {
      node
    }
  }

  public override fun visitRelOpExcludePath(node: Rel.Op.Exclude.Path, ctx: C): PlanNode {
    val root = visitRexOpVar(node.root, ctx) as Rex.Op.Var
    val steps = _visitList(node.steps, ctx, ::visitRelOpExcludeStep)
    return if (root !== node.root || steps !== node.steps) {
      plan.relOpExcludePath(root, steps)
    } else {
      node
    }
  }

  public override fun visitRelOpExcludeStep(node: Rel.Op.Exclude.Step, ctx: C): PlanNode {
    val type = visitRelOpExcludeType(node.type, ctx) as Rel.Op.Exclude.Type
    val substeps = _visitList(node.substeps, ctx, ::visitRelOpExcludeStep)
    return if (type !== node.type || substeps !== node.substeps) {
      plan.relOpExcludeStep(type, substeps)
    } else {
      node
    }
  }

  public override fun visitRelOpExcludeTypeStructSymbol(node: Rel.Op.Exclude.Type.StructSymbol,
      ctx: C): PlanNode {
    val symbol = node.symbol
    return node
  }

  public override fun visitRelOpExcludeTypeStructKey(node: Rel.Op.Exclude.Type.StructKey, ctx: C):
      PlanNode {
    val key = node.key
    return node
  }

  public override fun visitRelOpExcludeTypeCollIndex(node: Rel.Op.Exclude.Type.CollIndex, ctx: C):
      PlanNode {
    val index = node.index
    return node
  }

  public override fun visitRelOpExcludeTypeStructWildcard(node: Rel.Op.Exclude.Type.StructWildcard,
      ctx: C): PlanNode = node

  public override fun visitRelOpExcludeTypeCollWildcard(node: Rel.Op.Exclude.Type.CollWildcard,
      ctx: C): PlanNode = node

  public override fun visitRelOpErr(node: Rel.Op.Err, ctx: C): PlanNode {
    val message = node.message
    return node
  }

  public override fun visitRelBinding(node: Rel.Binding, ctx: C): PlanNode {
    val name = node.name
    val type = node.type
    return node
  }
}
