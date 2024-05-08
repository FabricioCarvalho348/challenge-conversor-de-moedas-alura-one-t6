import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ConversorDeMoedas {

    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/d69ec71274a06091bd1cb241/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMenu();

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    currencyConvert(scanner);
                    break;
                case 2:
                    listCurrencyCodes();
                    break;
                case 3:
                    System.out.println("Saindo do programa.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opção inválida. Por favor, escolha uma opção válida.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n================ Menu ================");
        System.out.println("=       1. Converter moeda           =");
        System.out.println("=       2. Listar moedas válidas     =");
        System.out.println("=       3. Sair                      =");
        System.out.print("= Escolha uma opção: ");
    }

    private static void currencyConvert(Scanner scanner) {
        System.out.print("Digite a moeda de origem (por exemplo, USD): ");
        String coinConversion = scanner.nextLine().toUpperCase();

        System.out.print("Digite a moeda de destino (por exemplo, CAD): ");
        String coinConvert = scanner.nextLine().toUpperCase();

        System.out.print("Digite o valor a ser convertido: ");
        double value = scanner.nextDouble();

        try {
            String apiUrl = API_BASE_URL + coinConversion;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);
                double exchangeRate = jsonObject.getAsJsonObject("conversion_rates").get(coinConvert).getAsDouble();
                double convertedValue = value * exchangeRate;

                System.out.printf("%.2f %s = %.2f %s\n", value, coinConversion, convertedValue, coinConvert);
            } else {
                System.out.println("Erro ao fazer a solicitação: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao processar a solicitação. Por favor, tente novamente.");
        }
    }

    private static void listCurrencyCodes() {
        try {
            String apiUrl = "https://v6.exchangerate-api.com/v6/d69ec71274a06091bd1cb241/codes";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonElement jsonResponse = new Gson().fromJson(response.body(), JsonElement.class);
                JsonArray supportedCodes = jsonResponse.getAsJsonObject().get("supported_codes").getAsJsonArray();

                System.out.println("================================ Códigos de Moedas Disponíveis ================================");
                int cont = 0;
                for (JsonElement element : supportedCodes) {
                    JsonArray codeInfo = element.getAsJsonArray();
                    String code = codeInfo.get(0).getAsString();
                    String name = codeInfo.get(1).getAsString();
                    System.out.print(code + " - " + name + "\t");
                    cont++;
                    if (cont == 15) {
                        System.out.println("\n");
                        cont = 0;
                    }
                }
            } else {
                System.out.println("Erro ao fazer a solicitação: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao processar a solicitação. Por favor, tente novamente.");
        }
    }
}


