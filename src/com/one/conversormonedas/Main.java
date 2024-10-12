package com.one.conversormonedas;

import com.one.conversormonedas.enums.Moneda;
import com.one.conversormonedas.exceptions.ErrorConversionMonedaException;
import com.one.conversormonedas.modelos.ConversionCantidad;
import com.one.conversormonedas.placeholder.TextPrompt;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;

public class Main {
  static LinkedList<ConversionCantidad> historialConversiones = new LinkedList<>();

  public static void main(String[] args) {
    JFrame frame = crearVentanaPrincipal();
    agregarMenu(frame);
    frame.setVisible(true);
  }

  private static JFrame crearVentanaPrincipal() {
    JFrame frame = new JFrame("Conversor de Monedas | ONE");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(700, 200);
    frame.setLocationRelativeTo(null);
    frame.setLayout(new MigLayout("wrap 2", "[grow][grow]", "[] [] [] [] []"));

    String[] monedas = Arrays.stream(Moneda.values()).map(moneda -> moneda.name() + " - " + moneda.getNombreMoneda()).toArray(String[]::new);

    JLabel JLBTitulo = new JLabel("Conversor de Monedas | ONE");
    JTextField JTFCantidadEntrada = new JTextField();
    new TextPrompt("Ingrese la cantidad a convertir", JTFCantidadEntrada);
    JLabel JLBEtiquetaEntrada = new JLabel("Tipo de moneda ingresada:");
    JLabel JLBEtiquetaSalida = new JLabel("Moneda a obtener:");
    JComboBox<String> JCBDesplegableEntrada = new JComboBox<>(monedas);
    JComboBox<String> JCBDesplegableSalida = new JComboBox<>(monedas);
    JButton JBTConvertir = new JButton("Convertir");

    frame.add(JLBTitulo, "span 2, align center, wrap");
    frame.add(JTFCantidadEntrada, "span 2, growx");
    frame.add(JLBEtiquetaEntrada, "growx");
    frame.add(JLBEtiquetaSalida, "growx, wrap");
    frame.add(JCBDesplegableEntrada, "growx");
    frame.add(JCBDesplegableSalida, "growx, wrap");
    frame.add(JBTConvertir, "span 2, grow");

    JBTConvertir.addActionListener(e -> convertirMoneda(frame, JTFCantidadEntrada, JCBDesplegableEntrada, JCBDesplegableSalida));

    return frame;
  }

  private static void convertirMoneda(JFrame frame, JTextField JTFCantidadEntrada, JComboBox<String> JCBDesplegableEntrada, JComboBox<String> JCBDesplegableSalida) {
    String monedaOrigen = String.valueOf(JCBDesplegableEntrada.getSelectedItem()).substring(0, 3);
    String monedaDestino = String.valueOf(JCBDesplegableSalida.getSelectedItem()).substring(0, 3);
    String cantidadTexto = JTFCantidadEntrada.getText();

    try {
      double cantidad = validarCantidad(cantidadTexto);
      double cantidadConvertida = obtenerCantidadConvertida(monedaOrigen, monedaDestino, cantidad);
      ConversionCantidad conversionCantidad = new ConversionCantidad(cantidad, monedaOrigen, cantidadConvertida, monedaDestino, LocalDateTime.now());

      historialConversiones.addFirst(conversionCantidad);
      JOptionPane.showMessageDialog(frame, String.format("%,.2f %s equivale%s a %,.2f %s", cantidad, monedaOrigen, (cantidad > 1 ? "n" : ""), cantidadConvertida, monedaDestino), "Conversión exitosa", JOptionPane.INFORMATION_MESSAGE);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(frame, "Por favor, ingrese una cantidad válida.", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException | InterruptedException ex) {
      JOptionPane.showMessageDialog(frame, "Error al convertir la moneda.", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (ErrorConversionMonedaException ex) {
      JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private static double validarCantidad(String cantidadTexto) throws NumberFormatException {
    double cantidad = Double.parseDouble(cantidadTexto);
    if (cantidad < 0 || cantidadTexto.startsWith("-")) {
      throw new NumberFormatException();
    }
    return cantidad;
  }

  private static double obtenerCantidadConvertida(String monedaOrigen, String monedaDestino, double cantidad) throws IOException, InterruptedException, ErrorConversionMonedaException {
    ConversionMoneda conversionMoneda = new ConversionMoneda();
    return conversionMoneda.obtenerTasaConversion(monedaOrigen, monedaDestino) * cantidad;
  }

  private static void agregarMenu(JFrame f) {
    JMenuBar menubar = new JMenuBar();
    f.setJMenuBar(menubar);

    JMenu menu = new JMenu("Opciones");
    menubar.add(menu);

    JMenuItem opcionHistorial = new JMenuItem("Historial");
    opcionHistorial.addActionListener(e -> mostrarHistorial(f));
    menu.add(opcionHistorial);

    JMenuItem opcionSalir = new JMenuItem("Salir");
    opcionSalir.addActionListener(e -> System.exit(0));
    menu.add(opcionSalir);
  }

  private static void mostrarHistorial(JFrame f) {
    JDialog dialog = new JDialog(f, "Historial | Conversor de Monedas", true);
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(f);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setLayout(new MigLayout("wrap", "[grow]", "[] []"));

    JLabel JLBTitulo = new JLabel("Historial | Conversor de Monedas");
    dialog.add(JLBTitulo, "span 2, align center, wrap");

    DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"Cantidad entrante", "Cantidad convertida", "Fecha"}, 0);
    JTable JTBHistorialConversiones = new JTable(modeloTabla);
    dialog.add(new JScrollPane(JTBHistorialConversiones), "span, grow");

    historialConversiones.forEach(conversionCantidad -> modeloTabla.addRow(new Object[]{conversionCantidad.getCantidadEntradaFormateada(), conversionCantidad.getCantidadSalidaFormateada(), conversionCantidad.getFechaFormateada()}));

    dialog.setVisible(true);
  }
}