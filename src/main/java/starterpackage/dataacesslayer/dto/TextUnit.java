package starterpackage.dataacesslayer.dto;

import java.util.Optional;

/**
 * Classe que representa uma unidade de texto de Streams PDF, todos os Tj(sem suporte para TJ array) têm a sua propria Unit, com
 * todos os operadores de posição(atualmente só Td e Tm) simplificados para um Tm, da mesma forma
 * que a documentação do PDF diz que acontece internamente.
 * @author Mateus Rocha
 */
public class TextUnit {

    private float[] tm;
    private Optional<String> tj;

    /**
     * Instancia a classe com um Tj(arrays ainda não funcionam) e uma Tm,
     * essa Tm deve estar multiplicada com todos os Td que modificam ela(veja
     * a documentação dos arquivos PDF).
     * @param tm Essa Tm deve estar multiplicada com todos os Td que modificam ela(veja a documentação dos arquivos PDF).
     * @param tj Tj com o texto da unit(arrays ainda não funcionam)
     */

    public TextUnit(float[] tm, Optional<String> tj){
        this.tm = tm;
        this.tj = tj;
    }

    /**
     * Retorna a text matrix do jeito que aparece na definição
     * @return Retorna a text matrix do jeito que aparece na definição
     */
    public float[] getTm() {
        return tm;
    }

    /**
     * Se o Text Object lido tiver um Tj(de string) a optional vai estar presente, se tiver um array nao
     * @return Se o Text Object lido tiver um Tj(de string) a optional vai estar presente, se tiver um array nao
     */
    public Optional<String> getTj() {
        return tj;
    }

    /**
     * Seta a textMatrix
     * @param tm Seta a textMatrix
     */
    public void setTm(float[] tm) {
        this.tm = tm;
    }

    public void setTj(Optional<String> tj) {
        this.tj = tj;
    }

}
