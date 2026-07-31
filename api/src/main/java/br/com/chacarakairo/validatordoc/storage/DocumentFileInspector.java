package br.com.chacarakairo.validatordoc.storage;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Set;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class DocumentFileInspector {

    private static final long MAX_SIZE = 20L * 1024L * 1024L;
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "application/pdf");
    private final Tika tika = new Tika();

    public InspectedFile inspect(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("O arquivo é obrigatório.");
            }
            byte[] bytes = file.getBytes();
            if (bytes.length > MAX_SIZE) {
                throw new IllegalArgumentException("O arquivo excede o limite de 20 MB.");
            }

            String detected = tika.detect(new ByteArrayInputStream(bytes), file.getOriginalFilename());
            if (!ALLOWED.contains(detected)) {
                throw new IllegalArgumentException("Formato de arquivo não permitido.");
            }
            validateSignature(bytes, detected);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String sha256 = HexFormat.of().formatHex(digest.digest(bytes));
            return new InspectedFile(bytes, new FileInspectionResult(detected, sha256, bytes.length));
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Não foi possível analisar o arquivo.", exception);
        }
    }

    private void validateSignature(byte[] bytes, String mediaType) {
        boolean valid = switch (mediaType) {
            case "image/jpeg" -> bytes.length >= 3 && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff;
            case "image/png" -> bytes.length >= 8 && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e && bytes[3] == 0x47 && bytes[4] == 0x0d && bytes[5] == 0x0a && bytes[6] == 0x1a && bytes[7] == 0x0a;
            case "application/pdf" -> bytes.length >= 5 && bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46 && bytes[4] == 0x2d;
            default -> false;
        };
        if (!valid) {
            throw new IllegalArgumentException("A assinatura binária não corresponde ao formato detectado.");
        }
    }
}
