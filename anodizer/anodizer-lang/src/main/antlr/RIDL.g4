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
  : TYPE NAME type SEMICOLON              # definitionAlias
  | TYPE NAME enum SEMICOLON              # definitionEnum
  | TYPE NAME struct SEMICOLON            # definitionStruct
  | TYPE NAME union SEMICOLON             # definitionUnion
  | NAMESPACE NAME L_BRACE body R_BRACE   # definitionNamespace
  ;

enum
  : ENUM L_BRACE ENUMERAL (COMMA ENUMERAL)* COMMA? R_BRACE
  ;

struct
  : STRUCT L_BRACE field (COMMA field)* COMMA? R_BRACE
  ;

field
  : NAME COLON type
  ;

union
  : UNION L_BRACE variant (COMMA variant)* COMMA? R_BRACE
  ;

variant
  : NAME              # variantUnit
  | NAME COLON type   # variantType
  | NAME COLON enum   # variantEnum
  | NAME COLON struct # variantStruct
  | NAME COLON union  # variantUnion
  ;

type
  : typeNamed
  | typePrimitive
  | typeArray
  | typeUnit
  ;

typeNamed
  : root=COCO? NAME (COCO NAME)*
  ;

typePrimitive
  : T_VOID                                                                # typeVoid
  | T_BOOL                                                                # typeBool
  | T_INT                                                                 # typeInt
  | T_FLOAT                                                               # typeFloat
  | T_DECIMAL (L_PAREN p=(LODASH|INT) (COMMA e=(LODASH|INT))? R_PAREN)?   # typeDecimal
  | T_STRING                                                              # typeString
  | T_BLOB (L_PAREN INT R_PAREN)?                                         # typeBlob
  | T_CLOB (L_PAREN INT R_PAREN)?                                         # typeClob
  ;

typeArray
  : typeNamed     L_BRACKET size=INT? R_BRACKET # typeArrayNamed
  | typePrimitive L_BRACKET size=INT? R_BRACKET # typeArrayPrimitive
  | typeArray     L_BRACKET size=INT? R_BRACKET # typeArrayNested
  ;

typeUnit
  : UNIT
  ;

AS: 'as';
INCLUDE: 'include';
NAMESPACE: 'namespace';
TYPE: 'type';

T_VOID: 'void';
T_BOOL: 'bool';
T_INT: 'int';
T_FLOAT: 'float';
T_DECIMAL: 'decimal';
T_STRING: 'string';
T_BLOB: 'blob';
T_CLOB: 'clob';

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
LODASH: '_';

NAME
  : [a-z][a-z0-9_]*
  ;

INT
  : [0-9]+
  ;

ENUMERAL
  : [A-Z][A-Z0-9_]*
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
