package starterpackage.businesslayer;

import org.apache.logging.log4j.LogManager;
import org.openpdf.text.pdf.*;
import org.apache.logging.log4j.Logger;
import starterpackage.businesslayer.filtros.DetectorDeFiltro;
import starterpackage.dataacesslayer.dto.TextUnit;
import starterpackage.statictools.CustomMath;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Classe capaz de ler todos os Text Objects de um pdf(somente os Tj e Tm deles)
 * @author Mateus Rocha
 */
public class GabaritoScanner {
    PdfReader reader;
    PdfDictionary catalog;
    PdfArray contents;
    PRStream stream;
    byte[] streamBytes;
    String streamString,objString;
    char[] streamCharArray,objCharArray;

    Pattern btPattern,etPattern,tmPattern,tjPattern;
    Matcher btMatcher,etMatcher,tmMatcher,tjMatcher;

    List<List<TextUnit>> streamsList = new ArrayList<>();
    DetectorDeFiltro detectorDeFiltro;

    Logger logger = LogManager.getLogger();


    /**
     * Instancia o scanner
     * @param arquivoDir diretório do arquivo pdf para scanear
     * @param detectorDeFiltro para tentar achar filtros no nome do arquivo ou em alguma Tj
     * @throws FileNotFoundException Se o arquivo não for encontrado
     * @throws IOException Se o {@link PdfReader} não aceitar o arquivo(provavelmente o arquivo é invalido se a exception for lançada)
     */
    public GabaritoScanner(String arquivoDir,DetectorDeFiltro detectorDeFiltro) throws FileNotFoundException, IOException{
        reader = new PdfReader(new FileInputStream(arquivoDir));
        catalog = reader.getCatalog();
        contents = catalog.getAsDict(new PdfName("Pages")).getAsArray(new PdfName("Kids")).getAsDict(0).getAsArray(new PdfName("Contents"));
        this.detectorDeFiltro = detectorDeFiltro;
        detectorDeFiltro.findFiltro(new File(arquivoDir).getName());
    }

    /**
     * Le os Text Objects do gabarito
     * @return Retorna uma List com todos os Text Objects de todas as streams(cada stream sendo uma lista de {@link TextUnit} dentro da lista principal),
     * contendo seus Tj e Tm(veja a documentação do {@link TextUnit})
     */
    public List<List<TextUnit>> scanGabarito() {
        lerTodosTextObjects();
        return streamsList;
    }

    /**
     * Le todos os text objects de todas as streams e salva eles na variavel streamsList da classe
     */
    private void lerTodosTextObjects(){
        int index = 0;
        boolean descartarObj = true;
        while(contents.size() != index) {
            stream = (PRStream) PdfReader.getPdfObject(contents.getPdfObject(index));
            try {
                streamBytes = PdfReader.getStreamBytes(stream);
            }catch (IOException e){
                throw new IOError(e);
            }
            streamString = new String(streamBytes, Charset.forName("UTF-8"));
            streamCharArray = streamString.toCharArray();
            btPattern = Pattern.compile("BT");
            btMatcher = btPattern.matcher(streamString);
            etPattern = Pattern.compile("ET");
            etMatcher = etPattern.matcher(streamString);
            List<TextUnit> streamAtual = new ArrayList<>();
            while(true) { //Cada loop desse while lida com um TextObject
                boolean btFound = btMatcher.find();
                boolean etFound = etMatcher.find();
                //descartarObj é uma boolean que diz se o objeto foi totalmente lido e pode ser descartado para começar um loop limpo.
                if(!descartarObj){
                    btMatcher.reset();
                    objString += streamString.substring(0, etMatcher.end());
                    objCharArray = objString.toCharArray();
                    descartarObj = true;
                }else if (btFound && etFound) {
                    objString = streamString.substring(btMatcher.start(), etMatcher.end());
                    objCharArray = objString.toCharArray();
                }else if (!btFound && !etFound) {
                    objString = "";
                    break;
                } else if (!etFound) {
                    objString = streamString.substring(btMatcher.start());
                    objCharArray = objString.toCharArray();
                    while (objCharArray[objCharArray.length - 1] == ' '){
                        objCharArray[objCharArray.length -1] = '\n';
                        objString = new String(objCharArray);
                    }
                    descartarObj = false;
                    break;
                }

                tmPattern = Pattern.compile("Tm");
                tmMatcher = tmPattern.matcher(objString);
                tjPattern = Pattern.compile("Tj|TJ");
                tjMatcher = tjPattern.matcher(objString);

                long tjCount = tjMatcher.results().count();
                tjMatcher.reset();
                if (tjCount == 1) {
                    tjMatcher.find();
                    tmMatcher.find();
                    logger.trace("Inicio do Text Object");
                    float[] tm = getProximoTm();
                    Optional<String> tj = getProximoTj(false);
                    streamAtual.add(new TextUnit(tm, tj));
                    logger.trace("Fim do Text Object");
                }else if(tjCount > 1){
                    logger.trace("Inicio do Text Object");
                    Pattern tdPattern = Pattern.compile("Td|TD");
                    Matcher tdMatcher = tdPattern.matcher(objString);
                    tmMatcher.find(); // Sempre vai ter pelo menos um Tm
                    boolean tjFound = tjMatcher.find();
                    boolean tdFound = tdMatcher.find();
                    float[][] td = {};
                    if(tdFound) td = getProximoTd(tdMatcher);
                    float[] tm = new float[6];
                    boolean beforeTd = true;
                    int tmAtual = 1;
                    while (tjFound) { //Cada loop desse while lida com uma TextUnit
                        Matcher proximoTm = tmPattern.matcher(objString);
                        for(int i = 0;i<tmAtual;i++){
                            proximoTm.find();
                        }
                        if(proximoTm.find()) {
                            if (proximoTm.start() < tdMatcher.start()){
                                tmMatcher =proximoTm;
                                tmAtual++;
                                if(tjMatcher.start() < tdMatcher.start()){
                                    beforeTd = true;
                                }
                            }
                        }

                        if(beforeTd) {
                            if (tdFound && tjMatcher.start() > tdMatcher.start()){
                                beforeTd=false;
                                td = getProximoTd(tdMatcher);
                                continue;
                            }
                            logger.trace("Inicio da UNIT");
                            tm = getProximoTm();
                            Optional<String> tj = getProximoTj(false);
                            streamAtual.add(new TextUnit(tm, tj));
                            logger.trace("Unit persistida");
                            tjFound = tjMatcher.find();
                        }else {
                            logger.trace("Inicio da UNIT");
                            if (tjMatcher.start() > tdMatcher.start()) {
                                td = getProximoTd(tdMatcher);
                                tdFound = tdMatcher.find();
                            }
                            Optional<String> tj = getProximoTj(false);
                            float[][] tmMatrix = {{tm[0],tm[1],0},{tm[2],tm[3],0},{tm[4],tm[5],1}};
                            try {
                                tmMatrix = CustomMath.multiplicarMatriz(td, tmMatrix);
                                tm = new float[] {tmMatrix[0][0],tmMatrix[0][1],tmMatrix[1][0],tmMatrix[1][1],tmMatrix[2][0],tmMatrix[2][1]};
                                streamAtual.add(new TextUnit(tm,tj));
                            }catch (CustomMath.TamanhoInvalidoException e){
                                throw new Error(e);
                            }
                            if (!tjMatcher.find()) break;
                            logger.trace("  Td:" + Arrays.deepToString(td));
                            logger.trace("  Tm Multiplicado:" + Arrays.deepToString(tmMatrix));
                            logger.trace("Unit Persistida");
                        }
                    }
                    logger.trace("Fim do Text Object");
                }
            }
            logger.debug("Stream " + contents.getPdfObject(index) .toString() + " parseada");
            index++;
            streamsList.add(streamAtual);
        }
    }
    /**
     * Retorna a proxima text matrix encontrada pelo matcher em forma de float[]
     * @return Retorna a proxima text matrix encontrada pelo matcher em forma de float[]
     */
    private float[] getProximoTm(){
        float[] tm = new float[6];
        int currentIndex = 5;
        int numeroEndIndex = tmMatcher.start() - 2;
        String numero = null;
        while(true){
            if(objCharArray[numeroEndIndex - 1] == ' ' || objCharArray[numeroEndIndex - 1] == '\n' ){
                if(numero != null){
                    numero = objCharArray[numeroEndIndex] + numero;
                }else {
                    numero = String.valueOf(objCharArray[numeroEndIndex]);
                }
                tm[currentIndex] = Float.valueOf(numero);
                currentIndex--;
                numeroEndIndex--;//no fim do loop diminui mais um, assim pula o espaço e o proximo loop é o proximo numero
                if(currentIndex == -1){
                    break;
                }
                numero = null;
            }else{
                if(numero != null){
                    numero = objCharArray[numeroEndIndex] + numero;
                }else {
                    numero = String.valueOf(objCharArray[numeroEndIndex]);
                }
            }
            numeroEndIndex--;
        }
        logger.trace("  Tm:" + Arrays.toString(tm));
        return tm;
    }

    /**
     * Retorna o proximo Tj encontrado pelo matcher em forma de String[Se for TJ com jota maiusculo(veja a referencia do pdf) ele retorna optional vazia].
     * Alem disso, caso não tenha filtro no detectorDeFiltro, o metodo executa o metodo findFiltro do detector com a Tj encontrada.
     * @param tjPendente true se o tj estiver no inicio da file e nao tiver \n antes(pendente pq ele  so pode pertencer a um elemento de outra stream se ele estiver na primeira linha da stream)
     * @return Retorna o proximo Tj encontrado pelo matcher em forma de String[Se for TJ com jota maiusculo(veja a referencia do pdf) ele retorna optional vazia]
     */
    private Optional<String> getProximoTj(boolean tjPendente){
        if(objCharArray[tjMatcher.end() - 1] == 'J'){
            logger.trace("  Esse Object tem um TJ(array)");
            return Optional.empty();
        }
        String toReturn = "";
        int stringEndIndex = tjMatcher.start() - 2; // a sigla Tj n tem espaço da string apesar do Rups colocar
        boolean condicao = tjPendente?stringEndIndex >= 0:objCharArray[stringEndIndex] != '\n';
         while(condicao){
            toReturn = objCharArray[stringEndIndex] + toReturn;
            stringEndIndex --;
            condicao = tjPendente?stringEndIndex >= 0:objCharArray[stringEndIndex] != '\n';
        }
        toReturn = toReturn.substring(1,toReturn.length()); // tirar o primeiro parentese
        toReturn = toReturn.translateEscapes(); //Pra processar as backslashs(pelo que eu entendi elas estão na mesma encoding da string e pra os bytes ate 255 a PDFDocEncoding é igual ao UTF-16
        if(detectorDeFiltro.getFiltro().isEmpty()) detectorDeFiltro.findFiltro(toReturn);
        logger.trace("  Tj:" + toReturn);
        return Optional.of(toReturn);
    }

    private float[][] getProximoTd(Matcher tdMatcher){
        float[][] td = {{1, 0, 0},{0,1,0},{0,0,1}};//Matriz baseada em Td usada para modificar Tm
        int currentIndex = 1; // 1 é ty e 0 é tx
        int numeroEndIndex = tdMatcher.start() - 2;
        String numero = null;
        while(true) {
            if(objCharArray[numeroEndIndex - 1] == ' ' || objCharArray[numeroEndIndex - 1] == '\n' ){
                if(numero != null){
                    numero = objCharArray[numeroEndIndex] + numero;
                }else {
                    numero = String.valueOf(objCharArray[numeroEndIndex]);
                }
                td[2][currentIndex] = Float.valueOf(numero);
                currentIndex--;
                numeroEndIndex--;//no fim do loop diminui mais um, assim pula o espaço e o proximo loop é o proximo numero
                if(currentIndex == -1){
                    break;
                }
                numero = null;
            }else{
                if(numero != null){
                    numero = objCharArray[numeroEndIndex] + numero;
                }else {
                    numero = String.valueOf(objCharArray[numeroEndIndex]);
                }
            }
            numeroEndIndex--;
        }

        if(objCharArray[tdMatcher.end() - 1] == 'd'){
            return td;
        }else{
            return td;//TODO:TL do TD maiusculo
        }
    }

}
