package es.tfg.simuladorteoriacolas.items;

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

    public void deleteAllItemsBySimulation(Simulation simulation){
        List<Item> itemList= itemRepository.findAllByIdSimulation(simulation);
        Item item;
        for (var i=0; i<itemList.size();i++){
            itemTypesService.deleteByItem(itemList.get(i));
            itemList.get(i).setIdSimulation(null);
            itemRepository.delete(itemList.get(i));
        }
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

    public Item save(Integer id, String name,Integer positionX,Integer positionY,String description,Simulation simulation,List<Item> connectedItems, Integer connectedPositionX, Integer connectedPositionY){
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
        item.setConnectedItem(connectedItems);
        item.setConnectedPositionX(connectedPositionX);
        item.setConnectedPositionY(connectedPositionY);
        return itemRepository.save(item);

    }
}