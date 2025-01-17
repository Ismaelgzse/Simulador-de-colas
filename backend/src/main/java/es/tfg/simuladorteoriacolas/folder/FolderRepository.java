package es.tfg.simuladorteoriacolas.folder;

import es.tfg.simuladorteoriacolas.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Integer> {

    Folder findByIdFolder(Integer id);

    List<Folder> findAllByUserCreator(UserEntity user);

    Folder findByNameFolder(String name);
}
