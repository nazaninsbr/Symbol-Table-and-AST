package ast.node.expression;

import ast.Visitor;
import ast.node.expression.BinaryOperator;
public class BinaryExpression extends Expression {

    private Expression left;
    private Expression right;
    private BinaryOperator binaryOperator;

    public BinaryExpression(Expression left, Expression right, BinaryOperator binaryOperator) {
        this.left = left;
        this.right = right;
        this.binaryOperator = binaryOperator;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public BinaryOperator getBinaryOperator() {
        return binaryOperator;
    }

    public void setBinaryOperator(BinaryOperator binaryOperator) {
        this.binaryOperator = binaryOperator;
    }
/*
    BinaryOperator string_to_binaryoperator(String binary_op){
        BinaryOperator binaryOperator;
        switch(binary_op){
            case"+":
                binaryOperator = BinaryOperator.add;
                break;
            case"-":
                binaryOperator = BinaryOperator.sub;
                break;
            case"*":
                binaryOperator = BinaryOperator.mult;
                break;
            case"/":
                binaryOperator = BinaryOperator.div;
                break;
            case"&&":
                binaryOperator = BinaryOperator.and;
                break;
            case"||":
                binaryOperator = BinaryOperator.or;
                break;
            case"==":
                binaryOperator = BinaryOperator.eq;
                break;
            case"<":
                binaryOperator = BinaryOperator.lt;
                break;
            case">":
                binaryOperator = BinaryOperator.gt;
                break;                
     //       case"<>":
     //           binaryOperator = BinaryOperator.lte;
      //          break;
            case"<>":
                binaryOperator = BinaryOperator.gte;
                break;    
            case"=":
                binaryOperator = BinaryOperator.assign;
                break;
            default:
                binaryOperator = binaryOperator;  
        }
        return binaryOperator;
    }
*/

    @Override
    public String toString() {
        return "BinaryExpression " + binaryOperator.name();
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

