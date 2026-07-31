package br.com.chacarakairo.validatordoc.document;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DocumentRequirements {

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png");
    private static final Set<String> PDF_TYPES = Set.of("application/pdf");
    private static final Set<String> IMAGE_OR_PDF_TYPES = Set.of("image/jpeg", "image/png", "application/pdf");

    private static final Map<DocumentType, List<RequiredDocumentSlot>> REQUIREMENTS = Map.of(
        DocumentType.RG, List.of(
            new RequiredDocumentSlot(DocumentSlot.FRONT, IMAGE_TYPES),
            new RequiredDocumentSlot(DocumentSlot.BACK, IMAGE_TYPES)
        ),
        DocumentType.CIN, List.of(
            new RequiredDocumentSlot(DocumentSlot.FRONT, IMAGE_TYPES),
            new RequiredDocumentSlot(DocumentSlot.BACK, IMAGE_TYPES)
        ),
        DocumentType.CNH, List.of(
            new RequiredDocumentSlot(DocumentSlot.DOCUMENT, IMAGE_OR_PDF_TYPES)
        ),
        DocumentType.COREN_CARD, List.of(
            new RequiredDocumentSlot(DocumentSlot.FRONT, IMAGE_TYPES),
            new RequiredDocumentSlot(DocumentSlot.BACK, IMAGE_TYPES)
        ),
        DocumentType.CRIMINAL_RECORD_CERTIFICATE, List.of(
            new RequiredDocumentSlot(DocumentSlot.DOCUMENT, PDF_TYPES)
        ),
        DocumentType.COURSE_CERTIFICATE, List.of(
            new RequiredDocumentSlot(DocumentSlot.DOCUMENT, IMAGE_OR_PDF_TYPES)
        ),
        DocumentType.RESIDENCE_PROOF, List.of(
            new RequiredDocumentSlot(DocumentSlot.DOCUMENT, IMAGE_OR_PDF_TYPES)
        )
    );

    private DocumentRequirements() {
    }

    public static List<RequiredDocumentSlot> forType(DocumentType type) {
        return REQUIREMENTS.getOrDefault(type, List.of());
    }
}
