package br.com.chacarakairo.validatordoc.security;

public record SanitizedFile(byte[] content, String mediaType) {
}
