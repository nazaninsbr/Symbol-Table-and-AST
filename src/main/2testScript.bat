del *.class
del *.tokens
del *.java
java org.antlr.v4.Tool Smoola.g4
javac *.java
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/0.txt