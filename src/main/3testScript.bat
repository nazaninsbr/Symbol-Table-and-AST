del *.class
del *.tokens
del *.java
java org.antlr.v4.Tool Smoola.g4
javac *.java
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/0.txt > ./outputs/out0.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/1.txt > ./outputs/out1.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/2.txt > ./outputs/out2.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/3.txt > ./outputs/out3.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/4.txt > ./outputs/out4.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/5.txt > ./outputs/out5.txt
java org.antlr.v4.runtime.misc.TestRig Smoola program  < ./Tests/6.txt > ./outputs/out6.txt
