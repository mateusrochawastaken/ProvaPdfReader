package starterpackage;

import starterpackage.businesslayer.GabaritoManager;
import starterpackage.businesslayer.QuestoesService;
import starterpackage.dataacesslayer.dto.Gabarito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Essa classe é um CLI que informa o usuário sobre o gabarito, permitindo que ele visualize os resultados gerados pelas
 * classes da businesslayer.
 * @author Mateus Rocha
 */
public class GabaritoCLI {
    String arquivo = "";
    GabaritoManager gabaritoManager;
    Gabarito gabarito;

    public static void main() {
        GabaritoCLI gabaritoCLI = new GabaritoCLI();
        Scanner scanner = new Scanner(System.in);
        QuestoesService questoesService;
        System.out.println("Programa ledor de gabarito\n");
        while(true) {
            try {
                if(gabaritoCLI.arquivo.isEmpty()) {
                    System.out.print("Digite um arquivo para ler:");
                    gabaritoCLI.arquivo = scanner.next();
                }
                questoesService = new QuestoesService(gabaritoCLI.arquivo, 1, 90);
                break;
            } catch (IOException e) {
                if(e.getClass() == FileNotFoundException.class){
                    System.out.println("Arquivo não encontrado, tente de novo");
                    gabaritoCLI.arquivo = "";
                    continue;
                }
                System.out.println("Arquivo invalido.");
                throw new Error(e);
            }
        }
        System.out.println("Lendo arquivo " + new File(gabaritoCLI.arquivo).getName());
        gabaritoCLI.gabarito = questoesService.gerarGabarito();
        gabaritoCLI.gabaritoManager = new GabaritoManager(gabaritoCLI.gabarito);
        System.out.print("\nArquivo lido com sucesso!");
        while (true) {
            System.out.print("\n[1]Exibir estatísticas de leitura\n[2]Ver resultados(questões e alternativas)\n[3]Sair\n\nSelecione uma opção:");
            String opcao = scanner.next();
            if (opcao.equals("1")) {
                gabaritoCLI.printEstatisticas();
            }else if(opcao.equals("2")){
                gabaritoCLI.printGabarito();
            }else if (opcao.equals("3")){
                break;
            }
            System.out.println("Aperte enter para continuar");
            try {
                System.in.read();
            }catch (IOException e){
                throw new Error(e);
            }
        }
    }

    void printEstatisticas(){
        try {
            System.out.println("\n|*|Estatísticas de leitura\n");
            int sequencia = gabaritoManager.getSequencia();
            System.out.printf("Sequencia de questões encontrada = %d\nQuantidade de questoes desejada = %d\n",
                    sequencia,gabarito.getQuantidadeQuestoes());
            System.out.println("Questões encontradas:" + Arrays.toString(gabarito.keySet().toArray()) );
            List<Integer> naoEncontrados;
            if(sequencia != gabarito.getQuantidadeQuestoes()){
                naoEncontrados=gabaritoManager.getNaoEncontrados();
                System.out.println("Questões não encontradas:"+Arrays.toString(naoEncontrados.toArray()));
            }
        }catch (Exception e){
            throw new Error(e);
        }
    }

    void printGabarito(){
        System.out.println("|*|Questões");
        for(Map.Entry<Integer,Character> v:gabarito.entrySet()){
            System.out.printf("Questão %d : %c\n",v.getKey(),v.getValue());
        }
    }
}
