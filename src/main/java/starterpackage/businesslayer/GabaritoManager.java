package starterpackage.businesslayer;

import starterpackage.dataacesslayer.dto.Gabarito;

import java.util.*;

/**
 * Classe capaz de extrair estatisticas de data transfer objects do tipo {@link Gabarito}
 */
public class GabaritoManager {

    Gabarito gabarito;

    public GabaritoManager(Gabarito gabarito){
        this.gabarito=gabarito;
    }

    /**
     * Retorna a sequência de questões encontrada pelo algoritmo, ou seja,
     * questões que eram desejadas e foram encontradas.
     */
    public int getSequencia() {
        int sequenciaHit = 0;
        List<Integer> questoes = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : gabarito.entrySet()) {
            if (entry.getKey() >= gabarito.getPrimeiraQuestao() && entry.getKey() <= gabarito.getUltimaQuestao()) {
                sequenciaHit++;
                questoes.add(entry.getKey());
            }
        }
        return sequenciaHit;
    }

    /**
     * Retorna uma {@link List} de Integer com todas as questões que
     * eram desejadas mas NÃO foram encontradas pelo algoritmo
     */
    public List<Integer> getNaoEncontrados() {
        Set<Integer> questoes = gabarito.keySet();
        List<Integer> naoEncontrados = new ArrayList<>();
        int current = gabarito.getPrimeiraSuposta();
        while (current != gabarito.getUltimaSuposta() + 1) {
            if (!questoes.contains(current)) {
                naoEncontrados.add(current);
            }
            current++;
        }
        return naoEncontrados;
    }

    public Gabarito getGabarito(){return gabarito;}
}
