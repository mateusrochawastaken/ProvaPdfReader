package starterpackage.businesslayer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import starterpackage.businesslayer.filtros.DetectorDeFiltro;
import starterpackage.businesslayer.filtros.Filtro;
import starterpackage.businesslayer.filtros.FiltroEnem;
import starterpackage.dataacesslayer.dto.TextUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GabaritoScannerTest {

    GabaritoScanner gabaritoScanner;

    @Test
    public void scanGabaritoTest(){
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            Map<String, Filtro> beanMapFake = new HashMap<>();
            beanMapFake.put("ENEM",new FiltroEnem());
            gabaritoScanner = new GabaritoScanner(classloader.getResource("testFiles/Enem2023PrimeiroDiaAmareloGabarito.pdf").getPath(),
                    new DetectorDeFiltro(beanMapFake));
        }catch (Exception e){
            e.printStackTrace();
            throw new Error(e);
        }
        List<List<TextUnit>> resultado = gabaritoScanner.scanGabarito();
        Assertions.assertEquals(8,resultado.size(),"A quantidade de streams encontrada está errada.");
        Assertions.assertEquals(64,resultado.get(4).size(),"A quantidade de TextObjects nas streams está errada(generalização a partir da stream4)");
        Assertions.assertEquals("52",resultado.get(4).get(0).getTj().get(),"O conteúdo dos TextObjects esta errado(generalização pelo item 0 da stream 4)");
        Assertions.assertArrayEquals(new float[] {8,0,0,8,410.484f,540.5376f},resultado.get(4).get(0).getTm(),"O conteúdo dos TextObjects esta errado(generalização pelo item 0 da stream 4)");
    }

    @Test
    public void multiplosTjTest(){
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            Map<String, Filtro> beanMapFake = new HashMap<>();
            beanMapFake.put("ENEM",new FiltroEnem());
            gabaritoScanner = new GabaritoScanner(classloader.getResource("testFiles/Enem2024PrimeiroDiaAmareloGabarito.pdf").getPath(),
                    new DetectorDeFiltro(beanMapFake));
        }catch (Exception e){
            e.printStackTrace();
            throw new Error(e);
        }
        List<List<TextUnit>> resultado = gabaritoScanner.scanGabarito();
        Assertions.assertEquals(101,resultado.get(4).size(),"A quantidade de TextObjects nas streams está errada(generalização a partir da stream4)");
        Assertions.assertEquals("QUESTÃO",resultado.get(4).get(0).getTj().get(),"O conteúdo dos TextObjects esta errado(generalização pelo item 0 da stream 4)");
        Assertions.assertArrayEquals(new float[] {8.01f,0,0,8.01f,88.0311f,629.4382f},resultado.get(4).get(0).getTm(),"O conteúdo dos TextObjects esta errado(generalização pelo item 0 da stream 4)");
    }
}
