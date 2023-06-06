package es.tfg.simuladorteoriacolas.simulation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.tfg.simuladorteoriacolas.folder.Folder;
import es.tfg.simuladorteoriacolas.user.UserEntity;
import jakarta.persistence.*;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.sql.Blob;

@Entity
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idSimulation;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    private String statusSimulation;

    private Integer timeSimulation;

    @Lob
    @JsonIgnore
    private Blob imageFile;

    @JsonIgnore
    private String mimeImage;

    @ManyToOne
    private UserEntity userCreator;

    @ManyToOne
    private Folder folder;

    public Integer getIdSimulation() {
        return idSimulation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatusSimulation() {
        return statusSimulation;
    }

    public void setStatusSimulation(String statusSimulation) {
        this.statusSimulation = statusSimulation;
    }

    public Integer getTimeSimulation() {
        return timeSimulation;
    }

    public void setTimeSimulation(Integer timeSimulation) {
        this.timeSimulation = timeSimulation;
    }

    public void setUserCreator(UserEntity userCreator) {
        this.userCreator = userCreator;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Simulation setImageFile(String path) throws IOException {
        ClassPathResource cpr = new ClassPathResource(path);
        imageFile = BlobProxy.generateProxy(cpr.getInputStream(), cpr.contentLength());
        return this;
    }

    public Simulation setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
        return this;
    }

    public String getMimeImage() {
        return mimeImage;
    }


    public Simulation setMimeImage(String mimeImage) {
        this.mimeImage = mimeImage;
        return this;
    }

    @JsonProperty("imageFile")
    public String imageFile() {
        return "/api/simulation/" + getIdSimulation() + "/image";
    }
}
