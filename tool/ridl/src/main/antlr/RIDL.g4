grammar RIDL;

document
  : body EOF
  ;

body
  : include* definition*
  ;

include
  : INCLUDE STRING (AS? NAME)
  ;

definition
  : TYPE NAME type SEMICOLON              #definitionType
  | NAMESPACE NAME L_BRACE body R_BRACE   #definitionNamespace
  ;

type
  : typeNamed
  | typePrimitive
  | typeArray
  | typeStruct
  | typeUnion
  | typeEnum
  | typeUnit
  ;

typeNamed
  : root=COCO? NAME (COCO NAME)*
  ;

typePrimitive
  : T_BOOL
  | T_INT32
  | T_INT64
  | T_FLOAT32
  | T_FLOAT64
  | T_STRING
  | T_BYTE
  | T_BYTES
  ;

typeArray
  : typeNamed L_BRACKET size=INTEGER? R_BRACKET       #typeArrayNamed
  | typePrimitive L_BRACKET size=INTEGER? R_BRACKET   #typeArrayPrimitive
  ;

typeStruct
  : STRUCT L_BRACE typeStructField (COMMA typeStructField)* COMMA? R_BRACE
  ;

typeStructField
  : NAME COLON type
  ;

typeUnion
  : UNION L_BRACE typeUnionVariant (COMMA typeUnionVariant)* COMMA? R_BRACE
  ;

typeUnionVariant
  : NAME COLON type
  ;

typeEnum
  : ENUM L_BRACE ENUMERATOR (COMMA ENUMERATOR)* R_BRACE
  ;

typeUnit
  : UNIT
  ;

AS: 'as';
INCLUDE: 'include';
NAMESPACE: 'namespace';
TYPE: 'type';

T_BOOL: 'bool';
T_INT32: 'int32';
T_INT64: 'int64';
T_FLOAT32: 'float32';
T_FLOAT64: 'float64';
T_STRING: 'string';
T_BYTE: 'byte';
T_BYTES: 'bytes';

ARRAY: 'array';
STRUCT: 'struct';
UNION: 'union';
ENUM: 'enum';
UNIT: 'unit';

COMMA: ',';
COLON: ':';
COCO: '::';
SEMICOLON: ';';
L_ANGLE: '<';
R_ANGLE: '>';
L_PAREN: '(';
R_PAREN: ')';
L_BRACE: '{';
R_BRACE: '}';
L_BRACKET: '[';
R_BRACKET: ']';

NAME
  : [a-z][a-z0-9_]*
  ;

INTEGER
  : [0-9]+
  ;

ENUMERATOR
  : [A-Z][A-Z0-9_]+
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
