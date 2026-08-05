package starterpackage.businesslayer.filtros;

import starterpackage.businesslayer.QuestoesDetector;
import starterpackage.dataacesslayer.dto.TextUnit;

import java.util.HashMap;
import java.util.List;

/**
 * Representa uma maneira de filtrar as TJ das {@link TextUnit},
 * cada implementação deve filtrar de um jeito especifico para
 * determinada aplicação, o objetivo dos filtros é serem usados pela
 * a classe {@link QuestoesDetector}
 */
public interface Filtro{
    /**
     * Filtra as Lists das entries do banco passado no argumento, e coloca as desejadas num {@link HashMap}
     * chamado 'grupos', além disso, ele atualiza o banco deixando apenas as entries nas quais as Lists
     * foram adicionadas ao 'grupos', depois retorna o banco atualizado.
     * @param banco banco para filtrar
     * @return banco filtrado
     */
    HashMap<Integer,List<TextUnit>> filtrarBanco(HashMap<Integer, List<TextUnit>> banco);

    /**
     * Retorna o Map de grupos, setado durante o metodo {@link #filtrarBanco(HashMap)}
     */
    HashMap<String,String> getGrupos();

    /**
     * Verifica se um texto segue o padrão que o filtro considera como resposta,
     * por exemplo, o filtro genérico verifica se a resposta tem somente 1 char,
     * comum em alternativas da grande maioria dos gabaritos
     * @param texto texto para verificar se é resposta
     * @return boolean indicando se é ou não resposta
     */
    boolean verificarResposta(String texto);

    /**
     * Retorna uma string com o criterio para achar esse filtro em um arquivo, por exemplo, na prova do enem,
     * o criterio é "Enem", portanto o programa procura no arquivo pdf menções a essa String para sugerir filtros
     * ao usuario
     */
    String getCriterio();
}