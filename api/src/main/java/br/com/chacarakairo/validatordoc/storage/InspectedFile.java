package br.com.chacarakairo.validatordoc.storage;

public record InspectedFile(byte[] bytes, FileInspectionResult inspection) {
}
