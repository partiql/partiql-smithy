$version: "2"

metadata validators = []
metadata suppressions = []

namespace org.partiql.sdk.core

string Catalog

list Catalogs {
    member: Catalog
}

list Namespace {
    member: String
}

structure Name {
    @default([])
    namespace: Namespace

    @required
    name: String
}

map Properties {
    key: String
    value: String
}
