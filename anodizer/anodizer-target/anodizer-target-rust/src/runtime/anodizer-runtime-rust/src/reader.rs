use ion_rs::{Decimal, IonReader, IonType, Reader, Str, Symbol};
use ion_rs::types::Bytes;

use crate::result::{RidlError, RidlResult};
use crate::result::RidlError::ReadError;

pub struct RidlReader<'a> {
    pub reader: Reader<'a>,
}

#[inline]
pub fn read_err<T>(description: &str) -> RidlResult<T> {
    return Err(ReadError(description.to_string()));
}

impl<'a> RidlReader<'a> {

    pub fn new(reader: Reader) -> RidlReader {
        RidlReader { reader }
    }

    pub fn assert_type(&self, ion_type: IonType) -> RidlResult<()> {
        let actual = self.reader.ion_type().ok_or(ReadError("Unexpected end of value.".to_string()))?;
        if actual != ion_type {
            return read_err("Unexpected type");
        }
        Ok(())
    }

    pub fn assert_field(&self, field: String) -> RidlResult<()> {
        self.reader.current();
        let symbol = self.reader.field_name()?;
        let actual = match symbol.text_or_error() {
            Ok(v) => v,
            Err(e) => return Err(RidlError::from(e)),
        };
        if field != actual {
            return read_err("Field name did not match expected field name.");
        }
        Ok(())
    }

    pub fn assert_tag(&self, tag: String) -> RidlResult<()> {
        let annotations: Vec<Symbol> = self.reader.annotations()
            .filter_map(|s| s.ok())
            .collect();
        if annotations.len() != 1 {
            return read_err("Expected a type annotation, but found none.");
        }
        let actual = annotations[0].text().ok_or(ReadError("Failed to read annotation".to_string()))?;
        if actual != tag {
            return read_err("Type annotation did not match expected value.");
        }
        Ok(())
    }

    pub fn read_bool(&mut self) -> RidlResult<bool> {
        self.reader.read_bool().map_err(|e| RidlError::from(e))
    }

    pub fn read_int(&mut self) -> RidlResult<i64> {
        self.reader.read_i64().map_err(|e| RidlError::from(e))
    }

    pub fn read_float(&mut self) -> RidlResult<f64> {
        self.reader.read_f64().map_err(|e| RidlError::from(e))
    }

    pub fn read_decimal(&mut self, precision: Option<u64>, exponent: Option<i64>) -> RidlResult<Decimal> {
        let value = self.reader.read_decimal().map_err(|e| RidlError::from(e))?;
        if precision.is_some() && precision.unwrap() < value.precision() {
            return read_err("Decimal value precision exceeded the type's precision constraint");
        }
        if exponent.is_some() && -exponent.unwrap() < value.scale() {
            return read_err("Decimal value exponent exceeded the type's exponent constraint");
        }
        Ok(value)
    }

    pub fn read_string(&mut self) -> RidlResult<Str> {
        self.reader.read_string().map_err(|e| RidlError::from(e))
    }

    pub fn read_blob(&mut self, size: u64) -> RidlResult<Bytes> {
        let bytes = self.reader.read_blob().map_err(|e| RidlError::from(e))?.0;
        let len = bytes.as_ref().len() as u64;
        if len != size {
            return read_err("Blob size does not match definition size constraint.");
        }
        Ok(bytes)
    }

    pub fn read_clob(&mut self, size: u64) -> RidlResult<Bytes> {
        let bytes = self.reader.read_clob().map_err(|e| RidlError::from(e))?.0;
        let len = bytes.as_ref().len() as u64;
        if len != size {
            return read_err("Clob size does not match definition size constraint.");
        }
        Ok(bytes)
    }
}
