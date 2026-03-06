package clientapp.natadataservicemanagement.model;

import lombok.Getter;

@Getter
public enum NotificationType {
    STATUS_CHANGED(true),
    CASE_FINISHED(true),
    DOCUMENT_REQUIRED(true);

    private final boolean automatic;

    NotificationType(boolean automatic){
        this.automatic = automatic;
    }

}
