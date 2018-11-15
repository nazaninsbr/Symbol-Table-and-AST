del *.class
del *.tokens
del our_smoola*.java
java org.antlr.v4.Tool our_smoola.g4
javac our_smoola*.java
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/1.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/2.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/3.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/4.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/5.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/6.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/7.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/8.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/9.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/10.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/11.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/12.txt
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/13.sml
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/14.sml
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/15.sml
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/16.sml
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/17.sml
java org.antlr.v4.runtime.misc.TestRig our_smoola program -gui < ./Tests/18.sml
