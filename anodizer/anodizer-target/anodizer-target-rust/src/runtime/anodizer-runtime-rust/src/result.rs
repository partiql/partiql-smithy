use ion_rs::IonError;
use thiserror::Error;

pub type RidlResult<T> = Result<T, RidlError>;

#[derive(Error, Debug, PartialEq)]
pub enum RidlError {
    #[error("An error reading the input occurred, most likely due to malformed Ion: {0}")]
    Ion(IonError),

    #[error("{0}")]
    ReadError(String),

    #[error("{0}")]
    WriteError(String),
}

impl From<IonError> for RidlError {
    fn from(e: IonError) -> Self {
        RidlError::Ion(e)
    }
}
