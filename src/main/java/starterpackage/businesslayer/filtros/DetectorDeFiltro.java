package starterpackage.businesslayer.filtros;

import java.util.Map;
import java.util.Optional;

/**
 * Classe que detecta se um texto é uma keyword definida para detecção por algum filtro
 */
public class DetectorDeFiltro {

    Optional<Filtro> filtro = Optional.empty();
    Map<String,Filtro> filtroMap;
    String nomeBean = "";
    boolean detectado = false;

    public DetectorDeFiltro(Map<String,Filtro> filtroMap){
        this.filtroMap = filtroMap;
    }

    /**
     * Chama todos os filtros do map e pede o criterio de cada(string que diz que texto usar para detectar o filtro),
     * depois checa se o texto contem cada um deles
     *
     * @param texto texto para checar se indica filtro
     */
    public void findFiltro(String texto){
        texto = texto.toUpperCase();
        for(Map.Entry<String,Filtro> entry:filtroMap.entrySet()) {
            String criterio = entry.getValue().getCriterio();
            if(criterio == null) continue;
            criterio = criterio.toUpperCase();
            if(texto.contains(criterio)){
                detectado = true;
                filtro = Optional.of(entry.getValue());
                nomeBean = entry.getKey();
            }
        }
    }

    /**
     * Se a classe tiver encontrado algum filtro retorna ele, se não, retorna {@link Optional} vazia
     */
    public Optional<Filtro> getFiltro(){
        return filtro;
    }

    /**
     * Retorna o nome do filtro encontrado pela classe na ApplicationContext(verifique se a classe encontrou algo antes de executar esse metodo ou vai obter uma string vazia)
     */
    public String getNomeBean(){
        return nomeBean;
    }

}
