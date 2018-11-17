grammar Smoola;

@header{
    import ast.node.Program;
    import ast.node.Node;
    import ast.node.declaration.*;
    import ast.node.expression.*;
    import ast.node.expression.Value.*;
    import ast.node.statement.*;
    import ast.Type.Type;
    import ast.Type.PrimitiveType.*;
    import ast.Type.UserDefinedType.UserDefinedType;
    import ast.Type.ArrayType.ArrayType;
}

@members {
    Program create_program_object(){
        return new Program();
    }
    
    ClassDeclaration create_class_object(String class_name, String parent_name){
        Identifier class_id = create_identifier_object(class_name); 
        Identifier parent_class_id = new Identifier(parent_name); 
        ClassDeclaration this_class_dec = new ClassDeclaration(class_id, parent_class_id);
        return this_class_dec;
    }

    MethodDeclaration create_methodDeclaration_object(String method_name){
        Identifier method_id = new Identifier(method_name);
        return new MethodDeclaration(method_id);
    }

    VarDeclaration create_varDeclaration_object(String var_name, Type var_type){
        Identifier this_var_id = new Identifier(var_name);
        return new VarDeclaration(this_var_id, var_type);
    }

    MethodDeclaration add_arg_to_MethodDeclaration(String arg_name, Type arg_type, MethodDeclaration this_method){
        Identifier arg_name_id = new Identifier(arg_name);
        VarDeclaration this_arg_dec = new VarDeclaration(arg_name_id, arg_type);
        this_method.addArg(this_arg_dec);
        return this_method;
    }
    Identifier create_identifier_object(String name){
        return new Identifier(name);
    }
}


    program:
       {Program prog = create_program_object();} mainClass[prog] (classDeclaration[prog])* EOF 
    ;
    mainClass[Program prog]:
        // name should be checked later
        'class' class_name = ID {ClassDeclaration main_class_dec = create_class_object($class_name.text, "null"); $prog.setMainClass(main_class_dec);} '{' 'def' ID '()' ':' 'int' '{'  statements 'return' expression ';' '}' '}'
    ;
    classDeclaration[Program prog]:
        'class' class_name = ID ('extends' parent_class = ID )? {ClassDeclaration new_class_dec = create_class_object($class_name.text, $parent_class.text); $prog.addClass(new_class_dec);} '{' (var_dec = varDeclaration {new_class_dec.addVarDeclaration($var_dec.this_var);})* (method_dec = methodDeclaration {new_class_dec.addMethodDeclaration($method_dec.this_method);})* '}'
    ;
    varDeclaration returns [VarDeclaration this_var]:
        'var' var_name = ID ':' this_type = type ';' { VarDeclaration this_variable_dec = create_varDeclaration_object($var_name.text, $this_type.this_type);}
    ;
    methodDeclaration returns [MethodDeclaration this_method]:
        'def' method_name = ID { MethodDeclaration this_method = create_methodDeclaration_object($method_name.text);} ('()' | ('(' arg_name = ID ':' arg_type = type { this_method = add_arg_to_MethodDeclaration($arg_name.text, $arg_type.this_type, this_method);} (',' arg_name_2 = ID ':' arg_type_2 = type { this_method = add_arg_to_MethodDeclaration($arg_name_2.text, $arg_type_2.this_type, this_method);})* ')')) ':' type '{' varDeclaration* statements 'return' expression ';' '}'
    ;
    statements:
        (statement)*
    ;
    statement:
        statementBlock |
        statementCondition |
        statementLoop |
        statementWrite |
        statementAssignment
    ;
    statementBlock:
        '{'  statements '}'
    ;
    statementCondition:
        'if' '('expression')' 'then' statement ('else' statement)?
    ;
    statementLoop:
        'while' '(' expression ')' statement
    ;
    statementWrite:
        'writeln(' expression ')' ';'
    ;
    statementAssignment:
        expression ';'
    ;

    expression:
		expressionAssignment
	;

    expressionAssignment:
		expressionOr '=' expressionAssignment
	    |	expressionOr
	;

    expressionOr:
		expressionAnd expressionOrTemp
	;

    expressionOrTemp:
		'||' expressionAnd expressionOrTemp
	    |
	;

    expressionAnd:
		expressionEq expressionAndTemp
	;

    expressionAndTemp:
		'&&' expressionEq expressionAndTemp
	    |
	;

    expressionEq:
		expressionCmp expressionEqTemp
	;

    expressionEqTemp:
		('==' | '<>') expressionCmp expressionEqTemp
	    |
	;

    expressionCmp:
		expressionAdd expressionCmpTemp
	;

    expressionCmpTemp:
		('<' | '>') expressionAdd expressionCmpTemp
	    |
	;

    expressionAdd:
		expressionMult expressionAddTemp
	;

    expressionAddTemp:
		('+' | '-') expressionMult expressionAddTemp
	    |
	;

        expressionMult:
		expressionUnary expressionMultTemp
	;

    expressionMultTemp:
		('*' | '/') expressionUnary expressionMultTemp
	    |
	;

    expressionUnary:
		('!' | '-') expressionUnary
	    |	expressionMem
	;

    expressionMem:
		expressionMethods expressionMemTemp
	;

    expressionMemTemp:
		'[' expression ']'
	    |
	;
	expressionMethods:
	    expressionOther expressionMethodsTemp
	;
	expressionMethodsTemp:
	    '.' (ID '()' | ID '(' (expression (',' expression)*) ')' | 'length') expressionMethodsTemp
	    |
	;
    expressionOther:
		CONST_NUM
        |	CONST_STR
        |   'new ' 'int' '[' expression ']'
        |   'new ' ID '()'
        |   'this'
        |   'true'
        |   'false'
        |	ID
        |   ID '[' expression ']'
        |	'(' expression ')'
	;
	type returns [Type this_type]:
	    'int' {IntType this_type = new IntType();} |
	    'boolean' {BooleanType this_type = new BooleanType();} |
	    'string' {StringType this_type = new StringType();} |
	    'int[]' {ArrayType this_type = new ArrayType();} |
	    name = ID {Identifier this_id = new Identifier($name.text); UserDefinedType this_type = new UserDefinedType(); this_type.setName(this_id);}
	;
    CONST_NUM:
		[0-9]+
	;

    CONST_STR:
		'"' ~('\r' | '\n' | '"')* '"'
	;
    NL:
		'\r'? '\n' -> skip
	;

    ID:
	   [a-zA-Z_][a-zA-Z0-9_]*
	;

    COMMENT:
		'#'(~[\r\n])* -> skip
	;

    WS:
    	[ \t] -> skip
    ;