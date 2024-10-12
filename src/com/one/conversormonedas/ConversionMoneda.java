package com.one.conversormonedas;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.one.conversormonedas.exceptions.ErrorConversionMonedaException;
import com.one.conversormonedas.modelos.ValoresMoneda;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConversionMoneda {
  private static final Gson gson = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .setPrettyPrinting()
      .create();
  private static final Properties properties = new Properties();
  private static final HttpClient client = HttpClient.newHttpClient();

  static {
    try (FileInputStream input = new FileInputStream("src/com/one/conversormonedas/config/config.properties")) {
      properties.load(input);
    } catch (IOException e) {
      throw new ExceptionInInitializerError("Error al cargar las propiedades: " + e.getMessage());
    }
  }

  public double obtenerTasaConversion(String monedaOrigen, String monedaDestino) throws IOException, InterruptedException {
    HttpResponse<String> response = respuestaApi(monedaOrigen);

    if (response.statusCode() == 200) {
      String json = response.body();

      ValoresMoneda valoresMoneda = gson.fromJson(json, ValoresMoneda.class);
      Double tasaConversion = valoresMoneda.conversion_rates().get(monedaDestino);

      if (tasaConversion != null) {
        return tasaConversion;
      } else {
        throw new ErrorConversionMonedaException("No se encontró una tasa de conversión para la moneda " + monedaDestino);
      }
    } else {
      throw new ErrorConversionMonedaException("Error al obtener los datos de la API.");
    }
  }

  public HttpResponse<String> respuestaApi(String monedaOrigen) throws IOException, InterruptedException {
    String API_KEY = properties.getProperty("API_KEY");
    String direccion = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + URLEncoder.encode(monedaOrigen, StandardCharsets.UTF_8);

    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(direccion)).build();

    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }
}
