package br.com.chacarakairo.validatordoc.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class DocumentFileInspectorTest {

    private final DocumentFileInspector inspector = new DocumentFileInspector();

    @Test
    void acceptsValidPngAndCalculatesSha256() {
        byte[] png = new byte[] {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0x00};
        MockMultipartFile file = new MockMultipartFile("file", "proof.png", "image/png", png);

        InspectedFile result = inspector.inspect(file);

        assertThat(result.inspection().detectedMediaType()).isEqualTo("image/png");
        assertThat(result.inspection().sha256()).hasSize(64);
    }

    @Test
    void rejectsExecutableDisguisedAsPdf() {
        MockMultipartFile file = new MockMultipartFile("file", "proof.pdf", "application/pdf", new byte[] {0x4d, 0x5a, 0x01});

        assertThatThrownBy(() -> inspector.inspect(file))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
