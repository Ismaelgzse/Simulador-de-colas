package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemTypesService {
    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private SinkRepository sinkRepository;

    @Autowired
    private SourceRepository sourceRepository;

    public void deleteByItem(Item item){
        switch (item.getDescription()){
            case "Sink":
                Sink sink= sinkRepository.findByItem(item);
                sink.setItem(null);
                sinkRepository.save(sink);
                sinkRepository.delete(sink);
                break;
            case "Server":
                Server server=serverRepository.findByItem(item);
                server.setItem(null);
                serverRepository.save(server);
                serverRepository.delete(server);
                break;
            case "Source":
                Source source= sourceRepository.findByItem(item);
                source.setItem(null);
                sourceRepository.save(source);
                sourceRepository.delete(source);
                break;
            case "Queue":
                Queue queue=queueRepository.findByItem(item);
                queue.setItem(null);
                queueRepository.save(queue);
                queueRepository.delete(queue);
                break;
        }
    }

    public Queue findQueueByItem(Item item){
        return queueRepository.findByItem(item);
    }

    public Server findServerByItem(Item item){
        return serverRepository.findByItem(item);
    }

    public Sink findSinkByItem(Item item){
        return sinkRepository.findByItem(item);
    }

    public Source findSourceByItem(Item item){
        return sourceRepository.findByItem(item);
    }

    public Queue save(Queue queue){
        return queueRepository.save(queue);
    }

    public Server save(Server server){
        return serverRepository.save(server);
    }

    public Sink save(Sink sink){
        return sinkRepository.save(sink);
    }

    public Source save(Source source){
        return sourceRepository.save(source);
    }

    public Server save(Item item, Integer id, Server serverAux){
        Server server;
        if (id!=null){
            server= serverRepository.findByIdServer(id);
        }
        else {
            server= new Server();
        }
        server.setItem(item);
        server.setOutServer(serverAux.getOutServer());
        server.setCicleTime(serverAux.getCicleTime());
        server.setSetupTime(serverAux.getSetupTime());
        return serverRepository.save(server);
    }

    public Queue save(Item item, Integer id, Queue queueAux){
        Queue queue;
        if (id!=null){
            queue= queueRepository.findByIdQueue(id);
        }
        else {
            queue= new Queue();
        }
        queue.setItem(item);
        queue.setCapacityQueue(queueAux.getCapacityQueue());
        queue.setOutQueue(queueAux.getOutQueue());
        queue.setInQueue(queueAux.getInQueue());
        queue.setDisciplineQueue(queueAux.getDisciplineQueue());
        return queueRepository.save(queue);
    }

    public Sink save(Item item, Integer id, Sink sinkAux){
        Sink sink;
        if (id!=null){
            sink= sinkRepository.findByIdSink(id);
        }
        else {
            sink= new Sink();
        }
        sink.setItem(item);
        sink.setIdSink(sinkAux.getInSink());
        return sinkRepository.save(sink);
    }

    public Source save(Item item, Integer id, Source sourceAux){
        Source source;
        if (id!=null){
            source= sourceRepository.findByIdSource(id);
        }
        else {
            source= new Source();
        }
        source.setItem(item);
        source.setNumberProducts(sourceAux.getNumberProducts());
        source.setInterArrivalTime(sourceAux.getInterArrivalTime());
        source.setOutSource(sourceAux.getOutSource());
        return sourceRepository.save(source);
    }


}
