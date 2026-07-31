package br.com.chacarakairo.validatordoc.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class DocumentSanitizerTest {

    private final DocumentSanitizer sanitizer = new DocumentSanitizer();

    @Test
    void shouldDecodeAndReencodePng() throws Exception {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream source = new ByteArrayOutputStream();
        ImageIO.write(image, "png", source);

        SanitizedFile sanitized = sanitizer.sanitize(source.toByteArray(), "image/png");

        assertThat(sanitized.mediaType()).isEqualTo("image/png");
        assertThat(ImageIO.read(new ByteArrayInputStream(sanitized.content()))).isNotNull();
    }
}
