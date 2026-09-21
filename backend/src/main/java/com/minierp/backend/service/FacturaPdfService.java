package com.minierp.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.minierp.backend.entity.DetalleVenta;
import com.minierp.backend.entity.Venta;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class FacturaPdfService {

    private static final String EMPRESA = "Mini ERP - Muebles para Hogar y Oficina";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Color GRIS = new Color(230, 230, 230);

    public byte[] generar(Venta venta) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.LETTER, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, salida);
            doc.open();

            // Encabezado
            Paragraph empresa = new Paragraph(EMPRESA, TITULO);
            empresa.setAlignment(Element.ALIGN_CENTER);
            doc.add(empresa);

            Paragraph titulo = new Paragraph("FACTURA " + venta.getNumeroFactura(), NEGRITA);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(15);
            doc.add(titulo);

            // Datos del cliente
            doc.add(new Paragraph("Fecha: " + venta.getFecha().format(FORMATO_FECHA), NORMAL));
            doc.add(new Paragraph("Cliente: " + venta.getCliente().getNombre(), NORMAL));
            doc.add(new Paragraph("NIT: " + venta.getCliente().getNit(), NORMAL));
            doc.add(new Paragraph("Direccion: " + valor(venta.getCliente().getDireccion()), NORMAL));
            doc.add(new Paragraph("Atendido por: " + venta.getUsuario().getNombreCompleto(), NORMAL));

            // Detalle de productos
            PdfPTable tabla = new PdfPTable(new float[]{1.3f, 3.7f, 1f, 1.5f, 1.5f});
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(15);
            for (String encabezado : new String[]{"Codigo", "Producto", "Cant.", "Precio", "Subtotal"}) {
                PdfPCell celda = celda(encabezado, NEGRITA, Element.ALIGN_CENTER);
                celda.setBackgroundColor(GRIS);
                tabla.addCell(celda);
            }
            for (DetalleVenta d : venta.getDetalles()) {
                tabla.addCell(celda(d.getProducto().getCodigo(), NORMAL, Element.ALIGN_LEFT));
                tabla.addCell(celda(d.getProducto().getNombre(), NORMAL, Element.ALIGN_LEFT));
                tabla.addCell(celda(String.valueOf(d.getCantidad()), NORMAL, Element.ALIGN_CENTER));
                tabla.addCell(celda(moneda(d.getPrecioUnitario()), NORMAL, Element.ALIGN_RIGHT));
                tabla.addCell(celda(moneda(d.getSubtotal()), NORMAL, Element.ALIGN_RIGHT));
            }
            doc.add(tabla);

            // Totales
            PdfPTable totales = new PdfPTable(new float[]{3f, 1.5f});
            totales.setWidthPercentage(45);
            totales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totales.setSpacingBefore(10);
            agregarTotal(totales, "Subtotal", venta.getSubtotal(), NORMAL);
            agregarTotal(totales, "IVA (12%)", venta.getIva(), NORMAL);
            agregarTotal(totales, "TOTAL", venta.getTotal(), NEGRITA);
            doc.add(totales);

            doc.close();
            return salida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar la factura", e);
        }
    }

    private void agregarTotal(PdfPTable tabla, String etiqueta, BigDecimal monto, Font fuente) {
        tabla.addCell(celda(etiqueta, fuente, Element.ALIGN_RIGHT));
        tabla.addCell(celda(moneda(monto), fuente, Element.ALIGN_RIGHT));
    }

    private PdfPCell celda(String texto, Font fuente, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setHorizontalAlignment(alineacion);
        celda.setPadding(5);
        return celda;
    }

    private String moneda(BigDecimal valor) {
        return String.format(Locale.US, "Q %,.2f", valor);
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "Ciudad" : texto;
    }
}