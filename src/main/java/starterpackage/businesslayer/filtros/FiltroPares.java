package starterpackage.businesslayer.filtros;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import starterpackage.dataacesslayer.dto.TextUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Filtra apenas as List que tem tamanho 2, ou seja, os pares(veja {@link Filtro})
 */
@Component("GENERICO")
public class FiltroPares implements Filtro{

    HashMap<String,String> grupos;

    /**
     * Filtra apenas as List que tem tamanho 2, ou seja, os pares(veja {@link Filtro})
     * @param banco banco para filtrar
     * @return banco filtrado
     */
    public HashMap<Integer, List<TextUnit>> filtrarBanco(HashMap<Integer,List<TextUnit>> banco) {
        HashMap<Integer,List<TextUnit>> bancoFuturo = (HashMap<Integer, List<TextUnit>>) banco.clone(); //Esse clone é idêntico ao banco, inclusive nas keys e nos values.
        for(Map.Entry<Integer,List<TextUnit>> entry:banco.entrySet()){
            List<TextUnit> value = entry.getValue();
            if(value.size() < 2){
                bancoFuturo.remove(entry.getKey());
            }else if(value.size() == 2){
                String tj0 = value.get(0).getTj().isPresent()?value.get(0).getTj().get():"N/A,TJ Array nao suportados ainda";
                String tj1 = value.get(1).getTj().isPresent()?value.get(1).getTj().get():"N/A,TJ Array nao suportados ainda";
                if(grupos == null){
                    grupos = new HashMap<>();}
                grupos.put(tj0,tj1);
            }
        }
        return bancoFuturo;
    }

    public HashMap<String,String> getGrupos(){
        return grupos;
    }

    public boolean verificarResposta(String resposta){
        return resposta.length() == 1;
    }

    public String getCriterio(){
        return null;
    }
}
