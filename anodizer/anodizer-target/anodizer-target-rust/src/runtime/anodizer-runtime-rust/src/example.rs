#![allow(dead_code)]
#![allow(non_snake_case)]
#![allow(unused_imports)]
use crate::arena::Arena;
use crate::reader::RidlReader;
use crate::result::RidlResult;
use crate::writer::RidlWriter;
use bumpalo::Bump;
use derive_new::new;
use ion_rs::data_source::ToIonDataSource;
use ion_rs::element::*;
use ion_rs::types::Coefficient;
use ion_rs::*;
use std::fmt;
use std::io::Write;

pub type BoxBool = bool;
pub type BoxInt = i64;
pub type BoxFloat = f64;
pub type BoxDecimal = Decimal;
pub type BoxStr = String;
pub type BoxBlob = Blob;
pub type BoxClob = Clob;
pub type DecimalA = Decimal;
pub type DecimalB = Decimal;
pub type DecimalC = Decimal;
pub type BlobA = Blob;
pub type ClobB = Clob;
pub type ArrA = Vec<bool>;
pub type ArrB = Vec<bool>;
pub type ArrC = Vec<BoxBool>;
pub type ArrD = Vec<BoxBool>;
pub type ArrNested = Vec<Vec<bool>>;
#[derive(Debug, PartialEq)]
pub enum EnumA {
    HelloWorld,
    GoodnightMoon,
}
#[derive(Debug, PartialEq)]
pub enum EnumB {
    A,
    B,
    C,
}
pub type Nil = ();
#[derive(new, Debug, PartialEq)]
pub struct StructA {
    pub x: bool,
    pub y: i64,
    pub z: String,
}
#[derive(new, Debug, PartialEq)]
pub struct StructB {
    pub x: Vec<bool>,
    pub y: Vec<i64>,
    pub z: Vec<String>,
}
#[derive(new, Debug, PartialEq)]
pub struct StructC<'a> {
    pub x: &'a Decimal,
    pub y: &'a Blob,
    pub z: &'a Clob,
}
#[derive(new, Debug, PartialEq)]
pub struct StructD<'a> {
    pub a: &'a StructA,
    pub b: &'a StructB,
}
pub type UnionAA = bool;
pub type UnionAB = i64;
pub type UnionAC = String;

#[derive(Debug, PartialEq)]
pub enum UnionA {
    A(UnionAA),
    B(UnionAB),
    C(UnionAC),
}
#[derive(new, Debug, PartialEq)]
pub struct UnionBA {
    pub x: bool,
    pub y: i64,
    pub z: String,
}
#[derive(new, Debug, PartialEq)]
pub struct UnionBB {
    pub x: Vec<bool>,
    pub y: Vec<i64>,
    pub z: Vec<String>,
}
#[derive(new, Debug, PartialEq)]
pub struct UnionBC<'a> {
    pub x: &'a Decimal,
    pub y: &'a Blob,
    pub z: &'a Clob,
}

#[derive(Debug, PartialEq)]
pub enum UnionB<'a> {
    A(UnionBA),
    B(UnionBB),
    C(UnionBC<'a>),
}
pub type UnionCAA = bool;
pub type UnionCAB = i64;
pub type UnionCAC = String;

#[derive(Debug, PartialEq)]
pub enum UnionCA {
    A(UnionCAA),
    B(UnionCAB),
    C(UnionCAC),
}
pub type UnionCBA = Vec<bool>;
pub type UnionCBB = Vec<i64>;
pub type UnionCBC = Vec<String>;

#[derive(Debug, PartialEq)]
pub enum UnionCB {
    A(UnionCBA),
    B(UnionCBB),
    C(UnionCBC),
}

#[derive(Debug, PartialEq)]
pub enum UnionC {
    A(UnionCA),
    B(UnionCB),
}
#[derive(new, Debug, PartialEq)]
pub struct UnionDA {
    pub x: bool,
    pub y: i64,
    pub z: String,
}
#[derive(new, Debug, PartialEq)]
pub struct UnionDB {
    pub x: Vec<bool>,
    pub y: Vec<i64>,
    pub z: Vec<String>,
}
#[derive(new, Debug, PartialEq)]
pub struct UnionDC<'a> {
    pub x: &'a Decimal,
    pub y: &'a Blob,
    pub z: &'a Clob,
}
#[derive(new, Debug, PartialEq)]
pub struct UnionDD<'a> {
    pub a: &'a StructA,
    pub b: &'a StructB,
}

#[derive(Debug, PartialEq)]
pub enum UnionD<'a> {
    A(UnionDA),
    B(UnionDB),
    C(UnionDC<'a>),
    D(UnionDD<'a>),
}
#[derive(new, Debug, PartialEq)]
pub struct AliasToStructA {
    pub x: bool,
    pub y: i64,
    pub z: String,
}

pub struct KitchenFactory {
    bump: Bump,
}

impl KitchenFactory {
    pub fn new() -> KitchenFactory {
        KitchenFactory { bump: Bump::new() }
    }
}

impl KitchenFactory {
    pub fn decimal(&self, coefficient: i64, exponent: i64) -> &mut Decimal {
        self.bump.alloc(Decimal::new(coefficient, exponent))
    }

    pub fn blob(&self, bytes: Vec<u8>) -> &mut Blob {
        self.bump.alloc(Blob(Bytes::from(bytes)))
    }

    pub fn clob(&self, bytes: Vec<u8>) -> &mut Clob {
        self.bump.alloc(Clob(Bytes::from(bytes)))
    }

    pub fn struct_a(&self, x: bool, y: i64, z: String) -> &mut StructA {
        self.bump.alloc(StructA { x, y, z })
    }

    pub fn struct_b(&self, x: Vec<bool>, y: Vec<i64>, z: Vec<String>) -> &mut StructB {
        self.bump.alloc(StructB { x, y, z })
    }

    pub fn struct_c<'a>(&self, x: &'a Decimal, y: &'a Blob, z: &'a Clob) -> &mut StructC<'a> {
        self.bump.alloc(StructC { x, y, z })
    }

    pub fn struct_d<'a>(&self, a: &'a StructA, b: &'a StructB) -> &mut StructD<'a> {
        self.bump.alloc(StructD { a, b })
    }

    pub fn union_b_a(&self, x: bool, y: i64, z: String) -> &mut UnionBA {
        self.bump.alloc(UnionBA { x, y, z })
    }

    pub fn union_b_b(&self, x: Vec<bool>, y: Vec<i64>, z: Vec<String>) -> &mut UnionBB {
        self.bump.alloc(UnionBB { x, y, z })
    }

    pub fn union_b_c<'a>(&self, x: &'a Decimal, y: &'a Blob, z: &'a Clob) -> &mut UnionBC<'a> {
        self.bump.alloc(UnionBC { x, y, z })
    }

    pub fn union_d_a(&self, x: bool, y: i64, z: String) -> &mut UnionDA {
        self.bump.alloc(UnionDA { x, y, z })
    }

    pub fn union_d_b(&self, x: Vec<bool>, y: Vec<i64>, z: Vec<String>) -> &mut UnionDB {
        self.bump.alloc(UnionDB { x, y, z })
    }

    pub fn union_d_c<'a>(&self, x: &'a Decimal, y: &'a Blob, z: &'a Clob) -> &mut UnionDC<'a> {
        self.bump.alloc(UnionDC { x, y, z })
    }

    pub fn union_d_d<'a>(&self, a: &'a StructA, b: &'a StructB) -> &mut UnionDD<'a> {
        self.bump.alloc(UnionDD { a, b })
    }

    pub fn alias_to_struct_a(&self, x: bool, y: i64, z: String) -> &mut AliasToStructA {
        self.bump.alloc(AliasToStructA { x, y, z })
    }
}
pub trait KitchenWriter {
    fn write_box_bool<'a>(&mut self, value: &'a BoxBool) -> RidlResult<()>;
    fn write_box_int<'a>(&mut self, value: &'a BoxInt) -> RidlResult<()>;
    fn write_box_float<'a>(&mut self, value: &'a BoxFloat) -> RidlResult<()>;
    fn write_box_decimal<'a>(&mut self, value: &'a BoxDecimal) -> RidlResult<()>;
    fn write_box_str<'a>(&mut self, value: &'a BoxStr) -> RidlResult<()>;
    fn write_box_blob<'a>(&mut self, value: &'a BoxBlob) -> RidlResult<()>;
    fn write_box_clob<'a>(&mut self, value: &'a BoxClob) -> RidlResult<()>;
    fn write_decimal_a<'a>(&mut self, value: &'a DecimalA) -> RidlResult<()>;
    fn write_decimal_b<'a>(&mut self, value: &'a DecimalB) -> RidlResult<()>;
    fn write_decimal_c<'a>(&mut self, value: &'a DecimalC) -> RidlResult<()>;
    fn write_blob_a<'a>(&mut self, value: &'a BlobA) -> RidlResult<()>;
    fn write_clob_b<'a>(&mut self, value: &'a ClobB) -> RidlResult<()>;
    fn write_arr_a<'a>(&mut self, value: &'a ArrA) -> RidlResult<()>;
    fn write_arr_b<'a>(&mut self, value: &'a ArrB) -> RidlResult<()>;
    fn write_arr_c<'a>(&mut self, value: &'a ArrC) -> RidlResult<()>;
    fn write_arr_d<'a>(&mut self, value: &'a ArrD) -> RidlResult<()>;
    fn write_arr_nested<'a>(&mut self, value: &'a ArrNested) -> RidlResult<()>;
    fn write_enum_a<'a>(&mut self, value: &'a EnumA) -> RidlResult<()>;
    fn write_enum_b<'a>(&mut self, value: &'a EnumB) -> RidlResult<()>;
    fn write_nil<'a>(&mut self, value: &'a Nil) -> RidlResult<()>;
    fn write_struct_a<'a>(&mut self, value: &'a StructA) -> RidlResult<()>;
    fn write_struct_b<'a>(&mut self, value: &'a StructB) -> RidlResult<()>;
    fn write_struct_c<'a>(&mut self, value: &'a StructC<'a>) -> RidlResult<()>;
    fn write_struct_d<'a>(&mut self, value: &'a StructD<'a>) -> RidlResult<()>;
    fn write_union_a<'a>(&mut self, value: &'a UnionA) -> RidlResult<()>;
    fn write_union_a_a<'a>(&mut self, value: &'a UnionAA) -> RidlResult<()>;
    fn write_union_a_b<'a>(&mut self, value: &'a UnionAB) -> RidlResult<()>;
    fn write_union_a_c<'a>(&mut self, value: &'a UnionAC) -> RidlResult<()>;
    fn write_union_b<'a>(&mut self, value: &'a UnionB<'a>) -> RidlResult<()>;
    fn write_union_b_a<'a>(&mut self, value: &'a UnionBA) -> RidlResult<()>;
    fn write_union_b_b<'a>(&mut self, value: &'a UnionBB) -> RidlResult<()>;
    fn write_union_b_c<'a>(&mut self, value: &'a UnionBC<'a>) -> RidlResult<()>;
    fn write_union_c<'a>(&mut self, value: &'a UnionC) -> RidlResult<()>;
    fn write_union_c_a<'a>(&mut self, value: &'a UnionCA) -> RidlResult<()>;
    fn write_union_c_a_a<'a>(&mut self, value: &'a UnionCAA) -> RidlResult<()>;
    fn write_union_c_a_b<'a>(&mut self, value: &'a UnionCAB) -> RidlResult<()>;
    fn write_union_c_a_c<'a>(&mut self, value: &'a UnionCAC) -> RidlResult<()>;
    fn write_union_c_b<'a>(&mut self, value: &'a UnionCB) -> RidlResult<()>;
    fn write_union_c_b_a<'a>(&mut self, value: &'a UnionCBA) -> RidlResult<()>;
    fn write_union_c_b_b<'a>(&mut self, value: &'a UnionCBB) -> RidlResult<()>;
    fn write_union_c_b_c<'a>(&mut self, value: &'a UnionCBC) -> RidlResult<()>;
    fn write_union_d<'a>(&mut self, value: &'a UnionD<'a>) -> RidlResult<()>;
    fn write_union_d_a<'a>(&mut self, value: &'a UnionDA) -> RidlResult<()>;
    fn write_union_d_b<'a>(&mut self, value: &'a UnionDB) -> RidlResult<()>;
    fn write_union_d_c<'a>(&mut self, value: &'a UnionDC<'a>) -> RidlResult<()>;
    fn write_union_d_d<'a>(&mut self, value: &'a UnionDD<'a>) -> RidlResult<()>;
    fn write_alias_to_struct_a<'a>(&mut self, value: &'a AliasToStructA) -> RidlResult<()>;
}

pub struct KitchenWriterBuilder {}

impl KitchenWriterBuilder {
    pub fn text<W: Write>(sink: W) -> impl KitchenWriter {
        let writer = TextWriterBuilder::default()
            .build(sink)
            .expect("Failed to instantiate IonWriter.");
        KitchenWriterText {
            writer: RidlWriter::new(writer),
        }
    }

    pub fn packed<W: Write>(sink: W) -> impl KitchenWriter {
        let writer = BinaryWriterBuilder::default()
            .build(sink)
            .expect("Failed to instantiate IonWriter.");
        KitchenWriterText {
            writer: RidlWriter::new(writer),
        }
    }
}

pub struct KitchenWriterText<W, I>
where
    I: IonWriter<Output = W>,
{
    writer: RidlWriter<W, I>,
}

impl<W, I> KitchenWriterText<W, I>
where
    I: IonWriter<Output = W>,
{
    pub fn from(writer: I) -> KitchenWriterText<W, I> {
        KitchenWriterText {
            writer: RidlWriter::new(writer),
        }
    }
}

impl<W, I> KitchenWriter for KitchenWriterText<W, I>
where
    I: IonWriter<Output = W>,
{
    fn write_box_bool<'a>(&mut self, value: &'a BoxBool) -> RidlResult<()> {
        self.writer.set_tag("box_bool");
        self.writer.write_bool(*value)
    }

    fn write_box_int<'a>(&mut self, value: &'a BoxInt) -> RidlResult<()> {
        self.writer.set_tag("box_int");
        self.writer.write_int(*value)
    }

    fn write_box_float<'a>(&mut self, value: &'a BoxFloat) -> RidlResult<()> {
        self.writer.set_tag("box_float");
        self.writer.write_float(*value)
    }

    fn write_box_decimal<'a>(&mut self, value: &'a BoxDecimal) -> RidlResult<()> {
        self.writer.set_tag("box_decimal");
        self.writer.write_decimal(value, None, None)
    }

    fn write_box_str<'a>(&mut self, value: &'a BoxStr) -> RidlResult<()> {
        self.writer.set_tag("box_str");
        self.writer.write_string(value)
    }

    fn write_box_blob<'a>(&mut self, value: &'a BoxBlob) -> RidlResult<()> {
        self.writer.set_tag("box_blob");
        self.writer.write_blob(value, None)
    }

    fn write_box_clob<'a>(&mut self, value: &'a BoxClob) -> RidlResult<()> {
        self.writer.set_tag("box_clob");
        self.writer.write_clob(value, None)
    }

    fn write_decimal_a<'a>(&mut self, value: &'a DecimalA) -> RidlResult<()> {
        self.writer.set_tag("decimal_a");
        self.writer.write_decimal(value, Some(1), Some(2))
    }

    fn write_decimal_b<'a>(&mut self, value: &'a DecimalB) -> RidlResult<()> {
        self.writer.set_tag("decimal_b");
        self.writer.write_decimal(value, Some(1), None)
    }

    fn write_decimal_c<'a>(&mut self, value: &'a DecimalC) -> RidlResult<()> {
        self.writer.set_tag("decimal_c");
        self.writer.write_decimal(value, None, Some(2))
    }

    fn write_blob_a<'a>(&mut self, value: &'a BlobA) -> RidlResult<()> {
        self.writer.set_tag("blob_a");
        self.writer.write_blob(value, Some(1))
    }

    fn write_clob_b<'a>(&mut self, value: &'a ClobB) -> RidlResult<()> {
        self.writer.set_tag("clob_b");
        self.writer.write_clob(value, Some(1))
    }

    fn write_arr_a<'a>(&mut self, value: &'a ArrA) -> RidlResult<()> {
        self.writer.set_tag("arr_a");
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_b<'a>(&mut self, value: &'a ArrB) -> RidlResult<()> {
        self.writer.set_tag("arr_b");
        self.writer.write_array_start(value, Some(10))?;
        for v_0 in value {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_c<'a>(&mut self, value: &'a ArrC) -> RidlResult<()> {
        self.writer.set_tag("arr_c");
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.write_box_bool(v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_d<'a>(&mut self, value: &'a ArrD) -> RidlResult<()> {
        self.writer.set_tag("arr_d");
        self.writer.write_array_start(value, Some(10))?;
        for v_0 in value {
            self.write_box_bool(v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_nested<'a>(&mut self, value: &'a ArrNested) -> RidlResult<()> {
        self.writer.set_tag("arr_nested");
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_array_start(v_0, None)?;
            for v_1 in v_0 {
                self.writer.write_bool(*v_1)?;
            }
            self.writer.write_array_end()?;
        }
        self.writer.write_array_end()
    }

    fn write_enum_a<'a>(&mut self, value: &'a EnumA) -> RidlResult<()> {
        self.writer.set_tag("enum_a");
        match value {
            EnumA::HelloWorld => self.writer.write_symbol("HELLO_WORLD"),
            EnumA::GoodnightMoon => self.writer.write_symbol("GOODNIGHT_MOON"),
        }
    }

    fn write_enum_b<'a>(&mut self, value: &'a EnumB) -> RidlResult<()> {
        self.writer.set_tag("enum_b");
        match value {
            EnumB::A => self.writer.write_symbol("A"),
            EnumB::B => self.writer.write_symbol("B"),
            EnumB::C => self.writer.write_symbol("C"),
        }
    }

    fn write_nil<'a>(&mut self, value: &'a Nil) -> RidlResult<()> {
        self.writer.set_tag("nil");
        self.writer.write_symbol("unit")
    }

    fn write_struct_a<'a>(&mut self, value: &'a StructA) -> RidlResult<()> {
        self.writer.set_tag("struct_a");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }

    fn write_struct_b<'a>(&mut self, value: &'a StructB) -> RidlResult<()> {
        self.writer.set_tag("struct_b");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_array_start(&value.x, None)?;
        for v_0 in &value.x {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("y");
        self.writer.write_array_start(&value.y, None)?;
        for v_0 in &value.y {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("z");
        self.writer.write_array_start(&value.z, None)?;
        for v_0 in &value.z {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.step_out()
    }

    fn write_struct_c<'a>(&mut self, value: &'a StructC<'a>) -> RidlResult<()> {
        self.writer.set_tag("struct_c");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_decimal(&value.x, Some(1), Some(2))?;
        self.writer.set_field_name("y");
        self.writer.write_blob(&value.y, Some(10))?;
        self.writer.set_field_name("z");
        self.writer.write_clob(&value.z, Some(10))?;
        self.writer.step_out()
    }

    fn write_struct_d<'a>(&mut self, value: &'a StructD<'a>) -> RidlResult<()> {
        self.writer.set_tag("struct_d");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("a");
        self.write_struct_a(&value.a)?;
        self.writer.set_field_name("b");
        self.write_struct_b(&value.b)?;
        self.writer.step_out()
    }

    fn write_union_a<'a>(&mut self, value: &'a UnionA) -> RidlResult<()> {
        match value {
            UnionA::A(v) => self.write_union_a_a(v),
            UnionA::B(v) => self.write_union_a_b(v),
            UnionA::C(v) => self.write_union_a_c(v),
        }
    }

    fn write_union_a_a<'a>(&mut self, value: &'a UnionAA) -> RidlResult<()> {
        self.writer.set_tag("union_a.a");
        self.writer.write_bool(*value)
    }

    fn write_union_a_b<'a>(&mut self, value: &'a UnionAB) -> RidlResult<()> {
        self.writer.set_tag("union_a.b");
        self.writer.write_int(*value)
    }

    fn write_union_a_c<'a>(&mut self, value: &'a UnionAC) -> RidlResult<()> {
        self.writer.set_tag("union_a.c");
        self.writer.write_string(value)
    }

    fn write_union_b<'a>(&mut self, value: &'a UnionB<'a>) -> RidlResult<()> {
        match value {
            UnionB::A(v) => self.write_union_b_a(v),
            UnionB::B(v) => self.write_union_b_b(v),
            UnionB::C(v) => self.write_union_b_c(v),
        }
    }

    fn write_union_b_a<'a>(&mut self, value: &'a UnionBA) -> RidlResult<()> {
        self.writer.set_tag("union_b.a");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }

    fn write_union_b_b<'a>(&mut self, value: &'a UnionBB) -> RidlResult<()> {
        self.writer.set_tag("union_b.b");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_array_start(&value.x, None)?;
        for v_0 in &value.x {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("y");
        self.writer.write_array_start(&value.y, None)?;
        for v_0 in &value.y {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("z");
        self.writer.write_array_start(&value.z, None)?;
        for v_0 in &value.z {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.step_out()
    }

    fn write_union_b_c<'a>(&mut self, value: &'a UnionBC<'a>) -> RidlResult<()> {
        self.writer.set_tag("union_b.c");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_decimal(&value.x, Some(1), Some(2))?;
        self.writer.set_field_name("y");
        self.writer.write_blob(&value.y, Some(10))?;
        self.writer.set_field_name("z");
        self.writer.write_clob(&value.z, Some(10))?;
        self.writer.step_out()
    }

    fn write_union_c<'a>(&mut self, value: &'a UnionC) -> RidlResult<()> {
        match value {
            UnionC::A(v) => self.write_union_c_a(v),
            UnionC::B(v) => self.write_union_c_b(v),
        }
    }

    fn write_union_c_a<'a>(&mut self, value: &'a UnionCA) -> RidlResult<()> {
        match value {
            UnionCA::A(v) => self.write_union_c_a_a(v),
            UnionCA::B(v) => self.write_union_c_a_b(v),
            UnionCA::C(v) => self.write_union_c_a_c(v),
        }
    }

    fn write_union_c_a_a<'a>(&mut self, value: &'a UnionCAA) -> RidlResult<()> {
        self.writer.set_tag("union_c.a.a");
        self.writer.write_bool(*value)
    }

    fn write_union_c_a_b<'a>(&mut self, value: &'a UnionCAB) -> RidlResult<()> {
        self.writer.set_tag("union_c.a.b");
        self.writer.write_int(*value)
    }

    fn write_union_c_a_c<'a>(&mut self, value: &'a UnionCAC) -> RidlResult<()> {
        self.writer.set_tag("union_c.a.c");
        self.writer.write_string(value)
    }

    fn write_union_c_b<'a>(&mut self, value: &'a UnionCB) -> RidlResult<()> {
        match value {
            UnionCB::A(v) => self.write_union_c_b_a(v),
            UnionCB::B(v) => self.write_union_c_b_b(v),
            UnionCB::C(v) => self.write_union_c_b_c(v),
        }
    }

    fn write_union_c_b_a<'a>(&mut self, value: &'a UnionCBA) -> RidlResult<()> {
        self.writer.set_tag("union_c.b.a");
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_union_c_b_b<'a>(&mut self, value: &'a UnionCBB) -> RidlResult<()> {
        self.writer.set_tag("union_c.b.b");
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_union_c_b_c<'a>(&mut self, value: &'a UnionCBC) -> RidlResult<()> {
        self.writer.set_tag("union_c.b.c");
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_union_d<'a>(&mut self, value: &'a UnionD<'a>) -> RidlResult<()> {
        match value {
            UnionD::A(v) => self.write_union_d_a(v),
            UnionD::B(v) => self.write_union_d_b(v),
            UnionD::C(v) => self.write_union_d_c(v),
            UnionD::D(v) => self.write_union_d_d(v),
        }
    }

    fn write_union_d_a<'a>(&mut self, value: &'a UnionDA) -> RidlResult<()> {
        self.writer.set_tag("union_d.a");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }

    fn write_union_d_b<'a>(&mut self, value: &'a UnionDB) -> RidlResult<()> {
        self.writer.set_tag("union_d.b");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_array_start(&value.x, None)?;
        for v_0 in &value.x {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("y");
        self.writer.write_array_start(&value.y, None)?;
        for v_0 in &value.y {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("z");
        self.writer.write_array_start(&value.z, None)?;
        for v_0 in &value.z {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.step_out()
    }

    fn write_union_d_c<'a>(&mut self, value: &'a UnionDC<'a>) -> RidlResult<()> {
        self.writer.set_tag("union_d.c");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_decimal(&value.x, Some(1), Some(2))?;
        self.writer.set_field_name("y");
        self.writer.write_blob(&value.y, Some(10))?;
        self.writer.set_field_name("z");
        self.writer.write_clob(&value.z, Some(10))?;
        self.writer.step_out()
    }

    fn write_union_d_d<'a>(&mut self, value: &'a UnionDD<'a>) -> RidlResult<()> {
        self.writer.set_tag("union_d.d");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("a");
        self.write_struct_a(&value.a)?;
        self.writer.set_field_name("b");
        self.write_struct_b(&value.b)?;
        self.writer.step_out()
    }

    fn write_alias_to_struct_a<'a>(&mut self, value: &'a AliasToStructA) -> RidlResult<()> {
        self.writer.set_tag("alias_to_struct_a");
        self.writer.step_in(IonType::Struct)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }
}

pub struct KitchenWriterPacked<W, I>
where
    I: IonWriter<Output = W>,
{
    writer: RidlWriter<W, I>,
}

impl<W, I> KitchenWriterPacked<W, I>
where
    I: IonWriter<Output = W>,
{
    pub fn from(writer: I) -> KitchenWriterPacked<W, I> {
        KitchenWriterPacked {
            writer: RidlWriter::new(writer),
        }
    }
}

impl<W, I> KitchenWriter for KitchenWriterPacked<W, I>
where
    I: IonWriter<Output = W>,
{
    fn write_box_bool<'a>(&mut self, value: &'a BoxBool) -> RidlResult<()> {
        self.writer.write_bool(*value)
    }

    fn write_box_int<'a>(&mut self, value: &'a BoxInt) -> RidlResult<()> {
        self.writer.write_int(*value)
    }

    fn write_box_float<'a>(&mut self, value: &'a BoxFloat) -> RidlResult<()> {
        self.writer.write_float(*value)
    }

    fn write_box_decimal<'a>(&mut self, value: &'a BoxDecimal) -> RidlResult<()> {
        self.writer.write_decimal(value, None, None)
    }

    fn write_box_str<'a>(&mut self, value: &'a BoxStr) -> RidlResult<()> {
        self.writer.write_string(value)
    }

    fn write_box_blob<'a>(&mut self, value: &'a BoxBlob) -> RidlResult<()> {
        self.writer.write_blob(value, None)
    }

    fn write_box_clob<'a>(&mut self, value: &'a BoxClob) -> RidlResult<()> {
        self.writer.write_clob(value, None)
    }

    fn write_decimal_a<'a>(&mut self, value: &'a DecimalA) -> RidlResult<()> {
        self.writer.write_decimal(value, Some(1), Some(2))
    }

    fn write_decimal_b<'a>(&mut self, value: &'a DecimalB) -> RidlResult<()> {
        self.writer.write_decimal(value, Some(1), None)
    }

    fn write_decimal_c<'a>(&mut self, value: &'a DecimalC) -> RidlResult<()> {
        self.writer.write_decimal(value, None, Some(2))
    }

    fn write_blob_a<'a>(&mut self, value: &'a BlobA) -> RidlResult<()> {
        self.writer.write_blob(value, Some(1))
    }

    fn write_clob_b<'a>(&mut self, value: &'a ClobB) -> RidlResult<()> {
        self.writer.write_clob(value, Some(1))
    }

    fn write_arr_a<'a>(&mut self, value: &'a ArrA) -> RidlResult<()> {
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_b<'a>(&mut self, value: &'a ArrB) -> RidlResult<()> {
        self.writer.write_array_start(value, Some(10))?;
        for v_0 in value {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_c<'a>(&mut self, value: &'a ArrC) -> RidlResult<()> {
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.write_box_bool(v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_d<'a>(&mut self, value: &'a ArrD) -> RidlResult<()> {
        self.writer.write_array_start(value, Some(10))?;
        for v_0 in value {
            self.write_box_bool(v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_arr_nested<'a>(&mut self, value: &'a ArrNested) -> RidlResult<()> {
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_array_start(v_0, None)?;
            for v_1 in v_0 {
                self.writer.write_bool(*v_1)?;
            }
            self.writer.write_array_end()?;
        }
        self.writer.write_array_end()
    }

    fn write_enum_a<'a>(&mut self, value: &'a EnumA) -> RidlResult<()> {
        match value {
            EnumA::HelloWorld => self.writer.write_symbol("HELLO_WORLD"),
            EnumA::GoodnightMoon => self.writer.write_symbol("GOODNIGHT_MOON"),
        }
    }

    fn write_enum_b<'a>(&mut self, value: &'a EnumB) -> RidlResult<()> {
        match value {
            EnumB::A => self.writer.write_symbol("A"),
            EnumB::B => self.writer.write_symbol("B"),
            EnumB::C => self.writer.write_symbol("C"),
        }
    }

    fn write_nil<'a>(&mut self, value: &'a Nil) -> RidlResult<()> {
        self.writer.write_symbol("unit")
    }

    fn write_struct_a<'a>(&mut self, value: &'a StructA) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }

    fn write_struct_b<'a>(&mut self, value: &'a StructB) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_array_start(&value.x, None)?;
        for v_0 in &value.x {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("y");
        self.writer.write_array_start(&value.y, None)?;
        for v_0 in &value.y {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("z");
        self.writer.write_array_start(&value.z, None)?;
        for v_0 in &value.z {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.step_out()
    }

    fn write_struct_c<'a>(&mut self, value: &'a StructC<'a>) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_decimal(&value.x, Some(1), Some(2))?;
        self.writer.set_field_name("y");
        self.writer.write_blob(&value.y, Some(10))?;
        self.writer.set_field_name("z");
        self.writer.write_clob(&value.z, Some(10))?;
        self.writer.step_out()
    }

    fn write_struct_d<'a>(&mut self, value: &'a StructD<'a>) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("a");
        self.write_struct_a(&value.a)?;
        self.writer.set_field_name("b");
        self.write_struct_b(&value.b)?;
        self.writer.step_out()
    }

    fn write_union_a<'a>(&mut self, value: &'a UnionA) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        match value {
            UnionA::A(v) => {
                self.writer.write_int(0)?;
                self.write_union_a_a(v)?;
            }
            UnionA::B(v) => {
                self.writer.write_int(1)?;
                self.write_union_a_b(v)?;
            }
            UnionA::C(v) => {
                self.writer.write_int(2)?;
                self.write_union_a_c(v)?;
            }
        }
        self.writer.step_out()
    }

    fn write_union_a_a<'a>(&mut self, value: &'a UnionAA) -> RidlResult<()> {
        self.writer.write_bool(*value)
    }

    fn write_union_a_b<'a>(&mut self, value: &'a UnionAB) -> RidlResult<()> {
        self.writer.write_int(*value)
    }

    fn write_union_a_c<'a>(&mut self, value: &'a UnionAC) -> RidlResult<()> {
        self.writer.write_string(value)
    }

    fn write_union_b<'a>(&mut self, value: &'a UnionB<'a>) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        match value {
            UnionB::A(v) => {
                self.writer.write_int(0)?;
                self.write_union_b_a(v)?;
            }
            UnionB::B(v) => {
                self.writer.write_int(1)?;
                self.write_union_b_b(v)?;
            }
            UnionB::C(v) => {
                self.writer.write_int(2)?;
                self.write_union_b_c(v)?;
            }
        }
        self.writer.step_out()
    }

    fn write_union_b_a<'a>(&mut self, value: &'a UnionBA) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }

    fn write_union_b_b<'a>(&mut self, value: &'a UnionBB) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_array_start(&value.x, None)?;
        for v_0 in &value.x {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("y");
        self.writer.write_array_start(&value.y, None)?;
        for v_0 in &value.y {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("z");
        self.writer.write_array_start(&value.z, None)?;
        for v_0 in &value.z {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.step_out()
    }

    fn write_union_b_c<'a>(&mut self, value: &'a UnionBC<'a>) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_decimal(&value.x, Some(1), Some(2))?;
        self.writer.set_field_name("y");
        self.writer.write_blob(&value.y, Some(10))?;
        self.writer.set_field_name("z");
        self.writer.write_clob(&value.z, Some(10))?;
        self.writer.step_out()
    }

    fn write_union_c<'a>(&mut self, value: &'a UnionC) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        match value {
            UnionC::A(v) => {
                self.writer.write_int(0)?;
                self.write_union_c_a(v)?;
            }
            UnionC::B(v) => {
                self.writer.write_int(1)?;
                self.write_union_c_b(v)?;
            }
        }
        self.writer.step_out()
    }

    fn write_union_c_a<'a>(&mut self, value: &'a UnionCA) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        match value {
            UnionCA::A(v) => {
                self.writer.write_int(0)?;
                self.write_union_c_a_a(v)?;
            }
            UnionCA::B(v) => {
                self.writer.write_int(1)?;
                self.write_union_c_a_b(v)?;
            }
            UnionCA::C(v) => {
                self.writer.write_int(2)?;
                self.write_union_c_a_c(v)?;
            }
        }
        self.writer.step_out()
    }

    fn write_union_c_a_a<'a>(&mut self, value: &'a UnionCAA) -> RidlResult<()> {
        self.writer.write_bool(*value)
    }

    fn write_union_c_a_b<'a>(&mut self, value: &'a UnionCAB) -> RidlResult<()> {
        self.writer.write_int(*value)
    }

    fn write_union_c_a_c<'a>(&mut self, value: &'a UnionCAC) -> RidlResult<()> {
        self.writer.write_string(value)
    }

    fn write_union_c_b<'a>(&mut self, value: &'a UnionCB) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        match value {
            UnionCB::A(v) => {
                self.writer.write_int(0)?;
                self.write_union_c_b_a(v)?;
            }
            UnionCB::B(v) => {
                self.writer.write_int(1)?;
                self.write_union_c_b_b(v)?;
            }
            UnionCB::C(v) => {
                self.writer.write_int(2)?;
                self.write_union_c_b_c(v)?;
            }
        }
        self.writer.step_out()
    }

    fn write_union_c_b_a<'a>(&mut self, value: &'a UnionCBA) -> RidlResult<()> {
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_union_c_b_b<'a>(&mut self, value: &'a UnionCBB) -> RidlResult<()> {
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_union_c_b_c<'a>(&mut self, value: &'a UnionCBC) -> RidlResult<()> {
        self.writer.write_array_start(value, None)?;
        for v_0 in value {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()
    }

    fn write_union_d<'a>(&mut self, value: &'a UnionD<'a>) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        match value {
            UnionD::A(v) => {
                self.writer.write_int(0)?;
                self.write_union_d_a(v)?;
            }
            UnionD::B(v) => {
                self.writer.write_int(1)?;
                self.write_union_d_b(v)?;
            }
            UnionD::C(v) => {
                self.writer.write_int(2)?;
                self.write_union_d_c(v)?;
            }
            UnionD::D(v) => {
                self.writer.write_int(3)?;
                self.write_union_d_d(v)?;
            }
        }
        self.writer.step_out()
    }

    fn write_union_d_a<'a>(&mut self, value: &'a UnionDA) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }

    fn write_union_d_b<'a>(&mut self, value: &'a UnionDB) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_array_start(&value.x, None)?;
        for v_0 in &value.x {
            self.writer.write_bool(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("y");
        self.writer.write_array_start(&value.y, None)?;
        for v_0 in &value.y {
            self.writer.write_int(*v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.set_field_name("z");
        self.writer.write_array_start(&value.z, None)?;
        for v_0 in &value.z {
            self.writer.write_string(v_0)?;
        }
        self.writer.write_array_end()?;
        self.writer.step_out()
    }

    fn write_union_d_c<'a>(&mut self, value: &'a UnionDC<'a>) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_decimal(&value.x, Some(1), Some(2))?;
        self.writer.set_field_name("y");
        self.writer.write_blob(&value.y, Some(10))?;
        self.writer.set_field_name("z");
        self.writer.write_clob(&value.z, Some(10))?;
        self.writer.step_out()
    }

    fn write_union_d_d<'a>(&mut self, value: &'a UnionDD<'a>) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("a");
        self.write_struct_a(&value.a)?;
        self.writer.set_field_name("b");
        self.write_struct_b(&value.b)?;
        self.writer.step_out()
    }

    fn write_alias_to_struct_a<'a>(&mut self, value: &'a AliasToStructA) -> RidlResult<()> {
        self.writer.step_in(IonType::SExp)?;
        self.writer.set_field_name("x");
        self.writer.write_bool(value.x)?;
        self.writer.set_field_name("y");
        self.writer.write_int(value.y)?;
        self.writer.set_field_name("z");
        self.writer.write_string(&value.z)?;
        self.writer.step_out()
    }
}
pub trait KitchenReader<'a> {
    fn read_box_bool(&mut self) -> RidlResult<&'a BoxBool>;
    fn read_box_int(&mut self) -> RidlResult<&'a BoxInt>;
    fn read_box_float(&mut self) -> RidlResult<&'a BoxFloat>;
    fn read_box_decimal(&mut self) -> RidlResult<&'a BoxDecimal>;
    fn read_box_str(&mut self) -> RidlResult<&'a BoxStr>;
    fn read_box_blob(&mut self) -> RidlResult<&'a BoxBlob>;
    fn read_box_clob(&mut self) -> RidlResult<&'a BoxClob>;
    fn read_decimal_a(&mut self) -> RidlResult<&'a DecimalA>;
    fn read_decimal_b(&mut self) -> RidlResult<&'a DecimalB>;
    fn read_decimal_c(&mut self) -> RidlResult<&'a DecimalC>;
    fn read_blob_a(&mut self) -> RidlResult<&'a BlobA>;
    fn read_clob_b(&mut self) -> RidlResult<&'a ClobB>;
    fn read_arr_a(&mut self) -> RidlResult<&'a ArrA>;
    fn read_arr_b(&mut self) -> RidlResult<&'a ArrB>;
    fn read_arr_c(&mut self) -> RidlResult<&'a ArrC>;
    fn read_arr_d(&mut self) -> RidlResult<&'a ArrD>;
    fn read_arr_nested(&mut self) -> RidlResult<&'a ArrNested>;
    fn read_enum_a(&mut self) -> RidlResult<&'a EnumA>;
    fn read_enum_b(&mut self) -> RidlResult<&'a EnumB>;
    fn read_nil(&mut self) -> RidlResult<&'a Nil>;
    fn read_struct_a(&mut self) -> RidlResult<&'a StructA>;
    fn read_struct_b(&mut self) -> RidlResult<&'a StructB>;
    fn read_struct_c(&mut self) -> RidlResult<&'a StructC<'a>>;
    fn read_struct_d(&mut self) -> RidlResult<&'a StructD<'a>>;
    fn read_union_a(&mut self) -> RidlResult<&'a UnionA>;
    fn read_union_a_a(&mut self) -> RidlResult<&'a UnionAA>;
    fn read_union_a_b(&mut self) -> RidlResult<&'a UnionAB>;
    fn read_union_a_c(&mut self) -> RidlResult<&'a UnionAC>;
    fn read_union_b(&mut self) -> RidlResult<&'a UnionB<'a>>;
    fn read_union_b_a(&mut self) -> RidlResult<&'a UnionBA>;
    fn read_union_b_b(&mut self) -> RidlResult<&'a UnionBB>;
    fn read_union_b_c(&mut self) -> RidlResult<&'a UnionBC<'a>>;
    fn read_union_c(&mut self) -> RidlResult<&'a UnionC>;
    fn read_union_c_a(&mut self) -> RidlResult<&'a UnionCA>;
    fn read_union_c_a_a(&mut self) -> RidlResult<&'a UnionCAA>;
    fn read_union_c_a_b(&mut self) -> RidlResult<&'a UnionCAB>;
    fn read_union_c_a_c(&mut self) -> RidlResult<&'a UnionCAC>;
    fn read_union_c_b(&mut self) -> RidlResult<&'a UnionCB>;
    fn read_union_c_b_a(&mut self) -> RidlResult<&'a UnionCBA>;
    fn read_union_c_b_b(&mut self) -> RidlResult<&'a UnionCBB>;
    fn read_union_c_b_c(&mut self) -> RidlResult<&'a UnionCBC>;
    fn read_union_d(&mut self) -> RidlResult<&'a UnionD<'a>>;
    fn read_union_d_a(&mut self) -> RidlResult<&'a UnionDA>;
    fn read_union_d_b(&mut self) -> RidlResult<&'a UnionDB>;
    fn read_union_d_c(&mut self) -> RidlResult<&'a UnionDC<'a>>;
    fn read_union_d_d(&mut self) -> RidlResult<&'a UnionDD<'a>>;
    fn read_alias_to_struct_a(&mut self) -> RidlResult<&'a AliasToStructA>;
}

pub struct KitchenReaderBuilder {}

impl KitchenReaderBuilder {
    pub fn text<'a, I: 'a + ToIonDataSource>(arena: &'a Arena, input: I) -> KitchenReaderText {
        let reader = ReaderBuilder::default()
            .build(input)
            .expect("Failed to instantiate IonReader.");
        KitchenReaderText {
            arena,
            reader: RidlReader::new(reader),
        }
    }

    pub fn packed<'a, I: 'a + ToIonDataSource>(arena: &'a Arena, input: I) -> KitchenReaderPacked {
        let reader = ReaderBuilder::default()
            .build(input)
            .expect("Failed to instantiate IonReader.");
        KitchenReaderPacked {
            arena,
            reader: RidlReader::new(reader),
        }
    }
}

pub struct KitchenReaderText<'a> {
    arena: &'a Arena,
    reader: RidlReader<'a>,
}

impl<'a> KitchenReader<'a> for KitchenReaderText<'a> {
    fn read_box_bool(&mut self) -> RidlResult<&'a BoxBool> {
        todo!()
    }

    fn read_box_int(&mut self) -> RidlResult<&'a BoxInt> {
        todo!()
    }

    fn read_box_float(&mut self) -> RidlResult<&'a BoxFloat> {
        todo!()
    }

    fn read_box_decimal(&mut self) -> RidlResult<&'a BoxDecimal> {
        todo!()
    }

    fn read_box_str(&mut self) -> RidlResult<&'a BoxStr> {
        todo!()
    }

    fn read_box_blob(&mut self) -> RidlResult<&'a BoxBlob> {
        todo!()
    }

    fn read_box_clob(&mut self) -> RidlResult<&'a BoxClob> {
        todo!()
    }

    fn read_decimal_a(&mut self) -> RidlResult<&'a DecimalA> {
        todo!()
    }

    fn read_decimal_b(&mut self) -> RidlResult<&'a DecimalB> {
        todo!()
    }

    fn read_decimal_c(&mut self) -> RidlResult<&'a DecimalC> {
        todo!()
    }

    fn read_blob_a(&mut self) -> RidlResult<&'a BlobA> {
        todo!()
    }

    fn read_clob_b(&mut self) -> RidlResult<&'a ClobB> {
        todo!()
    }

    fn read_arr_a(&mut self) -> RidlResult<&'a ArrA> {
        todo!()
    }

    fn read_arr_b(&mut self) -> RidlResult<&'a ArrB> {
        todo!()
    }

    fn read_arr_c(&mut self) -> RidlResult<&'a ArrC> {
        todo!()
    }

    fn read_arr_d(&mut self) -> RidlResult<&'a ArrD> {
        todo!()
    }

    fn read_arr_nested(&mut self) -> RidlResult<&'a ArrNested> {
        todo!()
    }

    fn read_enum_a(&mut self) -> RidlResult<&'a EnumA> {
        todo!()
    }

    fn read_enum_b(&mut self) -> RidlResult<&'a EnumB> {
        todo!()
    }

    fn read_nil(&mut self) -> RidlResult<&'a Nil> {
        todo!()
    }

    fn read_struct_a(&mut self) -> RidlResult<&'a StructA> {
        todo!()
    }

    fn read_struct_b(&mut self) -> RidlResult<&'a StructB> {
        todo!()
    }

    fn read_struct_c(&mut self) -> RidlResult<&'a StructC<'a>> {
        todo!()
    }

    fn read_struct_d(&mut self) -> RidlResult<&'a StructD<'a>> {
        todo!()
    }

    fn read_union_a(&mut self) -> RidlResult<&'a UnionA> {
        todo!()
    }

    fn read_union_a_a(&mut self) -> RidlResult<&'a UnionAA> {
        todo!()
    }

    fn read_union_a_b(&mut self) -> RidlResult<&'a UnionAB> {
        todo!()
    }

    fn read_union_a_c(&mut self) -> RidlResult<&'a UnionAC> {
        todo!()
    }

    fn read_union_b(&mut self) -> RidlResult<&'a UnionB<'a>> {
        todo!()
    }

    fn read_union_b_a(&mut self) -> RidlResult<&'a UnionBA> {
        todo!()
    }

    fn read_union_b_b(&mut self) -> RidlResult<&'a UnionBB> {
        todo!()
    }

    fn read_union_b_c(&mut self) -> RidlResult<&'a UnionBC<'a>> {
        todo!()
    }

    fn read_union_c(&mut self) -> RidlResult<&'a UnionC> {
        todo!()
    }

    fn read_union_c_a(&mut self) -> RidlResult<&'a UnionCA> {
        todo!()
    }

    fn read_union_c_a_a(&mut self) -> RidlResult<&'a UnionCAA> {
        todo!()
    }

    fn read_union_c_a_b(&mut self) -> RidlResult<&'a UnionCAB> {
        todo!()
    }

    fn read_union_c_a_c(&mut self) -> RidlResult<&'a UnionCAC> {
        todo!()
    }

    fn read_union_c_b(&mut self) -> RidlResult<&'a UnionCB> {
        todo!()
    }

    fn read_union_c_b_a(&mut self) -> RidlResult<&'a UnionCBA> {
        todo!()
    }

    fn read_union_c_b_b(&mut self) -> RidlResult<&'a UnionCBB> {
        todo!()
    }

    fn read_union_c_b_c(&mut self) -> RidlResult<&'a UnionCBC> {
        todo!()
    }

    fn read_union_d(&mut self) -> RidlResult<&'a UnionD<'a>> {
        todo!()
    }

    fn read_union_d_a(&mut self) -> RidlResult<&'a UnionDA> {
        todo!()
    }

    fn read_union_d_b(&mut self) -> RidlResult<&'a UnionDB> {
        todo!()
    }

    fn read_union_d_c(&mut self) -> RidlResult<&'a UnionDC<'a>> {
        todo!()
    }

    fn read_union_d_d(&mut self) -> RidlResult<&'a UnionDD<'a>> {
        todo!()
    }

    fn read_alias_to_struct_a(&mut self) -> RidlResult<&'a AliasToStructA> {
        todo!()
    }
}

pub struct KitchenReaderPacked<'a> {
    arena: &'a Arena,
    reader: RidlReader<'a>,
}

impl<'a> KitchenReader<'a> for KitchenReaderPacked<'a> {
    fn read_box_bool(&mut self) -> RidlResult<&'a BoxBool> {
        todo!()
    }

    fn read_box_int(&mut self) -> RidlResult<&'a BoxInt> {
        todo!()
    }

    fn read_box_float(&mut self) -> RidlResult<&'a BoxFloat> {
        todo!()
    }

    fn read_box_decimal(&mut self) -> RidlResult<&'a BoxDecimal> {
        todo!()
    }

    fn read_box_str(&mut self) -> RidlResult<&'a BoxStr> {
        todo!()
    }

    fn read_box_blob(&mut self) -> RidlResult<&'a BoxBlob> {
        todo!()
    }

    fn read_box_clob(&mut self) -> RidlResult<&'a BoxClob> {
        todo!()
    }

    fn read_decimal_a(&mut self) -> RidlResult<&'a DecimalA> {
        todo!()
    }

    fn read_decimal_b(&mut self) -> RidlResult<&'a DecimalB> {
        todo!()
    }

    fn read_decimal_c(&mut self) -> RidlResult<&'a DecimalC> {
        todo!()
    }

    fn read_blob_a(&mut self) -> RidlResult<&'a BlobA> {
        todo!()
    }

    fn read_clob_b(&mut self) -> RidlResult<&'a ClobB> {
        todo!()
    }

    fn read_arr_a(&mut self) -> RidlResult<&'a ArrA> {
        todo!()
    }

    fn read_arr_b(&mut self) -> RidlResult<&'a ArrB> {
        todo!()
    }

    fn read_arr_c(&mut self) -> RidlResult<&'a ArrC> {
        todo!()
    }

    fn read_arr_d(&mut self) -> RidlResult<&'a ArrD> {
        todo!()
    }

    fn read_arr_nested(&mut self) -> RidlResult<&'a ArrNested> {
        todo!()
    }

    fn read_enum_a(&mut self) -> RidlResult<&'a EnumA> {
        todo!()
    }

    fn read_enum_b(&mut self) -> RidlResult<&'a EnumB> {
        todo!()
    }

    fn read_nil(&mut self) -> RidlResult<&'a Nil> {
        todo!()
    }

    fn read_struct_a(&mut self) -> RidlResult<&'a StructA> {
        todo!()
    }

    fn read_struct_b(&mut self) -> RidlResult<&'a StructB> {
        todo!()
    }

    fn read_struct_c(&mut self) -> RidlResult<&'a StructC<'a>> {
        todo!()
    }

    fn read_struct_d(&mut self) -> RidlResult<&'a StructD<'a>> {
        todo!()
    }

    fn read_union_a(&mut self) -> RidlResult<&'a UnionA> {
        todo!()
    }

    fn read_union_a_a(&mut self) -> RidlResult<&'a UnionAA> {
        todo!()
    }

    fn read_union_a_b(&mut self) -> RidlResult<&'a UnionAB> {
        todo!()
    }

    fn read_union_a_c(&mut self) -> RidlResult<&'a UnionAC> {
        todo!()
    }

    fn read_union_b(&mut self) -> RidlResult<&'a UnionB<'a>> {
        todo!()
    }

    fn read_union_b_a(&mut self) -> RidlResult<&'a UnionBA> {
        todo!()
    }

    fn read_union_b_b(&mut self) -> RidlResult<&'a UnionBB> {
        todo!()
    }

    fn read_union_b_c(&mut self) -> RidlResult<&'a UnionBC<'a>> {
        todo!()
    }

    fn read_union_c(&mut self) -> RidlResult<&'a UnionC> {
        todo!()
    }

    fn read_union_c_a(&mut self) -> RidlResult<&'a UnionCA> {
        todo!()
    }

    fn read_union_c_a_a(&mut self) -> RidlResult<&'a UnionCAA> {
        todo!()
    }

    fn read_union_c_a_b(&mut self) -> RidlResult<&'a UnionCAB> {
        todo!()
    }

    fn read_union_c_a_c(&mut self) -> RidlResult<&'a UnionCAC> {
        todo!()
    }

    fn read_union_c_b(&mut self) -> RidlResult<&'a UnionCB> {
        todo!()
    }

    fn read_union_c_b_a(&mut self) -> RidlResult<&'a UnionCBA> {
        todo!()
    }

    fn read_union_c_b_b(&mut self) -> RidlResult<&'a UnionCBB> {
        todo!()
    }

    fn read_union_c_b_c(&mut self) -> RidlResult<&'a UnionCBC> {
        todo!()
    }

    fn read_union_d(&mut self) -> RidlResult<&'a UnionD<'a>> {
        todo!()
    }

    fn read_union_d_a(&mut self) -> RidlResult<&'a UnionDA> {
        todo!()
    }

    fn read_union_d_b(&mut self) -> RidlResult<&'a UnionDB> {
        todo!()
    }

    fn read_union_d_c(&mut self) -> RidlResult<&'a UnionDC<'a>> {
        todo!()
    }

    fn read_union_d_d(&mut self) -> RidlResult<&'a UnionDD<'a>> {
        todo!()
    }

    fn read_alias_to_struct_a(&mut self) -> RidlResult<&'a AliasToStructA> {
        todo!()
    }
}
