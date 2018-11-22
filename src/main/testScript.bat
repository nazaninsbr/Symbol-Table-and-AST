del *.class
del *.tokens
del *.java
java org.antlr.v4.Tool Smoola.g4
javac *.java
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/1.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/2.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/3.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/4.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/5.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/6.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/7.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/8.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/9.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/10.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/11.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/12.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/13.sml
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/14.sml
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/15.sml
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/16.sml
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/17.sml
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/18.sml
