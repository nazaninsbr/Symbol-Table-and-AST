package ast;

import ast.node.Node;
import ast.node.Program;
import ast.node.declaration.ClassDeclaration;
import ast.node.declaration.MainMethodDeclaration;
import ast.node.declaration.MethodDeclaration;
import ast.node.declaration.VarDeclaration;
import ast.node.expression.*;
import ast.node.expression.Value.BooleanValue;
import ast.node.expression.Value.IntValue;
import ast.node.expression.Value.StringValue;
import ast.node.statement.*;

import java.util.ArrayList;
import java.util.List;

import ast.Type.UserDefinedType.UserDefinedType; 

import symbolTable.SymbolTable;
import symbolTable.SymbolTableVariableItemBase;
import symbolTable.SymbolTableMethodItem;
import symbolTable.ItemAlreadyExistsException;

public class VisitorImpl implements Visitor {

    boolean no_error;
    boolean second_round; 
    SymbolTable symTable;  
    int index; 

    @Override
    public void visit(Node node) {
        //TODO: implement appropriate visit functionality
    }

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
            System.out.println("Line:<LineNumber>:Redefinition of class "+class_name);
            String new_class_name = "Temporary_ClassName_"+Integer.toString(index)+"_"+class_name;
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
        for(int i = 0; i < prog_classes.size(); ++i) {
            add_class_to_symbol_table(prog_classes.get(i).getName().getName(), prog_classes.get(i));
        }
    }

    void check_class_existance_condition_with_symTable(Program program){
        if (symTable.isSymbolTableEmpty()){
            System.out.println("Line:<LineNumber>:No class exists in the program");
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

    void check_conditions_for_inside_classes_without_symTable(Program program){
        Visitor class_visitor = new VisitorImpl();

        ClassDeclaration main_class = program.getMainClass(); 
        List<ClassDeclaration> prog_classes = program.getClasses();

        main_class.accept(class_visitor);
        for(int i = 0; i < prog_classes.size(); ++i){
            prog_classes.get(i).accept(class_visitor);
        }
    }

    void check_conditions_for_inside_classes_with_symTable(Program program){
        
    }

    @Override
    public void visit(Program program) {
        if (no_error==false && second_round==false && symTable==null){
            index = 0;
            no_error = true;
            second_round = false; 
            symTable = new SymbolTable(); 
        }
        if (second_round==false){
            // check_class_existance_condition_without_symTable(program);
            // check_class_name_conditions_without_symTable(program);
            // check_conditions_for_inside_classes_without_symTable(program);
            check_class_name_conditions_with_symTable(program);
            check_class_existance_condition_with_symTable(program);
            check_conditions_for_inside_classes_with_symTable(program);
        }
        else if (no_error==true){

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

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        check_method_existance_condition_without_symTable(classDeclaration);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(MainMethodDeclaration mainMethodDeclaration) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(Identifier identifier) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(Length length) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(MethodCall methodCall) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(NewArray newArray) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(NewClass newClass) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(This instance) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(BooleanValue value) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(IntValue value) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(StringValue value) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(Assign assign) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(Block block) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(Conditional conditional) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(While loop) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(Write write) {
        //TODO: implement appropriate visit functionality
    }
}
