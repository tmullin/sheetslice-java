package net.tmullin.sheetslice.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Cropper {
    private final InputStream inStream;
    private final OutputStream outStream;
    private List<Crop> crops = new ArrayList<>();

    public Cropper(InputStream inStream, OutputStream outStream) {
        this.inStream = inStream;
        this.outStream = outStream;
    }

    public Cropper crop(int page, float llx, float lly, float urx, float ury) {
        Crop c = new Crop(page, llx, lly, urx, ury);
        crops.add(c);
        return this;
    }

    public void run() throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inStream);

        Document doc = new Document();
        doc.setMargins(0, 0, 0, 0);
        PdfWriter writer = PdfWriter.getInstance(doc, outStream);
        doc.open();
        PdfContentByte cb = writer.getDirectContent();

        crops.forEach(c -> {
            PdfImportedPage page = writer.getImportedPage(reader, c.page);
            doc.setPageSize(new Rectangle(c.cropBox.getWidth(), c.cropBox.getHeight()));
            doc.newPage();
            cb.addTemplate(page, -c.cropBox.getLeft(), -c.cropBox.getBottom());

        });

        doc.close();
        writer.close();
        reader.close();
    }

    static class Crop {
        int page;
        Rectangle cropBox;

        Crop(int page, float llx, float lly, float urx, float ury) {
            this.page = page;
            this.cropBox = new Rectangle(llx, lly, urx, ury);
        }
    }
}
