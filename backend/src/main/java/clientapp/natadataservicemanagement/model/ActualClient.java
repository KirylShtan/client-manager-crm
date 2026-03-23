package clientapp.natadataservicemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
@Entity
@Table(name = "actual_clients")

public class ActualClient extends Client {

}
