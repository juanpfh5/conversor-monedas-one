package com.one.conversormonedas.modelos;

import java.time.LocalDateTime;

public class ConversionCantidad {
  private final double cantidadEntrada;
  private final String monedaEntrada;
  private final double cantidadSalida;
  private final String monedaSalida;
  private final LocalDateTime fecha;

  public ConversionCantidad(double cantidadEntrada, String monedaEntrada, double cantidadSalida, String monedaSalida, LocalDateTime fecha) {
    this.cantidadEntrada = cantidadEntrada;
    this.monedaEntrada = monedaEntrada;
    this.cantidadSalida = cantidadSalida;
    this.monedaSalida = monedaSalida;
    this.fecha = fecha;
  }

  public String getCantidadEntradaFormateada() {
    return String.format("%.2f %s", cantidadEntrada, monedaEntrada);
  }

  public String getCantidadSalidaFormateada() {
    return String.format("%.2f %s", cantidadSalida, monedaSalida);
  }

  public String getFechaFormateada() {
    return String.format("%02d/%02d/%02d %02d:%02d:%02d", fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear(), fecha.getHour(), fecha.getMinute(), fecha.getSecond());
  }
}
