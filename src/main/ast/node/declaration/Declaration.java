package ast.node.declaration;

import ast.node.Node;
import ast.Visitor;

public abstract class Declaration extends Node {

	@Override
    public void accept(Visitor visitor) {}
}