@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.builder

import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableList
import kotlin.collections.MutableSet
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
import org.partiql.types.StaticType

public class PartiQlPlanBuilder(
  public var catalogs: MutableList<Catalog> = mutableListOf(),
  public var statement: Statement? = null,
) {
  public fun catalogs(catalogs: MutableList<Catalog>): PartiQlPlanBuilder = this.apply {
    this.catalogs = catalogs
  }

  public fun statement(statement: Statement?): PartiQlPlanBuilder = this.apply {
    this.statement = statement
  }

  public fun build(): PartiQLPlan = PartiQLPlan(catalogs = catalogs, statement = statement!!)
}

public class CatalogBuilder(
  public var name: String? = null,
  public var items: MutableList<Catalog.Item> = mutableListOf(),
) {
  public fun name(name: String?): CatalogBuilder = this.apply {
    this.name = name
  }

  public fun items(items: MutableList<Catalog.Item>): CatalogBuilder = this.apply {
    this.items = items
  }

  public fun build(): Catalog = Catalog(name = name!!, items = items)
}

public class CatalogItemValueBuilder(
  public var path: MutableList<String> = mutableListOf(),
  public var type: StaticType? = null,
) {
  public fun path(path: MutableList<String>): CatalogItemValueBuilder = this.apply {
    this.path = path
  }

  public fun type(type: StaticType?): CatalogItemValueBuilder = this.apply {
    this.type = type
  }

  public fun build(): Catalog.Item.Value = Catalog.Item.Value(path = path, type = type!!)
}

public class CatalogItemFnBuilder(
  public var path: MutableList<String> = mutableListOf(),
  public var specific: String? = null,
) {
  public fun path(path: MutableList<String>): CatalogItemFnBuilder = this.apply {
    this.path = path
  }

  public fun specific(specific: String?): CatalogItemFnBuilder = this.apply {
    this.specific = specific
  }

  public fun build(): Catalog.Item.Fn = Catalog.Item.Fn(path = path, specific = specific!!)
}

public class CatalogItemAggBuilder(
  public var path: MutableList<String> = mutableListOf(),
  public var specific: String? = null,
) {
  public fun path(path: MutableList<String>): CatalogItemAggBuilder = this.apply {
    this.path = path
  }

  public fun specific(specific: String?): CatalogItemAggBuilder = this.apply {
    this.specific = specific
  }

  public fun build(): Catalog.Item.Agg = Catalog.Item.Agg(path = path, specific = specific!!)
}

public class RefBuilder(
  public var catalog: Int? = null,
  public var symbol: Int? = null,
) {
  public fun catalog(catalog: Int?): RefBuilder = this.apply {
    this.catalog = catalog
  }

  public fun symbol(symbol: Int?): RefBuilder = this.apply {
    this.symbol = symbol
  }

  public fun build(): Ref = Ref(catalog = catalog!!, symbol = symbol!!)
}

public class RefCastBuilder(
  public var input: PartiQLValueType? = null,
  public var target: PartiQLValueType? = null,
) {
  public fun input(input: PartiQLValueType?): RefCastBuilder = this.apply {
    this.input = input
  }

  public fun target(target: PartiQLValueType?): RefCastBuilder = this.apply {
    this.target = target
  }

  public fun build(): Ref.Cast = Ref.Cast(input = input!!, target = target!!)
}

public class StatementQueryBuilder(
  public var root: Rex? = null,
) {
  public fun root(root: Rex?): StatementQueryBuilder = this.apply {
    this.root = root
  }

  public fun build(): Statement.Query = Statement.Query(root = root!!)
}

public class IdentifierSymbolBuilder(
  public var symbol: String? = null,
  public var caseSensitivity: Identifier.CaseSensitivity? = null,
) {
  public fun symbol(symbol: String?): IdentifierSymbolBuilder = this.apply {
    this.symbol = symbol
  }

  public fun caseSensitivity(caseSensitivity: Identifier.CaseSensitivity?): IdentifierSymbolBuilder
      = this.apply {
    this.caseSensitivity = caseSensitivity
  }

  public fun build(): Identifier.Symbol = Identifier.Symbol(symbol = symbol!!, caseSensitivity =
      caseSensitivity!!)
}

public class IdentifierQualifiedBuilder(
  public var root: Identifier.Symbol? = null,
  public var steps: MutableList<Identifier.Symbol> = mutableListOf(),
) {
  public fun root(root: Identifier.Symbol?): IdentifierQualifiedBuilder = this.apply {
    this.root = root
  }

  public fun steps(steps: MutableList<Identifier.Symbol>): IdentifierQualifiedBuilder = this.apply {
    this.steps = steps
  }

  public fun build(): Identifier.Qualified = Identifier.Qualified(root = root!!, steps = steps)
}

public class RexBuilder(
  public var type: StaticType? = null,
  public var op: Rex.Op? = null,
) {
  public fun type(type: StaticType?): RexBuilder = this.apply {
    this.type = type
  }

  public fun op(op: Rex.Op?): RexBuilder = this.apply {
    this.op = op
  }

  public fun build(): Rex = Rex(type = type!!, op = op!!)
}

public class RexOpLitBuilder(
  public var `value`: PartiQLValue? = null,
) {
  public fun `value`(`value`: PartiQLValue?): RexOpLitBuilder = this.apply {
    this.`value` = `value`
  }

  public fun build(): Rex.Op.Lit = Rex.Op.Lit(value = value!!)
}

public class RexOpVarBuilder(
  public var depth: Int? = null,
  public var ref: Int? = null,
) {
  public fun depth(depth: Int?): RexOpVarBuilder = this.apply {
    this.depth = depth
  }

  public fun ref(ref: Int?): RexOpVarBuilder = this.apply {
    this.ref = ref
  }

  public fun build(): Rex.Op.Var = Rex.Op.Var(depth = depth!!, ref = ref!!)
}

public class RexOpGlobalBuilder(
  public var ref: Ref? = null,
) {
  public fun ref(ref: Ref?): RexOpGlobalBuilder = this.apply {
    this.ref = ref
  }

  public fun build(): Rex.Op.Global = Rex.Op.Global(ref = ref!!)
}

public class RexOpPathIndexBuilder(
  public var root: Rex? = null,
  public var key: Rex? = null,
) {
  public fun root(root: Rex?): RexOpPathIndexBuilder = this.apply {
    this.root = root
  }

  public fun key(key: Rex?): RexOpPathIndexBuilder = this.apply {
    this.key = key
  }

  public fun build(): Rex.Op.Path.Index = Rex.Op.Path.Index(root = root!!, key = key!!)
}

public class RexOpPathKeyBuilder(
  public var root: Rex? = null,
  public var key: Rex? = null,
) {
  public fun root(root: Rex?): RexOpPathKeyBuilder = this.apply {
    this.root = root
  }

  public fun key(key: Rex?): RexOpPathKeyBuilder = this.apply {
    this.key = key
  }

  public fun build(): Rex.Op.Path.Key = Rex.Op.Path.Key(root = root!!, key = key!!)
}

public class RexOpPathSymbolBuilder(
  public var root: Rex? = null,
  public var key: String? = null,
) {
  public fun root(root: Rex?): RexOpPathSymbolBuilder = this.apply {
    this.root = root
  }

  public fun key(key: String?): RexOpPathSymbolBuilder = this.apply {
    this.key = key
  }

  public fun build(): Rex.Op.Path.Symbol = Rex.Op.Path.Symbol(root = root!!, key = key!!)
}

public class RexOpCastBuilder(
  public var cast: Ref.Cast? = null,
  public var arg: Rex? = null,
) {
  public fun cast(cast: Ref.Cast?): RexOpCastBuilder = this.apply {
    this.cast = cast
  }

  public fun arg(arg: Rex?): RexOpCastBuilder = this.apply {
    this.arg = arg
  }

  public fun build(): Rex.Op.Cast = Rex.Op.Cast(cast = cast!!, arg = arg!!)
}

public class RexOpCallStaticBuilder(
  public var fn: Ref? = null,
  public var args: MutableList<Rex> = mutableListOf(),
) {
  public fun fn(fn: Ref?): RexOpCallStaticBuilder = this.apply {
    this.fn = fn
  }

  public fun args(args: MutableList<Rex>): RexOpCallStaticBuilder = this.apply {
    this.args = args
  }

  public fun build(): Rex.Op.Call.Static = Rex.Op.Call.Static(fn = fn!!, args = args)
}

public class RexOpCallDynamicBuilder(
  public var args: MutableList<Rex> = mutableListOf(),
  public var candidates: MutableList<Rex.Op.Call.Dynamic.Candidate> = mutableListOf(),
) {
  public fun args(args: MutableList<Rex>): RexOpCallDynamicBuilder = this.apply {
    this.args = args
  }

  public fun candidates(candidates: MutableList<Rex.Op.Call.Dynamic.Candidate>):
      RexOpCallDynamicBuilder = this.apply {
    this.candidates = candidates
  }

  public fun build(): Rex.Op.Call.Dynamic = Rex.Op.Call.Dynamic(args = args, candidates =
      candidates)
}

public class RexOpCallDynamicCandidateBuilder(
  public var fn: Ref? = null,
  public var parameters: MutableList<PartiQLValueType> = mutableListOf(),
  public var coercions: MutableList<Ref.Cast?> = mutableListOf(),
) {
  public fun fn(fn: Ref?): RexOpCallDynamicCandidateBuilder = this.apply {
    this.fn = fn
  }

  public fun parameters(parameters: MutableList<PartiQLValueType>): RexOpCallDynamicCandidateBuilder
      = this.apply {
    this.parameters = parameters
  }

  public fun coercions(coercions: MutableList<Ref.Cast?>): RexOpCallDynamicCandidateBuilder =
      this.apply {
    this.coercions = coercions
  }

  public fun build(): Rex.Op.Call.Dynamic.Candidate = Rex.Op.Call.Dynamic.Candidate(fn = fn!!,
      parameters = parameters, coercions = coercions)
}

public class RexOpCaseBuilder(
  public var branches: MutableList<Rex.Op.Case.Branch> = mutableListOf(),
  public var default: Rex? = null,
) {
  public fun branches(branches: MutableList<Rex.Op.Case.Branch>): RexOpCaseBuilder = this.apply {
    this.branches = branches
  }

  public fun default(default: Rex?): RexOpCaseBuilder = this.apply {
    this.default = default
  }

  public fun build(): Rex.Op.Case = Rex.Op.Case(branches = branches, default = default!!)
}

public class RexOpCaseBranchBuilder(
  public var condition: Rex? = null,
  public var rex: Rex? = null,
) {
  public fun condition(condition: Rex?): RexOpCaseBranchBuilder = this.apply {
    this.condition = condition
  }

  public fun rex(rex: Rex?): RexOpCaseBranchBuilder = this.apply {
    this.rex = rex
  }

  public fun build(): Rex.Op.Case.Branch = Rex.Op.Case.Branch(condition = condition!!, rex = rex!!)
}

public class RexOpCollectionBuilder(
  public var values: MutableList<Rex> = mutableListOf(),
) {
  public fun values(values: MutableList<Rex>): RexOpCollectionBuilder = this.apply {
    this.values = values
  }

  public fun build(): Rex.Op.Collection = Rex.Op.Collection(values = values)
}

public class RexOpStructBuilder(
  public var fields: MutableList<Rex.Op.Struct.Field> = mutableListOf(),
) {
  public fun fields(fields: MutableList<Rex.Op.Struct.Field>): RexOpStructBuilder = this.apply {
    this.fields = fields
  }

  public fun build(): Rex.Op.Struct = Rex.Op.Struct(fields = fields)
}

public class RexOpStructFieldBuilder(
  public var k: Rex? = null,
  public var v: Rex? = null,
) {
  public fun k(k: Rex?): RexOpStructFieldBuilder = this.apply {
    this.k = k
  }

  public fun v(v: Rex?): RexOpStructFieldBuilder = this.apply {
    this.v = v
  }

  public fun build(): Rex.Op.Struct.Field = Rex.Op.Struct.Field(k = k!!, v = v!!)
}

public class RexOpPivotBuilder(
  public var key: Rex? = null,
  public var `value`: Rex? = null,
  public var rel: Rel? = null,
) {
  public fun key(key: Rex?): RexOpPivotBuilder = this.apply {
    this.key = key
  }

  public fun `value`(`value`: Rex?): RexOpPivotBuilder = this.apply {
    this.`value` = `value`
  }

  public fun rel(rel: Rel?): RexOpPivotBuilder = this.apply {
    this.rel = rel
  }

  public fun build(): Rex.Op.Pivot = Rex.Op.Pivot(key = key!!, value = value!!, rel = rel!!)
}

public class RexOpSubqueryBuilder(
  public var `constructor`: Rex? = null,
  public var rel: Rel? = null,
  public var coercion: Rex.Op.Subquery.Coercion? = null,
) {
  public fun `constructor`(`constructor`: Rex?): RexOpSubqueryBuilder = this.apply {
    this.`constructor` = `constructor`
  }

  public fun rel(rel: Rel?): RexOpSubqueryBuilder = this.apply {
    this.rel = rel
  }

  public fun coercion(coercion: Rex.Op.Subquery.Coercion?): RexOpSubqueryBuilder = this.apply {
    this.coercion = coercion
  }

  public fun build(): Rex.Op.Subquery = Rex.Op.Subquery(constructor = constructor!!, rel = rel!!,
      coercion = coercion!!)
}

public class RexOpSelectBuilder(
  public var `constructor`: Rex? = null,
  public var rel: Rel? = null,
) {
  public fun `constructor`(`constructor`: Rex?): RexOpSelectBuilder = this.apply {
    this.`constructor` = `constructor`
  }

  public fun rel(rel: Rel?): RexOpSelectBuilder = this.apply {
    this.rel = rel
  }

  public fun build(): Rex.Op.Select = Rex.Op.Select(constructor = constructor!!, rel = rel!!)
}

public class RexOpTupleUnionBuilder(
  public var args: MutableList<Rex> = mutableListOf(),
) {
  public fun args(args: MutableList<Rex>): RexOpTupleUnionBuilder = this.apply {
    this.args = args
  }

  public fun build(): Rex.Op.TupleUnion = Rex.Op.TupleUnion(args = args)
}

public class RexOpErrBuilder(
  public var message: String? = null,
) {
  public fun message(message: String?): RexOpErrBuilder = this.apply {
    this.message = message
  }

  public fun build(): Rex.Op.Err = Rex.Op.Err(message = message!!)
}

public class RelBuilder(
  public var type: Rel.Type? = null,
  public var op: Rel.Op? = null,
) {
  public fun type(type: Rel.Type?): RelBuilder = this.apply {
    this.type = type
  }

  public fun op(op: Rel.Op?): RelBuilder = this.apply {
    this.op = op
  }

  public fun build(): Rel = Rel(type = type!!, op = op!!)
}

public class RelTypeBuilder(
  public var schema: MutableList<Rel.Binding> = mutableListOf(),
  public var props: MutableSet<Rel.Prop> = mutableSetOf(),
) {
  public fun schema(schema: MutableList<Rel.Binding>): RelTypeBuilder = this.apply {
    this.schema = schema
  }

  public fun props(props: MutableSet<Rel.Prop>): RelTypeBuilder = this.apply {
    this.props = props
  }

  public fun build(): Rel.Type = Rel.Type(schema = schema, props = props)
}

public class RelOpScanBuilder(
  public var rex: Rex? = null,
) {
  public fun rex(rex: Rex?): RelOpScanBuilder = this.apply {
    this.rex = rex
  }

  public fun build(): Rel.Op.Scan = Rel.Op.Scan(rex = rex!!)
}

public class RelOpScanIndexedBuilder(
  public var rex: Rex? = null,
) {
  public fun rex(rex: Rex?): RelOpScanIndexedBuilder = this.apply {
    this.rex = rex
  }

  public fun build(): Rel.Op.ScanIndexed = Rel.Op.ScanIndexed(rex = rex!!)
}

public class RelOpUnpivotBuilder(
  public var rex: Rex? = null,
) {
  public fun rex(rex: Rex?): RelOpUnpivotBuilder = this.apply {
    this.rex = rex
  }

  public fun build(): Rel.Op.Unpivot = Rel.Op.Unpivot(rex = rex!!)
}

public class RelOpDistinctBuilder(
  public var input: Rel? = null,
) {
  public fun input(input: Rel?): RelOpDistinctBuilder = this.apply {
    this.input = input
  }

  public fun build(): Rel.Op.Distinct = Rel.Op.Distinct(input = input!!)
}

public class RelOpFilterBuilder(
  public var input: Rel? = null,
  public var predicate: Rex? = null,
) {
  public fun input(input: Rel?): RelOpFilterBuilder = this.apply {
    this.input = input
  }

  public fun predicate(predicate: Rex?): RelOpFilterBuilder = this.apply {
    this.predicate = predicate
  }

  public fun build(): Rel.Op.Filter = Rel.Op.Filter(input = input!!, predicate = predicate!!)
}

public class RelOpSortBuilder(
  public var input: Rel? = null,
  public var specs: MutableList<Rel.Op.Sort.Spec> = mutableListOf(),
) {
  public fun input(input: Rel?): RelOpSortBuilder = this.apply {
    this.input = input
  }

  public fun specs(specs: MutableList<Rel.Op.Sort.Spec>): RelOpSortBuilder = this.apply {
    this.specs = specs
  }

  public fun build(): Rel.Op.Sort = Rel.Op.Sort(input = input!!, specs = specs)
}

public class RelOpSortSpecBuilder(
  public var rex: Rex? = null,
  public var order: Rel.Op.Sort.Order? = null,
) {
  public fun rex(rex: Rex?): RelOpSortSpecBuilder = this.apply {
    this.rex = rex
  }

  public fun order(order: Rel.Op.Sort.Order?): RelOpSortSpecBuilder = this.apply {
    this.order = order
  }

  public fun build(): Rel.Op.Sort.Spec = Rel.Op.Sort.Spec(rex = rex!!, order = order!!)
}

public class RelOpUnionBuilder(
  public var lhs: Rel? = null,
  public var rhs: Rel? = null,
) {
  public fun lhs(lhs: Rel?): RelOpUnionBuilder = this.apply {
    this.lhs = lhs
  }

  public fun rhs(rhs: Rel?): RelOpUnionBuilder = this.apply {
    this.rhs = rhs
  }

  public fun build(): Rel.Op.Union = Rel.Op.Union(lhs = lhs!!, rhs = rhs!!)
}

public class RelOpIntersectBuilder(
  public var lhs: Rel? = null,
  public var rhs: Rel? = null,
) {
  public fun lhs(lhs: Rel?): RelOpIntersectBuilder = this.apply {
    this.lhs = lhs
  }

  public fun rhs(rhs: Rel?): RelOpIntersectBuilder = this.apply {
    this.rhs = rhs
  }

  public fun build(): Rel.Op.Intersect = Rel.Op.Intersect(lhs = lhs!!, rhs = rhs!!)
}

public class RelOpExceptBuilder(
  public var lhs: Rel? = null,
  public var rhs: Rel? = null,
) {
  public fun lhs(lhs: Rel?): RelOpExceptBuilder = this.apply {
    this.lhs = lhs
  }

  public fun rhs(rhs: Rel?): RelOpExceptBuilder = this.apply {
    this.rhs = rhs
  }

  public fun build(): Rel.Op.Except = Rel.Op.Except(lhs = lhs!!, rhs = rhs!!)
}

public class RelOpLimitBuilder(
  public var input: Rel? = null,
  public var limit: Rex? = null,
) {
  public fun input(input: Rel?): RelOpLimitBuilder = this.apply {
    this.input = input
  }

  public fun limit(limit: Rex?): RelOpLimitBuilder = this.apply {
    this.limit = limit
  }

  public fun build(): Rel.Op.Limit = Rel.Op.Limit(input = input!!, limit = limit!!)
}

public class RelOpOffsetBuilder(
  public var input: Rel? = null,
  public var offset: Rex? = null,
) {
  public fun input(input: Rel?): RelOpOffsetBuilder = this.apply {
    this.input = input
  }

  public fun offset(offset: Rex?): RelOpOffsetBuilder = this.apply {
    this.offset = offset
  }

  public fun build(): Rel.Op.Offset = Rel.Op.Offset(input = input!!, offset = offset!!)
}

public class RelOpProjectBuilder(
  public var input: Rel? = null,
  public var projections: MutableList<Rex> = mutableListOf(),
) {
  public fun input(input: Rel?): RelOpProjectBuilder = this.apply {
    this.input = input
  }

  public fun projections(projections: MutableList<Rex>): RelOpProjectBuilder = this.apply {
    this.projections = projections
  }

  public fun build(): Rel.Op.Project = Rel.Op.Project(input = input!!, projections = projections)
}

public class RelOpJoinBuilder(
  public var lhs: Rel? = null,
  public var rhs: Rel? = null,
  public var rex: Rex? = null,
  public var type: Rel.Op.Join.Type? = null,
) {
  public fun lhs(lhs: Rel?): RelOpJoinBuilder = this.apply {
    this.lhs = lhs
  }

  public fun rhs(rhs: Rel?): RelOpJoinBuilder = this.apply {
    this.rhs = rhs
  }

  public fun rex(rex: Rex?): RelOpJoinBuilder = this.apply {
    this.rex = rex
  }

  public fun type(type: Rel.Op.Join.Type?): RelOpJoinBuilder = this.apply {
    this.type = type
  }

  public fun build(): Rel.Op.Join = Rel.Op.Join(lhs = lhs!!, rhs = rhs!!, rex = rex!!, type =
      type!!)
}

public class RelOpAggregateBuilder(
  public var input: Rel? = null,
  public var strategy: Rel.Op.Aggregate.Strategy? = null,
  public var calls: MutableList<Rel.Op.Aggregate.Call> = mutableListOf(),
  public var groups: MutableList<Rex> = mutableListOf(),
) {
  public fun input(input: Rel?): RelOpAggregateBuilder = this.apply {
    this.input = input
  }

  public fun strategy(strategy: Rel.Op.Aggregate.Strategy?): RelOpAggregateBuilder = this.apply {
    this.strategy = strategy
  }

  public fun calls(calls: MutableList<Rel.Op.Aggregate.Call>): RelOpAggregateBuilder = this.apply {
    this.calls = calls
  }

  public fun groups(groups: MutableList<Rex>): RelOpAggregateBuilder = this.apply {
    this.groups = groups
  }

  public fun build(): Rel.Op.Aggregate = Rel.Op.Aggregate(input = input!!, strategy = strategy!!,
      calls = calls, groups = groups)
}

public class RelOpAggregateCallBuilder(
  public var agg: Ref? = null,
  public var setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier? = null,
  public var args: MutableList<Rex> = mutableListOf(),
) {
  public fun agg(agg: Ref?): RelOpAggregateCallBuilder = this.apply {
    this.agg = agg
  }

  public fun setQuantifier(setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier?):
      RelOpAggregateCallBuilder = this.apply {
    this.setQuantifier = setQuantifier
  }

  public fun args(args: MutableList<Rex>): RelOpAggregateCallBuilder = this.apply {
    this.args = args
  }

  public fun build(): Rel.Op.Aggregate.Call = Rel.Op.Aggregate.Call(agg = agg!!, setQuantifier =
      setQuantifier!!, args = args)
}

public class RelOpExcludeBuilder(
  public var input: Rel? = null,
  public var paths: MutableList<Rel.Op.Exclude.Path> = mutableListOf(),
) {
  public fun input(input: Rel?): RelOpExcludeBuilder = this.apply {
    this.input = input
  }

  public fun paths(paths: MutableList<Rel.Op.Exclude.Path>): RelOpExcludeBuilder = this.apply {
    this.paths = paths
  }

  public fun build(): Rel.Op.Exclude = Rel.Op.Exclude(input = input!!, paths = paths)
}

public class RelOpExcludePathBuilder(
  public var root: Rex.Op.Var? = null,
  public var steps: MutableList<Rel.Op.Exclude.Step> = mutableListOf(),
) {
  public fun root(root: Rex.Op.Var?): RelOpExcludePathBuilder = this.apply {
    this.root = root
  }

  public fun steps(steps: MutableList<Rel.Op.Exclude.Step>): RelOpExcludePathBuilder = this.apply {
    this.steps = steps
  }

  public fun build(): Rel.Op.Exclude.Path = Rel.Op.Exclude.Path(root = root!!, steps = steps)
}

public class RelOpExcludeStepBuilder(
  public var type: Rel.Op.Exclude.Type? = null,
  public var substeps: MutableList<Rel.Op.Exclude.Step> = mutableListOf(),
) {
  public fun type(type: Rel.Op.Exclude.Type?): RelOpExcludeStepBuilder = this.apply {
    this.type = type
  }

  public fun substeps(substeps: MutableList<Rel.Op.Exclude.Step>): RelOpExcludeStepBuilder =
      this.apply {
    this.substeps = substeps
  }

  public fun build(): Rel.Op.Exclude.Step = Rel.Op.Exclude.Step(type = type!!, substeps = substeps)
}

public class RelOpExcludeTypeStructSymbolBuilder(
  public var symbol: String? = null,
) {
  public fun symbol(symbol: String?): RelOpExcludeTypeStructSymbolBuilder = this.apply {
    this.symbol = symbol
  }

  public fun build(): Rel.Op.Exclude.Type.StructSymbol = Rel.Op.Exclude.Type.StructSymbol(symbol =
      symbol!!)
}

public class RelOpExcludeTypeStructKeyBuilder(
  public var key: String? = null,
) {
  public fun key(key: String?): RelOpExcludeTypeStructKeyBuilder = this.apply {
    this.key = key
  }

  public fun build(): Rel.Op.Exclude.Type.StructKey = Rel.Op.Exclude.Type.StructKey(key = key!!)
}

public class RelOpExcludeTypeCollIndexBuilder(
  public var index: Int? = null,
) {
  public fun index(index: Int?): RelOpExcludeTypeCollIndexBuilder = this.apply {
    this.index = index
  }

  public fun build(): Rel.Op.Exclude.Type.CollIndex = Rel.Op.Exclude.Type.CollIndex(index = index!!)
}

public class RelOpExcludeTypeStructWildcardBuilder() {
  public fun build(): Rel.Op.Exclude.Type.StructWildcard = Rel.Op.Exclude.Type.StructWildcard()
}

public class RelOpExcludeTypeCollWildcardBuilder() {
  public fun build(): Rel.Op.Exclude.Type.CollWildcard = Rel.Op.Exclude.Type.CollWildcard()
}

public class RelOpErrBuilder(
  public var message: String? = null,
) {
  public fun message(message: String?): RelOpErrBuilder = this.apply {
    this.message = message
  }

  public fun build(): Rel.Op.Err = Rel.Op.Err(message = message!!)
}

public class RelBindingBuilder(
  public var name: String? = null,
  public var type: StaticType? = null,
) {
  public fun name(name: String?): RelBindingBuilder = this.apply {
    this.name = name
  }

  public fun type(type: StaticType?): RelBindingBuilder = this.apply {
    this.type = type
  }

  public fun build(): Rel.Binding = Rel.Binding(name = name!!, type = type!!)
}
