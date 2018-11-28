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
import ast.Type.Type;

import java.util.ArrayList;
import java.util.List;

import ast.Type.UserDefinedType.UserDefinedType; 
import ast.Type.ArrayType.ArrayType;
import ast.Type.PrimitiveType.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableVariableItemBase;
import symbolTable.SymbolTableMethodItem;
import symbolTable.ItemAlreadyExistsException;


public class VisitorImpl implements Visitor {

    boolean no_error;
    boolean second_round; 
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

    void check_class_name_conditions_with_symTable(Program program){
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

    void print_program_content(Program prog){
        System.out.println(prog.toString());
        ClassDeclaration main_class = prog.getMainClass();
        print_mainclassdeclaration_content(main_class);

        List<ClassDeclaration> classes = prog.getClasses();;
        for (int i = 0; i < classes.size(); i++){
            print_classdeclration_content(classes.get(i));
        }
    }
    void print_mainclassdeclaration_content(ClassDeclaration main_class){
        System.out.println(main_class.toString());
        //BADI bashe ya na?
        print_expression_content(main_class.getName());// ID ham bege ya faghat name??? ya kollan print?
        //System.out.println(cls.getName().getName());
        //?main method ro hich ja estefade nakarde ke????????????
        ArrayList<MethodDeclaration> main_methods = main_class.getMethodDeclarations();
        for(int j=0; j<main_methods.size(); j++){
            print_mainmethoddeclaration_content(main_methods.get(j));
        }    
    }
    void print_classdeclration_content(ClassDeclaration cls){
        System.out.println(cls.toString());
        //BADI bashe ya na?
        print_expression_content(cls.getName());// ID ham bege ya faghat name??? ya kollan print?
        //System.out.println(cls.getName().getName()); 
         print_expression_content(cls.getParentName());// ID ham bege ya faghat name??? ya kollan print?
        //System.out.println(cls.getParentName().getName());
        ArrayList<VarDeclaration> vars = cls.getVarDeclarations();
        for(int j=0; j<vars.size(); j++){
           print_vardeclaration_content(vars.get(j));
        }
        ArrayList<MethodDeclaration> methods = cls.getMethodDeclarations();
        for(int j=0; j<methods.size(); j++){
            print_methoddeclration_content(methods.get(j));
        }
    }
    void print_mainmethoddeclaration_content(MethodDeclaration main_mtd){
        System.out.println(main_mtd.toString());
        //BADI bashe ya na??
        //print_expression_content(main_mtd.getName());// ID ham bege ya faghat name??? ya kollan print?
        //System.out.println(main_mtd.getName().getName());//??

        //System.out.println(main_mtd.getName().getName()); INJA ??
        ArrayList<Statement> statements = main_mtd.getBody();
        for(int k=0; k<statements.size(); k++){
            print_statement_content(statements.get(k));
        }
        print_expression_content(main_mtd.getReturnValue());      
    }
    void print_methoddeclration_content(MethodDeclaration mtd){
        System.out.println(mtd.toString());
        //BADI bashe ya na??
        print_expression_content(mtd.getName());// ID ham bege ya faghat name??? ya kollan print?
        //System.out.println(mtd.getName().getName());//??
        ArrayList<VarDeclaration> args = mtd.getArgs();
        for (int i = 0; i < args.size(); i++){
            print_vardeclaration_content(args.get(i));
        }
        ArrayList<VarDeclaration> localVars = mtd.getLocalVars();
        for(int l=0; l<localVars.size(); l++){
            print_vardeclaration_content(localVars.get(l));
        }
        //System.out.println(mtd.getName().getName()); INJA ??
        ArrayList<Statement> statements = mtd.getBody();
        for(int k=0; k<statements.size(); k++){
            print_statement_content(statements.get(k));
        }
        print_expression_content(mtd.getReturnValue());    
    }
    void print_vardeclaration_content(VarDeclaration var){
        System.out.println(var.toString());
        print_expression_content(var.getIdentifier());// ID ham bege ya faghat name??? ya kollan print?
        //System.out.println(var.getIdentifier().getName());
        print_type_content(var.getType());//TYPE bege???
        //System.out.println(localVars.get(l).getType().toString());
    }
    void print_expression_content(Expression expr){
        String expr_type = expr.getClass().getSimpleName();
        if(expr_type.equals("BooleanValue")){
            //
            System.out.println(((BooleanValue)expr).toString());
        }
        else if(expr_type.equals("IntValue")){
            System.out.println(((IntValue)expr).toString());
        }
        else if(expr_type.equals("StringValue")){
            System.out.println(((StringValue)expr).toString());
        }
        else if(expr_type.equals("ArrayCall")){
            System.out.println(((ArrayCall)expr).toString());
            //pre order???
            print_expression_content(((ArrayCall)expr).getInstance());
            print_expression_content(((ArrayCall)expr).getIndex());
        }
        else if(expr_type.equals("BinaryExpression")){
            System.out.println(((BinaryExpression)expr).toString());
            print_expression_content(((BinaryExpression)expr).getLeft());
            System.out.println(((BinaryExpression)expr).getBinaryOperator());//??string //print beshe??
            print_expression_content(((BinaryExpression)expr).getRight());
        }
        else if(expr_type.equals("Identifier")){
            System.out.println(((Identifier)expr).toString());
            //String value print kone?
        }
        else if(expr_type.equals("Length")){
            System.out.println(((Length)expr).toString());
            print_expression_content(((Length)expr).getExpression());
        }
        else if(expr_type.equals("MethodCall")){
            System.out.println(((MethodCall)expr).toString());
            //arg??
            ArrayList<Expression> methodcall_args = ((MethodCall)expr).getArgs();
            for (int i = 0; i < methodcall_args.size(); i++){
                print_expression_content(methodcall_args.get(i));
            }
            print_expression_content(((MethodCall)expr).getInstance());
            print_expression_content(((MethodCall)expr).getMethodName());
        }
        else if(expr_type.equals("NewArray")){
            System.out.println(((NewArray)expr).toString());
            print_expression_content(((NewArray)expr).getExpression());
            //print_expression_content(((NewArray)expr).getIntSize()); ///???
            System.out.println(((NewArray)expr).getIntSize());
        }
        else if(expr_type.equals("NewClass")){
            System.out.println(((NewClass)expr).toString());
            print_expression_content(((NewClass)expr).getClassName());
        }
        else if(expr_type.equals("NewClassAndMethodCall")){
            System.out.println(((NewClassAndMethodCall)expr).toString());
            print_expression_content(((NewClassAndMethodCall)expr).getnewClass());
            print_expression_content(((NewClassAndMethodCall)expr).getmethodCall());
        }
        else if(expr_type.equals("This")){
            System.out.println(((This)expr).toString());
        }
               
        else if(expr_type.equals("UnaryExpression")){
            System.out.println(((UnaryExpression)expr).toString());
            System.out.println(((UnaryExpression)expr).getUnaryOperator());//??string //print beshe??
            print_expression_content(((UnaryExpression)expr).getValue());
        }
        else{
            //System.out.println(expr.toString());
            return;
        }    
    }
    void print_statement_content(Statement stm){
        String stm_type = stm.getClass().getSimpleName();
        if(stm_type.equals("Assign")){
            System.out.println(((Assign)stm).toString());
            print_expression_content(((Assign)stm).getlValue());
            print_expression_content(((Assign)stm).getrValue());
        }
        else if(stm_type.equals("Block")){
            System.out.println(((Block)stm).toString());
            ArrayList<Statement> body = ((Block)stm).getBody();
            for (int i = 0; i < body.size(); i++){
                print_statement_content(body.get(i));
            }
        }
        else if(stm_type.equals("Conditional")){
            System.out.println(((Conditional)stm).toString());
            print_expression_content(((Conditional)stm).getExpression());
            print_statement_content(((Conditional)stm).getConsequenceBody());
            print_statement_content(((Conditional)stm).getAlternativeBody());
        }
        else if(stm_type.equals("While")){
            System.out.println(((While)stm).toString());
            print_expression_content(((While)stm).getCondition());
            print_statement_content(((While)stm).getBody());
        }
        else if(stm_type.equals("Write")){
            System.out.println(((Write)stm).toString());
            print_expression_content(((Write)stm).getArg());
        }
        else{
            System.out.println(stm.toString());
            return;
        }    
    }
    void print_type_content(Type type_v){
        System.out.println(type_v.toString());
        if (type_v.toString() == "int[]"){
            System.out.println(((ArrayType)type_v).getSize());//LAZAEME??
            //print_expression_content(type_v.getSize()); ///????
        }
    }

    void print_program_content_1(Program prog){
        List<ClassDeclaration> classes = prog.getClasses(); 
        for(int i=0; i<classes.size(); i++){
            System.out.println(classes.get(i)); 
            ArrayList<VarDeclaration> vars = classes.get(i).getVarDeclarations();
            for(int j=0; j<vars.size(); j++){
                System.out.println(vars.get(j));
            }
            ArrayList<MethodDeclaration> methods = classes.get(i).getMethodDeclarations();
            for(int j=0; j<methods.size(); j++){
                ArrayList<VarDeclaration> localVars = methods.get(j).getLocalVars();
                for(int l=0; l<localVars.size(); l++){
                    System.out.println(localVars.get(l));
                }
                System.out.println(methods.get(j).getName().getName());
                ArrayList<Statement> statements = methods.get(j).getBody();
                for(int k=0; k<statements.size(); k++){
                    System.out.println(statements.get(k).toString());
                    if(statements.get(k).toString() == "Assign"){
                        System.out.println(((Assign)statements.get(k)));
                    }
                    else if(statements.get(k).toString() == "Conditional"){
                        System.out.println(((Conditional)statements.get(k)));
                    }
                    else if(statements.get(k).toString() == "While"){
                        System.out.println(((While)statements.get(k)));
                    }
                    else if(statements.get(k).toString() == "Write"){
                        System.out.println(((Write)statements.get(k)));
                    }
                }
            }
        }
    }
    

    @Override
    public void visit(Program program) {
        if (no_error==false && second_round==false && symTable==null){
            index = 0;
            no_error = true;
            second_round = false; 
            symTable = new SymbolTable(); 
            check_class_name_conditions_with_symTable(program);
            check_class_existance_condition_with_symTable(program);
        }
        if (no_error==true){
            second_round = true; 
            System.out.println(program);
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

    void check_method_existance_condition_with_symTable(ClassDeclaration classDeclaration){
        ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations(); 
        for(int j=0; j<vars.size(); j++){
            add_variable_to_sym_table(vars.get(j));
        }
        // symTable.top.printSymbolTableItems();

        ArrayList<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        for(int i=0; i<methodDeclarations.size(); i++){
            add_method_to_symbol_table(methodDeclarations.get(i).getName().getName(), methodDeclarations.get(i));
            // symTable.top.printSymbolTableItems();
            methodDeclarations.get(i).accept(this);
        }
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        if(second_round==false){
            symTable.push(new SymbolTable(symTable.top));
            check_method_existance_condition_with_symTable(classDeclaration);
            symTable.pop();
        } 
        else if(second_round==true){
            System.out.println(classDeclaration);
            classDeclaration.getName().accept(this);
            if(! classDeclaration.getParentName().getName().equals("null"))
                classDeclaration.getParentName().accept(this);
            ArrayList<VarDeclaration> vars = classDeclaration.getVarDeclarations();
            for(int j=0; j<vars.size(); j++){
                vars.get(j).accept(this);
            }
            ArrayList<MethodDeclaration> methods = classDeclaration.getMethodDeclarations();
            for(int j=0; j<methods.size(); j++){
                methods.get(j).accept(this);
            }
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

    void check_for_statements(ArrayList<Statement> body){
        for(int i=0; i<body.size(); i++){
            body.get(i).accept(this);
            // if(body.get(i).toString() == "Assign"){
            //     Assign x = (Assign)body.get(i).accept(this);
            //     x.accept(this);
            // }
            // else if(body.get(i).toString() == "Conditional"){
            //     Conditional x = (Conditional)body.get(i);
            //     x.accept(this);
            // }
            // else if(body.get(i).toString() == "While"){
            //     While x = (While)body.get(i);
            //     x.accept(this);
            // }
            // else if(body.get(i).toString() == "Write"){
            //     Write x = (Write)body.get(i);
            //     x.accept(this);
            // }
            // else if(body.get(i).toString() == "Block"){
            //     Block x = (Block)body.get(i); 
            //     x.accept(this);
            // }
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
            System.out.println(methodDeclaration);
            methodDeclaration.getName().accept(this);
            ArrayList<VarDeclaration> args = methodDeclaration.getArgs();
            for (int i = 0; i < args.size(); i++){
                args.get(i).accept(this);
            }
            ArrayList<VarDeclaration> localVars = methodDeclaration.getLocalVars();
            for(int l=0; l<localVars.size(); l++){
                localVars.get(l).accept(this);
            }
            ArrayList<Statement> body = methodDeclaration.getBody();
            for(int j=0; j<body.size(); j++){
                body.get(j).accept(this);
            }
            methodDeclaration.getReturnValue().accept(this);
        }

    }

   // @Override
    //public void visit(MainMethodDeclaration mainMethodDeclaration) {
        //TODO: implement appropriate visit functionality
    //}

    @Override
    public void visit(VarDeclaration varDeclaration) {
        if(second_round==true){
            System.out.println(varDeclaration);
            Identifier id = varDeclaration.getIdentifier();
            id.accept(this);
        }
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        if(second_round==true){
            System.out.println(arrayCall);
            arrayCall.getInstance().accept(this);
            arrayCall.getIndex().accept(this);
        }
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        if(second_round==true){
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
        if(second_round==true){
            System.out.println(length);
            Expression exp = length.getExpression();
            exp.accept(this);
        }
    }

    @Override
    public void visit(MethodCall methodCall) {
        if(second_round==true){
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
        if(second_round==true){
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
        } else {
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
}
