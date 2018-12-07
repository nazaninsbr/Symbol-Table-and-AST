package ast;


import ast.node.Program;
import ast.node.declaration.ClassDeclaration;
import ast.node.declaration.MethodDeclaration;
import ast.node.declaration.VarDeclaration;
import ast.node.expression.*;
import ast.node.expression.Value.BooleanValue;
import ast.node.expression.Value.IntValue;
import ast.node.expression.Value.StringValue;
import ast.node.statement.*;
import ast.Type.*;

import java.util.ArrayList;
import java.util.List;

import ast.Type.UserDefinedType.UserDefinedType; 
import ast.Type.ArrayType.ArrayType;
import ast.Type.PrimitiveType.*;
import symbolTable.*;




public class VisitorImpl implements Visitor {

    boolean no_error;
    boolean second_round; 
    Program this_prog; 
    SymbolTable symTable;  
    int index; 


    void check_class_name_conditions_without_symTable(Program program){
        ArrayList<String> errors = new ArrayList<>();

        ArrayList<String> class_names = new ArrayList<>();
        class_names.add(program.getMainClass().getName().getName());

        List<ClassDeclaration> prog_classes = program.getClasses();

        for(int i = 0; i < prog_classes.size(); ++i) {
            String this_class_name = prog_classes.get(i).getName().getName(); 
            int temp = class_names.contains(this_class_name) ? 1 : 2 ;
            if (temp==1){
                Identifier new_name_id = new Identifier("Temporary_ClassName_"+Integer.toString(i)+"_"+this_class_name);
                prog_classes.get(i).setName(new_name_id); 
                errors.add("Line:<LineNumber>:Redefinition of class "+this_class_name);
                no_error = false;
            } else {
                class_names.add(this_class_name);
            }
        }

        for(int i = 0; i < errors.size(); ++i) {
            System.out.println(errors.get(i));
        }
    }

    void add_class_to_symbol_table(String class_name, ClassDeclaration class_dec){
        try{
            UserDefinedType class_type = new UserDefinedType(); 
            class_type.setClassDeclaration(class_dec);
            SymbolTableVariableItemBase class_sym_table_item = new SymbolTableVariableItemBase(class_name, class_type, index); 
            symTable.put(class_sym_table_item);
        } catch(ItemAlreadyExistsException e) {
            no_error = false;
            System.out.println("Line:"+Integer.toString(class_dec.get_line_number())+":Redefinition of class "+class_name);
            String new_class_name = "Temporary_<>#$%$#**@@123^<>_ClassName_"+Integer.toString(index)+"_"+class_name;
            Identifier new_name_id = new Identifier(new_class_name);
            class_dec.setName(new_name_id);
            UserDefinedType class_type = new UserDefinedType(); 
            class_type.setClassDeclaration(class_dec);
            SymbolTableVariableItemBase class_sym_table_item = new SymbolTableVariableItemBase(new_class_name, class_type, index); 
            try {
                symTable.put(class_sym_table_item);
            }
            catch(ItemAlreadyExistsException ex){
                // this wont happen (I hope!)
            }
        }
        index += 1;
    }

    void add_object_class_to_symtable(){
        Identifier class_name = new Identifier("Object");
        Identifier parent_name = new Identifier("null");
        add_class_to_symbol_table("Object", new ClassDeclaration(class_name, parent_name));
    }

    void check_class_name_conditions_with_symTable(Program program){
        add_object_class_to_symtable();
        List<ClassDeclaration> prog_classes = program.getClasses();
        String main_class_name = program.getMainClass().getName().getName();
        add_class_to_symbol_table(main_class_name, program.getMainClass());
        program.getMainClass().accept(this);
        for(int i = 0; i < prog_classes.size(); ++i) {
            // System.out.println("**");
            add_class_to_symbol_table(prog_classes.get(i).getName().getName(), prog_classes.get(i));
            prog_classes.get(i).accept(this);
        }
    }

    void check_class_existance_condition_with_symTable(Program program){
        if (symTable.isSymbolTableEmpty()){
            System.out.println("Line:0:No class exists in the program");
            no_error = false;
        }
    }

    void check_class_existance_condition_without_symTable(Program program){
        boolean seen_class=false;

        ClassDeclaration main_class = program.getMainClass(); 
        if(main_class!=null){
            seen_class = true;
        } 
        else {
            List<ClassDeclaration> prog_classes = program.getClasses();
            if(prog_classes.size()>0){
                seen_class = true;
            }
        }

        if (seen_class==false){
            System.out.println("Line:<LineNumber>:No class exists in the program");
            no_error = false;
        }
    }

    void check_conditions_for_inside_classes(Program program){
        ClassDeclaration main_class = program.getMainClass(); 
        List<ClassDeclaration> prog_classes = program.getClasses();

        main_class.accept(this);
        for(int i = 0; i < prog_classes.size(); ++i){
            prog_classes.get(i).accept(this);
        }
    }    

    @Override
    public void visit(Program program) {
        if (no_error==false && second_round==false && symTable==null){
            index = 0;
            no_error = true;
            second_round = false; 
            this_prog = program;
            symTable = new SymbolTable(); 
            check_class_name_conditions_with_symTable(program);
            check_class_existance_condition_with_symTable(program);
        }
        if (no_error==true){
            second_round = true; 
            program.getMainClass().accept(this);
            List<ClassDeclaration> classes = program.getClasses(); 
            for(int i=0; i<classes.size(); i++){
                classes.get(i).accept(this);
            }
        }
    }

    void check_method_existance_condition_without_symTable(ClassDeclaration classDeclaration){
        ArrayList<String> errors = new ArrayList<>();

        ArrayList<String> method_names = new ArrayList<>();

        ArrayList<MethodDeclaration> methods = classDeclaration.getMethodDeclarations(); 

        for(int i = 0; i < methods.size(); ++i) {
            String this_method_name = methods.get(i).getName().getName(); 
            int temp = method_names.contains(this_method_name) ? 1 : 2 ;
            if (temp==1){
                Identifier new_name_id = new Identifier("Temporary_MethodName_"+Integer.toString(i)+"_"+this_method_name);
                methods.get(i).setName(new_name_id); 
                errors.add("Line:<LineNumber>:Redefinition of method "+this_method_name);
                no_error = false;
            } else {
                method_names.add(this_method_name);
            }
        }

        for(int i = 0; i < errors.size(); ++i) {
            System.out.println(errors.get(i));
        }
        
    }

    ArrayList<Type> create_arg_types(MethodDeclaration method_dec){
        ArrayList<VarDeclaration> args = method_dec.getArgs();
        ArrayList<Type> argTypes = new ArrayList<Type>();
        for (int i=0; i<args.size(); i++){
            argTypes.add(args.get(i).getType());
        }
        return argTypes; 
    }

    void add_method_to_symbol_table(String method_name, MethodDeclaration method_dec){
        ArrayList<Type> argTypes = create_arg_types(method_dec);
        try{
            SymbolTableMethodItem method_sym_table_item = new SymbolTableMethodItem(method_name, argTypes); 
            this.symTable.top.put(method_sym_table_item);
        } catch(ItemAlreadyExistsException e) {
            no_error = false;
            System.out.println("Line:"+Integer.toString(method_dec.get_line_number())+":Redefinition of method "+method_name);
            String new_method_name = "Temporary_<>#$%$#**@@123^<>_MethodName_"+Integer.toString(index)+"_"+method_name;
            Identifier new_name_id = new Identifier(new_method_name);
            method_dec.setName(new_name_id);
            SymbolTableMethodItem method_sym_table_item = new SymbolTableMethodItem(method_name, argTypes);  
            try {
                this.symTable.top.put(method_sym_table_item);
            }
            catch(ItemAlreadyExistsException ex){
                // this wont happen (I hope!)
            }
        }
        index += 1;
    }

    void check_method_and_var_existance_condition_with_symTable(ClassDeclaration classDeclaration){
        ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations(); 
        for(int j=0; j<vars.size(); j++){
            add_variable_to_sym_table(vars.get(j));
        }

        ArrayList<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        for(int i=0; i<methodDeclarations.size(); i++){
            add_method_to_symbol_table(methodDeclarations.get(i).getName().getName(), methodDeclarations.get(i));
            methodDeclarations.get(i).accept(this);
        }
    }

    void add_vars_and_methods_to_symbolTable_for_undefiend_checks(ClassDeclaration classDeclaration){
        ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations(); 
        for(int j=0; j<vars.size(); j++){
            add_variable_to_sym_table(vars.get(j));
        }

        ArrayList<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        for(int i=0; i<methodDeclarations.size(); i++){
            add_method_to_symbol_table(methodDeclarations.get(i).getName().getName(), methodDeclarations.get(i));
        }
    }

    SymbolTable add_every_thing_to_symbol_table_no_errors(ClassDeclaration classDeclaration, SymbolTable s){
        ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations(); 
        for(int j=0; j<vars.size(); j++){
            try{
                SymbolTableVariableItemBase var_sym_table_item = new SymbolTableVariableItemBase(vars.get(j).getIdentifier().getName(), vars.get(j).getType(), index); 
                s.top.put(var_sym_table_item);
            } catch(ItemAlreadyExistsException e) {
                no_error = false;
            }
            index += 1;
        }

        ArrayList<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        for(int i=0; i<methodDeclarations.size(); i++){
            try{
                ArrayList<Type> argTypes = create_arg_types(methodDeclarations.get(i));
                SymbolTableMethodItem method_sym_table_item = new SymbolTableMethodItem(methodDeclarations.get(i).getName().getName(), argTypes); 
                s.top.put(method_sym_table_item);
            } catch(ItemAlreadyExistsException e) {
                no_error = false;
            }
            index += 1;
        }
        return s;
    }

    void fill_the_pre_sym_table_with_parent_data(String parent_name){
        Boolean found = false;
        ClassDeclaration mainClass = this.this_prog.getMainClass();
        if(mainClass.getName().getName().equals(parent_name)){
            found=true;
            SymbolTable s = new SymbolTable();
            s = add_every_thing_to_symbol_table_no_errors(mainClass, s); 
            this.symTable.top.setPreSymbolTable(s.top);
        }
        if(found==false){
            List<ClassDeclaration> prog_classes = this.this_prog.getClasses();
            for(int i = 0; i < prog_classes.size(); ++i) {
                if(prog_classes.get(i).getName().getName().equals(parent_name)){
                    SymbolTable s = new SymbolTable();
                    s = add_every_thing_to_symbol_table_no_errors(prog_classes.get(i), s); 
                    this.symTable.top.setPreSymbolTable(s.top);
                    break;
                }
            }
        }
    }

    void ___add_every_thing_to_symbol_table_no_errors(ClassDeclaration classDeclaration){
        ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations(); 
        for(int j=0; j<vars.size(); j++){
            try{
                SymbolTableVariableItemBase var_sym_table_item = new SymbolTableVariableItemBase(vars.get(j).getIdentifier().getName(), vars.get(j).getType(), index); 
                this.symTable.top.put(var_sym_table_item);
            } catch(ItemAlreadyExistsException e) {
                no_error = false;
            }
            index += 1;
        }

        ArrayList<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        for(int i=0; i<methodDeclarations.size(); i++){
            try{
                ArrayList<Type> argTypes = create_arg_types(methodDeclarations.get(i));
                SymbolTableMethodItem method_sym_table_item = new SymbolTableMethodItem(methodDeclarations.get(i).getName().getName(), argTypes); 
                this.symTable.top.put(method_sym_table_item);
            } catch(ItemAlreadyExistsException e) {
                no_error = false;
            }
            index += 1;
        }
    }

    void ___fill_the_sym_table_with_parent_data(String parent_name){
        Boolean found = false;
        ClassDeclaration mainClass = this.this_prog.getMainClass();
        if(mainClass.getName().getName().equals(parent_name)){
            found=true;
            ___add_every_thing_to_symbol_table_no_errors(mainClass); 
        }
        if(found==false){
            List<ClassDeclaration> prog_classes = this.this_prog.getClasses();
            for(int i = 0; i < prog_classes.size(); ++i) {
                if(prog_classes.get(i).getName().getName().equals(parent_name)){
                    ___add_every_thing_to_symbol_table_no_errors(prog_classes.get(i)); 
                    break;
                }
            }
        }
    }


    void continue_phase3_checks_for_class(ClassDeclaration classDeclaration){
        ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations(); 
        for(int j=0; j<vars.size(); j++){
            vars.get(j).accept(this);
        }

        ArrayList<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        for(int i=0; i<methodDeclarations.size(); i++){
            methodDeclarations.get(i).accept(this);
        }
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        if(second_round==false){
            symTable.push(new SymbolTable(symTable.top));            
            if (! classDeclaration.getParentName().getName().equals("null")) {
                fill_the_pre_sym_table_with_parent_data(classDeclaration.getParentName().getName());
            }
            check_method_and_var_existance_condition_with_symTable(classDeclaration);
            symTable.pop();
        } 
        else if(second_round==true && no_error==true){
            symTable.push(new SymbolTable(symTable)); 
            add_vars_and_methods_to_symbolTable_for_undefiend_checks(classDeclaration);
            if (! classDeclaration.getParentName().getName().equals("null")) {
                ___fill_the_sym_table_with_parent_data(classDeclaration.getParentName().getName());
            }
            continue_phase3_checks_for_class(classDeclaration);
            symTable.pop();
        }
    }

    void add_variable_to_sym_table(VarDeclaration this_var){
        try{
            SymbolTableVariableItemBase var_sym_table_item = new SymbolTableVariableItemBase(this_var.getIdentifier().getName(), this_var.getType(), index); 
            this.symTable.top.put(var_sym_table_item);
        } catch(ItemAlreadyExistsException e) {
            no_error = false;
            System.out.println("Line:"+Integer.toString(this_var.get_line_number())+":Redefinition of variable "+this_var.getIdentifier().getName());
            String new_var_name = "Temporary_<>#$%$#**@@123^<>_VarName_"+Integer.toString(index)+"_"+this_var.getIdentifier().getName();
            Identifier new_name_id = new Identifier(new_var_name);
            this_var.setIdentifier(new_name_id);
            SymbolTableVariableItemBase var_sym_table_item = new SymbolTableVariableItemBase(this_var.getIdentifier().getName(), this_var.getType(), index); 
            try {
                this.symTable.top.put(var_sym_table_item);
            }
            catch(ItemAlreadyExistsException ex){
                // this wont happen (I hope!)
            }
        }
        index += 1;
    }

    void check_variable_existance_condition_with_symTable(MethodDeclaration methodDeclaration){
        ArrayList<VarDeclaration> args = methodDeclaration.getArgs();
        ArrayList<VarDeclaration> localVars = methodDeclaration.getLocalVars();
        for(int i=0; i<args.size(); i++){
            add_variable_to_sym_table(args.get(i)); 
        }
        for(int i=0; i<localVars.size(); i++){
            add_variable_to_sym_table(localVars.get(i)); 
        }
    }

    void check_variable_type_defined_condition_with_symTable(MethodDeclaration methodDeclaration){
        ArrayList<VarDeclaration> args = methodDeclaration.getArgs();
        ArrayList<VarDeclaration> localVars = methodDeclaration.getLocalVars();
        for(int i=0; i<args.size(); i++){
            add_variable_to_sym_table(args.get(i)); 
            args.get(i).accept(this);
        }
        for(int i=0; i<localVars.size(); i++){
            add_variable_to_sym_table(localVars.get(i)); 
            localVars.get(i).accept(this);
        }
    }

    void check_for_statements(ArrayList<Statement> body){
        for(int i=0; i<body.size(); i++){
            body.get(i).accept(this);
        }
    }

    void check_method_statement_type_conditions(MethodDeclaration methodDeclaration){

    }
    
    void check_method_return_type_conditions(MethodDeclaration methodDeclaration){

    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        if(second_round==false){
            symTable.push(new SymbolTable(symTable.top));
            check_variable_existance_condition_with_symTable(methodDeclaration);
            check_for_statements(methodDeclaration.getBody());
            symTable.pop();
        }
        else if(second_round==true){
            symTable.push(new SymbolTable(symTable.top));
            check_variable_type_defined_condition_with_symTable(methodDeclaration);
            check_method_statement_type_conditions(methodDeclaration);
            check_method_return_type_conditions(methodDeclaration);
        }

    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        if(second_round==true){
            try {
                if (varDeclaration.getType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")){
                    UserDefinedType x = (UserDefinedType) varDeclaration.getType(); 
                    symTable.top.get(x.getName().getName());
                }
            }
            catch(ItemNotFoundException ex){
                UserDefinedType x = (UserDefinedType) varDeclaration.getType(); 
                System.out.println("Line:"+Integer.toString(varDeclaration.get_line_number())+":name "+x.getName().getName()+" is not defined");
                x.set_is_none_type();
            }
        }
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        if (second_round==false){
            arrayCall.getInstance().accept(this);
            arrayCall.getIndex().accept(this);
        }
        else if(second_round==true){
            System.out.println(arrayCall);
            arrayCall.getInstance().accept(this);
            arrayCall.getIndex().accept(this);
        }
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        if(second_round==false){
            binaryExpression.getLeft().accept(this);
            binaryExpression.getRight().accept(this);
        }
        else if(second_round==true){
            System.out.println(binaryExpression);
            // System.out.println(binaryExpression.getBinaryOperator());
            binaryExpression.getLeft().accept(this);
            binaryExpression.getRight().accept(this);
        }
    }

    @Override
    public void visit(Identifier identifier) {
        if(second_round==true){
            System.out.println(identifier);
        }
    }

    @Override
    public void visit(Length length) {
        if (second_round==false) {
            Expression exp = length.getExpression();
            exp.accept(this);
        }
        else if(second_round==true){
            System.out.println(length);
            Expression exp = length.getExpression();
            exp.accept(this);
        }
    }

    @Override
    public void visit(MethodCall methodCall) {
        if(second_round==false){
            methodCall.getInstance().accept(this);
            ArrayList<Expression> methodcall_args = methodCall.getArgs();
            for (int i = 0; i < methodcall_args.size(); i++){
                methodcall_args.get(i).accept(this);
            }
        }
        else if(second_round==true){
            System.out.println(methodCall);
            methodCall.getInstance().accept(this);
            methodCall.getMethodName().accept(this);
            ArrayList<Expression> methodcall_args = methodCall.getArgs();
            for (int i = 0; i < methodcall_args.size(); i++){
                methodcall_args.get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(NewArray newArray) {
        if(newArray.getIntSize()<=0 && second_round==false){
            no_error = false;
            System.out.println("Line:"+Integer.toString(newArray.get_line_number())+":Array length should not be zero or negative");
        }
        else if(second_round==true){
            System.out.println(newArray);
            newArray.getExpression().accept(this);
        }
    }

    @Override
    public void visit(NewClass newClass) {
        if(second_round==true){
            System.out.println(newClass);
            System.out.println(newClass.getClassName());
        }
    }

    @Override
    public void visit(This instance) {
        if(second_round==true){
            System.out.println(instance);
        }
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        if(second_round==false){
            Expression exp = unaryExpression.getValue();
            exp.accept(this);
        }
        else if(second_round==true){
            System.out.println(unaryExpression);
            Expression exp = unaryExpression.getValue();
            exp.accept(this);
        }
    }

    @Override
    public void visit(BooleanValue value) {
        if(second_round==true){
            System.out.println(value);
        }
    }

    @Override
    public void visit(IntValue value) {
        if(second_round==true){
            System.out.println(value);
        }
    }

    @Override
    public void visit(StringValue value) {
        if(second_round==true){
            System.out.println(value);
        }
    }

    void check_statement_expressions_for_newArray_expr(ArrayList<Expression> exprs){
        for(int i=0; i<exprs.size(); i++){
            exprs.get(i).accept(this);
        }
    }

    @Override
    public void visit(Assign assign) {
        ArrayList<Expression> exprs = new ArrayList<Expression>();
        exprs.add(assign.getlValue()); 
        if (assign.getrValue()!=null){
            exprs.add(assign.getrValue());
        }
        if(second_round==false){
            check_statement_expressions_for_newArray_expr(exprs);
        }
        else if(second_round==true){
            System.out.println(assign);
            check_statement_expressions_for_newArray_expr(exprs);
        }
    }

    @Override
    public void visit(Block block) {
        if(second_round==false){
            check_for_statements(block.getBody());
        } else if(second_round==true) {
            System.out.println(block);
            check_for_statements(block.getBody());
        }
    }

    @Override
    public void visit(Conditional conditional) {
        ArrayList<Expression> exprs = new ArrayList<Expression>();
        exprs.add(conditional.getExpression());
        ArrayList<Statement> statements = new ArrayList<Statement>();
        if(conditional.getConsequenceBody()!=null)
            statements.add(conditional.getConsequenceBody()); 
        if(conditional.getAlternativeBody()!=null)
            statements.add(conditional.getAlternativeBody());
        if(second_round==false){
            check_statement_expressions_for_newArray_expr(exprs);
            check_for_statements(statements);
        }
        else if(second_round==true){
            // System.out.println("XXXXXX");
            System.out.println(conditional);
            // System.out.println("YYYYYYY");
            check_statement_expressions_for_newArray_expr(exprs);
            check_for_statements(statements);
        }
    }

    @Override
    public void visit(While loop) {
        ArrayList<Expression> exprs = new ArrayList<Expression>();
        exprs.add(loop.getCondition());
        ArrayList<Statement> statements = new ArrayList<Statement>();
        if(loop.getBody()!= null)
            statements.add(loop.getBody());
        if(second_round==false){
            check_statement_expressions_for_newArray_expr(exprs);
            check_for_statements(statements);
        }
        else if(second_round==true){
            System.out.println(loop);
            check_statement_expressions_for_newArray_expr(exprs);
            check_for_statements(statements);
        } 
    }

    @Override
    public void visit(Write write) {
        ArrayList<Expression> exprs = new ArrayList<Expression>();
        exprs.add(write.getArg());
        if(second_round==false){
            check_statement_expressions_for_newArray_expr(exprs);
        }
        else if(second_round==true){
            System.out.println(write);
            check_statement_expressions_for_newArray_expr(exprs);
        }
    }

    @Override
    public void visit(MethodCallInMain methodCallInMain) {
        //TODO: implement appropriate visit functionality
    }
}
