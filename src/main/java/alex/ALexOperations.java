package alex;

import asint.ClaseLexica;

public class ALexOperations {
  private AnalizadorLexicoTiny alex;
  public ALexOperations(AnalizadorLexicoTiny alex) {
   this.alex = alex;   
  }
  public UnidadLexica unidadId() {
     return new UnidadLexica(alex.fila(),ClaseLexica.IDENTIF,alex.lexema()); 
  }
  public UnidadLexica unidadBoolean() {
     return new UnidadLexica(alex.fila(),ClaseLexica.BOOLEAN,"boolean"); 
  }
  public UnidadLexica unidadTrue() {
     return new UnidadLexica(alex.fila(),ClaseLexica.TRUE,"true"); 
  }
  public UnidadLexica unidadFalse() {
     return new UnidadLexica(alex.fila(),ClaseLexica.FALSE,"false"); 
  }
  public UnidadLexica unidadInt() {
     return new UnidadLexica(alex.fila(),ClaseLexica.INT,"int"); 
  }
  public UnidadLexica unidadValorInt() {
     return new UnidadLexica(alex.fila(),ClaseLexica.VALOR_INT,alex.lexema()); 
  } 
  public UnidadLexica unidadDouble() {
     return new UnidadLexica(alex.fila(),ClaseLexica.DOUBLE,"double"); 
  }
  public UnidadLexica unidadValorDouble() {
     return new UnidadLexica(alex.fila(),ClaseLexica.VALOR_DOUBLE,alex.lexema()); 
  }
  public UnidadLexica unidadChar() {
     return new UnidadLexica(alex.fila(),ClaseLexica.CHAR,"char"); 
  }
  public UnidadLexica unidadValorChar() {
     return new UnidadLexica(alex.fila(),ClaseLexica.VALOR_CHAR,alex.lexema()); 
  }
  public UnidadLexica unidadVoid() {
     return new UnidadLexica(alex.fila(),ClaseLexica.VOID,"void"); 
  }
  public UnidadLexica unidadNew() {
     return new UnidadLexica(alex.fila(),ClaseLexica.NEW,"new"); 
  }
  public UnidadLexica unidadNull() {
     return new UnidadLexica(alex.fila(),ClaseLexica.NULL,"null"); 
  }
  public UnidadLexica unidadClass() {
     return new UnidadLexica(alex.fila(),ClaseLexica.CLASS,"class"); 
  }
  public UnidadLexica unidadReturn() {
     return new UnidadLexica(alex.fila(),ClaseLexica.RETURN,"return"); 
  }
  public UnidadLexica unidadIf() {
     return new UnidadLexica(alex.fila(),ClaseLexica.IF,"if"); 
  }
  public UnidadLexica unidadElse() {
     return new UnidadLexica(alex.fila(),ClaseLexica.ELSE,"else"); 
  }
  public UnidadLexica unidadFor() {
     return new UnidadLexica(alex.fila(),ClaseLexica.FOR,"for"); 
  }
  public UnidadLexica unidadOr() {
     return new UnidadLexica(alex.fila(),ClaseLexica.OR,"||"); 
  }
  public UnidadLexica unidadAnd() {
     return new UnidadLexica(alex.fila(),ClaseLexica.AND,"&&"); 
  }
  public UnidadLexica unidadIgualComp() {
     return new UnidadLexica(alex.fila(),ClaseLexica.IGUAL_COMP,"=="); 
  }
  public UnidadLexica unidadDistComp() {
     return new UnidadLexica(alex.fila(),ClaseLexica.DIST_COMP,"!="); 
  }
  public UnidadLexica unidadMenor() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MENOR,"<"); 
  }
  public UnidadLexica unidadMenorIgual() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MENOR_IGUAL,"<="); 
  }
  public UnidadLexica unidadMayor() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MAYOR,">"); 
  }
  public UnidadLexica unidadMayorIgual() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MAYOR_IGUAL,">="); 
  }
  public UnidadLexica unidadMas() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MAS,"+"); 
  } 
  public UnidadLexica unidadMenos() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MENOS,"-"); 
  } 
  public UnidadLexica unidadMul() {
     return new UnidadLexica(alex.fila(),ClaseLexica.POR,"*"); 
  } 
  public UnidadLexica unidadDiv() {
     return new UnidadLexica(alex.fila(),ClaseLexica.DIV,"/"); 
  }
  public UnidadLexica unidadMod() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MOD,"%"); 
  }
  public UnidadLexica unidadNegado() {
     return new UnidadLexica(alex.fila(),ClaseLexica.NEGADO,"!"); 
  }
  public UnidadLexica unidadMasMas() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MAS_MAS,"++"); 
  }
  public UnidadLexica unidadMenosMenos() {
     return new UnidadLexica(alex.fila(),ClaseLexica.MENOS_MENOS,"--"); 
  }
  public UnidadLexica unidadIgual() {
     return new UnidadLexica(alex.fila(),ClaseLexica.IGUAL,"="); 
  }
  public UnidadLexica unidadParAp() {
     return new UnidadLexica(alex.fila(),ClaseLexica.PAR_AP,"("); 
  } 
  public UnidadLexica unidadParCi() {
     return new UnidadLexica(alex.fila(),ClaseLexica.PAR_CI,")"); 
  }  
  public UnidadLexica unidadCorchAp() {
     return new UnidadLexica(alex.fila(),ClaseLexica.CORCH_AP,"["); 
  }
  public UnidadLexica unidadCorchCi() {
     return new UnidadLexica(alex.fila(),ClaseLexica.CORCH_CI,"]"); 
  }
  public UnidadLexica unidadLlaveAp() {
     return new UnidadLexica(alex.fila(),ClaseLexica.LLAVE_AP,"{"); 
  }
  public UnidadLexica unidadLlaveCi() {
     return new UnidadLexica(alex.fila(),ClaseLexica.LLAVE_CI,"}"); 
  }
  public UnidadLexica unidadComa() {
     return new UnidadLexica(alex.fila(),ClaseLexica.COMA,","); 
  }
  public UnidadLexica unidadPuntoYComa() {
     return new UnidadLexica(alex.fila(),ClaseLexica.PYC,";"); 
  }
  public UnidadLexica unidadPunto() {
     return new UnidadLexica(alex.fila(),ClaseLexica.PUNTO,"."); 
  }
  public UnidadLexica unidadDosPuntos() {
     return new UnidadLexica(alex.fila(),ClaseLexica.DOS_PUNTOS,":"); 
  }
  public UnidadLexica unidadInterrog() {
     return new UnidadLexica(alex.fila(),ClaseLexica.INTERROG,"?"); 
  }
  public UnidadLexica unidadThis() {
     return new UnidadLexica(alex.fila(),ClaseLexica.THIS,"this"); 
  }
  public UnidadLexica unidadEof() {
     return new UnidadLexica(alex.fila(),ClaseLexica.EOF,"<EOF>"); 
  }
}
