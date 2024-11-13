$version: "2"

metadata validators = []
metadata suppressions = []

namespace org.partiql.sdk

use org.partiql.sdk.core#Catalog
use org.partiql.sdk.core#Catalogs
use org.partiql.sdk.core#Name
use org.partiql.sdk.core#Properties

/// ---------------------------------------------
/// PartiQL Database Service (PDBS)
/// ---------------------------------------------
///
/// References
///  * https://arrow.apache.org/adbc/current/java/api/org/apache/arrow/adbc/core/package-summary.html
///  * https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html
///
service Database {
    version: "1"
    operations: [
        GetInfo
        GetCatalogs
        GetCatalog
        GetTable
        // Exec(text)
        // Exec(plan)
    ]
}

@readonly
operation GetInfo {
    input: GetInfoInput
    output: GetInfoOutput
}

@input
structure GetInfoInput {}

@output
structure GetInfoOutput {
    name: String
    version: String
    properties: Properties
}

@readonly
operation GetCatalogs {
    input: GetCatalogsInput
    output: GetCatalogsOutput
}

@input
structure GetCatalogsInput {}

@output
structure GetCatalogsOutput {
    catalogs: Catalogs
}

@readonly
operation GetCatalog {
    input: GetCatalogInput
    output: GetCatalogOutput
}

@input
structure GetCatalogInput {
    catalog: Catalog
    properties: Properties
}

@output
structure GetCatalogOutput {
    catalogs: Catalogs
}

@readonly
operation GetTable {
    input: GetTableInput
    output: GetTableOutput
}

@input
structure GetTableInput {
    catalog: Catalog
    name: Name
}

@output
structure GetTableOutput {
    placeholder: String
}
