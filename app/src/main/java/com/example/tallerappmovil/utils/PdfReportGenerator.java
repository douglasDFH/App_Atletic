package com.example.tallerappmovil.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.tallerappmovil.model.AsistenciaAtleta;
import com.example.tallerappmovil.model.AsistenciaHistorial;
import com.example.tallerappmovil.model.MarcaPersonal;
import com.example.tallerappmovil.model.ReporteAtleta;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdfReportGenerator {

    private static final int PAGE_W = 595;
    private static final int PAGE_H = 842;
    private static final int MARGIN = 30;
    private static final int CONTENT_W = PAGE_W - 2 * MARGIN;
    private static final int ROW_H = 20;
    private static final int HEADER_H = 90;

    private static final int COLOR_NAVY  = Color.rgb(26, 43, 74);
    private static final int COLOR_TEAL  = Color.rgb(0, 176, 160);
    private static final int COLOR_GRAY  = Color.rgb(100, 100, 100);
    private static final int COLOR_LGRAY = Color.rgb(240, 242, 245);
    private static final int COLOR_LINE  = Color.rgb(200, 205, 210);

    // ── Reporte A: Asistencia por sesión ─────────────────────────────────────

    public static void exportarAsistenciaSesion(Context ctx,
                                                 List<AsistenciaAtleta> atletas,
                                                 Map<Long, String> estados,
                                                 String grupo, String hora, String lugar) {
        PdfDocument doc = new PdfDocument();
        State s = new State(doc);

        drawHeader(s, "Registro de Asistencia", grupo + "  ·  " + hora + "  ·  " + lugar);

        // columnas: N° (35) | Atleta (330) | Estado (120) | suma=485 + margen
        float[] cols = {MARGIN, MARGIN + 35, MARGIN + 365};
        String[] headers = {"N°", "Atleta", "Estado"};
        drawTableHeader(s, cols, headers);

        int i = 1;
        int presentes = 0, ausentes = 0, justif = 0;
        for (AsistenciaAtleta a : atletas) {
            checkNewPage(s, doc);
            String estado = estados.getOrDefault(a.getAtletaId(), "--");
            String[] cells = {String.valueOf(i), a.getNombreCompleto(), estadoLabel(estado)};
            int color = estadoColor(estado);
            drawRow(s, cols, cells, i % 2 == 0, color, 2);
            if ("PRESENTE".equals(estado))    presentes++;
            else if ("AUSENTE".equals(estado)) ausentes++;
            else if ("JUSTIFICADO".equals(estado)) justif++;
            i++;
        }

        s.y += 14;
        drawSummaryLine(s, "Resumen: Presentes " + presentes + " · Ausentes " + ausentes
                + " · Justificados " + justif
                + " · Total " + atletas.size());

        doc.finishPage(s.page);
        abrirPdf(ctx, doc, "asistencia_sesion_" + timestamp() + ".pdf");
    }

    // ── Reporte B: Historial de asistencia del atleta ─────────────────────────

    public static void exportarHistorialAtleta(Context ctx,
                                                List<AsistenciaHistorial> historial,
                                                String atletaNombre,
                                                int presentes, int ausentes, int justificados) {
        PdfDocument doc = new PdfDocument();
        State s = new State(doc);

        int total = historial.size();
        int pct = total > 0 ? Math.round(presentes * 100f / total) : 0;

        drawHeader(s, "Historial de Asistencia", atletaNombre);

        float[] cols = {MARGIN, MARGIN + 60, MARGIN + 120, MARGIN + 300, MARGIN + 415};
        String[] headers = {"Fecha", "Hora", "Grupo", "Lugar", "Estado"};
        drawTableHeader(s, cols, headers);

        SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        SimpleDateFormat horaFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

        int i = 1;
        for (AsistenciaHistorial h : historial) {
            checkNewPage(s, doc);
            String fecha = "--", hora = "--";
            try {
                Date d = isoFmt.parse(h.getHoraInicio());
                if (d != null) { fecha = dateFmt.format(d); hora = horaFmt.format(d); }
            } catch (Exception ignored) {}
            String[] cells = {
                fecha, hora,
                nvl(h.getGrupoNombre(), "--"),
                nvl(h.getLugar(), "--"),
                estadoLabel(nvl(h.getEstado(), "--"))
            };
            drawRow(s, cols, cells, i % 2 == 0, estadoColor(h.getEstado()), 4);
            i++;
        }

        s.y += 14;
        drawSummaryLine(s, "Presentes " + presentes + " · Ausentes " + ausentes
                + " · Justificados " + justificados
                + " · Asistencia " + pct + "% (" + total + " sesiones)");

        doc.finishPage(s.page);
        abrirPdf(ctx, doc, "historial_" + sanitize(atletaNombre) + "_" + timestamp() + ".pdf");
    }

    // ── Reporte C: Marcas del atleta ─────────────────────────────────────────

    public static void exportarMarcas(Context ctx,
                                       List<MarcaPersonal> marcas,
                                       String titulo) {
        PdfDocument doc = new PdfDocument();
        State s = new State(doc);

        drawHeader(s, "Historial de Marcas Deportivas", titulo);

        float[] cols = {MARGIN, MARGIN + 25, MARGIN + 95, MARGIN + 245, MARGIN + 355, MARGIN + 455};
        String[] headers = {"#", "Fecha", "Disciplina", "Resultado", "Observaciones", "Récord"};
        drawTableHeader(s, cols, headers);

        int i = 1;
        for (MarcaPersonal m : marcas) {
            checkNewPage(s, doc);
            String resultado = nvl(m.getResultado(), "--")
                    + (m.getUnidad() != null ? " " + m.getUnidad() : "");
            String[] cells = {
                String.valueOf(i),
                nvl(m.getFecha(), "--"),
                nvl(m.getDisciplina(), "--"),
                resultado,
                nvl(m.getObservaciones(), ""),
                m.isEsMejorMarca() ? "★" : ""
            };
            int recordColor = m.isEsMejorMarca() ? COLOR_TEAL : -1;
            drawRow(s, cols, cells, i % 2 == 0, recordColor, 5);
            i++;
        }

        s.y += 14;
        long records = marcas.stream().filter(MarcaPersonal::isEsMejorMarca).count();
        drawSummaryLine(s, "Total marcas: " + marcas.size() + "  ·  Récords personales: " + records);

        doc.finishPage(s.page);
        abrirPdf(ctx, doc, "marcas_" + timestamp() + ".pdf");
    }

    // ── Reporte D: Estadísticas generales del club ────────────────────────────

    public static void exportarEstadisticas(Context ctx,
                                             String mes,
                                             int totalAtletas,
                                             int proxCompetencias,
                                             int promedioAsistencia,
                                             int totalPresentes,
                                             int totalAusentes,
                                             List<ReporteAtleta> ranking) {
        PdfDocument doc = new PdfDocument();
        State s = new State(doc);

        drawHeader(s, "Estadísticas del Club", mes);

        // Resumen general
        Paint boldPaint = bold(12, COLOR_NAVY);
        Paint valPaint  = regular(12, COLOR_TEAL);
        Paint secPaint  = bold(11, COLOR_NAVY);

        s.y += 8;
        canvas(s).drawText("Resumen del periodo", MARGIN, s.y, secPaint);
        s.y += 16;
        drawKV(s, "Total atletas activos:", String.valueOf(totalAtletas), boldPaint, valPaint);
        drawKV(s, "Próximas competencias:", String.valueOf(proxCompetencias), boldPaint, valPaint);
        drawKV(s, "Promedio de asistencia:", promedioAsistencia + "%", boldPaint, valPaint);
        drawKV(s, "Total presentes (mes):", String.valueOf(totalPresentes), boldPaint, valPaint);
        drawKV(s, "Total ausentes (mes):", String.valueOf(totalAusentes), boldPaint, valPaint);

        s.y += 14;
        canvas(s).drawText("Ranking de Asistencia", MARGIN, s.y, secPaint);
        s.y += 6;
        canvas(s).drawLine(MARGIN, s.y, PAGE_W - MARGIN, s.y, linePaint());
        s.y += 10;

        if (ranking != null && !ranking.isEmpty()) {
            float[] cols = {MARGIN, MARGIN + 28, MARGIN + 280, MARGIN + 335, MARGIN + 375, MARGIN + 415, MARGIN + 465};
            String[] headers = {"#", "Atleta", "Sesiones", "✓", "✗", "J", "%"};
            drawTableHeader(s, cols, headers);

            int i = 1;
            for (ReporteAtleta r : ranking) {
                checkNewPage(s, doc);
                int pct = r.getTotalSesiones() > 0
                        ? Math.round(r.getPresentes() * 100f / r.getTotalSesiones()) : 0;
                int color = pct >= 80 ? Color.rgb(39, 174, 96)
                          : pct >= 50 ? Color.rgb(243, 156, 18) : Color.rgb(231, 76, 60);
                String[] cells = {
                    String.valueOf(i),
                    nvl(r.getNombreCompleto(), "--"),
                    String.valueOf(r.getTotalSesiones()),
                    String.valueOf(r.getPresentes()),
                    String.valueOf(r.getAusentes()),
                    String.valueOf(r.getJustificados()),
                    pct + "%"
                };
                drawRow(s, cols, cells, i % 2 == 0, color, 6);
                i++;
            }
        } else {
            canvas(s).drawText("Sin datos de asistencia para este periodo.", MARGIN, s.y, regular(10, COLOR_GRAY));
            s.y += ROW_H;
        }

        doc.finishPage(s.page);
        abrirPdf(ctx, doc, "estadisticas_" + timestamp() + ".pdf");
    }

    // ── Helpers de dibujo ─────────────────────────────────────────────────────

    private static void drawHeader(State s, String title, String subtitle) {
        Canvas c = canvas(s);

        // Barra de color navy
        Paint navyPaint = new Paint();
        navyPaint.setColor(COLOR_NAVY);
        c.drawRect(0, 0, PAGE_W, HEADER_H - 10, navyPaint);

        // Línea teal
        Paint tealLine = new Paint();
        tealLine.setColor(COLOR_TEAL);
        tealLine.setStrokeWidth(4);
        c.drawLine(0, HEADER_H - 10, PAGE_W, HEADER_H - 10, tealLine);

        // Textos del header
        c.drawText("Club Atlético Santa Cruz de la Sierra", MARGIN, 28, bold(14, Color.WHITE));
        c.drawText(title, MARGIN, 50, bold(12, Color.rgb(0, 200, 190)));
        c.drawText(subtitle, MARGIN, 68, regular(10, Color.rgb(180, 200, 220)));

        // Fecha generación (derecha)
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        Paint fechaPaint = regular(9, Color.rgb(160, 185, 210));
        c.drawText("Generado: " + fecha, PAGE_W - MARGIN - 120, 28, fechaPaint);
        c.drawText("AthleteSCZ", PAGE_W - MARGIN - 60, 50, regular(9, Color.rgb(0, 200, 190)));

        s.y = HEADER_H + 12;
    }

    private static void drawTableHeader(State s, float[] cols, String[] headers) {
        Canvas c = canvas(s);
        Paint bg = new Paint(); bg.setColor(COLOR_LGRAY);
        c.drawRect(MARGIN, s.y - 12, PAGE_W - MARGIN, s.y + 8, bg);

        Paint p = bold(9, COLOR_NAVY);
        for (int i = 0; i < headers.length && i < cols.length; i++) {
            c.drawText(headers[i], cols[i] + 3, s.y, p);
        }
        s.y += ROW_H;
        c.drawLine(MARGIN, s.y - ROW_H + 9, PAGE_W - MARGIN, s.y - ROW_H + 9, linePaint());
    }

    private static void drawRow(State s, float[] cols, String[] cells,
                                 boolean shade, int accentCol, int accentIdx) {
        Canvas c = canvas(s);
        if (shade) {
            Paint bg = new Paint(); bg.setColor(Color.rgb(250, 251, 252));
            c.drawRect(MARGIN, s.y - 12, PAGE_W - MARGIN, s.y + 5, bg);
        }
        Paint p = regular(9, Color.rgb(40, 40, 40));
        for (int i = 0; i < cells.length && i < cols.length; i++) {
            Paint cp = (accentCol != -1 && i == accentIdx)
                    ? bold(9, accentCol) : p;
            String text = truncate(cells[i], maxCharsForCol(cols, i));
            c.drawText(text, cols[i] + 3, s.y, cp);
        }
        s.y += ROW_H;
        Paint line = new Paint(); line.setColor(COLOR_LINE); line.setStrokeWidth(0.5f);
        c.drawLine(MARGIN, s.y - ROW_H + 7, PAGE_W - MARGIN, s.y - ROW_H + 7, line);
    }

    private static void drawKV(State s, String key, String value, Paint kPaint, Paint vPaint) {
        canvas(s).drawText(key, MARGIN, s.y, kPaint);
        canvas(s).drawText(value, MARGIN + 200, s.y, vPaint);
        s.y += 18;
    }

    private static void drawSummaryLine(State s, String text) {
        Paint bg = new Paint(); bg.setColor(COLOR_LGRAY);
        canvas(s).drawRect(MARGIN, s.y - 12, PAGE_W - MARGIN, s.y + 6, bg);
        canvas(s).drawText(text, MARGIN + 6, s.y, bold(9, COLOR_NAVY));
        s.y += ROW_H;
    }

    private static void checkNewPage(State s, PdfDocument doc) {
        if (s.y > PAGE_H - 60) {
            drawPageFooter(s);
            doc.finishPage(s.page);
            s.pageNum++;
            PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, s.pageNum).create();
            s.page = doc.startPage(info);
            s.y = MARGIN + 20;
        }
    }

    private static void drawPageFooter(State s) {
        String footer = "Página " + s.pageNum + "  ·  AthleteSCZ — Club Atlético Santa Cruz";
        Paint p = regular(8, COLOR_GRAY);
        canvas(s).drawLine(MARGIN, PAGE_H - 28, PAGE_W - MARGIN, PAGE_H - 28, linePaint());
        canvas(s).drawText(footer, MARGIN, PAGE_H - 16, p);
    }

    private static void abrirPdf(Context ctx, PdfDocument doc, String fileName) {
        try {
            File dir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (dir != null) dir.mkdirs();
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            doc.writeTo(fos);
            fos.close();
            doc.close();

            Uri uri = FileProvider.getUriForFile(ctx,
                    ctx.getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ctx.startActivity(Intent.createChooser(intent, "Abrir PDF"));
        } catch (Exception e) {
            doc.close();
            Toast.makeText(ctx, "Error al generar el PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private static class State {
        PdfDocument.Page page;
        int pageNum = 1;
        float y;

        State(PdfDocument doc) {
            PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create();
            this.page = doc.startPage(info);
        }
    }

    private static Canvas canvas(State s) { return s.page.getCanvas(); }

    private static Paint bold(float sp, int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setTextSize(sp);
        p.setFakeBoldText(true);
        return p;
    }

    private static Paint regular(float sp, int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setTextSize(sp);
        return p;
    }

    private static Paint linePaint() {
        Paint p = new Paint();
        p.setColor(COLOR_LINE);
        p.setStrokeWidth(0.7f);
        return p;
    }

    private static String estadoLabel(String e) {
        if (e == null) return "--";
        switch (e) {
            case "PRESENTE":    return "Presente";
            case "AUSENTE":     return "Ausente";
            case "JUSTIFICADO": return "Justificado";
            default:            return e;
        }
    }

    private static int estadoColor(String e) {
        if ("PRESENTE".equals(e))    return Color.rgb(39, 174, 96);
        if ("AUSENTE".equals(e))     return Color.rgb(231, 76, 60);
        if ("JUSTIFICADO".equals(e)) return Color.rgb(243, 156, 18);
        return -1;
    }

    private static String nvl(String s, String def) { return s != null ? s : def; }

    private static String sanitize(String s) {
        return s == null ? "atleta" : s.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(new Date());
    }

    private static String truncate(String s, int maxChars) {
        if (s == null) return "";
        return s.length() > maxChars ? s.substring(0, maxChars - 1) + "…" : s;
    }

    private static int maxCharsForCol(float[] cols, int idx) {
        if (idx >= cols.length - 1) return 30;
        float colW = cols[idx + 1] - cols[idx];
        return Math.max(5, (int)(colW / 5.5f));
    }
}
