export CLASSPATH=".:/usr/local/lib/antlr-4.7.1-complete.jar:$CLASSPATH"
rm *.class
rm *.tokens
rm *.java
java -jar /usr/local/lib/antlr-4.7.1-complete.jar Smoola.g4
javac *.java
echo 0
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/0.txt > ./Results/0.txt
echo 1
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/1.txt > ./Results/1.txt
echo 2
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/2.txt > ./Results/2.txt
echo 3
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/3.txt > ./Results/3.txt
echo 4
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/4.txt > ./Results/4.txt
echo 5
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/5.txt > ./Results/5.txt
echo 6
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/6.txt > ./Results/6.txt
echo 7
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/7.txt > ./Results/7.txt
echo 8
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/8.txt > ./Results/8.txt
echo 9
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/9.txt > ./Results/9.txt
echo 10
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/10.txt > ./Results/10.txt
echo 11
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/11.txt > ./Results/11.txt
echo 12
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/12.txt > ./Results/12.txt
echo 13
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/13.sml > ./Results/13.sml
echo 14
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/14.sml > ./Results/14.sml
echo 15
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/15.sml > ./Results/15.sml
echo 16
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/16.sml > ./Results/16.sml
echo "my test 1"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/my-test1.sml > ./Results/my-test1.sml
echo 17
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/17.sml > ./Results/17.sml
echo "smltest"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/smltest.sml > ./Results/smltest.sml
echo 18
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/18.sml > ./Results/18.sml
echo 19
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/19.sml > ./Results/19.sml
echo 20
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/20.sml > ./Results/20.sml
echo "doc-sample2"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample2.sml > ./Results/doc-sample2.sml
echo "doc-sample3"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample3.sml > ./Results/doc-sample3.sml
echo "doc-sample4"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample4.sml > ./Results/doc-sample4.sml
echo "doc-sample5"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample5.sml > ./Results/doc-sample5.sml
echo "doc-sample6"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/doc-sample6.sml > ./Results/doc-sample6.sml
echo "Smoola_2Lexer"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/our_smoola_2Lexer.sml > ./Results/our_smoola_2Lexer.sml
echo 21
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/21.txt > ./Results/21.txt
echo 23
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/23.txt > ./Results/23.txt
echo 24
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/24.txt > ./Results/24.txt
echo 25
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/25.txt > ./Results/25.txt
echo 26
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/26.txt > ./Results/26.txt
echo 27
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/27.txt > ./Results/27.txt
echo 28
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/28.txt > ./Results/28.txt
echo 29
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/29.txt > ./Results/29.txt
echo "naz-all-ph2.sml"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/naz-all-ph2.sml > ./Results/naz-all-ph2.sml
echo "naz-all-ph3.sml"
java org.antlr.v4.gui.TestRig Smoola program < ./Tests/naz-all-ph3.sml > ./Results/naz-all-ph3.sml


