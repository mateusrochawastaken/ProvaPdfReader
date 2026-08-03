package starterpackage.dataacesslayer.dto;

import java.util.HashMap;

/**
 * Entity que representa um gabarito, extende um {@link HashMap} que guarda o numero da questão e sua alternativa
 */
public class Gabarito extends HashMap<Integer,Character>{

    int quantidadeQuestoes;
    int primeiraSuposta;
    int primeiraQuestao;
    int ultimaQuestao;
    int ultimaSuposta;

    /**
     * Cria a entity a partir da primeira e da ultima questão que o usuario espera que sejam encontradas.
     * @param primeiraSuposta Primeira questao que o usuario espera encontrar.
     * @param ultimaSuposta Ultima questao que o usuario espera encontrar.
     */
    public Gabarito(int primeiraSuposta,int ultimaSuposta){
        this.quantidadeQuestoes = ultimaSuposta - primeiraSuposta + 1;
        this.primeiraSuposta = primeiraSuposta;
        this.ultimaSuposta = ultimaSuposta;
    }

    /**
     * Retorna a quantidade de questões estabelecida na inicialização
     * @return Quantidade de questões estabelecida na inicialização
     */
    public int getQuantidadeQuestoes(){
        return quantidadeQuestoes;
    }

    /**
     *Retorna o número da primeira questão que foi encontada pelo algoritmo(a questão com menor numero)
     */
    public int getPrimeiraQuestao() {return primeiraQuestao;}

    public void setPrimeiraQuestao(int primeiraQuestao) {this.primeiraQuestao = primeiraQuestao;}

    /**
     *Retorna o número da primeira questão que o usuário espera que exista(não garante que ela foi encontrada)
     */
    public int getPrimeiraSuposta() {return primeiraSuposta;}

    /**
     *Retorna o número da última questão que foi encontada pelo algoritmo(a questão com maior numero)
     */
    public int getUltimaQuestao() {return ultimaQuestao;}

    /**
     *Retorna o número da última questão que o usuário espera que exista(não garante que ela foi encontrada)
     */
    public int getUltimaSuposta() {return ultimaSuposta;}

    public void setUltimaQuestao(int ultimaQuestao) {this.ultimaQuestao = ultimaQuestao;}
}
