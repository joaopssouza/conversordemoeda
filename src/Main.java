package java;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    // Construtor da classe Main que lança IOException
    public Main() throws IOException {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        // Inicialização do objeto Gson com políticas específicas
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        // Endereço da API de conversão de moedas
        String endereco = "https://v6.exchangerate-api.com/v6/133d2803161f78a8a374d953/latest/USD";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        // Envio da requisição e obtenção da resposta da API
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Parsing da resposta JSON para um JsonObject
        JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);

        // Mensagem de boas-vindas
        System.out.println("""
                Bem vindo a esta aplicação de converter moeda
                por favor, siga todas as intruções ditas para que não haja problemas.
                para finalizar, digite "sair" a qualquer momento
                """);

        // Lista para armazenar as conversões realizadas
        List<Map<String, Object>> conversoes = new ArrayList<>();

        // Loop principal para a conversão de moedas
        while (true) {
            try {
                // Solicita a sigla da moeda de origem
                System.out.println("""
                                               
                        ===============================================
                                                
                        Para converter o valor, Digite a sigla da Moeda:
                        * A sigla tem que ser em letras maiusculas *

                        Dólar Americano (USD);
                        Euro (EUR);
                        Real (BRL);
                        peso argentino (ARS);
                        Libra Esterlina (GBP).
                                                
                        """);

                String sigla1 = sc.nextLine();
                if (sigla1.equalsIgnoreCase("sair")) {
                    break;
                }

                // Obtém a taxa de conversão da moeda de origem
                double moeda1 = jsonObject.getAsJsonObject("conversion_rates").get(sigla1).getAsDouble();

                // Solicita a sigla da moeda de destino
                System.out.println("""
                                                
                        Agora digite a sigla da moeda para qual moeda deve ser convertida:
                        * A sigla tem que ser em letras maiusculas *
                                                
                        Dólar Americano (USD);
                        Euro (EUR);
                        Real (BRL);
                        peso argentino (ARS);
                        Libra Esterlina (GBP).
                                                
                        """);

                String sigla2 = sc.nextLine();
                if (sigla2.equalsIgnoreCase("sair")) {
                    break;
                }

                // Obtém a taxa de conversão da moeda de destino
                double moeda2 = jsonObject.getAsJsonObject("conversion_rates").get(sigla2).getAsDouble();

                // Solicita o valor a ser convertido
                System.out.println("Digite o valor a ser convertido");
                double valor = sc.nextDouble();
                // Chama o método para converter o valor
                converter(valor, moeda1, moeda2, sigla1, sigla2);

                // Obtém a data e hora atuais
                LocalDateTime agora = LocalDateTime.now();
                String data = agora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Cria um mapa para armazenar os detalhes da conversão
                Map<String, Object> conversao = new HashMap<>();
                conversao.put("data", data);
                conversao.put("moeda_origem", sigla1);
                conversao.put("valor_origem", valor);
                conversao.put("moeda_destino", sigla2);
                conversao.put("valor_destino", Math.round(valor * (moeda2 / moeda1) * 100.0) / 100.0);

                // Adiciona a conversão à lista de conversões
                conversoes.add(conversao);

            } catch (NullPointerException e) {
                // Mensagem de erro em caso de exceção
                System.out.println("""
                        Erro: você digitou algo errado ou inválido;
                                                
                        Identifique o problema e possíveis soluções:
                                                
                        -> moeda não reconhecida:
                        certifique que a digitou em letra maiuscula.\s
                        Ex.:\s
                        certo: "USD"
                        errado: "usd"
                                                
                        se não resolver, a moeda pode não estar no sistema.
                                                
                        -> valor inválido:
                        certifique de usar "," e "." corretamente.
                        Ex.:\s
                        certo: 5,50
                        errado: 5.50
                                                
                        certo: 1.000,50
                        errado: 1,000.50
                                                
                        certifique tambem de não utilizar letras ou quaisquer outro caractere.
                        """);
            }
            // Limpa o buffer do scanner
            sc.nextLine();
        }

        // Define o diretório e o arquivo para salvar o histórico de conversões
        File diretorio = new File("C:\\Users\\ffgus\\Desktop\\Conversor de moedas\\src\\historico");
        File arquivo = new File(diretorio, "conversoes.txt");
        // Escreve o histórico de conversões no arquivo
        FileWriter escrita = new FileWriter(arquivo, true);
        escrita.write("\n");
        escrita.write(gson.toJson(conversoes));
        escrita.close();
        System.out.println("Conversões adicionais salvas no arquivo.");
        sc.close();
    }

    // Método para converter o valor de uma moeda para outra
    public static void converter(double valor, double moeda1, double moeda2, String sigla1, String sigla2) {
        System.out.println(valor + " em " + sigla1 + " equivale a " + (valor * (moeda2 / moeda1)) + " em " + sigla2);
    }
}