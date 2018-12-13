package ast.Type.ArrayType;

import ast.Type.Type;

public class ArrayType extends Type {
    int size;
    @Override
    public String toString() {
        return "int[]";
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void printSize(){
        System.out.println(this.size);
    }
}
