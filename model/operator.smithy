$version: "2"

metadata validators = []
metadata suppressions = []

namespace org.partiql.sdk.operator

// TODOs
// ----------
// * RelCommon equivalent for Hint/Type information
// * Define Type
// * Define Value
// * Define the various helpers like sorts/aggregations/fields etc.

union Operator {
    rel: Rel
    rex: Rex
}

/// A Rel is an operator which returns a relation (collection of binding tuples).
union Rel {
    aggregate: RelAggregate
    correlate: RelCorrelate
    distinct: RelDistinct
    except: RelExcept
    exclude: RelExclude
    filter: RelFilter
    intersect: RelIntersect
    enumerate: RelEnumerate
    join: RelJoin
    limit: RelLimit
    offset: RelOffset
    project: RelProject
    scan: RelScan
    sort: RelSort
    union: RelUnion
    unpivot: RelUnpivot
}

/// The relation operator for aggregation.
structure RelAggregate {
    @required
    input: Rel
    @required
    measures: MeasureList
    @required
    groups: RexList
}

/// The relation operator for correlated subqueries and nested-loop joins.
structure RelCorrelate {
    @required
    left: Rel
    @required
    right: Rel
    @required
    joinType: JoinType
}

/// The relation operator for duplicate elimination.
structure RelDistinct {
    @required
    input: Rel
}

/// The relational operator for set (or multiset) difference.
structure RelExcept {
    @required
    isAll: Boolean
    @required
    left: Rel
    @required
    right: Rel
}

/// The relational operator for exclusion projections.
structure RelExclude {
    @required
    input: Rel
    @required
    exclusion: ExclusionList
}

/// The relational operator for filtering rows.
structure RelFilter {
    @required
    input: Rel
    @required
    predicate: Rex
}

/// The relational operator for set (or multiset) intersection.
structure RelIntersect {
    @required
    isAll: Boolean
    @required
    left: Rel
    @required
    right: Rel
}

/// The relational operator for an ordered scan with an index.
structure RelEnumerate {
    @required
    input: Rex
}

/// The relational operator for joining two relations.
structure RelJoin {
    @required
    left: Rel
    @required
    right: Rel
    @required
    joinType: JoinType
    // optional
    condition: Rex
}

/// The relational operator for limiting a relation's returned rows.
structure RelLimit {
    @required
    input: Rel
    @required
    limit: Rex
}

/// The relational operator for skipping rows.
structure RelOffset {
    @required
    input: Rel
    @required
    offset: Rex
}

/// The relational operator for projecting a value into a relation.
structure RelScan {
    @required
    input: Rex
}

structure RelSort {
    @required
    input: Rel
    @required
    collations: CollationList
}

/// The relational operator for set (or multiset) union.
structure RelUnion {
    @required
    isAll: Boolean
    @required
    left: Rel
    @required
    right: Rel
}

/// The relational operator to produce key-value pair rows from a value.
structure RelUnpivot {
    @required
    input: Rex
}

/// A Rex is an operator which returns a value.
union Rex {
    array: RexArray
    bag: RexBag
    call: RexCall
    case: RexCase
    cast: RexCast
    coalesce: RexCoalesce
    error: RexError
    lit: RexLit
    nullIf: RexNullIf
    path: RexPath
    pivot: RexPivot
    select: RexSelect
    spread: RexSpread
    struct: RexStruct
    subquery: RexSubquery
}

/// An expression (value operator) to construct an array value.
structure RexArray {
    @required
    values: RexList
}

/// An expression (value operator) to construct a bag value.
structure RexBag {
    @required
    values: RexList
}

/// An expression (value operator) for function calls.
union RexCall {
    static: RexCallStatic
    dynamic: RexCallDynamic
}

/// An expression (value operator) for a CASE-WHEN.
structure RexCase {
    @required
    match: Rex
}

/// An expression (value operator) for CAST(operand AS target)
structure RexCast {
    @required
    operand: Rex
    @required
    target: Type
}

/// An expression (value operator) for COALESCE(args...)
structure RexCoalesce {
    @required
    args: RexList
}

/// An expression (value operator) for a literal value.
structure RexLit {
    @required
    value: Value
}

/// An expression (value operator) for NULLIF(v1, v2).
structure RexNullIf {
    @required
    v1: Rex
    @required
    v2: Rex
}

/// An expression (value operator) for the various path operators.
union RexPath {
    /// a[0]
    index: RexPathIndex
    /// a['foo']
    key: RexPathKey
    /// a.foo
    symbol: RexPathSymbol
}

/// An expression (value operator) which produces a struct by mapping key-value pairs of a relation.
structure RexPivot {
    @required
    input: Rel
    @required
    key: Rex
    @required
    value: Rex
}

/// An expression (value operator) which produces a bag by evaluating the constructor.
structure RexSelect {
    @required
    input: Rel
    @required
    constructor: Rex
}

/// An expression (value operator) for the TUPLEUNION(args...) operator.
structure RexSpread {
    @required
    args: RexList
}

/// An expression (value operator) for the 
structure RexStruct {
    @required
    fields: FieldList
}

/// An expression (value operator) for the various SQL subquery forms.
union RexSubquery {
    /// a = (subquery)
    scalar: RexSubqueryScalar
    /// a IN (subquery)
    containment: RexSubqueryIn
    /// EXISTS/UNIQUE
    comparison: RexSubqueryComp
    /// ANY/ALL
    test: RexSubqueryTest
}

/// An expression (value operator) backed by a resolved table (global variable).
structure RexTable {
    name: Name
}

/// An expression (value operator) referencing a tuple variable in some scope.
structure RexVar {
    @required
    scope: Int
    @required
    offset: Int
}
