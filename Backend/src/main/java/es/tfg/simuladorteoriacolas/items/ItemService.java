package es.tfg.simuladorteoriacolas.items;

import es.tfg.simuladorteoriacolas.items.connections.ConnectionService;
import es.tfg.simuladorteoriacolas.items.types.ItemTypesService;
import es.tfg.simuladorteoriacolas.simulation.Simulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemTypesService itemTypesService;

    @Autowired
    private ConnectionService connectionService;

    public void deleteAllItemsBySimulation(Simulation simulation){
        List<Item> itemList= itemRepository.findAllByIdSimulation(simulation);
        Item item;
        for (var i=0; i<itemList.size();i++){
            connectionService.deleteAllConnectionsRelatedToAnItem(itemList.get(i));
            itemTypesService.deleteByItem(itemList.get(i));
            itemRepository.deleteById(itemList.get(i).getIdItem());
        }
    }

    public void deleteById(Integer idItem){
        Item item=itemRepository.findById(idItem).orElseThrow();
        itemTypesService.deleteByItem(item);
        itemRepository.deleteById(idItem);
    }

    public Optional<Item> findById(Integer idItem){
        return itemRepository.findById(idItem);
    }

    public Item save(Item item){
        return itemRepository.save(item);
    }

    public List<Item> findAllBySimulation(Simulation simulation){
        return itemRepository.findAllByIdSimulation(simulation);
    }

    public Item save(Integer id, String name,Integer positionX,Integer positionY,String description,Simulation simulation){
        Item item;
        if (id!=null){
            item= findById(id).orElseThrow();
        }
        else {
            item= new Item();
        }
        item.setName(name);
        item.setDescription(description);
        item.setPositionX(positionX);
        item.setPositionY(positionY);
        item.setIdSimulation(simulation);
        return itemRepository.save(item);

    }
}
