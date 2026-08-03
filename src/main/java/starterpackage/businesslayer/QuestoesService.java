package starterpackage.businesslayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openpdf.text.pdf.PdfReader;
import starterpackage.dataacesslayer.dto.Gabarito;
import starterpackage.dataacesslayer.dto.TextUnit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Será um algoritmo para detectar questões e suas respostas em pdf de gabaritos reconhecendo padroes nas text matrices
 * @author Mateus Rocha
 */
//TODO:Reconhecer coluna de questoes, pois geralmente ficam uma questao em baixo da outra e isso ajudaria o algoritmo
public class QuestoesService {
    float cordenadaQuestaoX;
    float cordenadaAlX;
    int primeiraSuposta;
    int ultimaSuposta;
    int quantidadeQuestoes;
    HashMap<Integer,List<TextUnit>> bancoX;
    HashMap<Integer,List<TextUnit>> bancoY;
    HashMap<String,String> pares;
    List<List<TextUnit>> streamsList;
    enum XY{X,Y}
    Logger logger = LogManager.getLogger();

    /**
     * Escanea todos os textos de um pdf de gabarito.
     * @param arquivoDir diretorio do arquivo pdf para scanear
     * @param primeiraQuestao primeira questao no arquivo
     * @param ultimaQuestao ultima questao no arquivo
     * @throws FileNotFoundException Se o arquivo não for encontrado
     * @throws IOException Se o {@link PdfReader} não aceitar o arquivo(provavelmente o arquivo é invalido se a exception for lançada)
     */
    public QuestoesService(String arquivoDir,int primeiraQuestao,int ultimaQuestao) throws IOException, FileNotFoundException {
        this.streamsList = new GabaritoScanner(arquivoDir).scanGabarito();
        this.quantidadeQuestoes = ultimaQuestao-primeiraQuestao + 1;
        this.primeiraSuposta = primeiraQuestao;
        this.ultimaSuposta = ultimaQuestao;
    }

    /**
     * Gera, por meio de um algoritmo que tenta reconhecer padrões
     * de questões e respostas num pdf, um {@link HashMap} que relaciona essas duas.
     * @return {@link Gabarito}(HashMap) com key questao e value resposta
     */
    public Gabarito gerarGabarito(){
        setupBancos();
        int primeiraQuestao = Integer.MAX_VALUE;
        int ultimaQuestao = 0;
        Gabarito gabarito = new Gabarito(primeiraSuposta,ultimaSuposta);
        for(Map.Entry<String,String> entry:pares.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            int questao;
            char resposta;
            try{
                questao = Integer.parseInt(key);
                if(value.length() != 1) continue; // TODO:Fazer alternativas possiveis, por exemplo so poder de A a D
                resposta = value.toCharArray()[0];
            }
            catch (NumberFormatException e){
                try {
                    questao = Integer.parseInt(value);
                    if(key.length() != 1) continue;
                    resposta = key.toCharArray()[0];
                }catch (NumberFormatException e2){
                   continue;
                }
            }
            if(gabarito.size() < gabarito.getQuantidadeQuestoes()){
                gabarito.put(questao,resposta);
                if(questao<primeiraQuestao){
                    primeiraQuestao = questao;
                }
                if(questao>ultimaQuestao){
                    ultimaQuestao = questao;
                }
            }
        }
        gabarito.setPrimeiraQuestao(primeiraQuestao);
        gabarito.setUltimaQuestao(ultimaQuestao);
        return gabarito;
    }

    /**
     * Seta o bancoX e o bancoY,Maps que ligam os TextObjects com tm aproximadamente igual e o Map pares que contem os tj dos TextObjects
     * que so coincidem tm 2 vezes(pois é provavel que sejam alternativa e resposta), todos os setados acima sao variaveis desta classe
     */
    private void setupBancos(){
        faseContagemSetupBanco();
        logger.debug("Contagem concluida");
        faseFiltragemSetupBanco(XY.X);
        faseFiltragemSetupBanco(XY.Y);
    }

    /**
     * Cria os dois bancos({@link HashMap}), nos quais o Integer representa uma coordenada X ou Y e a {@link List}
     * representa todos os {@link TextUnit} que tem aproximadamente essa coordenada
     */
    private void faseContagemSetupBanco(){
        bancoX = new HashMap<>();
        bancoY = new HashMap<>();

        int index = 0;
        while(streamsList.size() != index){
            List<TextUnit> currentStream = streamsList.get(index);
            for(TextUnit t:currentStream){
                int tCoordenadaX =(int) t.getTm()[4]; //aproximação pq nem sempre vai ser exato, mas parecido
                int tCoordenadaY = (int)t.getTm()[5]; //aproximação pq nem sempre vai ser exato, mas parecido
                List<TextUnit> listaX = bancoX.get(tCoordenadaX) == null?new ArrayList<TextUnit>():bancoX.get(tCoordenadaX);
                listaX.add(t);
                List<TextUnit> listaY = bancoY.get(tCoordenadaY) == null?new ArrayList<TextUnit>():bancoY.get(tCoordenadaY);
                listaY.add(t);
                bancoX.put(tCoordenadaX,listaX);
                bancoY.put(tCoordenadaY,listaY);
            }
            index++;
        }
    }

    /**
     * Filtra, dentre as Lists das entries do bancoX ou do bancoY, quais tem size 2, e os coloca num {@link HashMap}
     * chamado pares, alem disso, ele atualiza os bancos deixando apenas as entries nas quais as Lists tem size 2
     * @param xy Enum que identifica para qual banco fazer a filtragem
     */
    private void faseFiltragemSetupBanco(XY xy) {
        HashMap<Integer,List<TextUnit>> banco = xy==XY.X?bancoX:bancoY;
        HashMap<Integer,List<TextUnit>> bancoFuturo = (HashMap<Integer, List<TextUnit>>) banco.clone(); //Esse clone é idêntico ao banco, inclusive nas keys e nos values.
        for(Map.Entry<Integer,List<TextUnit>> entry:banco.entrySet()){
            List<TextUnit> value = entry.getValue();
            if(value.size() < 2){
                bancoFuturo.remove(entry.getKey());
            }else if(value.size() == 2){
                String tj0 = value.get(0).getTj().isPresent()?value.get(0).getTj().get():"N/A,TJ Array nao suportados ainda";
                String tj1 = value.get(1).getTj().isPresent()?value.get(1).getTj().get():"N/A,TJ Array nao suportados ainda";
                if(pares == null){pares = new HashMap<>();}
                pares.put(tj0,tj1);
            }
        }
        if(xy == XY.X){bancoX = bancoFuturo;}
        else{bancoY = bancoFuturo;}
    }
}
