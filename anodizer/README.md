# Anodizer

Anodizer is a toolkit to generate data structures with Ion bindings; it supports a human-readable Ion text encoding, and an efficient packed binary Ion encoding.
It can be used for Ion message serde (like protobuf), as well as a Smithy SDK Protocol (like gRPC + protobuf).

## About

### What?

Anodizer is a toolkit to generate data structures with Ion bindings; it supports a human-readable Ion text encoding, and an efficient packed binary Ion encoding.
Anodizer integrates as with Smithy as a codegen backend, a serde layer, and an SDK protocol.

### Why?

Ion provides efficient readers and writers to its textual and binary encodings; however these are low-level APIs for consuming a value stream which are rote and tedious to work with.
Ion does not provide a way of defining structural types and mapping them to objects in languages such as Java or Rust.
Anodizer solves this by mapping algebraic types (compatible with host languages) from Smithy to an Ion encoding.


### How?

Users model their types with the Smithy (or the IDL); then invoke the anodizer tool to generate Kotlin and Rust data structures and serialization logic.
The tool generates a reader and writer for both a text and packed Ion encoding. The textual-encoding is self-describing and human readable; whereas the packed encoding is terse and efficient.

## Usage

* Anodizer can be used as a standalone codegen+serde solution via an anodizer model.
* Anodizer can be used as an extension to smithy codegen packages.

```shell
anodizer --help
```

## Examples

See `examples/` which cover the following questions.

_What is the anodizer?_

1. Mappings from algebraic types to the Ion data model.
2. Generate ISL for some mappings (text and packed).
3. Generate Kotlin and Rust for these mappings along with the readers/writers.

_What does it have to do with Smithy?_

1. Smithy is an IDL for services with operations and algebraic data types (show smithy model + ISL).
2. Smithy has generators for these shapes to kotlin, rust, typescript etc (show smithy-kotlin).
3. Using the anodizer mappings for the Smithy generated data shapes.
4. Using the anodizer mappings as a protocol for the Smithy generated AWS SDKs.

## Development

Wow so many packages! This is confusing ... what is going on??

### Packages

* anodizer (cli)
* anodizer-core (model and interfaces)
* anodizer-lang (idl)
* anodizer-target
  * anodizer-target-isl
  * anodizer-target-kotlin (+runtime)
  * anodizer-target-rust (+runtime)
* anodizer-target-util (codegen)

### Smithy Integration

Smithy has core + aws codegen packages; the anodizer provides a package to add Ion serde to both core smithy codegen
and the aws sdk codegen as a protocol. The convention is use the core+aws package names prefixed with "smithy-anodizer".

* smithy-anodizer (core)
  * smithy-anodizer
  * smithy-anodizer-kotlin
  * smithy-anodizer-rust
* smithy-anodizer-aws (sdk)
  * smithy-anodizer-aws-kotlin
  * smithy-anodizer-aws-rust

## FAQs

* How can I use this?
  * Please see usage.
* How can I use this with Smithy?
  * See examples/ which has several smithy examples
* How does this relate to Ion Schema Language (ISL)?
  * It differs from Ion Schema Language (ISL) insofar as the anodizer is used to define algebraic types for use in host languages rather than describing constraints on Ion values themselves. Anodizer (and Smithy) is designed to have idiomatic type definitions across application languages, and emphasizes encoding efficiency. The generated code produces optimized readers and writers for both a text and packed Ion encoding to bridge the gap between human-readable and terse binary encodings.
