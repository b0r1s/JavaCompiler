package asint;

import alex.AnalizadorLexicoTiny;
import asem.AnalizadorAmbito;
import asem.AnalizadorTipos;
import ast.General.Clase;
import ast.General.Met;
import ast.Nodo;
import gencod.GeneradorCodigo;
import java_cup.runtime.Symbol;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws Exception {
        Reader input = new InputStreamReader(new FileInputStream("./examples/input6Gen.txt"));
        AnalizadorLexicoTiny alex = new AnalizadorLexicoTiny(input);
        Symbol ast = null;
        AnalizadorSintacticoTiny asint = new AnalizadorSintacticoTiny(alex);

        try {
            ast = asint.parse();

            int numErrLex = alex.getErrores().getNumErrores();
            if(numErrLex>0) {
                System.out.print("El programa tiene "+numErrLex+" errores léxicos. ");
                System.out.println("La compilación parará");
                System.exit(1);
            }
            System.out.println("ANÁLISIS LÉXICO: Correcto\n");

            int numErrSintact = asint.errores.getNumErrores();
            if(numErrSintact>0) {
                System.out.print("El programa tiene "+numErrSintact+" errores sintáticos. ");
                System.out.println("La compilación parará");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Error sintáctico inesperado");
            System.out.println("Puede que se haya alcanzado el final de archivo por falta de "+
                    "'}' en método o ';' en atributo");
            System.exit(1);
        }
        System.out.println("ANÁLISIS SINTÁCTICO: Correcto\n");
        System.out.println(ast.value);

        AnalizadorAmbito aamb = new AnalizadorAmbito((Nodo) ast.value);
        int numErrAmb = aamb.getErrores().getNumErrores();
        if(numErrAmb>0) {
            aamb.getErrores().printErrSem();
            System.out.print("El programa tiene "+numErrAmb+" errores de ámbito. ");
            System.out.println("La compilación parará");
            System.exit(1);
        }
        System.out.println("ANÁLISIS SEMÁNTICO: Ámbitos correctos\n");

        AnalizadorTipos atip = new AnalizadorTipos((Nodo) ast.value);
        int numErrTip = atip.getErrores().getNumErrores();
        if(numErrTip>0) {
            atip.getErrores().printErrSem();
            System.out.print("El programa tiene "+numErrTip+" errores de tipo. ");
            System.out.println("La compilación parará");
            System.exit(1);
        }
        System.out.println("ANÁLISIS SEMÁNTICO: Tipos correctos\n");

        if(!aamb.tieneMainCorrecto()) {
            System.out.println("El programa no tiene un (y solo un) método void main()");
            System.out.println("Este método sirve de entrada para la ejecución del programa");
            System.out.println("La compilación parará");
            System.exit(1);
        }
        Clase claseMain = aamb.getMain();
        Met metMain = (Met) claseMain.getCampos().get("main");
        GeneradorCodigo genCod = new GeneradorCodigo(claseMain,metMain);
        String codigoP = genCod.generaCodigoP(false);
        try {
            //System.out.println(codigoP);
            String file = "maquinaP/codigo.txt";
            Files.write((new File(file)).toPath(), codigoP.getBytes(StandardCharsets.UTF_8));
            System.out.println("Instrucciones escritas en "+file);
            System.out.println("COMPILACIÓN TERMINADA");
        } catch (IOException e) {
            System.out.println("Error escribiendo en el archivo");
        }

    }
}   
   
