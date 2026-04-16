package clientapp.natadataservicemanagement.dto;
public record SendResult(
        boolean success,
        String channel,
        String recipient,
        String message
) {}