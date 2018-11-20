package ast.node;

import ast.Visitor;

public abstract class Node {
	int line_number; 

	public void set_line_number(int line_number){
		this.line_number = line_number;
	}

	public int get_line_number(){
		return this.line_number;
	}
}
