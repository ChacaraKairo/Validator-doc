package br.com.chacarakairo.validatordoc.security;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.springframework.stereotype.Component;

@Component
public class DocumentSanitizer {

    public SanitizedFile sanitize(byte[] content, String mediaType) {
        return switch (mediaType) {
            case "image/jpeg" -> sanitizeImage(content, "jpg", "image/jpeg");
            case "image/png" -> sanitizeImage(content, "png", "image/png");
            case "application/pdf" -> sanitizePdf(content);
            default -> throw new IllegalArgumentException("Formato sem sanitizador disponível.");
        };
    }

    private SanitizedFile sanitizeImage(byte[] content, String format, String mediaType) {
        try {
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(content));
            if (source == null) {
                throw new IllegalArgumentException("Imagem inválida ou corrompida.");
            }
            int imageType = "jpg".equals(format) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
            BufferedImage clean = new BufferedImage(source.getWidth(), source.getHeight(), imageType);
            Graphics2D graphics = clean.createGraphics();
            graphics.drawImage(source, 0, 0, null);
            graphics.dispose();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (!ImageIO.write(clean, format, output)) {
                throw new IllegalStateException("Não foi possível reencodar a imagem.");
            }
            return new SanitizedFile(output.toByteArray(), mediaType);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Falha ao sanitizar a imagem.", exception);
        }
    }

    private SanitizedFile sanitizePdf(byte[] content) {
        try (PDDocument document = Loader.loadPDF(content); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("PDF protegido por senha não é aceito.");
            }
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            catalog.setOpenAction(null);
            catalog.setActions(null);
            catalog.setNames(null);
            document.getDocumentInformation().setCustomMetadataValue("sanitized-by", "validator-doc");
            document.save(output);
            return new SanitizedFile(output.toByteArray(), "application/pdf");
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("PDF inválido ou não sanitizável.", exception);
        }
    }
}
