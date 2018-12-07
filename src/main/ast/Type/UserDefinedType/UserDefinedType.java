package ast.Type.UserDefinedType;

import ast.Type.Type;
import ast.node.declaration.ClassDeclaration;
import ast.node.expression.Identifier;

public class UserDefinedType extends Type {
    private ClassDeclaration classDeclaration;
    private Identifier name;
    private boolean is_none_type = false;

    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public void setClassDeclaration(ClassDeclaration classDeclaration) {
        this.classDeclaration = classDeclaration;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return classDeclaration.getName().getName();
    }

    public void set_is_none_type(){
        this.is_none_type = true;
    }

    public boolean get_is_none_type(){
        return this.is_none_type;
    }
}
