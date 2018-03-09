package net.tmullin.sheetslice.app.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import net.tmullin.sheetslice.app.inject.S3;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class PdfService {
    private final AmazonS3 s3;
    private final String bucket;
    private final String prefix;

    @Inject
    public PdfService(AmazonS3 s3, @S3.Bucket String bucket, @S3.Prefix String prefix) {
        this.s3 = s3;
        this.bucket = bucket;
        this.prefix = prefix;
    }

    public List<String> list(String subPath) {
        if (!subPath.isEmpty() && !subPath.endsWith("/")) {
            subPath += "/";
        }

        ObjectListing listing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(prefix + subPath)
                .withDelimiter("/"));

        return Stream.concat(
                listing.getCommonPrefixes().stream(),
                listing.getObjectSummaries().stream().map(S3ObjectSummary::getKey)
        ).collect(Collectors.toList());
    }

    /**
     * {@code file} must support {@link InputStream#reset()}.
     */
    public String upload(int width, InputStream file, String name) throws IOException {
        final String filePrefix = prefix + name + "/";

        file.reset();
        createPageImages(width, file, filePrefix);

        file.reset();
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("application/pdf");
        s3.putObject(bucket, filePrefix + "original.pdf", file, meta);

        return filePrefix;
    }

    private void createPageImages(int width, InputStream file, String filePrefix) throws IOException {
        new CreatePageImages(width, file, filePrefix);
    }

    private final class CreatePageImages {
        private final int width;
        private final String filePrefix;
        private final PDDocument doc;
        private final PDFRenderer renderer;

        CreatePageImages(int width, InputStream file, String filePrefix) throws IOException {
            this.width = width;
            this.filePrefix = filePrefix;

            try (PDDocument doc = PDDocument.load(file)) {
                this.doc = doc;
                this.renderer = new PDFRenderer(doc);

                for (int i = 0; i < doc.getNumberOfPages(); i++) {
                    byte[] imageData = processPage(i);
                    uploadPage(i, imageData);
                }
            }
        }

        private byte[] processPage(int i) throws IOException {
            PDPage page = doc.getPage(i);
            PDRectangle bbox = page.getBBox();
            float scale = (float) width / bbox.getWidth();

            BufferedImage image = renderer.renderImage(i, scale);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIOUtil.writeImage(image, "png", out);
            return out.toByteArray();
        }

        private void uploadPage(int i, byte[] imageData) {
            ByteArrayInputStream in = new ByteArrayInputStream(imageData);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(imageData.length);
            meta.setContentType("image/png");
            s3.putObject(bucket, filePrefix + "pages/" + i + ".png", in, meta);
        }
    }
}
