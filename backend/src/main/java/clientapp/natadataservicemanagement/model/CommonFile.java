package clientapp.natadataservicemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "common_files")
public class CommonFile{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false, unique = true)
    private String storedName;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false, unique = true, length = 1000)
    private String path;
    @Column(nullable = false)
    private LocalDateTime uploadedAt;





}