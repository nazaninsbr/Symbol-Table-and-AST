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
import ast.node.expression.UnaryOperator;
import java.util.ArrayList;
import java.util.List;

import ast.Type.UserDefinedType.UserDefinedType; 
import ast.Type.ArrayType.ArrayType;
import ast.Type.PrimitiveType.*;
import ast.Type.NoType.NoType;
import symbolTable.*;




public class VisitorImpl implements Visitor {

    boolean no_error;
    boolean second_round; 
    Program this_prog; 
    ClassDeclaration curr_class; 
    SymbolTable symTable;  
    int index; 


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
            if(! program.getMainClass().getMethodDeclarations().get(0).getName().getName().equals("main")){
                System.out.println("Line:"+Integer.toString((program.getMainClass().getMethodDeclarations().get(0)).get_line_number())+":main method was not found");
            }            
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
                method_sym_table_item.set_return_type(methodDeclarations.get(i).getReturnType());
                s.top.put(method_sym_table_item);
            } catch(ItemAlreadyExistsException e) {
                no_error = false;
            }
            index += 1;
        }
        return s;
    }

    SymbolTable fill_the_pre_sym_table_with_parent_data(String parent_name, SymbolTable s, ArrayList<String> already_seen){
        Boolean found = false;
        ClassDeclaration mainClass = this.this_prog.getMainClass();
        if(mainClass.getName().getName().equals(parent_name)){
            found=true;
            s = add_every_thing_to_symbol_table_no_errors(mainClass, s);
            if (! mainClass.getParentName().getName().equals("null")){
                if (already_seen.contains(mainClass.getParentName().getName())) {
                    return s;
                }
                else{
                    already_seen.add(mainClass.getParentName().getName());
                }
                return fill_the_pre_sym_table_with_parent_data(mainClass.getParentName().getName(), s, already_seen);
            } 
        }
        if(found==false){
            List<ClassDeclaration> prog_classes = this.this_prog.getClasses();
            for(int i = 0; i < prog_classes.size(); ++i) {
                if(prog_classes.get(i).getName().getName().equals(parent_name)){
                    s = add_every_thing_to_symbol_table_no_errors(prog_classes.get(i), s);
                    if (! prog_classes.get(i).getParentName().getName().equals("null")){
                        if (already_seen.contains(prog_classes.get(i).getParentName().getName())) {
                            return s;
                        }
                        else{
                            already_seen.add(prog_classes.get(i).getParentName().getName());
                        }
                        return fill_the_pre_sym_table_with_parent_data(prog_classes.get(i).getParentName().getName(), s, already_seen);
                    }
                    break;
                }
            }
        }
        return s;
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

    void ___fill_the_sym_table_with_parent_data(String class_we_came_here_from, String parent_name, ArrayList<String> already_seen){
        Boolean found = false;
        Boolean is_after = false;
        ClassDeclaration mainClass = this.this_prog.getMainClass();
        if(mainClass.getName().getName().equals(parent_name)){
            found=true;
            ___add_every_thing_to_symbol_table_no_errors(mainClass); 
            if (already_seen.contains(mainClass.getParentName().getName())) {
                System.out.println("Line:"+Integer.toString(mainClass.get_line_number())+":circular dependency is not allowed");
                return;
            }
            else{
                already_seen.add(mainClass.getParentName().getName());
                if (! mainClass.getParentName().getName().equals("null") && !mainClass.getParentName().getName().equals("Object")){
                    ___fill_the_sym_table_with_parent_data(mainClass.getName().getName(), mainClass.getParentName().getName(), already_seen);
                }
            }
            
        }
        if(found==false){
            List<ClassDeclaration> prog_classes = this.this_prog.getClasses();
            for(int i = 0; i < prog_classes.size(); ++i) {
                if(prog_classes.get(i).getName().getName().equals(parent_name)){
                    ___add_every_thing_to_symbol_table_no_errors(prog_classes.get(i));
                    if (already_seen.contains(prog_classes.get(i).getParentName().getName())) {
                        if(is_after==false){
                            System.out.println("Line:"+Integer.toString(prog_classes.get(i).get_line_number())+":circular dependency is not allowed");
                            prog_classes.get(i).setParentName(new Identifier("Object"));
                        }
                        return;
                    }
                    already_seen.add(prog_classes.get(i).getParentName().getName()); 
                    if (! prog_classes.get(i).getParentName().getName().equals("null") && !prog_classes.get(i).getParentName().getName().equals("Object")){
                        ___fill_the_sym_table_with_parent_data(prog_classes.get(i).getName().getName(), prog_classes.get(i).getParentName().getName(), already_seen);
                    } 
                    break;
                }
                if(prog_classes.get(i).getName().getName().equals(class_we_came_here_from)){
                    is_after = true;
                }
            }
        }
    }
   
    void check_subtype_class(String class_we_came_here_from, String parent_name, ArrayList<String> already_seen){
        Boolean found = false;
        Boolean is_after = false;
        ClassDeclaration mainClass = this.this_prog.getMainClass();
        if(mainClass.getName().getName().equals(parent_name)){
            found=true;
            //___add_every_thing_to_symbol_table_no_errors(mainClass); 
            if (already_seen.contains(mainClass.getParentName().getName())) {
              //  System.out.println("Line:"+Integer.toString(mainClass.get_line_number())+":circular dependency is not allowed");
                return;
            }
            else{
                already_seen.add(mainClass.getParentName().getName());
                if (! mainClass.getParentName().getName().equals("null") && !mainClass.getParentName().getName().equals("Object")){
                    check_subtype_class(mainClass.getName().getName(), mainClass.getParentName().getName(), already_seen);
                }
            }        
        }
        if(found==false){
            List<ClassDeclaration> prog_classes = this.this_prog.getClasses();
            for(int i = 0; i < prog_classes.size(); ++i) {
                if(prog_classes.get(i).getName().getName().equals(parent_name)){
                   // ___add_every_thing_to_symbol_table_no_errors(prog_classes.get(i));
                    if (already_seen.contains(prog_classes.get(i).getParentName().getName())) {
                        if(is_after==false){
                            //System.out.println("Line:"+Integer.toString(prog_classes.get(i).get_line_number())+":circular dependency is not allowed");
                            //prog_classes.get(i).setParentName(new Identifier("Object"));
                        }
                        return;
                    }
                    already_seen.add(prog_classes.get(i).getParentName().getName()); 
                    if (! prog_classes.get(i).getParentName().getName().equals("null") && !prog_classes.get(i).getParentName().getName().equals("Object")){

                        check_subtype_class(prog_classes.get(i).getName().getName(), prog_classes.get(i).getParentName().getName(), already_seen);
                    } 
                    break;
                }
                if(prog_classes.get(i).getName().getName().equals(class_we_came_here_from)){
                    is_after = true;
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
        this.curr_class = classDeclaration;
        if(second_round==false){
            symTable.push(new SymbolTable(symTable.top));            
            if (! classDeclaration.getParentName().getName().equals("null")) {
                if(! classDeclaration.getParentName().getName().equals(classDeclaration.getName().getName())){
                    SymbolTable s = new SymbolTable();
                    ArrayList<String> already_seen = new ArrayList<String>();
                    already_seen.add(classDeclaration.getParentName().getName());
                    s = fill_the_pre_sym_table_with_parent_data(classDeclaration.getParentName().getName(), s, already_seen);
                    this.symTable.top.setPreSymbolTable(s.top);
                }
            }
            check_method_and_var_existance_condition_with_symTable(classDeclaration);
            symTable.pop();
        } 
        else if(second_round==true && no_error==true){
            symTable.push(new SymbolTable(symTable)); 
            add_vars_and_methods_to_symbolTable_for_undefiend_checks(classDeclaration);
            if (! classDeclaration.getParentName().getName().equals("null")) {
                try{
                    SymbolTableItem thisItem = symTable.top.get(classDeclaration.getParentName().getName());
                }
                catch(ItemNotFoundException ex){
                    System.out.println("Line:"+Integer.toString(classDeclaration.get_line_number())+":class "+ classDeclaration.getParentName().getName()+" is not declared");
                    Identifier new_parent_name = new Identifier("null");
                    classDeclaration.setParentName(new_parent_name);
                }               
            }
            if (! classDeclaration.getParentName().getName().equals("null")) {
                ArrayList<String> already_seen = new ArrayList<String>();
                ___fill_the_sym_table_with_parent_data(classDeclaration.getName().getName(), classDeclaration.getParentName().getName(), already_seen);
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

    void check_method_return_type_conditions(MethodDeclaration methodDeclaration){
        methodDeclaration.getReturnValue().accept(this);
        if(! methodDeclaration.getReturnValue().getType().toString().equals("NoType")){
            if ( (methodDeclaration.getReturnValue().getType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")) && (methodDeclaration.getReturnType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")) ){
                ArrayList<String> parents = new ArrayList<String>();
                String this_class_name = ((UserDefinedType)(methodDeclaration.getReturnValue().getType())).getClassDeclaration().getName().getName();
                parents.add(this_class_name);
                parents.add("Object");
                add_all_parent_names(this_class_name, parents);
                check_subtype_class(((UserDefinedType)(methodDeclaration.getReturnValue().getType())).getClassDeclaration().getName().getName(),((UserDefinedType)(methodDeclaration.getReturnValue().getType())).getClassDeclaration().getParentName().getName(),parents);
                boolean ok_subtype = in_this_array(parents, methodDeclaration.getReturnType().toString());

                if (!ok_subtype){
                    System.out.println("Line:"+Integer.toString(methodDeclaration.getReturnValue().get_line_number())+":"+methodDeclaration.getName().getName()+" must be "+methodDeclaration.getReturnType().toString());
                }
            }
            else if (! methodDeclaration.getReturnType().toString().equals(methodDeclaration.getReturnValue().getType().toString())){
                System.out.println("Line:"+Integer.toString(methodDeclaration.getReturnValue().get_line_number())+":"+methodDeclaration.getName().getName()+" must be "+methodDeclaration.getReturnType().toString());
            }
        }
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
            check_for_statements(methodDeclaration.getBody());
            check_method_return_type_conditions(methodDeclaration);
            symTable.pop();
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
            arrayCall.getInstance().accept(this);
            arrayCall.getIndex().accept(this);
            if ( arrayCall.getIndex().getType().toString().equals("int") || arrayCall.getIndex().getType().toString().equals("NoType")){
                if( (!arrayCall.getInstance().getType().toString().equals("int[]")) && (!arrayCall.getInstance().getType().toString().equals("NoType")) ){
                    System.out.println("Line:"+Integer.toString(arrayCall.getInstance().get_line_number())+":"+arrayCall.getInstance().getType().toString()+" object is not subscriptable");
                    arrayCall.setType(new NoType());
                }
                else {
                    arrayCall.setType(new IntType());
                }
            } 
            else {
                System.out.println("Line:"+Integer.toString(arrayCall.getIndex().get_line_number())+":list indices must be integers");
                if( (!arrayCall.getInstance().getType().toString().equals("int[]")) && (!arrayCall.getInstance().getType().toString().equals("NoType")) ){
                    System.out.println("Line:"+Integer.toString(arrayCall.getInstance().get_line_number())+":"+arrayCall.getInstance().getType().toString()+" object is not subscriptable");
                }
                arrayCall.setType(new NoType());
            }
        }
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        if(second_round==false){
            binaryExpression.getLeft().accept(this);
            binaryExpression.getRight().accept(this);
        }
        else if(second_round==true){
            Type this_binary_exp_type;
            binaryExpression.getLeft().accept(this);
            binaryExpression.getRight().accept(this);
            if ( !(binaryExpression.getLeft().getType().toString().equals("NoType") || binaryExpression.getRight().getType().toString().equals("NoType"))) {
                if (binaryExpression.getLeft().getType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType") && binaryExpression.getRight().getType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")) {
                    ArrayList<String> parents = new ArrayList<String>();
                    String this_class_name = ((UserDefinedType)(binaryExpression.getLeft().getType())).getClassDeclaration().getName().getName();
                    parents.add(this_class_name);
                    parents.add("Object");
                    add_all_parent_names(this_class_name, parents);
                    check_subtype_class(((UserDefinedType)(binaryExpression.getLeft().getType())).getClassDeclaration().getName().getName(),((UserDefinedType)(binaryExpression.getLeft().getType())).getClassDeclaration().getParentName().getName(),parents);
                    boolean ok_subtype = in_this_array(parents, binaryExpression.getRight().getType().toString());
                    if (! ok_subtype){
                        ArrayList<String> parents2 = new ArrayList<String>();
                        String this_class_name2 = ((UserDefinedType)(binaryExpression.getRight().getType())).getClassDeclaration().getName().getName();
                        parents2.add(this_class_name2);
                        parents2.add("Object");
                        add_all_parent_names(this_class_name2, parents2);
                        check_subtype_class(((UserDefinedType)(binaryExpression.getRight().getType())).getClassDeclaration().getName().getName(),((UserDefinedType)(binaryExpression.getRight().getType())).getClassDeclaration().getParentName().getName(),parents2);
                        boolean ok_subtype2 = in_this_array(parents2, binaryExpression.getLeft().getType().toString());
                        if (! ok_subtype2) {
                            System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                            this_binary_exp_type = new NoType();
                        }
                        else{
                            this_binary_exp_type = binaryExpression.getRight().getType();
                        }
                    }
                    else{
                        this_binary_exp_type = binaryExpression.getLeft().getType();
                    }
                }
                else if( ! binaryExpression.getLeft().getType().toString().equals(binaryExpression.getRight().getType().toString()) ){
                    System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                    this_binary_exp_type = new NoType();
                } 
                else {
                    if(binaryExpression.getBinaryOperator() == BinaryOperator.eq || binaryExpression.getBinaryOperator() == BinaryOperator.neq){
                        if (binaryExpression.getLeft().getType().toString().equals("int[]")) {
                            int size1 = ((ArrayType) binaryExpression.getLeft().getType()).getSize();
                            int size2 = ((ArrayType) binaryExpression.getRight().getType()).getSize();
                            if(size1!=size2){
                                System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":array sizes need to be the same");
                                this_binary_exp_type = new NoType();
                            }
                            else {
                                this_binary_exp_type = new BooleanType();
                            }
                        }
                        else {
                            this_binary_exp_type = new BooleanType();
                        }
                    }
                    else if (binaryExpression.getBinaryOperator() == BinaryOperator.and || binaryExpression.getBinaryOperator() == BinaryOperator.or ){
                        if((!binaryExpression.getLeft().getType().toString().equals("bool") )|| (!binaryExpression.getRight().getType().toString().equals("bool"))) {
                            System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                            this_binary_exp_type = new NoType();
                        }
                        else{
                            this_binary_exp_type = binaryExpression.getLeft().getType();
                        }
                    }
                    else if ( binaryExpression.getBinaryOperator() == BinaryOperator.mult || binaryExpression.getBinaryOperator() == BinaryOperator.div
                                    || binaryExpression.getBinaryOperator() == BinaryOperator.add || binaryExpression.getBinaryOperator() == BinaryOperator.sub
                                        || binaryExpression.getBinaryOperator() == BinaryOperator.lt || binaryExpression.getBinaryOperator() == BinaryOperator.gt ){
                        if((!binaryExpression.getLeft().getType().toString().equals("int") )|| (!binaryExpression.getRight().getType().toString().equals("int"))) {
                            System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                            this_binary_exp_type = new NoType();
                        }
                        else{
                            this_binary_exp_type = binaryExpression.getLeft().getType();
                        }
                    }
                    else{
                        this_binary_exp_type = binaryExpression.getLeft().getType();
                    }
                }
            } 
            else {
                if ( !(binaryExpression.getLeft().getType().toString().equals("NoType")) ){
                    if (binaryExpression.getBinaryOperator() == BinaryOperator.and || binaryExpression.getBinaryOperator() == BinaryOperator.or ){
                        if((!binaryExpression.getLeft().getType().toString().equals("bool") )) {
                            System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                            this_binary_exp_type = new NoType();
                        }
                        else{
                            this_binary_exp_type = binaryExpression.getLeft().getType();
                        }
                    }
                    else if ( binaryExpression.getBinaryOperator() == BinaryOperator.mult || binaryExpression.getBinaryOperator() == BinaryOperator.div
                                    || binaryExpression.getBinaryOperator() == BinaryOperator.add || binaryExpression.getBinaryOperator() == BinaryOperator.sub
                                        || binaryExpression.getBinaryOperator() == BinaryOperator.lt || binaryExpression.getBinaryOperator() == BinaryOperator.gt ){
                        if((!binaryExpression.getLeft().getType().toString().equals("int") )) {
                            System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                            this_binary_exp_type = new NoType();
                        }
                        else{
                            this_binary_exp_type = binaryExpression.getLeft().getType();
                        }
                    }
                    else{
                        this_binary_exp_type = binaryExpression.getLeft().getType();
                    }
                }
                else if ( !(binaryExpression.getRight().getType().toString().equals("NoType")) ){
                    if (binaryExpression.getBinaryOperator() == BinaryOperator.and || binaryExpression.getBinaryOperator() == BinaryOperator.or ){
                        if((!binaryExpression.getRight().getType().toString().equals("bool") )) {
                            System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                            this_binary_exp_type = new NoType();
                        }
                        else{
                            this_binary_exp_type = binaryExpression.getLeft().getType();
                        }
                    }
                    else if ( binaryExpression.getBinaryOperator() == BinaryOperator.mult || binaryExpression.getBinaryOperator() == BinaryOperator.div
                                    || binaryExpression.getBinaryOperator() == BinaryOperator.add || binaryExpression.getBinaryOperator() == BinaryOperator.sub
                                        || binaryExpression.getBinaryOperator() == BinaryOperator.lt || binaryExpression.getBinaryOperator() == BinaryOperator.gt ){
                        if((!binaryExpression.getRight().getType().toString().equals("int") )) {
                            System.out.println("Line:"+Integer.toString(binaryExpression.get_line_number())+":unsupported operand type for "+binaryExpression.getBinaryOperator());
                            this_binary_exp_type = new NoType();
                        }
                        else{
                            this_binary_exp_type = binaryExpression.getLeft().getType();
                        }
                    }
                    else{
                        this_binary_exp_type = binaryExpression.getLeft().getType();
                    }
                }
                else {
                    this_binary_exp_type = new NoType();
                }
            }
            if(!this_binary_exp_type.toString().equals("NoType") && (binaryExpression.getBinaryOperator() == BinaryOperator.neq || binaryExpression.getBinaryOperator() == BinaryOperator.eq || binaryExpression.getBinaryOperator() == BinaryOperator.lt || binaryExpression.getBinaryOperator() == BinaryOperator.gt ) ){
              binaryExpression.setType(new BooleanType());
            }
            else{
                binaryExpression.setType(this_binary_exp_type);
            }
        }
    }

    @Override
    public void visit(Identifier identifier) {
        if(second_round==true){
            try {
                SymbolTableItem thisItem = symTable.top.get(identifier.getName());
                SymbolTableVariableItemBase thisIdType = (SymbolTableVariableItemBase) thisItem;
                identifier.setType(thisIdType.getType());
            }
            catch(ItemNotFoundException ex){
                System.out.println("Line:"+Integer.toString(identifier.get_line_number())+":variable "+identifier.getName()+" is not declared");
                identifier.setType(new NoType());
            }
        }
    }

    @Override
    public void visit(Length length) {
        if (second_round==false) {
            Expression exp = length.getExpression();
            exp.accept(this);
        }
        else if(second_round==true){
            length.getExpression().accept(this);
            if(length.getExpression().getType().toString().equals("int[]")){
                length.setType(new IntType());
            } 
            else if (length.getExpression().getType().toString().equals("NoType")){
                length.setType(new NoType());
            } 
            else {
                System.out.println("Line:"+Integer.toString(length.get_line_number())+":object of type "+length.getExpression().getType().toString()+" has no length");
                length.setType(new NoType());
            }
        }
    }

    SymbolTable add_every_thing_to_symbol_table_no_errors_for_method_call(ClassDeclaration classDeclaration, SymbolTable s){
        ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations(); 
        for(int j=0; j<vars.size(); j++){
            try{
                SymbolTableVariableItemBase var_sym_table_item = new SymbolTableVariableItemBase(vars.get(j).getIdentifier().getName(), vars.get(j).getType(), index); 
                s.put(var_sym_table_item);
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
                method_sym_table_item.set_return_type(methodDeclarations.get(i).getReturnType());
                s.put(method_sym_table_item);
            } catch(ItemAlreadyExistsException e) {
                no_error = false;
            }
            index += 1;
        }
        return s;
    }

    void create_symbol_table_for_class(String this_class_name, SymbolTable s){
        ClassDeclaration mainClass = this.this_prog.getMainClass();
        if(mainClass.getName().getName().equals(this_class_name)){
            s = add_every_thing_to_symbol_table_no_errors_for_method_call(mainClass, s); 
            return;
        }
        List<ClassDeclaration> prog_classes = this.this_prog.getClasses();
        for(int i = 0; i < prog_classes.size(); ++i) {
            if(prog_classes.get(i).getName().getName().equals(this_class_name)){
                s = add_every_thing_to_symbol_table_no_errors_for_method_call(prog_classes.get(i), s);
                if (! prog_classes.get(i).getParentName().getName().equals("null") && !prog_classes.get(i).getParentName().getName().equals("Object")){
                    create_symbol_table_for_class(prog_classes.get(i).getParentName().getName(), s);
                } 
                break;
            }
        }
    }


    boolean find_class_and_get_symTable(MethodCall methodCall, SymbolTable s){
        methodCall.getInstance().accept(this);
        if (methodCall.getInstance().getType().toString().equals("NoType")){
            return false;
        } else if(methodCall.getInstance().getType().toString().equals("int") || methodCall.getInstance().getType().toString().equals("string") || methodCall.getInstance().getType().toString().equals("bool") || methodCall.getInstance().getType().toString().equals("int[]") ) {
            System.out.println("Line:"+Integer.toString(methodCall.get_line_number())+":method class is not allowed on a primitive type");
        } else{
            UserDefinedType this_type = (UserDefinedType) methodCall.getInstance().getType();
            create_symbol_table_for_class(this_type.getClassDeclaration().getName().getName(), s);
            return true;
        }
        return false;
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
            SymbolTable this_classes_symTable = new SymbolTable(); 
            boolean check_error = find_class_and_get_symTable(methodCall, this_classes_symTable);
            if (methodCall.getInstance().getType().toString().equals("NoType") || methodCall.getInstance().getType().toString().equals("null")) {
                if(methodCall.getInstance().getClass().getName().equals("Identifier")){
                    String the_class_name = ((Identifier) methodCall.getInstance()).getName();
                    System.out.println("Line:"+Integer.toString(methodCall.get_line_number())+":variable "+the_class_name+" is of a class that is not declared");
                    
                }
                methodCall.setType(new NoType());
                return;
            }
            if (check_error){
                try {
                    SymbolTableItem thisItem = this_classes_symTable.get("method_"+methodCall.getMethodName().getName());
                    SymbolTableMethodItem method_item = (SymbolTableMethodItem) thisItem;
                    ArrayList<Expression> args = methodCall.getArgs();
                    ArrayList<Type> argTypes = method_item.get_arg_types();
                    for(int i=0; i<args.size(); i++){
                        args.get(i).accept(this);
                        if (! args.get(i).getType().toString().equals("NoType")){
                            if ( (args.get(i).getType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")) && (argTypes.get(i).getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")) ){
                                ArrayList<String> parents = new ArrayList<String>();
                                String this_class_name = ((UserDefinedType)(args.get(i).getType())).getClassDeclaration().getName().getName();
                                parents.add(this_class_name);
                                parents.add("Object");
                                add_all_parent_names(this_class_name, parents);
                                check_subtype_class(((UserDefinedType)(args.get(i).getType())).getClassDeclaration().getName().getName(),((UserDefinedType)(args.get(i).getType())).getClassDeclaration().getParentName().getName(),parents);
                                boolean ok_subtype = in_this_array(parents, argTypes.get(i).toString());

                                if (!ok_subtype){
                                    System.out.println("Line:"+Integer.toString(methodCall.get_line_number())+":invalid type for argument number "+Integer.toString(i+1));
                                }
                            }
                            else if (! args.get(i).getType().toString().equals(argTypes.get(i).toString())){
                                System.out.println("Line:"+Integer.toString(methodCall.get_line_number())+":invalid type for argument number "+Integer.toString(i+1));
                            }
                        }
                    }
                    methodCall.setType(method_item.get_return_type());
                }
                catch(ItemNotFoundException ex){
                    String the_class_name = methodCall.getInstance().getType().toString();
                    System.out.println("Line:"+Integer.toString(methodCall.get_line_number())+":there is no method named "+methodCall.getMethodName().getName()+" in class "+the_class_name);
                    methodCall.setType(new NoType());
                }
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
            newArray.getExpression().accept(this);
            ArrayType this_array = new ArrayType(); 
            this_array.setSize(newArray.getIntSize());
            newArray.setType(this_array);
        }
    }

    @Override
    public void visit(NewClass newClass) {
        if(second_round==true){
            
            Identifier class_name = newClass.getClassName();
            try {
                SymbolTableItem thisItem = symTable.top.get(newClass.getClassName().getName());
                SymbolTableVariableItemBase thisClassType = (SymbolTableVariableItemBase) thisItem;
                UserDefinedType this_new_class_type = new UserDefinedType();
                this_new_class_type.setClassDeclaration(((UserDefinedType)(thisClassType.getType())).getClassDeclaration());
                this_new_class_type.setName(((UserDefinedType)(thisClassType.getType())).getName());
                newClass.setType(this_new_class_type);
            }
            catch(ItemNotFoundException ex){
                System.out.println("Line:"+Integer.toString(newClass.get_line_number())+":class "+ newClass.getClassName().getName()+" is not declared");
                newClass.setType(new NoType());
            }
        }
    }

    @Override
    public void visit(This instance) {
        if(second_round==true){
            UserDefinedType class_type = new UserDefinedType(); 
            class_type.setClassDeclaration(this.curr_class);
            instance.setType(class_type);
        }
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        if(second_round==false){
            Expression exp = unaryExpression.getValue();
            exp.accept(this);
        }
        else if(second_round==true){
            Expression exp = unaryExpression.getValue();
            exp.accept(this);
            
            if ( !(exp.getType().toString().equals("NoType"))) {
                if( unaryExpression.getUnaryOperator() == UnaryOperator.not && (! exp.getType().toString().equals("bool") )){
                    System.out.println("Line:"+Integer.toString(unaryExpression.get_line_number())+":unsupported operand type for not");
                    unaryExpression.setType(new NoType());
                }
                else if( unaryExpression.getUnaryOperator() ==UnaryOperator.minus && (! exp.getType().toString().equals("int") )){
                    System.out.println("Line:"+Integer.toString(unaryExpression.get_line_number())+":unsupported operand type for minus");
                     unaryExpression.setType(new NoType());
                }
                else {
                    unaryExpression.setType(exp.getType());
                }
            } else{
                unaryExpression.setType(new NoType());
            }
        }
    }

    @Override
    public void visit(BooleanValue value) {
        if(second_round==true){
            value.setType(new BooleanType());
        }
    }

    @Override
    public void visit(IntValue value) {
        if(second_round==true){
            value.setType(new IntType());
        }
    }

    @Override
    public void visit(StringValue value) {
        if(second_round==true){
            value.setType(new StringType());
        }
    }

    void check_statement_expressions_for_newArray_expr(ArrayList<Expression> exprs){
        for(int i=0; i<exprs.size(); i++){
            exprs.get(i).accept(this);
        }
    }
    public void print_this(ArrayList<String> list){
        System.out.println("--------");
        for (int i = 0; i < list.size(); i++ ){
            System.out.println(list.get(i));
        } 
        System.out.println("+++++++");
    }
    public boolean in_this_array(ArrayList<String> list,String name){
        for (int i = 0; i < list.size(); i++ ){
            if (list.get(i).equals(name))
                return true;
        }
        return false;
    }

    void add_all_parent_names(String this_class_name, ArrayList<String> parents){
        ClassDeclaration mainClass = this.this_prog.getMainClass();
        if(mainClass.getName().getName().equals(this_class_name)){
            parents.add(mainClass.getName().getName()); 
            return;
        }
        List<ClassDeclaration> prog_classes = this.this_prog.getClasses();
        for(int i = 0; i < prog_classes.size(); ++i) {
            if(prog_classes.get(i).getName().getName().equals(this_class_name)){
                parents.add(prog_classes.get(i).getName().getName());
                if (! prog_classes.get(i).getParentName().getName().equals("null") && !prog_classes.get(i).getParentName().getName().equals("Object")){
                    add_all_parent_names(prog_classes.get(i).getParentName().getName(), parents);
                } 
                break;
            }
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
            if (!(assign.getlValue().getClass().getName().equals("ast.node.expression.Identifier") || assign.getlValue().getClass().getName().equals("ast.node.expression.ArrayCall"))) {
                System.out.println("Line:"+Integer.toString(assign.getlValue().get_line_number())+":left side of assignment must be a valid lvalue");
            }
            else if (assign.getlValue().getClass().getName().equals("ast.node.expression.ArrayCall") ){
                if (!(((ArrayCall)assign.getlValue()).getInstance().getClass().getName().equals("ast.node.expression.Identifier"))){
                    System.out.println("Line:"+Integer.toString(assign.getlValue().get_line_number())+":left side of assignment must be a valid lvalue");
                }
            }
            assign.getlValue().accept(this);
            assign.getrValue().accept(this);
            if (!(assign.getlValue().getType().toString().equals("NoType") || assign.getrValue().getType().toString().equals("NoType"))) {
                
                if ( (assign.getrValue().getType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")) && (assign.getlValue().getType().getClass().getName().equals("ast.Type.UserDefinedType.UserDefinedType")) ){
                    ArrayList<String> parents = new ArrayList<String>();
                    String this_class_name = ((UserDefinedType)(assign.getrValue().getType())).getClassDeclaration().getName().getName();
                    parents.add(this_class_name);
                    parents.add("Object");
                    add_all_parent_names(this_class_name, parents);
                    check_subtype_class(((UserDefinedType)(assign.getrValue().getType())).getClassDeclaration().getName().getName(),((UserDefinedType)(assign.getrValue().getType())).getClassDeclaration().getParentName().getName(),parents);
                    boolean ok_subtype = in_this_array(parents,assign.getlValue().getType().toString());

                    if (!ok_subtype){
                        System.out.println("Line:"+Integer.toString(assign.get_line_number())+":unsupported operand type for assign");
                    }
                }
                else{
                    if( ! assign.getlValue().getType().toString().equals(assign.getrValue().getType().toString()) ){
                        System.out.println("Line:"+Integer.toString(assign.get_line_number())+":unsupported operand type for assign");
                    }                   
                }
                if(assign.getlValue().getClass().getName().equals("ast.node.expression.Identifier") && assign.getrValue().getClass().getName().equals("ast.node.expression.NewArray")){
                    try {
                        SymbolTableItem this_item = symTable.top.get(((Identifier) assign.getlValue()).getName());
                        SymbolTableVariableItemBase this_var_item = (SymbolTableVariableItemBase) this_item;
                        this_var_item.setType(assign.getrValue().getType()); 
                    }
                    catch(ItemNotFoundException ex){
                    }
                }
            }
        }
    }

    @Override
    public void visit(Block block) {
        if(second_round==false){
            check_for_statements(block.getBody());
        } else if(second_round==true) {
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
            conditional.getExpression().accept(this);
            if (!(conditional.getExpression().getType().toString().equals("bool") || conditional.getExpression().getType().toString().equals("NoType"))) {
                System.out.println("Line:"+Integer.toString(conditional.get_line_number())+":condition type must be boolean");
            }  
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
            loop.getCondition().accept(this);
            if (!(loop.getCondition().getType().toString().equals("bool") || loop.getCondition().getType().toString().equals("NoType"))) {
                System.out.println("Line:"+Integer.toString(loop.get_line_number())+":condition type must be boolean");
                // no type?
            }    
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
            write.getArg().accept(this);
            if (!(write.getArg().getType().toString().equals("string") || write.getArg().getType().toString().equals("NoType") || write.getArg().getType().toString().equals("int") || write.getArg().getType().toString().equals("int[]"))){
                System.out.println("Line:"+Integer.toString(write.get_line_number())+":unsupported type for writeln");
            }
        }
    }
/*
    @Override
    public void visit(MethodCallInMain methodCallInMain) {
        if(second_round==false){
            methodCallInMain.getInstance().accept(this);
            ArrayList<Expression> methodCallInMain_args = methodCallInMain.getArgs();
            for (int i = 0; i < methodCallInMain_args.size(); i++){
                methodCallInMain_args.get(i).accept(this);
            }
        }
        else if(second_round==true){
            if (this.curr_class.getName().getName().equals(this_prog.getMainClass().getName().getName()) ){
                methodCallInMain.getInstance().accept(this);
                methodCallInMain.getMethodName().accept(this);
                ArrayList<Expression> methodCallInMain_args = methodCallInMain.getArgs();
                for (int i = 0; i < methodCallInMain_args.size(); i++){
                    methodCallInMain_args.get(i).accept(this);
                }                
            }
            
            else{
                System.out.println("Line:"+Integer.toString(methodCallInMain.get_line_number())+":method call is illegal outside main class");
            }
        }
    }
    */
//////////////
    @Override
    public void visit(MethodCallInMain methodCallInMain) {
        MethodCall this_methodCall = new MethodCall(methodCallInMain.getInstance(),methodCallInMain.getMethodName());
        ArrayList<Expression> methodCallInMain_args = methodCallInMain.getArgs();
        this_methodCall.set_line_number(methodCallInMain.get_line_number());
        for (int i = 0; i < methodCallInMain_args.size(); i++){
            this_methodCall.addArg(methodCallInMain_args.get(i));
        }

        if(second_round==false){
            this_methodCall.accept(this);
        }
        else if(second_round==true){
            if (this.curr_class.getName().getName().equals(this_prog.getMainClass().getName().getName()) ){ 
                this_methodCall.accept(this);               
            }
            else{
                System.out.println("Line:"+Integer.toString(methodCallInMain.get_line_number())+":method call is illegal outside main class");
            }
        }
    }

}
