export CLASSPATH=".:/usr/local/lib/antlr-4.7.1-complete.jar:$CLASSPATH"
rm *.class
rm *.tokens
rm Smoola*.java
java -jar /usr/local/lib/antlr-4.7.1-complete.jar Smoola.g4
javac Smoola*.java
echo 1
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/1.txt
# echo 2
# java org.antlr.v4.gui.TestRig Smoola program -gui < ./Tests/2.txt 
# echo 3
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/3.txt
# echo 4
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/4.txt
# echo 5
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/5.txt
# echo 6
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/6.txt
# echo 7
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/7.txt
# echo 8
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/8.txt
# echo 9
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/9.txt
# echo 10
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/10.txt
# echo 11
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/11.txt
# echo 12
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/12.txt
# echo 13
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/13.sml
# echo 14
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/14.sml
# echo 15
# java org.antlr.v4.gui.TestRig Smoola program -gui < ./Tests/15.sml
# echo 16
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/16.sml
# echo "my test 1"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/my-test1.sml
# echo 17
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/17.sml
# echo "smltest"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/smltest.sml
# echo 18
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/18.sml
# echo 19
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/19.sml
# echo 20
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/20.sml
# echo "doc-sample2"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample2.sml
# echo "doc-sample3"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample3.sml
# echo "doc-sample4"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample4.sml
# echo "doc-sample5"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample5.sml
# echo "doc-sample6"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample6.sml
# echo "Smoola_2Lexer"
# java org.antlr.v4.gui.TestRig Smoola program < ./Tests/our_smoola_2Lexer.sml