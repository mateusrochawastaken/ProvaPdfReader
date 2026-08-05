package starterpackage.businesslayer.filtros;

import org.springframework.stereotype.Component;
import starterpackage.dataacesslayer.dto.TextUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Filtra as List que tem tamanho 2 ou 3(questoes 1 a 5 de ingles e espanhol)(veja {@link Filtro})
 * @author Mateus Rocha
 */
@Component("ENEM")
public class FiltroEnem implements Filtro{

    HashMap<String,String> grupos;

    /**
     * Filtra as List que tem tamanho 2 ou 3(questoes 1 a 5 de ingles e espanhol)(veja {@link Filtro})
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
                    grupos = new HashMap<>();
                }
                grupos.put(tj0,tj1);
            }else if(value.size() == 3){
                for(int i1 = 0;i1<entry.getValue().size();i1++){
                    List<TextUnit> listaAtual = entry.getValue();
                    TextUnit textUnit = listaAtual.get(i1);
                    if(textUnit.getTj().isEmpty()) break;
                    for(int i2 = 1;i2<6;i2++){
                        try {
                            if (i2 == Integer.parseInt(textUnit.getTj().get())) {
                                if(grupos == null) grupos = new HashMap<>();
                                if(listaAtual.get(1).getTm()[5] < listaAtual.get(2).getTm()[5]) { // A cordenada x das questoes de ingles é menor
                                    grupos.put(listaAtual.get(0).getTj().get(),listaAtual.get(1).getTj().get() + "," + listaAtual.get(2).getTj().get());
                                }else{
                                    grupos.put(listaAtual.get(0).getTj().get(),listaAtual.get(1).getTj().get() + "," + listaAtual.get(2).getTj().get());
                                }

                            }
                        }catch (NumberFormatException e){continue;}
                    }
                }
            }
        }
        return bancoFuturo;
    }

    public HashMap<String,String> getGrupos(){
        return grupos;
    }

    /**
     * Verifica se a resposta é A,B,C,D,E ou se é composta(ingles e espanhol),
     * as compostas seguem o padrão "RESP_INGLES,RESP_ESPANHOL", por exemplo "A,D".
     * @param texto texto para verificar se é resposta
     * @return boolean indicando se é ou não resposta
     */
    public boolean verificarResposta(String texto){
        Pattern patternABCDE = Pattern.compile("[ABCDE]");
        Matcher matcherABCDE = patternABCDE.matcher(texto);
        Pattern pattern = Pattern.compile("[A-E],[A-E]");
        Matcher matcher = pattern.matcher(texto);

        return matcherABCDE.find() || matcher.find();
    }

    public String getCriterio(){
        return "Enem";
    }
}
