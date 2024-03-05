grammar RIDL;

document
  : include* definition+ EOF
  ;

include
  : INCLUDE STRING
  ;

definition
  : TYPE IDENTIFIER type EOL
  ;

type
  : typeNamed
  | typePrimitive
  | typeIon
  | typeList
  | typeMap
  | typeTuple
  | typeArray
  | typeStruct
  | typeUnion
  | typeEnum
  | typeUnit
  ;

typeNamed
  : IDENTIFIER
  ;

typePrimitive
  : T_BOOL
  | T_I32
  | T_I64
  | T_F32
  | T_F64
  | T_DECIMAL
  | T_CHAR
  | T_STRING
  | T_BYTE
  | T_BYTES
  ;

typeIon
  : ION
  | ION_BOOL
  | ION_INT
  | ION_FLOAT
  | ION_DECIMAL
  | ION_TIMESTAMP
  | ION_STRING
  | ION_SYMBOL
  | ION_BLOB
  | ION_CLOB
  | ION_STRUCT
  | ION_LIST
  | ION_SEXP
  ;

typeList
  : LIST L_ANGLE type R_ANGLE
  ;

typeMap
  : MAP L_ANGLE typePrimitive COMMA type R_ANGLE
  ;

typeTuple
  : L_PAREN type (COMMA type)* R_PAREN
  ;

typeArray
  : typePrimitive L_BRACKET INTEGER R_BRACKET
  ;

typeStruct
  : STRUCT L_BRACE typeStructField+ R_BRACE
  ;

typeStructField
  : IDENTIFIER COLON type EOL
  ;

typeUnion
  : UNION L_BRACE typeUnionVariant+ R_BRACE
  ;

typeUnionVariant
  : IDENTIFIER type EOL
  ;

typeEnum
  : ENUM L_BRACE ENUMERATOR (COMMA ENUMERATOR)* R_BRACE
  ;

typeUnit
  : UNIT
  ;

INCLUDE: 'include';
TYPE: 'type';

T_BOOL: 'bool';
T_I32: 'i32';
T_I64: 'i64';
T_F32: 'f32';
T_F64: 'f64';
T_DECIMAL: 'decimal';
T_CHAR: 'char';
T_STRING: 'string';
T_BYTE: 'byte';
T_BYTES: 'bytes';

ION: 'ion';
ION_BOOL: 'ion.bool';
ION_INT: 'ion.int';
ION_FLOAT: 'ion.float';
ION_DECIMAL: 'ion.decimal';
ION_TIMESTAMP: 'ion.timestamp';
ION_STRING: 'ion.string';
ION_SYMBOL: 'ion.symbol';
ION_BLOB: 'ion.blob';
ION_CLOB: 'ion.clob';
ION_STRUCT: 'ion.struct';
ION_LIST: 'ion.list';
ION_SEXP: 'ion.sexp';

LIST: 'list';
MAP: 'map';

STRUCT: 'struct';
UNION: 'union';
ENUM: 'enum';
UNIT: 'unit';

COMMA: ',';
COLON: ':';
L_ANGLE: '<';
R_ANGLE: '>';
L_PAREN: '(';
R_PAREN: ')';
L_BRACE: '{';
R_BRACE: '}';
L_BRACKET: '[';
R_BRACKET: ']';
EOL: ';';

IDENTIFIER
  : [a-z][a-z_]*
  ;

INTEGER
  : [0-9]+
  ;

ENUMERATOR
  : [A-Z][A-Z_]+
  ;

STRING
  : '"' (~('"' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '"'
  ;

COMMENT_LINE
  : '//' ~[\r\n]* '\r'? '\n'? -> skip
  ;

COMMENT_BLOCK
  : '/*' .*? '*/' -> skip
  ;

WS
  : [ \r\n\t]+ -> skip
  ;

UNRECOGNIZED
  : .
  ;
