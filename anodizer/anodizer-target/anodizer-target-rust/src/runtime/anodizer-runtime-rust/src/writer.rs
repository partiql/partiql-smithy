use ion_rs::{Decimal, IonType, IonWriter};

use crate::result::{RidlError, RidlResult};

pub struct RidlWriter<W, I>
where
    I: IonWriter<Output = W>,
{
    writer: I,
}

#[inline]
fn write_err<T>(description: &str) -> RidlResult<T> {
    return Err(RidlError::WriteError(description.to_string()));
}

impl<W, I> RidlWriter<W, I>
where
    I: IonWriter<Output = W>,
{

    pub fn new(writer: I) -> RidlWriter<W, I> {
        RidlWriter { writer }
    }

    pub fn set_tag(&mut self, tag: &str) {
        let annotations: Vec<&str> = vec![tag];
        self.writer.set_annotations(annotations);
    }

    pub fn set_field_name(&mut self, field: &str) {
        self.writer.set_field_name(field);
    }

    pub fn write_bool(&mut self, value: bool) -> RidlResult<()> {
        self.writer.write_bool(value).map_err(|e| RidlError::from(e))
    }

    pub fn write_int(&mut self, value: i64) -> RidlResult<()> {
        self.writer.write_i64(value).map_err(|e| RidlError::from(e))
    }

    pub fn write_float(&mut self, value: f64) -> RidlResult<()> {
        self.writer.write_f64(value).map_err(|e| RidlError::from(e))
    }

    pub fn write_decimal(&mut self, value: &Decimal, precision: Option<u64>, exponent: Option<i64>) -> RidlResult<()> {
        if precision.is_some() && precision.unwrap() < value.precision() {
            return write_err("Decimal value precision exceeded the type's precision constraint");
        }
        if exponent.is_some() && -exponent.unwrap() < value.scale() {
            return write_err("Decimal value exponent exceeded the type's exponent constraint");
        }
        if let Err(e) = self.writer.write_decimal(value) {
            return Err(RidlError::from(e));
        }
        Ok(())
    }

    pub fn write_string<T: AsRef<str>>(&mut self, value: T) -> RidlResult<()> {
        self.writer.write_string(value).map_err(|e| RidlError::from(e))
    }

    pub fn write_symbol<T: AsRef<str>>(&mut self, value: T) -> RidlResult<()> {
        self.writer.write_symbol(value.as_ref()).map_err(|e| RidlError::from(e))
    }

    pub fn write_blob<T: AsRef<[u8]>>(&mut self, value: T, size: Option<u64>) -> RidlResult<()> {
        let bytes = value.as_ref();
        if size.is_some() && size.unwrap() != bytes.len() as u64 {
            return write_err("Blob size does not match type size constraint");
        }
        self.writer.write_blob(value).map_err(|e| RidlError::from(e))
    }

    pub fn write_clob<T: AsRef<[u8]>>(&mut self, value: T, size: Option<u64>) -> RidlResult<()> {
        let bytes = value.as_ref();
        if size.is_some() && size.unwrap() != bytes.len() as u64 {
            return write_err("Clob size does not match type size constraint");
        }
        self.writer.write_clob(value).map_err(|e| RidlError::from(e))
    }

    pub fn write_array_start<T>(&mut self, array: &Vec<T>, size: Option<usize>) -> RidlResult<()> {
        self.writer.step_in(IonType::List)?;
        if size.is_some() && size.unwrap() != array.len() {
            return write_err("Vec size does not match array size constraint")
        }
        Ok(())
    }

    pub fn write_array_end(&mut self) -> RidlResult<()> {
        self.step_out()
    }

    pub fn step_in(&mut self, kind: IonType) -> RidlResult<()> {
        self.writer.step_in(kind).map_err(|e| RidlError::from(e))
    }

    pub fn step_out(&mut self) -> RidlResult<()> {
        self.writer.step_out().map_err(|e| RidlError::from(e))
    }
}
