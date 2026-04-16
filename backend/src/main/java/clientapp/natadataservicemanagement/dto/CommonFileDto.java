package clientapp.natadataservicemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonFileDto {
    private Long id;
    private String originalName;
    private Long size;
    private String contentType;
    private String previewUrl;


}