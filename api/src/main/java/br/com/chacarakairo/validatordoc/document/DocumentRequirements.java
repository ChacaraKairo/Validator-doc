package br.com.chacarakairo.validatordoc.document;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DocumentRequirements {

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png");
    private static final Set<String> PDF_TYPES = Set.of("application/pdf");
    private static final Set<String> IMAGE_OR_PDF_TYPES = Set.of("image/jpeg", "image/png", "application/pdf");

    private static final Map<DocumentType, Map<DocumentSlot, Set<String>>> ALLOWED = Map.of(
        DocumentType.RG, Map.of(DocumentSlot.FRONT, IMAGE_TYPES, DocumentSlot.BACK, IMAGE_TYPES),
        DocumentType.CIN, Map.of(DocumentSlot.FRONT, IMAGE_TYPES, DocumentSlot.BACK, IMAGE_TYPES),
        DocumentType.CNH, Map.of(
            DocumentSlot.FRONT, IMAGE_TYPES,
            DocumentSlot.BACK, IMAGE_TYPES,
            DocumentSlot.DOCUMENT, PDF_TYPES
        ),
        DocumentType.COREN_CARD, Map.of(DocumentSlot.FRONT, IMAGE_TYPES, DocumentSlot.BACK, IMAGE_TYPES),
        DocumentType.CRIMINAL_RECORD_CERTIFICATE, Map.of(DocumentSlot.DOCUMENT, PDF_TYPES),
        DocumentType.COURSE_CERTIFICATE, Map.of(DocumentSlot.DOCUMENT, IMAGE_OR_PDF_TYPES),
        DocumentType.RESIDENCE_PROOF, Map.of(DocumentSlot.DOCUMENT, IMAGE_OR_PDF_TYPES)
    );

    private DocumentRequirements() {
    }

    public static List<RequiredDocumentSlot> forType(DocumentType type) {
        return ALLOWED.getOrDefault(type, Map.of()).entrySet().stream()
            .map(entry -> new RequiredDocumentSlot(entry.getKey(), entry.getValue()))
            .toList();
    }

    public static Set<String> acceptedMediaTypes(DocumentType type, DocumentSlot slot) {
        return ALLOWED.getOrDefault(type, Map.of()).getOrDefault(slot, Set.of());
    }

    public static boolean isComplete(DocumentType type, Set<DocumentSlot> uploadedSlots) {
        return switch (type) {
            case RG, CIN, COREN_CARD -> uploadedSlots.containsAll(Set.of(DocumentSlot.FRONT, DocumentSlot.BACK));
            case CNH -> uploadedSlots.contains(DocumentSlot.DOCUMENT)
                || uploadedSlots.containsAll(Set.of(DocumentSlot.FRONT, DocumentSlot.BACK));
            case CRIMINAL_RECORD_CERTIFICATE, COURSE_CERTIFICATE, RESIDENCE_PROOF ->
                uploadedSlots.contains(DocumentSlot.DOCUMENT);
        };
    }
}
