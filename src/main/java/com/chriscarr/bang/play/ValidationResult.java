package com.chriscarr.bang.play;

public record ValidationResult(boolean valid, String reason) {
    public static final ValidationResult OK = new ValidationResult(true, null);

    public static ValidationResult fail(String reason) {
        return new ValidationResult(false, reason);
    }
}
