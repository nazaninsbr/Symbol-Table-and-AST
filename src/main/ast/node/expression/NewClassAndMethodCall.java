package ast.node.expression;

import ast.Type.Type;
import ast.Visitor;

import java.util.ArrayList;

public class NewClassAndMethodCall extends Expression {
    private Expression newClass;
    private Expression methodCall;
    
    public NewClassAndMethodCall(Expression class_new, Expression method_new){
        this.newClass = class_new;
        this.methodCall = method_new;
    }

    public Expression getnewClass() {
        return newClass;
    }
    public Expression getmethodCall() {
        return methodCall;
    }

    @Override
    public String toString() {
        return "NewClassAndMethodCall";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
