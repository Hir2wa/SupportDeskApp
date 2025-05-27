package util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import model.Report;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.util.List;
import java.io.File;

public class PdfGenerator {
    public void generateReportPdf(List<Report> reports) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report PDF");
        fileChooser.setSelectedFile(new File("Support_Desk_Report.pdf"));
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String dest = fileToSave.getAbsolutePath();
            if (!dest.toLowerCase().endsWith(".pdf")) {
                dest += ".pdf";
            }

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Support Desk Report")
                    .setFontSize(18)
                    .setBold());

            Table table = new Table(new float[]{1, 2, 3, 2, 2});
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Reported By")));
            table.addHeaderCell(new Cell().add(new Paragraph("Reason")));
            table.addHeaderCell(new Cell().add(new Paragraph("Issue/Comment")));
            table.addHeaderCell(new Cell().add(new Paragraph("Status")));

            for (Report report : reports) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(report.getId()))));
                table.addCell(new Cell().add(new Paragraph(report.getReportedBy().getUsername())));
                table.addCell(new Cell().add(new Paragraph(report.getReason())));
                String target = report.getIssue() != null ? "Issue: " + report.getIssue().getId() :
                               report.getComment() != null ? "Comment: " + report.getComment().getId() : "-";
                table.addCell(new Cell().add(new Paragraph(target)));
                table.addCell(new Cell().add(new Paragraph(report.getStatus())));
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(null, "PDF saved successfully at: " + dest);
        } else {
            JOptionPane.showMessageDialog(null, "PDF generation cancelled.");
        }
    }
}