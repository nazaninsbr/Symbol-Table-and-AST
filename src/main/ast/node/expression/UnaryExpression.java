package ast.node.expression;

import ast.Visitor;
import ast.node.expression.UnaryOperator;
public class UnaryExpression extends Expression {

    private UnaryOperator unaryOperator;
    private Expression value;

    public UnaryExpression(UnaryOperator unaryOperator, Expression value) {
        this.unaryOperator = unaryOperator;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public void setUnaryOperator(UnaryOperator unaryOperator) {
        this.unaryOperator = unaryOperator;
    }

    @Override
    public String toString() {
        return "UnaryExpression " + unaryOperator.name();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
/*
    public UnaryOperator string_to_unaryoperator(String unary_op){
        UnaryOperator unaryOperator;
        switch(unary_op){
            case"!":
                unaryOperator = UnaryOperator.not;
                break;
            case"-":
                unaryOperator = UnaryOperator.minus;
                break;
            default:
                unaryOperator = unaryOperator;  
        }
        return unaryOperator;
    }
    */



