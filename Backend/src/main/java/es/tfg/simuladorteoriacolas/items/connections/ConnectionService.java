package es.tfg.simuladorteoriacolas.items.connections;

import es.tfg.simuladorteoriacolas.items.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    public Connection save(Integer id, Item originItem,Item destinationItem,Integer percentage){
        Connection connection;
        if (id==null){
            connection= new Connection();
        }
        else {
            connection= connectionRepository.findById(id).orElseThrow();
        }
        connection.setOriginItem(originItem);
        connection.setDestinationItem(destinationItem);
        connection.setPercentage(percentage);
        return connectionRepository.save(connection);
    }

    public Connection findById(Integer id){
        return connectionRepository.findById(id).orElseThrow();
    }

    public void delete(Connection connection){
        connectionRepository.delete(connection);
    }

    public List<Connection> findAllByOriginItem(Item item){
        return connectionRepository.findAllByOriginItem(item);
    }

    public List<Connection> findAllByDestinationItem(Item item){
        return connectionRepository.findAllByDestinationItem(item);
    }


    public void deleteAllConnectionsRelatedToAnItem(Item item){
        List<Connection> connectionListOrigin= findAllByOriginItem(item);
        for (Connection connection:connectionListOrigin) {
            connection.setOriginItem(null);
            connection.setDestinationItem(null);
            var savedConnection=connectionRepository.save(connection);
            connectionRepository.delete(savedConnection);
        }
        List<Connection> connectionListDestination= findAllByDestinationItem(item);
        for (Connection connection:connectionListDestination) {
            connection.setOriginItem(null);
            connection.setDestinationItem(null);
            var savedConnection=connectionRepository.save(connection);
            connectionRepository.delete(savedConnection);
        }
    }

}
