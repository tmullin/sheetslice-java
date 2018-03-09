package net.tmullin.sheetslice.app.services;

import com.lowagie.text.DocumentException;
import net.tmullin.sheetslice.pdf.Cropper;

import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class SliceService {
    public byte[] test() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (InputStream in = SliceService.class.getResourceAsStream("/damaged-coda.pdf")) {
            new Cropper(in, out)
                    .crop(2, 144, 720 + 49.68f - 72, 468, 756 + 49.68f - 72)
                    .crop(1, 144, 720 + 49.68f, 468, 756 + 49.68f)
                    .crop(2, 144, 720 + 49.68f - 72, 468, 756 + 49.68f - 72)
                    .crop(1, 144, 720 + 49.68f - 72/2, 468, 756 + 49.68f - 72/2)
                    .crop(1, 144, 720 + 49.68f - 72, 468, 756 + 49.68f - 72)
                    .run();
        } catch (DocumentException e) {
            throw new IOException(e);
        }

        return out.toByteArray();
    }
}
