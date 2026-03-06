package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.Client;
import clientapp.natadataservicemanagement.model.EmailTemplate;
import clientapp.natadataservicemanagement.model.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
    public EmailTemplate build(NotificationType notificationType, Client client) {
        switch (notificationType) {
            case STATUS_CHANGED:
                return new EmailTemplate(
                        "Case status changed",
                        "Hello, " + client.getFirstName() +
                         "<br><br> Your case status nr. " +
                         client.getCaseNumber() +
                         " has changed.<br><b>" + client.getStatus() + "</b>"
                );
                case CASE_FINISHED:
                    return new EmailTemplate(
                            "Case  finished",
                            "Your case nr. " + client.getCaseNumber() + " is finished."
                    );
            case DOCUMENT_REQUIRED:
                return  new EmailTemplate(
                        "Document/s required",
                        "To prolongue your case nr. " + client.getCaseNumber() +
                        " we need documents , contact your immigration agent please"

                );
                default:
                    return null;
        }

    }
}
