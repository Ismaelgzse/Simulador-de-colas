package es.tfg.simuladorteoriacolas.simulation.algorithm;

import es.tfg.simuladorteoriacolas.items.Item;
import es.tfg.simuladorteoriacolas.items.ItemDTO;
import es.tfg.simuladorteoriacolas.items.Semaphores.SemaphoreAsignation;
import es.tfg.simuladorteoriacolas.items.Semaphores.SemaphoresTypes;
import es.tfg.simuladorteoriacolas.items.Semaphores.SmallestQueueDecision;
import es.tfg.simuladorteoriacolas.items.connections.Connection;
import es.tfg.simuladorteoriacolas.items.products.Product;
import es.tfg.simuladorteoriacolas.items.types.Queue;
import es.tfg.simuladorteoriacolas.items.types.Server;
import es.tfg.simuladorteoriacolas.items.types.Sink;
import es.tfg.simuladorteoriacolas.items.types.Source;
import es.tfg.simuladorteoriacolas.simulation.Simulation;
import org.apache.commons.math3.distribution.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

public class Algorithm implements Runnable{

    private List<ItemDTO> simulation;

    public Algorithm(List<ItemDTO> simulation){
        this.simulation=simulation;
    }

    //ITEMS
    //Semaforos IN OUT para controlar entradas y salidas de los items
    //Estos semaforos conectan los dos hilos que conforman el item
    //Tienen 0 permisos, el semaforo In otorga al OUT los permisos
    //Semaforo OUT del item sirve para enviar el objeto al siguiente item
    //Tendrá que haber un OUT por cada posible conexión que tenga la salida de ese item
    //Tendrá 0 permisos este semaforo ya que los otorga la salida de este indicando que va a salir un objeto

    Double startSimulation;

    Random random = new Random();


    @Override
    public void run() {

        List<ItemDTO> simulation = new ArrayList<>();


        Item item1 = new Item();
        item1.setDescription("Source");
        item1.setIdItem(1);
        item1.setSendToStrategy("A la cola más pequeña (si está llena espera hasta que haya hueco)");

        Source source1 = new Source();
        source1.setNumberProducts("Ilimitados");
        source1.setInterArrivalTime("7");
        source1.setItem(item1);

        Item item2 = new Item();
        item2.setDescription("Queue");
        item2.setIdItem(2);
        item2.setSendToStrategy("Primera conexión disponible");

        Queue queue2 = new Queue();
        queue2.setItem(item2);
        queue2.setCapacityQueue("10000000");
        queue2.setDisciplineQueue("FIFO");

        Item item3 = new Item();
        item3.setDescription("Queue");
        item3.setIdItem(3);
        item3.setSendToStrategy("Primera conexión disponible");

        Queue queue3 = new Queue();
        queue3.setItem(item3);
        queue3.setCapacityQueue("Ilimitados");
        queue3.setDisciplineQueue("FIFO");


        Connection connection1 = new Connection();
        connection1.setOriginItem(item1);
        connection1.setDestinationItem(item2);
        connection1.setPercentage(50);

        Connection connection2 = new Connection();
        connection2.setOriginItem(item1);
        connection2.setDestinationItem(item3);
        connection2.setPercentage(50);


        ArrayList<Connection> connections1 = new ArrayList();
        connections1.add(connection1);
        connections1.add(connection2);

        ItemDTO itemDTO1 = new ItemDTO();
        itemDTO1.setItem(item1);
        itemDTO1.setSource(source1);
        itemDTO1.setConnections(connections1);


        Item item4 = new Item();
        item4.setDescription("Server");
        item4.setIdItem(4);
        item4.setSendToStrategy("A la cola más pequeña (si está llena espera hasta que haya hueco)");

        Server server4 = new Server();
        server4.setItem(item4);
        server4.setSetupTime("10");
        server4.setCicleTime("10");

        Connection connection3 = new Connection();
        connection3.setOriginItem(item2);
        connection3.setDestinationItem(item4);
        connection3.setPercentage(100);

        ArrayList<Connection> connections2 = new ArrayList();
        connections2.add(connection3);

        Connection connection4 = new Connection();
        connection4.setOriginItem(item3);
        connection4.setDestinationItem(item4);
        connection4.setPercentage(100);

        ArrayList<Connection> connections3 = new ArrayList();
        connections3.add(connection4);

        ItemDTO itemDTO2 = new ItemDTO();
        itemDTO2.setItem(item2);
        itemDTO2.setQueue(queue2);
        itemDTO2.setConnections(connections2);

        ItemDTO itemDTO3 = new ItemDTO();
        itemDTO3.setItem(item3);
        itemDTO3.setQueue(queue3);
        itemDTO3.setConnections(connections3);

        Item item5 = new Item();
        item5.setDescription("Sink");
        item5.setIdItem(5);
        item5.setSendToStrategy("Aleatorio");

        Sink sink5 = new Sink();
        sink5.setItem(item5);

        Connection connection5 = new Connection();
        connection5.setOriginItem(item4);
        connection5.setDestinationItem(item5);
        connection5.setPercentage(100);

        ArrayList<Connection> connections4 = new ArrayList();
        connections4.add(connection5);

        ItemDTO itemDTO4 = new ItemDTO();
        itemDTO4.setItem(item4);
        itemDTO4.setServer(server4);
        itemDTO4.setConnections(connections4);

        ItemDTO itemDTO5 = new ItemDTO();
        itemDTO5.setItem(item5);
        itemDTO5.setSink(sink5);
        itemDTO5.setConnections(null);

        simulation.add(itemDTO1);
        simulation.add(itemDTO2);
        simulation.add(itemDTO3);
        simulation.add(itemDTO4);
        simulation.add(itemDTO5);

        List<SemaphoreAsignation> semaphoreAsignationList = new ArrayList<>();

        for (ItemDTO itemDTO : simulation) {
            List<Semaphore> semaphoreList = new ArrayList<>();
            SemaphoreAsignation semaphoreAsignation = new SemaphoreAsignation();
            semaphoreAsignation.setIdOriginItem(itemDTO.getItem().getIdItem());
            if (itemDTO.getItem().getDescription().equals("Source")) {
                Semaphore semaphoreInOut = new Semaphore(0, true);
                SemaphoresTypes semaphoresTypes = new SemaphoresTypes();
                semaphoreList.add(semaphoreInOut);
                semaphoresTypes.setType("InOut");
                semaphoresTypes.setSemaphores(semaphoreList);
                List<SemaphoresTypes> semaphoresTypesList = new ArrayList<>();
                semaphoresTypesList.add(semaphoresTypes);

                semaphoreList = new ArrayList<>();
                Semaphore controlSemaphore = new Semaphore(1, true);
                semaphoresTypes = new SemaphoresTypes();
                semaphoreList.add(controlSemaphore);
                semaphoresTypes.setType("Control");
                semaphoresTypes.setSemaphores(semaphoreList);
                semaphoresTypesList.add(semaphoresTypes);

                semaphoreAsignation.setSemaphoresTypes(semaphoresTypesList);
                semaphoreAsignationList.add(semaphoreAsignation);
            }
            if (itemDTO.getItem().getDescription().equals("Queue")) {
                List<SemaphoresTypes> semaphoresTypesList = new ArrayList<>();
                Semaphore semaphoreInOut = new Semaphore(0, true);
                SemaphoresTypes semaphoresTypes = new SemaphoresTypes();
                semaphoreList.add(semaphoreInOut);
                semaphoresTypes.setType("InOut");
                semaphoresTypes.setSemaphores(semaphoreList);
                semaphoresTypesList.add(semaphoresTypes);

                semaphoresTypes = new SemaphoresTypes();
                Integer capacity;
                if (itemDTO.getQueue().getCapacityQueue().equals("Ilimitados")) {
                    capacity = (int) Double.POSITIVE_INFINITY;
                } else {
                    capacity = Integer.valueOf(itemDTO.getQueue().getCapacityQueue());
                }
                Semaphore semaphoreCapacityQueue = new Semaphore(capacity, true);
                semaphoreList = new ArrayList<>();
                semaphoreList.add(semaphoreCapacityQueue);
                semaphoresTypes.setType("Capacity");
                semaphoresTypes.setSemaphores(semaphoreList);
                semaphoresTypesList.add(semaphoresTypes);

                semaphoresTypes = new SemaphoresTypes();
                Semaphore productsSemaphore = new Semaphore(1, true);
                semaphoreList = new ArrayList<>();
                semaphoreList.add(productsSemaphore);
                semaphoresTypes.setType("Control");
                semaphoresTypes.setSemaphores(semaphoreList);
                semaphoresTypesList.add(semaphoresTypes);

                semaphoreAsignation.setSemaphoresTypes(semaphoresTypesList);

                semaphoreAsignationList.add(semaphoreAsignation);
            }
            if (itemDTO.getItem().getDescription().equals("Server")) {
                List<SemaphoresTypes> semaphoresTypesList = new ArrayList<>();
                Semaphore productsSemaphore = new Semaphore(1, true);
                SemaphoresTypes semaphoresTypes = new SemaphoresTypes();
                semaphoreList.add(productsSemaphore);
                semaphoresTypes.setType("Control");
                semaphoresTypes.setSemaphores(semaphoreList);
                semaphoresTypesList.add(semaphoresTypes);

                semaphoreAsignation.setSemaphoresTypes(semaphoresTypesList);
                semaphoreAsignationList.add(semaphoreAsignation);
            }
            if (itemDTO.getItem().getDescription().equals("Sink")) {
                List<SemaphoresTypes> semaphoresTypesList = new ArrayList<>();
                Semaphore productsSemaphore = new Semaphore(1, true);
                SemaphoresTypes semaphoresTypes = new SemaphoresTypes();
                semaphoreList.add(productsSemaphore);
                semaphoresTypes.setType("Control");
                semaphoresTypes.setSemaphores(semaphoreList);
                semaphoresTypesList.add(semaphoresTypes);

                semaphoreAsignation.setSemaphoresTypes(semaphoresTypesList);
                semaphoreAsignationList.add(semaphoreAsignation);
            }
        }


        for (ItemDTO itemDTO : simulation) {
            List<Connection> connectionList = itemDTO.getConnections();
            if (connectionList != null) {
                for (Connection connection : connectionList) {
                    //Checks if the connection share de destination item with other origin item
                    var diferentsOrigins = moreThanOneConnection(connection.getDestinationItem().getIdItem(), simulation);

                    SemaphoreAsignation semaphoreAsignation;

                    //Load the Semaphore asignation if exists, if not creates a new one (this shouldn't happend)
                    SemaphoreAsignation semaphoreAsignationAux = semaphoreAsignationAlreadyExist(connection.getOriginItem().getIdItem(), semaphoreAsignationList);
                    if (semaphoreAsignationAux != null) {
                        semaphoreAsignation = semaphoreAsignationAux;
                    } else {
                        semaphoreAsignation = new SemaphoreAsignation();
                        semaphoreAsignation.setIdOriginItem(connection.getOriginItem().getIdItem());
                    }

                    //In case the user selects an especific option of the send to strategy, we will need to store an additional object
                    //This object shall store the index of the object, in order to check the number of products within the queue, in order to decide which queue has the lest number of product within it
                    if ((itemDTO.getItem().getDescription().equals("Source") || itemDTO.getItem().getDescription().equals("Server")) && itemDTO.getConnections() != null && itemDTO.getConnections().size() > 1) {
                        var identifier = getIdentifierFromID(connection.getDestinationItem().getIdItem(), simulation);
                        SmallestQueueDecision smallestQueueDecision = new SmallestQueueDecision();
                        smallestQueueDecision.setIdentifier(identifier);
                        smallestQueueDecision.setTypeItem(simulation.get(identifier).getItem().getDescription());
                        Semaphore semaphore = getSemaphore(semaphoreAsignationList.get(identifier), "Control").getSemaphores().get(0);
                        smallestQueueDecision.setControlDestinationSemaphore(semaphore);
                        List<SmallestQueueDecision> smallestQueueDecisions = semaphoreAsignation.getSmallestQueueDecisions();
                        if (smallestQueueDecisions == null) {
                            smallestQueueDecisions = new ArrayList<>();
                        }
                        smallestQueueDecisions.add(smallestQueueDecision);
                        semaphoreAsignation.setSmallestQueueDecisions(smallestQueueDecisions);
                    }

                    if (!connectionAlreadyMade(diferentsOrigins, connection.getDestinationItem().getIdItem(), semaphoreAsignationList)) {
                        SemaphoresTypes newSemaphoresType = null;
                        SemaphoresTypes newSemaphoresType2 = null;
                        for (SemaphoresTypes semaphoresType : semaphoreAsignation.getSemaphoresTypes()) {
                            if (semaphoresType.getType().equals("Out")) {
                                newSemaphoresType = semaphoresType;
                            }
                            if (semaphoresType.getType().equals("AccessIn")) {
                                newSemaphoresType2 = semaphoresType;
                            }
                        }
                        //New
                        List<Semaphore> semaphores = null;
                        if (newSemaphoresType != null) {
                            semaphores = newSemaphoresType.getSemaphores();
                        }
                        List<Integer> destinationList;
                        List<Semaphore> semaphores2;
                        List<Integer> destinationList2;
                        List<Exchanger> exchangers;
                        if (semaphores == null) {
                            List<SemaphoresTypes> types = semaphoreAsignation.getSemaphoresTypes();

                            newSemaphoresType = new SemaphoresTypes();
                            newSemaphoresType.setType("Out");

                            newSemaphoresType2 = new SemaphoresTypes();
                            newSemaphoresType2.setType("AccessIn");

                            semaphores = new ArrayList<>();
                            destinationList = new ArrayList<>();
                            exchangers= new ArrayList<>();

                            semaphores2 = new ArrayList<>();
                            destinationList2 = new ArrayList<>();
                            types.add(newSemaphoresType);
                            types.add(newSemaphoresType2);

                        } else {
                            destinationList = newSemaphoresType.getIdDestinationItem();
                            exchangers=newSemaphoresType.getExchangers();

                            semaphores2 = newSemaphoresType2.getSemaphores();
                            destinationList2 = newSemaphoresType2.getIdDestinationItem();
                        }

                        Semaphore semaphoreOut = new Semaphore(0, true);
                        Semaphore accessInSemaphore = new Semaphore(0, true);
                        Exchanger<Product> echanger= new Exchanger<>();

                        semaphores.add(semaphoreOut);
                        exchangers.add(echanger);
                        destinationList.add(connection.getDestinationItem().getIdItem());
                        newSemaphoresType.setSemaphores(semaphores);
                        newSemaphoresType.setExchangers(exchangers);
                        newSemaphoresType.setIdDestinationItem(destinationList);


                        semaphores2.add(accessInSemaphore);
                        destinationList2.add(connection.getDestinationItem().getIdItem());
                        newSemaphoresType2.setSemaphores(semaphores2);
                        newSemaphoresType2.setIdDestinationItem(destinationList2);

                        //In de destino de item
                        newSemaphoresType = new SemaphoresTypes();
                        newSemaphoresType.setType("In");
                        exchangers= new ArrayList<>();
                        semaphores = new ArrayList<>();
                        semaphores.add(semaphoreOut);
                        exchangers.add(echanger);
                        newSemaphoresType.setSemaphores(semaphores);
                        newSemaphoresType.setExchangers(exchangers);
                        var semaphoreAsignationDest = semaphoreAsignationAlreadyExist(connection.getDestinationItem().getIdItem(), semaphoreAsignationList);
                        var listTypes = semaphoreAsignationDest.getSemaphoresTypes();
                        listTypes.add(newSemaphoresType);
                        semaphoreAsignationDest.setSemaphoresTypes(listTypes);

                        newSemaphoresType2 = new SemaphoresTypes();
                        newSemaphoresType2.setType("AccessOut");
                        semaphores2 = new ArrayList<>();
                        semaphores2.add(accessInSemaphore);
                        newSemaphoresType2.setSemaphores(semaphores2);
                        listTypes.add(newSemaphoresType2);
                        semaphoreAsignationDest.setSemaphoresTypes(listTypes);

                        for (Integer id : diferentsOrigins) {
                            if (!id.equals(connection.getOriginItem().getIdItem())) {
                                semaphoreAsignation = semaphoreAsignationAlreadyExist(id, semaphoreAsignationList);
                                newSemaphoresType = new SemaphoresTypes();
                                newSemaphoresType.setType("Out");
                                exchangers= new ArrayList<>();
                                semaphores = new ArrayList<>();
                                destinationList = new ArrayList<>();
                                destinationList.add(connection.getDestinationItem().getIdItem());
                                exchangers.add(echanger);
                                semaphores.add(semaphoreOut);
                                newSemaphoresType.setExchangers(exchangers);
                                newSemaphoresType.setSemaphores(semaphores);
                                newSemaphoresType.setIdDestinationItem(destinationList);
                                listTypes = semaphoreAsignation.getSemaphoresTypes();
                                listTypes.add(newSemaphoresType);
                                semaphoreAsignation.setSemaphoresTypes(listTypes);

                                newSemaphoresType2 = new SemaphoresTypes();
                                newSemaphoresType2.setType("AccessIn");
                                semaphores2 = new ArrayList<>();
                                destinationList2 = new ArrayList<>();
                                destinationList2.add(connection.getDestinationItem().getIdItem());
                                semaphores2.add(accessInSemaphore);
                                newSemaphoresType2.setSemaphores(semaphores2);
                                newSemaphoresType2.setIdDestinationItem(destinationList2);
                                listTypes = semaphoreAsignation.getSemaphoresTypes();
                                listTypes.add(newSemaphoresType2);
                                semaphoreAsignation.setSemaphoresTypes(listTypes);

                            }
                        }
                    }
                }
            }
        }

        List<Thread> itemList = new ArrayList<>();
        for (var i = 0; i < semaphoreAsignationList.size(); i++) {
            var item = simulation.get(i);
            var semaphoresItem = semaphoreAsignationList.get(i);
            switch (item.getItem().getDescription()) {
                case "Source":
                    int finalI = i;
                    var fuente = new Thread(() -> {
                        ArrayList<Integer> productList= new ArrayList<>();
                        var sourceIn = new Thread(() -> {
                            Double sleep;
                            TriangularDistribution triangularDistribution = null;
                            LogNormalDistribution logNormalDistribution = null;
                            BinomialDistribution binomialDistribution = null;
                            Integer max = null;
                            NormalDistribution normalDistribution = null;
                            LogisticDistribution logisticDistribution = null;
                            BetaDistribution betaDistribution = null;
                            GammaDistribution gammaDistribution = null;
                            UniformIntegerDistribution uniformIntegerDistribution = null;
                            WeibullDistribution weibullDistribution = null;

                            var strategyTime = getTimeStrategy(item.getSource().getInterArrivalTime());
                            switch ((String) strategyTime.keySet().toArray()[0]) {
                                case "Triangular":
                                    var numbers = strategyTime.get("Triangular");
                                    triangularDistribution = new TriangularDistribution(numbers.get(0), numbers.get(2), numbers.get(1));
                                    break;
                                case "LogNormal":
                                    numbers = strategyTime.get("LogNormal");
                                    logNormalDistribution = new LogNormalDistribution(numbers.get(0), numbers.get(1));
                                    break;
                                case "Binomial":
                                    numbers = strategyTime.get("Binomial");
                                    binomialDistribution = new BinomialDistribution(numbers.get(0), numbers.get(1));
                                    break;
                                case "Max":
                                    max = strategyTime.get("Max").get(0);
                                    if (strategyTime.keySet().toArray()[1].equals("Normal")) {
                                        numbers = strategyTime.get("Normal");
                                        normalDistribution = new NormalDistribution(numbers.get(0), numbers.get(1));
                                    } else {
                                        numbers = strategyTime.get("Logistic");
                                        logisticDistribution = new LogisticDistribution(numbers.get(0), numbers.get(1));
                                    }
                                    break;
                                case "Beta":
                                    numbers = strategyTime.get("Beta");
                                    max = numbers.get(2);
                                    betaDistribution = new BetaDistribution(numbers.get(0), numbers.get(1));
                                    break;
                                case "Gamma":
                                    numbers = strategyTime.get("Gamma");
                                    gammaDistribution = new GammaDistribution(numbers.get(0), numbers.get(1));
                                    break;
                                case "Uniform":
                                    numbers = strategyTime.get("Uniform");
                                    uniformIntegerDistribution = new UniformIntegerDistribution(numbers.get(0), numbers.get(1));
                                    break;
                                case "Weibull":
                                    numbers = strategyTime.get("Weibull");
                                    weibullDistribution = new WeibullDistribution(numbers.get(0), numbers.get(1));
                                    break;
                                case "mins":
                                    max = strategyTime.get("mins").get(0);
                                    break;
                                case "hr":
                                    max = strategyTime.get("hr").get(0);
                                    break;
                                case "":
                                    max = strategyTime.get("").get(0);
                                    break;
                            }
                            var inOutSemaphoreType = getSemaphore(semaphoresItem, "InOut");
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            Semaphore inOutSemaphore = null;
                            Semaphore controlSemaphore = null;
                            if (inOutSemaphoreType != null) {
                                inOutSemaphore = inOutSemaphoreType.getSemaphores().get(0);
                                controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                            }
                            var isInfinite= item.getSource().getNumberProducts().equals("Ilimitados");
                            var numberProducts = isInfinite ? Double.POSITIVE_INFINITY : Double.parseDouble(item.getSource().getNumberProducts());
                            try {
                                while (isInfinite || numberProducts > 0) {
                                    if (!isInfinite){
                                        numberProducts--;
                                    }
                                    switch ((String) strategyTime.keySet().toArray()[0]) {
                                        case "Triangular":
                                            sleep = triangularDistribution.sample();
                                            break;
                                        case "LogNormal":
                                            sleep = logNormalDistribution.sample();
                                            break;
                                        case "Binomial":
                                            sleep = (double) binomialDistribution.sample();
                                            break;
                                        case "Max":
                                            if (strategyTime.keySet().toArray()[1].equals("Normal")) {
                                                sleep = normalDistribution.sample();
                                                if (sleep < max) {
                                                    sleep = (double) max;
                                                }
                                            } else {
                                                sleep = logisticDistribution.sample();
                                                if (sleep < max) {
                                                    sleep = (double) max;
                                                }
                                            }
                                            break;
                                        case "Beta":
                                            sleep = betaDistribution.sample();
                                            sleep = sleep * max;
                                            break;
                                        case "Gamma":
                                            sleep = gammaDistribution.sample();
                                            break;
                                        case "Uniform":
                                            sleep = (double) uniformIntegerDistribution.sample();
                                            break;
                                        case "Weibull":
                                            sleep = weibullDistribution.sample();
                                            break;
                                        case "mins":
                                            sleep = max * 60.0 * 1000.0;
                                            break;
                                        case "hr":
                                            sleep = max * 60.0 * 60.0 * 1000.0;
                                            break;
                                        case "":
                                            sleep = (double) max * 1000.0;
                                            break;
                                        default:
                                            sleep = 0.0;
                                    }
                                    Thread.sleep(sleep.longValue());
                                    inOutSemaphore.release();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });

                        var sourceOut = new Thread(() -> {
                            var sendToStrategy = item.getItem().getSendToStrategy();
                            var percentages = getPercentages(item);
                            List<Semaphore> outSemaphores = null;
                            List<Semaphore> accessInSemaphores = null;
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            var outSemaphoresType = getSemaphore(semaphoresItem, "Out");
                            var accessInSemaphoresType = getSemaphore(semaphoresItem, "AccessIn");
                            var outExchangers= getExchangers(semaphoresItem,"Out");

                            var queues = semaphoresItem.getSmallestQueueDecisions();
                            Semaphore controlSemaphore = null;

                            if (outSemaphoresType != null) {
                                outSemaphores = outSemaphoresType.getSemaphores();
                                accessInSemaphores = accessInSemaphoresType.getSemaphores();
                                controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                            }
                            var inOutSemaphoreType = getSemaphore(semaphoresItem, "InOut");
                            Semaphore inOutSemaphore = null;
                            if (inOutSemaphoreType != null) {
                                inOutSemaphore = inOutSemaphoreType.getSemaphores().get(0);
                            }
                            try {
                                var numProducts = 0;
                                while (true) {
                                    Integer sendTo;
                                    inOutSemaphore.acquire();
                                    Product product= new Product("Standar",null,null);
                                    switch (sendToStrategy) {
                                        case "Aleatorio (lo manda independientemente de si hay hueco o no)":
                                            sendTo = random.nextInt(outSemaphores.size());
                                            if (!accessInSemaphores.get(sendTo).tryAcquire()) {
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                                controlSemaphore.release();
                                            } else {
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                                controlSemaphore.release();
                                                outExchangers.get(sendTo).exchange(product);
                                                outSemaphores.get(sendTo).release();
                                            }
                                            System.out.println("Enviado por la fuente " + finalI);
                                            break;
                                        case "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                            sendTo = random.nextInt(outSemaphores.size());
                                            accessInSemaphores.get(sendTo).acquire();
                                            controlSemaphore.acquire();
                                            numProducts++;
                                            item.getSource().setOutSource(numProducts);
                                            System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(product);
                                            outSemaphores.get(sendTo).release();
                                            System.out.println("Enviado por la fuente " + finalI);
                                            break;
                                        case "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)":
                                            sendTo = 0;
                                            while (!accessInSemaphores.get(sendTo).tryAcquire()) {
                                                if (outSemaphores.size() > (sendTo + 1)) {
                                                    sendTo++;
                                                } else {
                                                    sendTo = 0;
                                                }
                                            }
                                            controlSemaphore.acquire();
                                            numProducts++;
                                            item.getSource().setOutSource(numProducts);
                                            System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(product);
                                            outSemaphores.get(sendTo).release();
                                            System.out.println("Enviado por la fuente " + finalI);
                                            break;
                                        case "Porcentaje (si no hay hueco se envia aunque se pierda)":
                                            var randomNumber = random.nextInt(100) + 1;
                                            sendTo = 0;
                                            for (var index = 0; index < percentages.size(); index++) {
                                                if (randomNumber < percentages.get(index) && index == 0) {
                                                    sendTo = index;
                                                    break;
                                                } else if (randomNumber < percentages.get(index) && randomNumber > percentages.get(index - 1)) {
                                                    sendTo = index;
                                                    break;
                                                }
                                            }
                                            try {
                                                if (!accessInSemaphores.get(sendTo).tryAcquire()) {
                                                    controlSemaphore.acquire();
                                                    numProducts++;
                                                    item.getSource().setOutSource(numProducts);
                                                    System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                                    controlSemaphore.release();
                                                } else {
                                                    controlSemaphore.acquire();
                                                    numProducts++;
                                                    item.getSource().setOutSource(numProducts);
                                                    System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                                    controlSemaphore.release();
                                                    outExchangers.get(sendTo).exchange(product);
                                                    outSemaphores.get(sendTo).release();
                                                }
                                                System.out.println("Enviado por la fuente " + finalI);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                            randomNumber = random.nextInt(100) + 1;
                                            sendTo = 0;
                                            for (var index = 0; index < percentages.size(); index++) {
                                                if (randomNumber < percentages.get(index) && index == 0) {
                                                    sendTo = index;
                                                    break;
                                                } else if (randomNumber < percentages.get(index) && randomNumber > percentages.get(index - 1)) {
                                                    sendTo = index;
                                                    break;
                                                }
                                            }
                                            try {
                                                accessInSemaphores.get(sendTo).acquire();
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                                controlSemaphore.release();
                                                outExchangers.get(sendTo).exchange(product);
                                                outSemaphores.get(sendTo).release();
                                                System.out.println("Enviado por la fuente " + finalI);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "A la cola más pequeña (si está llena espera hasta que haya hueco)":
                                            try {
                                                var products = Double.POSITIVE_INFINITY;
                                                var queueProducts = 0;
                                                var smallestQueue = 0;
                                                for (var index = 0; index < queues.size(); index++) {
                                                    if (queues.get(index).getTypeItem().equals("Queue")) {
                                                        queues.get(index).getControlDestinationSemaphore().acquire();
                                                        queueProducts = simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue() == null ? 0 : simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue();
                                                        queues.get(index).getControlDestinationSemaphore().release();
                                                        if (queueProducts < products) {
                                                            products = queueProducts;
                                                            smallestQueue = index;
                                                        }
                                                    }
                                                }
                                                accessInSemaphores.get(smallestQueue).acquire();
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                System.out.println("Producto generado por fuente " + finalI + ", producido: " + numProducts);
                                                controlSemaphore.release();
                                                outExchangers.get(smallestQueue).exchange(product);
                                                outSemaphores.get(smallestQueue).release();
                                                System.out.println("Enviado por la fuente " + finalI);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        sourceIn.start();
                        sourceOut.start();
                    });
                    itemList.add(fuente);
                    break;
                case "Queue":
                    finalI = i;
                    var cola = new Thread(() -> {
                        List<Product> productList= new ArrayList<>();
                        var colaIn = new Thread(() -> {
                            var total = 0;
                            var inSemaphoreType = getSemaphore(semaphoresItem, "In");
                            var accessOutType = getSemaphore(semaphoresItem, "AccessOut");
                            var capacitySemaphoreType = getSemaphore(semaphoresItem, "Capacity");
                            var inOutSemaphoreType = getSemaphore(semaphoresItem, "InOut");
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            var inExchanger= getExchangers(semaphoresItem,"In").get(0);
                            Product inProduct;


                            Semaphore inOutSemaphore = null;
                            Semaphore inSemaphore = null;
                            Semaphore accessOutSemaphore = null;
                            Semaphore capacitySemaphore = null;
                            Semaphore controlSemaphore = null;

                            if (inOutSemaphoreType != null) {
                                inOutSemaphore = inOutSemaphoreType.getSemaphores().get(0);
                                accessOutSemaphore = accessOutType.getSemaphores().get(0);
                                inSemaphore = inSemaphoreType.getSemaphores().get(0);
                                capacitySemaphore = capacitySemaphoreType.getSemaphores().get(0);
                                controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                            }

                            while (true) {
                                try {
                                    if (capacitySemaphore.availablePermits() > 0) {
                                        accessOutSemaphore.release();
                                        inProduct= (Product) inExchanger.exchange(null);
                                        inProduct.setArrivalTime((double) System.currentTimeMillis());
                                        inSemaphore.acquire();
                                        capacitySemaphore.acquire();
                                        controlSemaphore.acquire();
                                        productList.add(inProduct);
                                        total = item.getQueue().getInQueue() == null ? 0 : item.getQueue().getInQueue();
                                        total++;
                                        item.getQueue().setInQueue(total);
                                        System.out.println(total + " productos en la cola " + finalI);
                                        controlSemaphore.release();
                                        inOutSemaphore.release();
                                        if (item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                            capacitySemaphore.release();
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        var colaOut = new Thread(() -> {
                            var sendToStrategy = item.getItem().getSendToStrategy();

                            var inOutSemaphoreType = getSemaphore(semaphoresItem, "InOut");
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            var outSemaphoreType = getSemaphore(semaphoresItem, "Out");
                            var accessInSemaphoreType = getSemaphore(semaphoresItem, "AccessIn");
                            var capacitySemaphoreType = getSemaphore(semaphoresItem, "Capacity");
                            var outExchangers= getExchangers(semaphoresItem,"Out");
                            Product outProduct;

                            Semaphore inOutSemaphore = null;
                            Semaphore capacitySemaphore = null;

                            List<Semaphore> outSemaphore = null;
                            List<Semaphore> accessInSemaphore = null;
                            Semaphore controlSemaphore = null;

                            if (inOutSemaphoreType != null) {
                                inOutSemaphore = inOutSemaphoreType.getSemaphores().get(0);
                                controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                                capacitySemaphore = capacitySemaphoreType.getSemaphores().get(0);
                                outSemaphore = outSemaphoreType.getSemaphores();
                                accessInSemaphore = accessInSemaphoreType.getSemaphores();
                            }

                            var percentages = getPercentages(item);
                            var totalIn = 0;
                            var totalOut = 0;
                            while (true) {

                                switch (sendToStrategy) {
                                    case "Aleatorio":
                                        var sendTo = random.nextInt(outSemaphore.size());
                                        try {
                                            inOutSemaphore.acquire();
                                            accessInSemaphore.get(sendTo).acquire();
                                            controlSemaphore.acquire();
                                            outProduct= item.getQueue().getDisciplineQueue().equals("FIFO")  ? productList.remove(productList.size()-1) : productList.remove(0);
                                            totalIn = item.getQueue().getInQueue();
                                            totalIn--;
                                            totalOut++;
                                            item.getQueue().setInQueue(totalIn);
                                            item.getQueue().setOutQueue(totalOut);
                                            System.out.println(totalIn + " productos en la cola " + finalI);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(outProduct);
                                            outSemaphore.get(sendTo).release();
                                            if (!item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                                capacitySemaphore.release();
                                            }
                                            System.out.println("Producto enviado al servidor");
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case "Porcentaje":
                                        var randomNumber = random.nextInt(100) + 1;
                                        sendTo = 0;
                                        for (var index = 0; index < percentages.size(); index++) {
                                            if (randomNumber < percentages.get(index) && index == 0) {
                                                sendTo = index;
                                                break;
                                            } else if (randomNumber < percentages.get(index) && randomNumber > percentages.get(index - 1)) {
                                                sendTo = index;
                                                break;
                                            }
                                        }
                                        try {
                                            inOutSemaphore.acquire();
                                            accessInSemaphore.get(sendTo).acquire();
                                            controlSemaphore.acquire();
                                            outProduct= item.getQueue().getDisciplineQueue().equals("FIFO")  ? productList.remove(productList.size()-1) : productList.remove(0);
                                            totalIn = item.getQueue().getInQueue();
                                            totalIn--;
                                            totalOut++;
                                            item.getQueue().setInQueue(totalIn);
                                            item.getQueue().setOutQueue(totalOut);
                                            System.out.println(totalIn + " productos en la cola " + finalI);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(outProduct);
                                            outSemaphore.get(sendTo).release();
                                            if (!item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                                capacitySemaphore.release();
                                            }
                                            System.out.println("Producto enviado al servidor");
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case "Primera conexión disponible":
                                        try {
                                            sendTo = 0;
                                            inOutSemaphore.acquire();
                                            while (!accessInSemaphore.get(sendTo).tryAcquire()) {
                                                if (outSemaphore.size() > (sendTo + 1)) {
                                                    sendTo++;
                                                } else {
                                                    sendTo = 0;
                                                }
                                            }
                                            controlSemaphore.acquire();
                                            outProduct= item.getQueue().getDisciplineQueue().equals("FIFO")  ? productList.remove(productList.size()-1) : productList.remove(0);
                                            totalIn = item.getQueue().getInQueue();
                                            totalIn--;
                                            totalOut++;
                                            item.getQueue().setInQueue(totalIn);
                                            item.getQueue().setOutQueue(totalOut);
                                            System.out.println(totalIn + " productos en la cola " + finalI);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(outProduct);
                                            outSemaphore.get(sendTo).release();
                                            if (!item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                                capacitySemaphore.release();
                                            }
                                            System.out.println("Producto enviado al servidor");
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                }
                            }
                        });
                        colaIn.start();
                        colaOut.start();
                    });
                    itemList.add(cola);
                    break;
                case "Server":
                    finalI = i;
                    var server = new Thread(() -> {
                        item.getServer().setPctBusyTime(0.0);
                        Double pctBusy = 0.0;
                        Double currentIdle = (double) System.currentTimeMillis();
                        Double currentBusy;
                        Double now = 0.0;
                        //0 for Idle
                        //1 for Busy;
                        Integer idleOrBusy = 0;
                        Double totalIdle = 0.0;
                        Double totalBusy = 0.0;
                        Double sleep;
                        TriangularDistribution triangularDistribution = null;
                        LogNormalDistribution logNormalDistribution = null;
                        BinomialDistribution binomialDistribution = null;
                        Integer max = null;
                        NormalDistribution normalDistribution = null;
                        LogisticDistribution logisticDistribution = null;
                        BetaDistribution betaDistribution = null;
                        GammaDistribution gammaDistribution = null;
                        UniformIntegerDistribution uniformIntegerDistribution = null;
                        WeibullDistribution weibullDistribution = null;

                        var strategyTime = getTimeStrategy(item.getServer().getCicleTime());
                        switch ((String) strategyTime.keySet().toArray()[0]) {
                            case "Triangular":
                                var numbers = strategyTime.get("Triangular");
                                triangularDistribution = new TriangularDistribution(numbers.get(0), numbers.get(2), numbers.get(1));
                                break;
                            case "LogNormal":
                                numbers = strategyTime.get("LogNormal");
                                logNormalDistribution = new LogNormalDistribution(numbers.get(0), numbers.get(1));
                                break;
                            case "Binomial":
                                numbers = strategyTime.get("Binomial");
                                binomialDistribution = new BinomialDistribution(numbers.get(0), numbers.get(1));
                                break;
                            case "Max":
                                max = strategyTime.get("Max").get(0);
                                if (strategyTime.keySet().toArray()[1].equals("Normal")) {
                                    numbers = strategyTime.get("Normal");
                                    normalDistribution = new NormalDistribution(numbers.get(0), numbers.get(1));
                                } else {
                                    numbers = strategyTime.get("Logistic");
                                    logisticDistribution = new LogisticDistribution(numbers.get(0), numbers.get(1));
                                }
                                break;
                            case "Beta":
                                numbers = strategyTime.get("Beta");
                                max = numbers.get(2);
                                betaDistribution = new BetaDistribution(numbers.get(0), numbers.get(1));
                                break;
                            case "Gamma":
                                numbers = strategyTime.get("Gamma");
                                gammaDistribution = new GammaDistribution(numbers.get(0), numbers.get(1));
                                break;
                            case "Uniform":
                                numbers = strategyTime.get("Uniform");
                                uniformIntegerDistribution = new UniformIntegerDistribution(numbers.get(0), numbers.get(1));
                                break;
                            case "Weibull":
                                numbers = strategyTime.get("Weibull");
                                weibullDistribution = new WeibullDistribution(numbers.get(0), numbers.get(1));
                                break;
                            case "mins":
                                max = strategyTime.get("mins").get(0);
                                break;
                            case "hr":
                                max = strategyTime.get("hr").get(0);
                                break;
                            case "":
                                max = strategyTime.get("").get(0);
                                break;
                        }

                        Product product;
                        var inSemaphoreType = getSemaphore(semaphoresItem, "In");
                        var accessOutSemaphoreType = getSemaphore(semaphoresItem, "AccessOut");
                        var outSemaphoresType = getSemaphore(semaphoresItem, "Out");
                        var accessInSemaphoreType = getSemaphore(semaphoresItem, "AccessIn");
                        var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                        var inExchanger= getExchangers(semaphoresItem,"In").get(0);
                        var outExchanger= getExchangers(semaphoresItem,"Out");

                        List<Semaphore> outSemaphores = null;
                        List<Semaphore> accessInSemaphores = null;
                        Semaphore accessOutSemaphore = null;
                        Semaphore inSemaphore = null;
                        Semaphore controlSemaphore = null;
                        var queues = semaphoresItem.getSmallestQueueDecisions() == null ? new ArrayList<SmallestQueueDecision>() : semaphoresItem.getSmallestQueueDecisions();


                        if (outSemaphoresType != null) {
                            accessInSemaphores = accessInSemaphoreType.getSemaphores();
                            outSemaphores = outSemaphoresType.getSemaphores();
                            accessOutSemaphore = accessOutSemaphoreType.getSemaphores().get(0);
                            inSemaphore = inSemaphoreType.getSemaphores().get(0);
                            controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                        }
                        var percentages = getPercentages(item);
                        var sendToStrategy = item.getItem().getSendToStrategy();

                        var total = 0;
                        var in = 0;
                        try {
                            Double setUpTimeServer = Double.parseDouble(item.getServer().getSetupTime()) * 1000.0;
                            while (true) {
                                switch ((String) strategyTime.keySet().toArray()[0]) {
                                    case "Triangular":
                                        sleep = triangularDistribution.sample();
                                        break;
                                    case "LogNormal":
                                        sleep = logNormalDistribution.sample();
                                        break;
                                    case "Binomial":
                                        sleep = (double) binomialDistribution.sample();
                                        break;
                                    case "Max":
                                        if (strategyTime.keySet().toArray()[1].equals("Normal")) {
                                            sleep = normalDistribution.sample();
                                            if (sleep < max) {
                                                sleep = (double) max;
                                            }
                                        } else {
                                            sleep = logisticDistribution.sample();
                                            if (sleep < max) {
                                                sleep = (double) max;
                                            }
                                        }
                                        break;
                                    case "Beta":
                                        sleep = betaDistribution.sample();
                                        sleep = sleep * max;
                                        break;
                                    case "Gamma":
                                        sleep = gammaDistribution.sample();
                                        break;
                                    case "Uniform":
                                        sleep = (double) uniformIntegerDistribution.sample();
                                        break;
                                    case "Weibull":
                                        sleep = weibullDistribution.sample();
                                        break;
                                    case "mins":
                                        sleep = max * 60.0 * 1000.0;
                                        break;
                                    case "hr":
                                        sleep = max * 60.0 * 60.0 * 1000.0;
                                        break;
                                    case "":
                                        sleep = (double) max * 1000.0;
                                        break;
                                    default:
                                        sleep = 0.0;
                                }

                                accessOutSemaphore.release();
                                product= (Product) inExchanger.exchange(null);
                                inSemaphore.acquire();
                                controlSemaphore.acquire();
                                in++;
                                pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                pctBusy = Double.isNaN(pctBusy) ? 0 : pctBusy;
                                item.getServer().setPctBusyTime(pctBusy);
                                System.out.println(in + " productos en el servidor " + finalI);
                                System.out.println("Pct Busy " + item.getServer().getPctBusyTime() + " en el servidor " + finalI);
                                controlSemaphore.release();
                                System.out.println("SetUp Servidor");
                                /*totalIdle = totalIdle + ((double) System.currentTimeMillis() - currentIdle);
                                controlSemaphore.acquire();
                                item.getServer().setBusyTime((totalIdle / (totalBusy + totalIdle)) * 100.0);
                                controlSemaphore.release();
                                currentIdle = ((double) System.currentTimeMillis()) + setUpTimeServer.longValue();
                                 */
                                Thread.sleep(setUpTimeServer.longValue());
                                System.out.println("Procesandose por el servidor " + finalI);
                                controlSemaphore.acquire();
                                now = (double) System.currentTimeMillis();
                                totalIdle = totalIdle + (now - currentIdle);
                                System.out.println("Tiempo idle " + totalIdle / 1000);
                                currentBusy = now;
                                idleOrBusy = 1;
                                item.getServer().setIdleOrBusy(idleOrBusy);
                                item.getServer().setLastTimeBusy(now);
                                item.getServer().setTotalIdle(totalIdle);
                                controlSemaphore.release();
                                Thread.sleep(sleep.longValue());
                                controlSemaphore.acquire();
                                now = (double) System.currentTimeMillis();
                                totalBusy = totalBusy + (now - currentBusy);
                                System.out.println("Tiempo busy " + totalBusy / 1000);
                                currentIdle = now;
                                idleOrBusy = 0;
                                item.getServer().setTotalBusy(totalBusy);
                                item.getServer().setIdleOrBusy(idleOrBusy);
                                item.getServer().setLastTimeIdle(now);
                                controlSemaphore.release();
                                switch (sendToStrategy) {
                                    case "Aleatorio (lo manda independientemente de si hay hueco o no)":
                                        var sendTo = random.nextInt(outSemaphores.size());
                                        if (!accessInSemaphores.get(sendTo).tryAcquire()) {
                                            controlSemaphore.acquire();
                                            in--;
                                            total++;
                                            item.getServer().setOutServer(total);
                                            controlSemaphore.release();
                                        } else {
                                            controlSemaphore.acquire();
                                            in--;
                                            total++;
                                            item.getServer().setOutServer(total);
                                            controlSemaphore.release();
                                            outExchanger.get(sendTo).exchange(product);
                                            outSemaphores.get(sendTo).release();
                                        }
                                        System.out.println("Producto enviado al sumidero");
                                        break;
                                    case "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                        var randomNumber = random.nextInt(100) + 1;
                                        sendTo = 0;
                                        for (var index = 0; index < percentages.size(); index++) {
                                            if (randomNumber < percentages.get(index) && index == 0) {
                                                sendTo = index;
                                                break;
                                            } else if (randomNumber < percentages.get(index) && randomNumber > percentages.get(index - 1)) {
                                                sendTo = index;
                                                break;
                                            }
                                        }
                                        accessInSemaphores.get(sendTo).acquire();
                                        controlSemaphore.acquire();
                                        in--;
                                        total++;
                                        item.getServer().setOutServer(total);
                                        controlSemaphore.release();
                                        outExchanger.get(sendTo).exchange(product);
                                        outSemaphores.get(sendTo).release();
                                        System.out.println("Enviado por el servidor " + finalI);
                                        break;
                                    case "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                        sendTo = random.nextInt(outSemaphores.size());
                                        accessInSemaphores.get(sendTo).acquire();
                                        controlSemaphore.acquire();
                                        in--;
                                        total++;
                                        item.getServer().setOutServer(total);
                                        controlSemaphore.release();
                                        outSemaphores.get(sendTo).release();
                                        System.out.println("Enviado por el servidor " + finalI);
                                        break;
                                    case "Primera conexión disponible":
                                        sendTo = 0;
                                        while (!accessInSemaphores.get(sendTo).tryAcquire()) {
                                            if (outSemaphores.size() > (sendTo + 1)) {
                                                sendTo++;
                                            } else {
                                                sendTo = 0;
                                            }
                                        }
                                        controlSemaphore.acquire();
                                        in--;
                                        total++;
                                        item.getServer().setOutServer(total);
                                        controlSemaphore.release();
                                        outExchanger.get(sendTo).exchange(product);
                                        outSemaphores.get(sendTo).release();
                                        System.out.println("Enviado por el servidor " + finalI);
                                        break;
                                    case "A la cola más pequeña (si está llena espera hasta que haya hueco)":
                                        try {
                                            var products = Double.POSITIVE_INFINITY;
                                            var queueProducts = 0;
                                            var smallestQueue = 0;
                                            for (var index = 0; index < queues.size(); index++) {
                                                if (queues.get(index).getTypeItem().equals("Queue")) {
                                                    queues.get(index).getControlDestinationSemaphore().acquire();
                                                    queueProducts = simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue() == null ? 0 : simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue();
                                                    queues.get(index).getControlDestinationSemaphore().release();
                                                    if (queueProducts < products) {
                                                        products = queueProducts;
                                                        smallestQueue = index;
                                                    }
                                                } else if (queues.get(index).getTypeItem().equals("Sink")) {
                                                    smallestQueue = index;
                                                    break;
                                                }
                                            }
                                            accessInSemaphores.get(smallestQueue).acquire();
                                            controlSemaphore.acquire();
                                            in--;
                                            total++;
                                            item.getServer().setOutServer(total);
                                            controlSemaphore.release();
                                            outExchanger.get(smallestQueue).exchange(product);
                                            outSemaphores.get(smallestQueue).release();
                                            System.out.println("Enviado por el servidor " + finalI);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    itemList.add(server);
                    break;
                case "Sink":
                    finalI = i;
                    var sink = new Thread(() -> {
                        var productsInSink=0;
                        var inSemaphoreType = getSemaphore(semaphoresItem, "In");
                        var accessOutType = getSemaphore(semaphoresItem, "AccessOut");
                        var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                        var inExchanger= getExchangers(semaphoresItem,"In").get(0);

                        Semaphore inSemaphore = null;
                        Semaphore controlSemaphore = null;
                        Semaphore accessOutSemaphore = null;

                        if (inSemaphoreType != null) {
                            accessOutSemaphore = accessOutType.getSemaphores().get(0);
                            inSemaphore = inSemaphoreType.getSemaphores().get(0);
                            controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                        }

                        try {
                            while (true) {
                                accessOutSemaphore.release();
                                inExchanger.exchange(null);
                                inSemaphore.acquire();
                                controlSemaphore.acquire();
                                productsInSink++;
                                item.getSink().setInSink(productsInSink);
                                System.out.println(productsInSink + " productos en el sumidero " + finalI);
                                controlSemaphore.release();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    itemList.add(sink);
            }
        }

        startSimulation = (double) System.currentTimeMillis();
        for (Thread hilo : itemList) {
            hilo.start();
        }
        System.out.println("Simulacion empezada");
        var daemonThread = new Thread(() -> {
            try {
                List<Semaphore> semaphoreList = new ArrayList<>();
                //Save all control semaphores in order to save the items safely
                for (SemaphoreAsignation semaphoreAsignation : semaphoreAsignationList) {
                    semaphoreList.add(getSemaphore(semaphoreAsignation, "Control").getSemaphores().get(0));
                }
                while (true) {
                    for (var i = 0; i < simulation.size(); i++) {
                        semaphoreList.get(i).acquire();
                        var item = simulation.get(i);
                        switch (simulation.get(i).getItem().getDescription()) {
                            case "Source":
                                System.out.println("La fuente " + i + " ha producido " + simulation.get(i).getSource().getOutSource());
                                //sourceService.save(simulation.get(i)))
                                break;
                            case "Queue":
                                System.out.println("La cola " + i + " tiene dentro " + simulation.get(i).getQueue().getInQueue());
                                System.out.println("La cola " + i + " han salido " + simulation.get(i).getQueue().getOutQueue());
                                //queueService.save(simulation.get(i)))
                                break;
                            case "Server":
                                System.out.println("El server " + i + " producido " + simulation.get(i).getServer().getOutServer());
                                var totalIdle = item.getServer().getTotalIdle() != null ? item.getServer().getTotalIdle() : 0.0;
                                var totalBusy = item.getServer().getTotalBusy() != null ? item.getServer().getTotalBusy() : 0.0;
                                var lastTimeIdle = item.getServer().getLastTimeIdle() != null ? item.getServer().getLastTimeIdle() : 0;
                                var lastTimeBusy = item.getServer().getLastTimeBusy() != null ? item.getServer().getLastTimeBusy() : 0;
                                var pctBusy = 0.0;
                                if (totalBusy != 0) {
                                    if (item.getServer().getIdleOrBusy() == 0) {
                                        totalIdle = totalIdle + ((double) System.currentTimeMillis() - lastTimeIdle);
                                        pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                    } else {
                                        totalBusy = totalBusy + ((double) System.currentTimeMillis() - lastTimeBusy);
                                        pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                    }
                                    item.getServer().setPctBusyTime(pctBusy);
                                    System.out.println("El server tiene un uso de " + simulation.get(i).getServer().getPctBusyTime());
                                }
                                //serverService.save(simulation.get(i)))
                                break;
                            case "Sink":
                                System.out.println("El desague " + i + " ha llegado " + simulation.get(i).getSink().getInSink());
                                //sinkService.save(simulation.get(i)))
                                break;
                        }
                        semaphoreList.get(i).release();
                    }
                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        daemonThread.start();

        /*Calendar ahora1 = Calendar.getInstance();
        long t1 = ahora1.getTimeInMillis();
        var hiloTiempo= new Thread(()->{
            try {
                while (true){
                    Thread.sleep(5000);
                    Calendar ahora2 = Calendar.getInstance();
                    long t2 = ahora2.getTimeInMillis();
                    System.out.println((t2-t1)/1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        hiloTiempo.start();

         */

    }

    private List<Exchanger> getExchangers(SemaphoreAsignation semaphoresItem, String type) {
        for (SemaphoresTypes semaphoreType : semaphoresItem.getSemaphoresTypes()) {
            if (semaphoreType.getType().equals(type)) {
                return semaphoreType.getExchangers();
            }
        }
        return null;
    }

    private Integer getIdentifierFromID(Integer idItem, List<ItemDTO> itemDTOList) {
        for (var i = 0; i < itemDTOList.size(); i++) {
            if (idItem.equals(itemDTOList.get(i).getItem().getIdItem())) {
                return i;
            }
        }
        return null;
    }

    private HashMap<String, List<Integer>> getTimeStrategy(String timeStrategy) {
        HashMap<String, List<Integer>> timeStrategyMap = new HashMap<>();
        if (timeStrategy.length() > 10 && timeStrategy.substring(0, 10).equals("Triangular")) {
            var numbers = timeStrategy.substring(11, timeStrategy.length() - 1);
            var posComa1 = -1;
            var posComa2 = -1;
            for (var i = 0; i < numbers.length(); i++) {
                if (numbers.charAt(i) == ',') {
                    if (posComa1 == -1) {
                        posComa1 = i;
                    } else {
                        posComa2 = i;
                        break;
                    }
                }
            }
            var firstNumber = numbers.substring(0, posComa1);
            var secondNumber = numbers.substring(posComa1 + 1, posComa2);
            var thirdNumber = numbers.substring(posComa2 + 1);
            var firstNumberInt = Integer.valueOf(firstNumber);
            var secondNumberInt = Integer.valueOf(secondNumber);
            var thirdNumberInt = Integer.valueOf(thirdNumber);
            List<Integer> numbersList = new ArrayList<>();
            numbersList.add(firstNumberInt);
            numbersList.add(secondNumberInt);
            numbersList.add(thirdNumberInt);
            timeStrategyMap.put("Triangular", numbersList);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 9 && timeStrategy.substring(0, 9).equals("LogNormal")) {
            var numbers = getNumbers(timeStrategy.substring(10, timeStrategy.length() - 1));
            timeStrategyMap.put("LogNormal", numbers);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 8 && timeStrategy.substring(0, 8).equals("Binomial")) {
            var numbers = getNumbers(timeStrategy.substring(9, timeStrategy.length() - 1));
            timeStrategyMap.put("Binomial", numbers);
        } else if (timeStrategy.length() > 8 && timeStrategy.substring(0, 3).equals("Max")) {
            var subInput = timeStrategy.substring(4, timeStrategy.length() - 1);
            var posComa = -1;
            for (var i = 0; i < subInput.length(); i++) {
                if (subInput.charAt(i) == ',') {
                    posComa = i;
                    break;
                }
            }
            var firstNumber = subInput.substring(0, posComa);
            var secondSubInput = subInput.substring(posComa + 1);
            var firstNumberInt = Integer.valueOf(firstNumber);
            List<Integer> firstNumberList = new ArrayList<>();
            firstNumberList.add(firstNumberInt);
            List<Integer> numberList = new ArrayList<>();
            timeStrategyMap.put("Max", firstNumberList);
            if (secondSubInput.substring(0, 6).equals("Normal") && secondSubInput.charAt(6) == '(' && secondSubInput.substring(secondSubInput.length() - 1).equals(")")) {
                var numbers = secondSubInput.substring(7, secondSubInput.length() - 1);
                numberList = getNumbers(numbers);
                timeStrategyMap.put("Normal", numberList);

            }
            if (secondSubInput.substring(0, 8).equals("Logistic") && secondSubInput.charAt(8) == '(' && secondSubInput.substring(secondSubInput.length() - 1).equals(")")) {
                var numbers = secondSubInput.substring(9, secondSubInput.length() - 1);
                numberList = getNumbers(numbers);
                timeStrategyMap.put("Logistic", numberList);
            }
            return timeStrategyMap;
        } else if (timeStrategy.length() > 4 && timeStrategy.substring(0, 4).equals("Beta")) {
            var numbers = timeStrategy.substring(11, timeStrategy.length() - 1);
            var posComa1 = -1;
            var posComa2 = -1;
            for (var i = 0; i < numbers.length(); i++) {
                if (numbers.charAt(i) == ',') {
                    if (posComa1 == -1) {
                        posComa1 = i;
                    } else {
                        posComa2 = i;
                        break;
                    }
                }
            }
            var firstNumber = numbers.substring(0, posComa1);
            var secondNumber = numbers.substring(posComa1 + 1, posComa2);
            var thirdNumber = numbers.substring(posComa2 + 1);
            var firstNumberInt = Integer.valueOf(firstNumber);
            var secondNumberInt = Integer.valueOf(secondNumber);
            var thirdNumberInt = Integer.valueOf(thirdNumber);
            List<Integer> numbersList = new ArrayList<>();
            numbersList.add(firstNumberInt);
            numbersList.add(secondNumberInt);
            numbersList.add(thirdNumberInt);
            timeStrategyMap.put("Beta", numbersList);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 5 && timeStrategy.substring(0, 5).equals("Gamma")) {
            var numbers = timeStrategy.substring(6, timeStrategy.length() - 1);
            var numberList = getNumbers(numbers);
            timeStrategyMap.put("Gamma", numberList);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 8 && timeStrategy.substring(0, 7).equals("Uniform")) {
            var numbers = timeStrategy.substring(8, timeStrategy.length() - 1);
            var numberList = getNumbers(numbers);
            timeStrategyMap.put("Uniform", numberList);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 8 && timeStrategy.substring(0, 7).equals("Weibull")) {
            var numbers = timeStrategy.substring(8, timeStrategy.length() - 1);
            var numberList = getNumbers(numbers);
            timeStrategyMap.put("Weibull", numberList);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 4 && timeStrategy.substring(0, 4).equals("mins")) {
            var number = timeStrategy.substring(5, timeStrategy.length() - 1);
            var numberInt = Integer.valueOf(number);
            List<Integer> numberList = new ArrayList<>();
            numberList.add(numberInt);
            timeStrategyMap.put("mins", numberList);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 2 && timeStrategy.substring(0, 2).equals("hr")) {
            var number = timeStrategy.substring(3, timeStrategy.length() - 1);
            var numberInt = Integer.valueOf(number);
            List<Integer> numberList = new ArrayList<>();
            numberList.add(numberInt);
            timeStrategyMap.put("hr", numberList);
            return timeStrategyMap;
        } else {
            var number = Integer.valueOf(timeStrategy);
            List<Integer> numberList = new ArrayList<>();
            numberList.add(number);
            timeStrategyMap.put("", numberList);
            return timeStrategyMap;
        }
        return null;
    }

    public List<Integer> getNumbers(String numbers) {
        List<Integer> numbersList = new ArrayList<>();
        var posComa = 0;
        for (var i = 0; i < numbers.length(); i++) {
            if (numbers.charAt(i) == ',') {
                posComa = i;
                break;
            }
        }
        var firstNumber = numbers.substring(0, posComa);
        var secondNumber = numbers.substring(posComa + 1);
        var firstNumberInt = Integer.valueOf(firstNumber);
        var secondNumberInt = Integer.valueOf(secondNumber);
        numbersList.add(firstNumberInt);
        numbersList.add(secondNumberInt);

        return numbersList;
    }

    private List<Integer> getPercentages(ItemDTO item) {
        List<Integer> percentagesList = new ArrayList<>();
        Integer cumulativePercentage = 0;
        for (Connection connection : item.getConnections()) {
            cumulativePercentage += connection.getPercentage();
            percentagesList.add(cumulativePercentage);
        }
        return percentagesList;
    }

    private SemaphoresTypes getSemaphore(SemaphoreAsignation semaphoresItem, String type) {
        for (SemaphoresTypes semaphoreType : semaphoresItem.getSemaphoresTypes()) {
            if (semaphoreType.getType().equals(type)) {
                return semaphoreType;
            }
        }
        return null;
    }

    private boolean connectionAlreadyMade(List<Integer> diferentsOrigins, Integer destinationId, List<SemaphoreAsignation> semaphoreAsignationList) {
        for (SemaphoreAsignation semaphoreAsignation : semaphoreAsignationList) {
            if (diferentsOrigins.contains(semaphoreAsignation.getIdOriginItem())) {
                for (SemaphoresTypes semaphoresTypes : semaphoreAsignation.getSemaphoresTypes()) {
                    if (semaphoresTypes.getType().equals("Out") || semaphoresTypes.getType().equals("AccessIn")) {
                        if (semaphoresTypes.getIdDestinationItem() != null && semaphoresTypes.getIdDestinationItem().contains(destinationId)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }


    private SemaphoreAsignation semaphoreAsignationAlreadyExist(Integer idItem, List<SemaphoreAsignation> semaphoreAsignationList) {
        for (SemaphoreAsignation semaphoreAsignation : semaphoreAsignationList) {
            if (semaphoreAsignation.getIdOriginItem().equals(idItem)) {
                return semaphoreAsignation;
            }
        }
        return null;
    }

    public List<Integer> moreThanOneConnection(Integer destination, List<ItemDTO> itemDTOList) {
        List<Integer> finalList = new ArrayList<>();
        for (ItemDTO itemDTO : itemDTOList) {
            if (itemDTO.getConnections() != null) {
                for (Connection connection : itemDTO.getConnections()) {
                    if (connection.getDestinationItem().getIdItem().equals(destination)) {
                        finalList.add(connection.getOriginItem().getIdItem());
                    }
                }
            }
        }
        return finalList;
    }

}
