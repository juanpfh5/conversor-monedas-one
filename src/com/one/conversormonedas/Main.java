package com.one.conversormonedas;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {
    Scanner scan = new Scanner(System.in);
    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().create();

    System.out.print("Ingrese el tipo de moneda: ");
    String moneda = scan.nextLine();

    Properties properties = new Properties();
    try (FileInputStream input = new FileInputStream("src/com/one/conversormonedas/config.properties")) {
      properties.load(input);
    }
    String API_KEY = properties.getProperty("API_KEY");

    String direccion = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + URLEncoder.encode(moneda, StandardCharsets.UTF_8);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(direccion)).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    int statusCode = response.statusCode();
    System.out.println("HTTP Status Code: " + statusCode);

    String json = response.body();
    System.out.println(json);
  }
}