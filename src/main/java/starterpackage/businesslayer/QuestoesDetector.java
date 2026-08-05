package starterpackage.businesslayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openpdf.text.pdf.PdfReader;
import starterpackage.businesslayer.filtros.DetectorDeFiltro;
import starterpackage.businesslayer.filtros.Filtro;
import starterpackage.dataacesslayer.dto.Gabarito;
import starterpackage.dataacesslayer.dto.TextUnit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


/**
 * È um algoritmo para detectar questões e suas respostas em pdf de gabaritos reconhecendo padroes nas text matrices
 * @author Mateus Rocha
 */
public class QuestoesDetector {

    DetectorDeFiltro detectorDeFiltro;
    boolean temFiltro = false;
    Filtro filtro;
    int primeiraSuposta;
    int ultimaSuposta;
    int quantidadeQuestoes;
    HashMap<Integer,List<TextUnit>> bancoX;
    HashMap<Integer,List<TextUnit>> bancoY;
    HashMap<String,String> grupos;
    List<List<TextUnit>> streamsList;
    Logger logger = LogManager.getLogger();

    /**
     * Escanea todos os textos de um pdf de gabarito.
     * @param arquivoDir diretorio do arquivo pdf para scanear
     * @param primeiraQuestao primeira questao no arquivo
     * @param ultimaQuestao ultima questao no arquivo
     * @throws FileNotFoundException Se o arquivo não for encontrado
     * @throws IOException Se o {@link PdfReader} não aceitar o arquivo(provavelmente o arquivo é invalido se a exception for lançada)
     */
    public QuestoesDetector(String arquivoDir, int primeiraQuestao, int ultimaQuestao,
                            Map<String, Filtro> filtroMap) throws IOException, FileNotFoundException {
        this.detectorDeFiltro = new DetectorDeFiltro(filtroMap);
        this.streamsList = new GabaritoScanner(arquivoDir,detectorDeFiltro).scanGabarito();
        if(detectorDeFiltro.getFiltro().isPresent()) {
            this.filtro = detectorDeFiltro.getFiltro().get();
            this.temFiltro = true;
        }
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
        for(Map.Entry<String,String> entry: grupos.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            int questao;
            String resposta;
            try{
                questao = Integer.parseInt(key);
                if(!filtro.verificarResposta(value)) continue;
                resposta = value;
            }
            catch (NumberFormatException e){
                try {
                    questao = Integer.parseInt(value);
                    if(!filtro.verificarResposta(key)) continue;
                    resposta = key;
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
     * Retorna o nome do filtro sendo usado pela classe no context do spring, se a classe não tiver filtro retorna uma string vazia
     */
    public String getFiltroNome(){
        if(temFiltro) return detectorDeFiltro.getNomeBean();
        else return "";
    }

    /**
     * Atualiza o filtro usado pela classe
     */
    public void setFiltro(Filtro filtro){
        this.filtro = filtro;
    }

    /**
     * Seta o bancoX e o bancoY,Maps que ligam os TextObjects com tm aproximadamente igual e o Map pares que contem os tj dos TextObjects
     * que coincidem Tm, geralmente duas vezes, pois é provavel que sejam alternativa e resposta, todos os setados acima sao variaveis desta classe
     */
    private void setupBancos(){
        faseContagemSetupBanco();
        logger.debug("Contagem concluida");
        bancoX = filtro.filtrarBanco(bancoX);
        bancoY = filtro.filtrarBanco(bancoY);
        grupos = filtro.getGrupos();
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
}
