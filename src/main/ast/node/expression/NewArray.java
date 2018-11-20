package ast.node.expression;

import ast.Visitor;

public class NewArray extends Expression {
    private Expression expression;
    private int size;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void setIntSize(int s){
        this.size = s;
    }

    public int getIntSize(){
        return this.size;
    }

    @Override
    public String toString() {
        return "NewArray";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
