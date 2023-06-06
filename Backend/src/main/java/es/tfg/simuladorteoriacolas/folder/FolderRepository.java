package es.tfg.simuladorteoriacolas.folder;

import es.tfg.simuladorteoriacolas.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    Folder findAllByIdFolder(Integer id);
    List<Folder> findAllByUserCreator(User user);
}
