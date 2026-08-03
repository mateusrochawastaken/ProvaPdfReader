package starterpackage.businesslayer;

import org.junit.jupiter.api.BeforeEach;

public class QuestoesServiceTest {

    QuestoesService questoesService;

    @BeforeEach
    public void beforeEach() {
        try {
            String arquivo = "/home/mateus/Documents/IntellijProjects/Misc/Enem2023PrimeiroDiaAmareloGabarito.pdf";
            questoesService = new QuestoesService(arquivo, 1, 85);
            //LogManager.getLogger(QuestoesService.class).
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

}
