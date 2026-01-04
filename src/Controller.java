import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

public class Controller {
    @FXML private ComboBox<String> fromCurrency;
    @FXML private ComboBox<String> toCurrency;
    @FXML private TextField amountField;
    @FXML private Label result;
    @FXML
    public void initialize() {
        fromCurrency.getItems().addAll("USD", "INR", "EUR", "JPY");
        toCurrency.getItems().addAll("USD", "INR", "EUR", "JPY");
        fromCurrency.setValue("USD");
        toCurrency.setValue("INR");
    }

    @FXML
    private void handleConvert() {
        try {
            String from = fromCurrency.getValue();
            String to = toCurrency.getValue();
            double amount = Double.parseDouble(amountField.getText());

            double rate = exchangeRate(from, to);
            double converted = amount * rate;

            result.setText("%.2f %s = %.2f %s".formatted(amount, from, converted, to));
        } catch (Exception e) {
            result.setText("Invalid Amount");
        }
    }

    private double exchangeRate(String from, String to) {
        try {
            String url = "https://open.er-api.com/v6/latest/%s".formatted(from);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            System.out.println("API response: " + json.toString(2)); // For debugging

            if (!json.getString("result").equals("success")) {
                System.out.println("API call failed");
                return 1.0;
            }

            JSONObject rates = json.getJSONObject("rates");
            return rates.getDouble(to);  // <-- Correct field
        } catch (Exception e) {
            e.printStackTrace();
            return 1.0;
        }
    }




}



