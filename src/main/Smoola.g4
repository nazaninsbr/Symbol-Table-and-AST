grammar Smoola;

@header{
    import ast.node.Program;
    import ast.VisitorImpl;
    import ast.Visitor;
    import ast.node.Node;
    import ast.node.declaration.*;
    import ast.node.expression.*;
    import ast.node.expression.Value.*;
    import ast.node.statement.*;
    import ast.Type.Type;
    import ast.Type.PrimitiveType.*;
    import ast.Type.UserDefinedType.UserDefinedType;
    import ast.Type.ArrayType.ArrayType;

    import java.util.ArrayList;
    import java.util.List;
}

@members {

    Program create_program_object(){
        return new Program();
    }

    void print_program_content(Program prog){
        List<ClassDeclaration> classes = prog.getClasses(); 
        for(int i=0; i<classes.size(); i++){
            System.out.println(classes.get(i).getName().getName()); 
            System.out.println(classes.get(i).getParentName().getName());
            ArrayList<VarDeclaration> vars = classes.get(i).getVarDeclarations();
            for(int j=0; j<vars.size(); j++){
                System.out.println(vars.get(j).getIdentifier().getName());
                System.out.println(vars.get(j).getType().toString());
            }
            ArrayList<MethodDeclaration> methods = classes.get(i).getMethodDeclarations();
            for(int j=0; j<methods.size(); j++){
                System.out.println(methods.get(j).getName().getName());
            }
        }
    }

    ClassDeclaration create_class_object(String class_name, String parent_name){
        Identifier class_id = create_identifier_object(class_name); 
        Identifier parent_class_id = create_identifier_object(parent_name); 
        ClassDeclaration this_class_dec = new ClassDeclaration(class_id, parent_class_id);
        return this_class_dec;
    }

    MethodDeclaration create_methodDeclaration_object(String method_name){
        Identifier method_id = create_identifier_object(method_name);
        return new MethodDeclaration(method_id);
    }

    VarDeclaration create_varDeclaration_object(String var_name, Type var_type){
        Identifier this_var_id = create_identifier_object(var_name);
        return new VarDeclaration(this_var_id, var_type);
    }

    MethodDeclaration create_main_method_object(String method_name){
        Identifier method_id = create_identifier_object(method_name);
        return new MethodDeclaration(method_id);
    }

    MethodDeclaration add_arg_to_MethodDeclaration(String arg_name, Type arg_type, MethodDeclaration this_method){
        Identifier arg_name_id = create_identifier_object(arg_name);
        VarDeclaration this_arg_dec = new VarDeclaration(arg_name_id, arg_type);
        this_method.addArg(this_arg_dec);
        return this_method;
    }

    Block create_block_statement_object(ArrayList<Statement> all_statements){
        Block this_statement = new Block();
        for(int i=0; i<all_statements.size(); i++){
            System.out.println(all_statements.get(i).toString());
            this_statement.addStatement(all_statements.get(i)); 
        }
        return this_statement;
    }

    Conditional create_conditional_statement_object(Expression conditional_expression, Statement consequence_body, Statement alternative_body){
        Conditional this_statement = new Conditional(conditional_expression, consequence_body);
        if (alternative_body!=null){
            this_statement.setAlternativeBody(alternative_body);
        }
        return this_statement; 
    }

    While create_loop_statement_object(Expression conditional_expression, Statement body){
        While this_statement = new While(conditional_expression, body);
        return this_statement;
    }

    NewClass create_class_instantiation_object(String class_name){
        Identifier class_id = create_identifier_object(class_name); 
        NewClass this_class = new NewClass(class_id); 
        return this_class;
    }

    ArrayCall create_array_call_instance(String name, Expression index){
        Identifier array_name_id = create_identifier_object(name); 
        ArrayCall this_array = new ArrayCall(array_name_id, index);
        return this_array; 
    }

    BooleanValue create_boolean_value_object(boolean constant){
        BooleanType this_type = new BooleanType();
        BooleanValue this_value = new BooleanValue(constant, this_type);
        return this_value;
    }

    IntValue create_int_value_object(int constant){
        IntType this_type = new IntType();
        IntValue this_value = new IntValue(constant, this_type);
        return this_value;
    }

    StringValue create_string_value_object(String constant){
        StringType this_type = new StringType();
        StringValue this_value  = new StringValue(constant, this_type);
        return this_value;
    }

    Identifier create_identifier_object(String name){
        return new Identifier(name);
    }
}


    program:
       {Program prog = create_program_object();} mainClass[prog] (classDeclaration[prog])* {print_program_content(prog);} EOF {Visitor prog_visitor = new VisitorImpl(); prog.accept(prog_visitor);} 
    ;
    mainClass[Program prog]:
        // name should be checked later
        'class' class_name = ID {ClassDeclaration main_class_dec = create_class_object($class_name.text, "null"); $prog.setMainClass(main_class_dec);} '{' 'def' method_name = ID {main_class_dec.addMethodDeclaration(create_main_method_object($method_name.text));} '()' ':' 'int' '{'  statements 'return' expression ';' '}' '}'
    ;
    classDeclaration[Program prog]:
        'class' class_name = ID ('extends' parent_class = ID )? { ClassDeclaration new_class_dec = create_class_object($class_name.text, $parent_class.text); $prog.addClass(new_class_dec);} '{' (var_dec = varDeclaration { new_class_dec.addVarDeclaration($var_dec.this_var);})* (method_dec = methodDeclaration {new_class_dec.addMethodDeclaration($method_dec.this_method);})* '}'
    ;
    varDeclaration returns [VarDeclaration this_var]:
        'var' var_name = ID ':' this_type = type ';' {$this_var = create_varDeclaration_object($var_name.text, $this_type.this_type);}
    ;
    methodDeclaration returns [MethodDeclaration this_method]:
        'def' method_name = ID { $this_method = create_methodDeclaration_object($method_name.text);} ('()' | ('(' arg_name = ID ':' arg_type = type { $this_method = add_arg_to_MethodDeclaration($arg_name.text, $arg_type.this_type, $this_method);} (',' arg_name_2 = ID ':' arg_type_2 = type { $this_method = add_arg_to_MethodDeclaration($arg_name_2.text, $arg_type_2.this_type, $this_method);})* ')')) ':' type '{'  (this_var = varDeclaration {$this_method.addLocalVar($this_var.this_var);})* statements 'return' expression ';' '}'
    ;


    statements returns [ArrayList<Statement> all_statements]:
        {$all_statements = new ArrayList<>();} (our_statement = statement {$all_statements.add($our_statement.this_statement);} )*
    ;
    statement returns [Statement this_statement]:
        block_body = statementBlock {$this_statement = create_block_statement_object($block_body.block_statements);} |
        conditional_statement = statementCondition {$this_statement = create_conditional_statement_object($conditional_statement.conditional_expression, $conditional_statement.consequence_body, $conditional_statement.alternative_body);}|
        loop_statement = statementLoop {$this_statement = create_loop_statement_object($loop_statement.conditional_expression, $loop_statement.body);} |
        write_statement = statementWrite {$this_statement = new Write($write_statement.print_expression);} |
        assign_statement = statementAssignment {$this_statement = new Assign($assign_statement.lvalue, $assign_statement.rvalue);}
    ;
    statementBlock returns [ArrayList<Statement> block_statements]:
        '{'  block_body = statements {$block_statements = new ArrayList<>($block_body.all_statements);} '}'
    ;
    statementCondition returns [Expression conditional_expression, Statement consequence_body, Statement alternative_body]:
        'if' '(' cond_expre = expression {$conditional_expression = $cond_expre.this_expression;} ')' 'then' cons_body = statement {$consequence_body = $cons_body.this_statement;} ('else' alt_body = statement {$alternative_body = $alt_body.this_statement;})?
    ;
    statementLoop returns [Expression conditional_expression, Statement body]:
        'while' '(' cond_expre = expression {$conditional_expression = $cond_expre.this_expression;} ')' loop_body = statement {$body = $loop_body.this_statement;}
    ;
    statementWrite returns [Expression print_expression]:
        'writeln(' print_expr = expression {$print_expression = $print_expr.this_expression;} ')' ';'
    ;
    statementAssignment returns [Expression lvalue, Expression rvalue]:
        expression ';' {}
    ;


    expression returns [Expression this_expression]:
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
    expressionOther returns [Expression this_expression]:
		number = CONST_NUM {$this_expression = create_int_value_object(Integer.parseInt($number.text));}
        |	str = CONST_STR {$this_expression = create_string_value_object($str.text);}
        |   'new ' 'int' '[' size_expression = expression ']' {$this_expression = new NewArray();}
        |   'new ' class_name = ID '()' {$this_expression = create_class_instantiation_object($class_name.text);}
        |   'this' {$this_expression = new This();}
        |   'true' {$this_expression = create_boolean_value_object(true);}
        |   'false' {$this_expression = create_boolean_value_object(false);}
        |	name = ID {$this_expression = create_identifier_object($name.text);}
        |   name = ID '[' index = expression ']' {$this_expression = create_array_call_instance($name.text, $index.this_expression);}
        |	'(' expression ')'
	;
	type returns [Type this_type]:
	    'int' {$this_type = new IntType();} |
	    'boolean' {$this_type = new BooleanType();} |
	    'string' {$this_type = new StringType();} |
	    'int[]' {$this_type = new ArrayType();} |
	    name = ID {Identifier this_id = new Identifier($name.text); UserDefinedType this_udef_type = new UserDefinedType(); this_udef_type.setName(this_id); $this_type = this_udef_type;}
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