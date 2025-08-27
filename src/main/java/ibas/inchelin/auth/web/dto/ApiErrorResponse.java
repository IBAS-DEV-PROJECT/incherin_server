package ibas.inchelin.auth.web.dto;

public record ApiErrorResponse(
        String error,
        String message
) {}

