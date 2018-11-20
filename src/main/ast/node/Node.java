package ast.node;

import ast.Visitor;

public abstract class Node {
	int line_number; 

	void set_line_number(int line_number){
		this.line_number = line_number;
	}

	int get_line_number(){
		return this.line_number;
	}
}
