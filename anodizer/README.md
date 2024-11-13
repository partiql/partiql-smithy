# Anodizer

Anodizer is a toolkit to generate data structures with Ion bindings; it supports a human-readable Ion text encoding, and an efficient packed binary Ion encoding.

## Goals

* Smithy as the IDL.
* Get it committed to Smithy — not a pet project.
* ISL codegen solves a different problem, and we are happy to have both options.
* Ion 1.1 provides many opportunities for an efficient application-level encoding.

## FAQs

* But Smithy!? – see smithy-anodizer
* But ISL!? – see docs/index.adoc#why
* But Protobuf!? – yes, that's a better version of this, but for packed encodings, whereas we have a text bijection.
* But another IDL!? – you don't have to use it ... it's for indirection.

## Demo

_What is the anodizer?_

1. Mappings from algebraic types to the Ion data model.
2. Generate ISL for some mappings (text and packed).
3. Generate Kotlin and Rust for these mappings along with the readers/writers.

_What does it have to do with Smithy?_

1. Smithy is an IDL for services with operations and algebraic data types (show smithy model + ISL).
2. Smithy has generators for these shapes to kotlin, rust, typescript etc (show smithy-kotlin).
3. Using the anodizer mappings for the Smithy generated data shapes.
4. Using the anodizer mappings as a protocol for the Smithy generated AWS SDKs.

## Usage

* anodizer can be used as a standalone codegen+serde solution via an anodizer model.
* anodizer can be used as an extension to smithy codegen packages.

## Development

Wow so many packages! This is confusing ... what is going on??

### Components

* anodizer (cli)
* anodizer-core (model and interfaces)
* anodizer-lang (ion-based IDL)
* anodizer-target
  * anodizer-target-isl
  * anodizer-target-kotlin (+runtime)
  * anodizer-target-rust (+runtime)
* anodizer-target-common

### Smithy Integration

Smithy has core + aws codegen packages; the anodizer provides a package to add Ion serde to both core smithy codegen
and the aws sdk codegen as a protocol. The convention is use the core+aws package names prefixed with "smithy-anodizer".

* smithy-anodizer
  * smithy-anodizer
  * smithy-anodizer-kotlin
  * smithy-anodizer-rust
* smithy-anodizer-aws
  * smithy-anodizer-aws-kotlin
  * smithy-anodizer-aws-rust
