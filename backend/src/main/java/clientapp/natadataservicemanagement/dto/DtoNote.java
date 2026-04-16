package clientapp.natadataservicemanagement.dto;


import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
@Setter
@Getter
public class DtoNote {

    @Nullable
    private String note;
    private Long version;

    public DtoNote() {

    }

    public DtoNote(@Nullable String note) {
        this.note = note;
    }
}
