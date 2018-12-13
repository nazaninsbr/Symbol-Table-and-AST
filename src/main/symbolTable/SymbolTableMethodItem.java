package symbolTable;

import ast.Type.Type;

import java.util.ArrayList;

public class SymbolTableMethodItem extends SymbolTableItem {

    ArrayList<Type> argTypes = new ArrayList<>();
    Type ret_type;

    public SymbolTableMethodItem(String name, ArrayList<Type> argTypes) {
        this.name = name;
        this.argTypes = argTypes;
    }

    public ArrayList<Type> get_arg_types(){
    	return this.argTypes;
    }

    public void set_return_type(Type ret_type){
    	this.ret_type = ret_type;
    }

    public Type get_return_type(){
    	return this.ret_type;
    }

    @Override
    public String getKey() {
        return "method_"+this.name;
    }
}
