package io.github.amzn.anodizer.core

/**
 * IonType without the ion-java dependency and DATAGRAM.
 */
public enum class Ion {
    NULL,
    BOOL,
    INT,
    FLOAT,
    DECIMAL,
    TIMESTAMP,
    SYMBOL,
    STRING,
    CLOB,
    BLOB,
    LIST,
    SEXP,
    STRUCT;
}
