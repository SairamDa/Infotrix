package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverter {

    private static final String API_KEY = "https://api.exchangeratesapi.host";
    private static final String API_BASE_URL = "https://open.er-api.com/v6/latest/";

    private Map<String, Double> favoriteCurrencies = new HashMap<>();

    public static void main(String[] args) {
        CurrencyConverter converter = new CurrencyConverter();
        converter.run();
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Convert Currency"); // convert currency
            System.out.println("2. View Favorites");     // view your added currency
            System.out.println("3. Add to Favorites");   // add your fav currency
            System.out.println("4. Update Favorite");    // update your fav currency
            System.out.println("5. Exit.....");           // exit
            System.out.print("Enter your choice: ");       // enter your choice from 1 - 5

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {                   //  by using switch
                case 1:
                    convertCurrency();          // call the method of convertCurrency
                    break;
                case 2:
                    viewFavorites();            // call the method of viewFav Currency
                    break;
                case 3:
                    addToFavorites();           // call the method of add fav Currency
                    break;
                case 4:
                    updateFavorite();           // call the method of update Currency
                    break;
                case 5:
                    System.out.println("Exiting...");     //exit.........
                    System.exit(0);
                    break;
                default:
                    System.err.println("Invalid choice. Please try again.");
            }
        }
    }

    private void convertCurrency() {                                    //method of convertCurrency
        System.out.print("Enter the amount to convert: ");
        double amountToConvert = new Scanner(System.in).nextDouble();

        System.out.print("Enter the base currency code: ");
        String baseCurrency = new Scanner(System.in).next();

        System.out.print("Enter the target currency code: ");
        String targetCurrency = new Scanner(System.in).next();

        double convertedAmount = performConversion(amountToConvert, baseCurrency, targetCurrency);

        System.out.printf("%.2f %s is equal to %.2f %s\n", amountToConvert, baseCurrency, convertedAmount, targetCurrency);
    }

    private void viewFavorites() {                                          // method of view fav Currency
        if (favoriteCurrencies.isEmpty()) {
            System.err.println("You have no favorite currencies yet.");
        } else {
            System.out.println("Your Favorite Currencies:");
            for (Map.Entry<String, Double> entry : favoriteCurrencies.entrySet())
            {
                System.out.printf("%s: %.4f\n", entry.getKey(), entry.getValue());
            }
        }
    }

    private void addToFavorites() {                                         // method of ADD Fav Currency
        System.out.print("Enter the currency code to add to favorites: ");
        String currencyCode = new Scanner(System.in).next();

        double exchangeRate = performConversion(1.0, "USD", currencyCode); // Using USD as the base currency for simplicity

        favoriteCurrencies.put(currencyCode, exchangeRate);

        System.out.printf("%s added to favorites with an exchange rate of %.4f\n", currencyCode, exchangeRate);
    }

    private void updateFavorite() {                                 //method of update fav Currency
        viewFavorites();

        System.out.print("Enter the currency code to update: ");
        String currencyCode = new Scanner(System.in).next();

        if (favoriteCurrencies.containsKey(currencyCode)) {
            System.out.print("Enter the new exchange rate: ");
            double newExchangeRate = new Scanner(System.in).nextDouble();

            favoriteCurrencies.put(currencyCode, newExchangeRate);

            System.out.printf("Exchange rate for %s updated to %.4f\n", currencyCode, newExchangeRate);
        } else {
            System.out.println("Currency not found in favorites.");
        }
    }

    private double performConversion(double amount, String baseCurrency, String targetCurrency) {
        try {
            // Construct the API URL
            URL url = new URL(API_BASE_URL + baseCurrency + "?apikey=" + API_KEY);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Get the exchange rates
                JSONObject rates;
                rates = jsonResponse.getJSONObject("rates");

                // Get the conversion rate for the target currency
                double exchangeRate = rates.getDouble(targetCurrency);

                // Perform the conversion
                return amount * exchangeRate;

            } else {
                System.err.println("Error: Unable to fetch exchange rates. Response Code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
