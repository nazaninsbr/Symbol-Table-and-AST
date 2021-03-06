package ast.node.declaration;

import ast.Visitor;
import ast.node.expression.Expression;
import ast.node.expression.Identifier;
import ast.node.statement.Statement;
import ast.Type.*;

import java.util.ArrayList;

public class MethodDeclaration extends Declaration {
    private Expression returnValue;
    private Identifier name;
    private ArrayList<VarDeclaration> args = new ArrayList<>();
    private ArrayList<VarDeclaration> localVars = new ArrayList<>();
    private ArrayList<Statement> body = new ArrayList<>();
    private Type return_type; 

    public MethodDeclaration(Identifier name) {
        this.name = name;
    }

    public Expression getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public ArrayList<VarDeclaration> getArgs() {
        return args;
    }

    public void addArg(VarDeclaration arg) {
        this.args.add(arg);
    }

    public ArrayList<Statement> getBody() {
        return body;
    }

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }

    public ArrayList<VarDeclaration> getLocalVars() {
        return localVars;
    }

    public void addLocalVar(VarDeclaration localVar) {
        this.localVars.add(localVar);
    }

    public void setReturnType(Type ret_type){
        this.return_type = ret_type;
    }

    public Type getReturnType(){
        return this.return_type;
    }

    @Override
    public String toString() {
        return "MethodDeclaration";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
