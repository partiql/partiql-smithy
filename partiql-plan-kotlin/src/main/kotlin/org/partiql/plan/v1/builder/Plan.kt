@file:JvmName("Plan")
@file:OptIn(PartiQLValueExperimental::class)

package org.partiql.plan.v1.builder

import org.partiql.plan.v1.Catalog
import org.partiql.plan.v1.Identifier
import org.partiql.plan.v1.PartiQLPlan
import org.partiql.plan.v1.Ref
import org.partiql.plan.v1.Rel
import org.partiql.plan.v1.Rex
import org.partiql.plan.v1.Statement
import kotlin.Int
import kotlin.OptIn
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.jvm.JvmName
import org.partiql.`value`.PartiQLValue
import org.partiql.`value`.PartiQLValueExperimental
import org.partiql.`value`.PartiQLValueType
import org.partiql.types.StaticType

public fun partiQLPlan(catalogs: List<Catalog>, statement: Statement): PartiQLPlan =
    PartiQLPlan(catalogs, statement)

public fun catalog(name: String, items: List<Catalog.Item>): Catalog = Catalog(name, items)

public fun catalogItemValue(path: List<String>, type: StaticType): Catalog.Item.Value =
    Catalog.Item.Value(path, type)

public fun catalogItemFn(path: List<String>, specific: String): Catalog.Item.Fn =
    Catalog.Item.Fn(path, specific)

public fun catalogItemAgg(path: List<String>, specific: String): Catalog.Item.Agg =
    Catalog.Item.Agg(path, specific)

public fun ref(catalog: Int, symbol: Int): Ref = Ref(catalog, symbol)

public fun refCast(input: PartiQLValueType, target: PartiQLValueType): Ref.Cast = Ref.Cast(input,
    target)

public fun statementQuery(root: Rex): Statement.Query = Statement.Query(root)

public fun identifierSymbol(symbol: String, caseSensitivity: Identifier.CaseSensitivity):
    Identifier.Symbol = Identifier.Symbol(symbol, caseSensitivity)

public fun identifierQualified(root: Identifier.Symbol, steps: List<Identifier.Symbol>):
    Identifier.Qualified = Identifier.Qualified(root, steps)

public fun rex(type: StaticType, op: Rex.Op): Rex = Rex(type, op)

public fun rexOpLit(`value`: PartiQLValue): Rex.Op.Lit = Rex.Op.Lit(value)

public fun rexOpVar(depth: Int, ref: Int): Rex.Op.Var = Rex.Op.Var(depth, ref)

public fun rexOpGlobal(ref: Ref): Rex.Op.Global = Rex.Op.Global(ref)

public fun rexOpPathIndex(root: Rex, key: Rex): Rex.Op.Path.Index = Rex.Op.Path.Index(root, key)

public fun rexOpPathKey(root: Rex, key: Rex): Rex.Op.Path.Key = Rex.Op.Path.Key(root, key)

public fun rexOpPathSymbol(root: Rex, key: String): Rex.Op.Path.Symbol = Rex.Op.Path.Symbol(root,
    key)

public fun rexOpCast(cast: Ref.Cast, arg: Rex): Rex.Op.Cast = Rex.Op.Cast(cast, arg)

public fun rexOpCallStatic(fn: Ref, args: List<Rex>): Rex.Op.Call.Static = Rex.Op.Call.Static(fn,
    args)

public fun rexOpCallDynamic(args: List<Rex>, candidates: List<Rex.Op.Call.Dynamic.Candidate>):
    Rex.Op.Call.Dynamic = Rex.Op.Call.Dynamic(args, candidates)

public fun rexOpCallDynamicCandidate(
  fn: Ref,
  parameters: List<PartiQLValueType>,
  coercions: List<Ref.Cast?>,
): Rex.Op.Call.Dynamic.Candidate = Rex.Op.Call.Dynamic.Candidate(fn, parameters, coercions)

public fun rexOpCase(branches: List<Rex.Op.Case.Branch>, default: Rex): Rex.Op.Case =
    Rex.Op.Case(branches, default)

public fun rexOpCaseBranch(condition: Rex, rex: Rex): Rex.Op.Case.Branch =
    Rex.Op.Case.Branch(condition, rex)

public fun rexOpCollection(values: List<Rex>): Rex.Op.Collection = Rex.Op.Collection(values)

public fun rexOpStruct(fields: List<Rex.Op.Struct.Field>): Rex.Op.Struct = Rex.Op.Struct(fields)

public fun rexOpStructField(k: Rex, v: Rex): Rex.Op.Struct.Field = Rex.Op.Struct.Field(k, v)

public fun rexOpPivot(
  key: Rex,
  `value`: Rex,
  rel: Rel,
): Rex.Op.Pivot = Rex.Op.Pivot(key, value, rel)

public fun rexOpSubquery(
  `constructor`: Rex,
  rel: Rel,
  coercion: Rex.Op.Subquery.Coercion,
): Rex.Op.Subquery = Rex.Op.Subquery(constructor, rel, coercion)

public fun rexOpSelect(`constructor`: Rex, rel: Rel): Rex.Op.Select = Rex.Op.Select(constructor,
    rel)

public fun rexOpTupleUnion(args: List<Rex>): Rex.Op.TupleUnion = Rex.Op.TupleUnion(args)

public fun rexOpErr(message: String): Rex.Op.Err = Rex.Op.Err(message)

public fun rel(type: Rel.Type, op: Rel.Op): Rel = Rel(type, op)

public fun relType(schema: List<Rel.Binding>, props: Set<Rel.Prop>): Rel.Type = Rel.Type(schema,
    props)

public fun relOpScan(rex: Rex): Rel.Op.Scan = Rel.Op.Scan(rex)

public fun relOpScanIndexed(rex: Rex): Rel.Op.ScanIndexed = Rel.Op.ScanIndexed(rex)

public fun relOpUnpivot(rex: Rex): Rel.Op.Unpivot = Rel.Op.Unpivot(rex)

public fun relOpDistinct(input: Rel): Rel.Op.Distinct = Rel.Op.Distinct(input)

public fun relOpFilter(input: Rel, predicate: Rex): Rel.Op.Filter = Rel.Op.Filter(input, predicate)

public fun relOpSort(input: Rel, specs: List<Rel.Op.Sort.Spec>): Rel.Op.Sort = Rel.Op.Sort(input,
    specs)

public fun relOpSortSpec(rex: Rex, order: Rel.Op.Sort.Order): Rel.Op.Sort.Spec =
    Rel.Op.Sort.Spec(rex, order)

public fun relOpUnion(lhs: Rel, rhs: Rel): Rel.Op.Union = Rel.Op.Union(lhs, rhs)

public fun relOpIntersect(lhs: Rel, rhs: Rel): Rel.Op.Intersect = Rel.Op.Intersect(lhs, rhs)

public fun relOpExcept(lhs: Rel, rhs: Rel): Rel.Op.Except = Rel.Op.Except(lhs, rhs)

public fun relOpLimit(input: Rel, limit: Rex): Rel.Op.Limit = Rel.Op.Limit(input, limit)

public fun relOpOffset(input: Rel, offset: Rex): Rel.Op.Offset = Rel.Op.Offset(input, offset)

public fun relOpProject(input: Rel, projections: List<Rex>): Rel.Op.Project = Rel.Op.Project(input,
    projections)

public fun relOpJoin(
  lhs: Rel,
  rhs: Rel,
  rex: Rex,
  type: Rel.Op.Join.Type,
): Rel.Op.Join = Rel.Op.Join(lhs, rhs, rex, type)

public fun relOpAggregate(
  input: Rel,
  strategy: Rel.Op.Aggregate.Strategy,
  calls: List<Rel.Op.Aggregate.Call>,
  groups: List<Rex>,
): Rel.Op.Aggregate = Rel.Op.Aggregate(input, strategy, calls, groups)

public fun relOpAggregateCall(
  agg: Ref,
  setQuantifier: Rel.Op.Aggregate.Call.SetQuantifier,
  args: List<Rex>,
): Rel.Op.Aggregate.Call = Rel.Op.Aggregate.Call(agg, setQuantifier, args)

public fun relOpExclude(input: Rel, paths: List<Rel.Op.Exclude.Path>): Rel.Op.Exclude =
    Rel.Op.Exclude(input, paths)

public fun relOpExcludePath(root: Rex.Op.Var, steps: List<Rel.Op.Exclude.Step>): Rel.Op.Exclude.Path
    = Rel.Op.Exclude.Path(root, steps)

public fun relOpExcludeStep(type: Rel.Op.Exclude.Type, substeps: List<Rel.Op.Exclude.Step>):
    Rel.Op.Exclude.Step = Rel.Op.Exclude.Step(type, substeps)

public fun relOpExcludeTypeStructSymbol(symbol: String): Rel.Op.Exclude.Type.StructSymbol =
    Rel.Op.Exclude.Type.StructSymbol(symbol)

public fun relOpExcludeTypeStructKey(key: String): Rel.Op.Exclude.Type.StructKey =
    Rel.Op.Exclude.Type.StructKey(key)

public fun relOpExcludeTypeCollIndex(index: Int): Rel.Op.Exclude.Type.CollIndex =
    Rel.Op.Exclude.Type.CollIndex(index)

public fun relOpExcludeTypeStructWildcard(): Rel.Op.Exclude.Type.StructWildcard =
    Rel.Op.Exclude.Type.StructWildcard()

public fun relOpExcludeTypeCollWildcard(): Rel.Op.Exclude.Type.CollWildcard =
    Rel.Op.Exclude.Type.CollWildcard()

public fun relOpErr(message: String): Rel.Op.Err = Rel.Op.Err(message)

public fun relBinding(name: String, type: StaticType): Rel.Binding = Rel.Binding(name, type)
