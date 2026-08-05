package starterpackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import starterpackage.businesslayer.filtros.Filtro;
import starterpackage.businesslayer.GabaritoManager;
import starterpackage.businesslayer.QuestoesDetector;
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
 * obs: Implementar CommandLineRunner faz a classe executar o metodo run quando o ApplicationContext do Spring for configurado
 */
@Component
public class GabaritoCLI implements CommandLineRunner {
    @Autowired
    Map<String,Filtro> filtroMap;
    String arquivo = "";
    GabaritoManager gabaritoManager;
    Gabarito gabarito;

    public void run(String... args){
        Scanner scanner = new Scanner(System.in);
        QuestoesDetector questoesDetector;
        System.out.println("Programa ledor de gabarito\n");
        while(true) {
            try {
                if(arquivo.isEmpty()) {
                    System.out.print("Digite um arquivo para ler:");
                    arquivo = scanner.next();
                }
                questoesDetector = new QuestoesDetector(arquivo,
                        1,90,filtroMap);
                break;
            } catch (IOException e) {
                if(e.getClass() == FileNotFoundException.class){
                    System.out.println("Arquivo não encontrado, tente de novo");
                    arquivo = "";
                    continue;
                }
                System.out.println("Arquivo invalido.");
                throw new Error(e);
            }
        }
        if(!questoesDetector.getFiltroNome().isEmpty()){
            System.out.printf("O programa selecionou automaticamente o filtro \"%s\" para este gabarito, digite \"M\" para " +
                    "configurar manualmente ou \"A\" para ficar com o filtro detectado:", questoesDetector.getFiltroNome());
            if(scanner.next().equals("M")) questoesDetector.setFiltro(pedirFiltro(scanner));
        }else questoesDetector.setFiltro(pedirFiltro(scanner));
        System.out.println("Lendo arquivo " + new File(arquivo).getName());
        gabarito = questoesDetector.gerarGabarito();
        gabaritoManager = new GabaritoManager(gabarito);
        System.out.print("\nArquivo lido com sucesso!");
        while (true) {
            System.out.print("\n[1]Exibir estatísticas de leitura\n[2]Ver resultados(questões e alternativas)\n[3]Sair\n\nSelecione uma opção:");
            String opcao = scanner.next();
            if (opcao.equals("1")) {
                printEstatisticas();
            }else if(opcao.equals("2")){
                printGabarito();
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

    /**
     * Pede ao usuário que digite um filtro ou ative o filtro generico, depois retorna o filtro obtido
     * @param scanner scanner para obter input do usuario
     * @return filtro obtido
     */
    Filtro pedirFiltro(Scanner scanner){
        System.out.print("Voce quer ativar algum filtro?('S' ou 'N'):");
        String filtro = scanner.next();
        if(!filtro.equals("S")) return filtroMap.get("GENERICO");
        else{
            while(true) {
                System.out.println("Tipos de Filtros:");
                int i = 1;
                for (String f : filtroMap.keySet()) {
                    String virgula = i == filtroMap.size() ? "" : ",";
                    System.out.print(f + virgula);
                    i++;
                }
                System.out.print("\nDigite o nome do filtro:");
                Filtro filtroDigitado = filtroMap.get(scanner.next().toUpperCase());
                if(filtroDigitado != null) return filtroDigitado;
                else {
                    System.out.println("Filtro Inválido! Tente novamente.");
                }
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
        for(Map.Entry<Integer,String> v:gabarito.entrySet()){
            System.out.printf("Questão %d : %s\n",v.getKey(),v.getValue());
        }
    }
}
