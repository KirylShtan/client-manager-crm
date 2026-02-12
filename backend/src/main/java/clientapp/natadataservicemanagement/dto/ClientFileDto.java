package clientapp.natadataservicemanagement.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientFileDto {
    private Long id;
    private String originalName;
    private Long size;
    private String contentType;
    private String previewUrl;

}
