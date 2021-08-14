# Java Compiler

This compiler will allow you to compile code written with a subset of the Java language instructions.

It compiles into assembler, which can be read by the P-code machine of the Pascal-P system.  
In `/maquinaP` we have a P-code machine emulator written in Haskell by professor Ricardo Peña.  
As he says, "It provides a complete interpreter of the original N. Wirth's P-machine".

By default, we just need to add jlex.jar into the dependencies of this project.  
In IntellIJ, we just need to go to File > Project Structure > Modules > Dependencies  
and add the `./src/main/java/jlex.java`

We just need to run the `Main.java` and the program will compile the file specified there.  
There is a bunch of examples in `/examples`  
The resulting assembler code is saved in `/maquinaP`  
In `salida.txt` we'll have the parsed tree.


If we need to compile any changes on files:

To compile the lexical parser `./src/main/java/alex/AnalizadorLexicoTiny` into `AnalizadorLexicoTiny.java` using JLex:
```
java -cp jlex.jar JLex.Main AnalizadorLexicoTiny
```

To compile the syntactic parser `./src/main/java/asint/Tiny.cup` into `AnalizadorSintacticoTiny.java` and `ClaseLexica.java` using CUP:
```
java java_cup.Main -parser AnalizadorSintacticoTiny -symbols ClaseLexica -nopositions Tiny.cup
```

See `/examples` and this [pdf](https://github.com/b0r1s/JavaCompiler/blob/master/Grammar.pdf) to learn about the accepted grammar.
