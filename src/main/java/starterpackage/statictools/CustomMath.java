package starterpackage.statictools;

import java.util.Arrays;

/**
 * Classe que faz operações matemáticas
 * @author Mateus Rocha
 */
public class CustomMath {

    public static class TamanhoInvalidoException extends Exception{

        public TamanhoInvalidoException(String message){
            super(message);
        }

        public TamanhoInvalidoException(int col1,int line2){
            super(String.format("Impossivel multiplicar as matrizes, pois j1=%d e i2=%d, deveriam ser iguais.",col1,line2));
        }
    }

    /**
     * Multiplica duas matrizes e retorna o resultado em forma de matriz
     * @param matriz1 matriz à esquerda
     * @param matriz2 matriz à direita
     * @return matriz com resultado
     */
    public static float[][] multiplicarMatriz(float[][] matriz1,float[][] matriz2) throws TamanhoInvalidoException{
        if(matriz1.length == 0 || matriz2.length == 0) throw new TamanhoInvalidoException("Nao da para multiplicar arrays nulos");
        if(matriz1[0].length != matriz2.length) throw new TamanhoInvalidoException(matriz1[0].length,matriz2.length);
        float[][] resultado = new float[matriz1.length][matriz2[0].length];
        int linhafixa = 0;
        int coluna = 0;
        for(int[] ij = {0,0};ij[0]<matriz1.length;){
            float resultadoElemento = 0;
            float[] linha = matriz1[ij[0]];
            for(int c = 0; c <matriz2.length; c++){ // c representa a coluna de m1 e a linha de m2, enquanto ij representa a linha de m1 e a coluna de m2
                resultadoElemento += linha[c] * matriz2[c][ij[1]];
            }
            resultado[ij[0]][ij[1]] = resultadoElemento;
            coluna ++;
            if(coluna < matriz2[0].length){ //Iteração normal, atualiza coluna fixa da m2
                ij[1] = coluna;
            }else{ //Iteração com a coluna final, reseta coluna fixa da m2 e atualiza linha fixa da m1
                linhafixa++;
                coluna = 0;
                ij[1] = coluna;
                ij[0] = linhafixa;
            }
        }
        return resultado;
    }
}