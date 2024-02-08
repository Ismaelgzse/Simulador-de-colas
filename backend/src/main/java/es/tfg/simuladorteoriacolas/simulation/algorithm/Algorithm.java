package es.tfg.simuladorteoriacolas.simulation.algorithm;

import es.tfg.simuladorteoriacolas.items.Item;
import es.tfg.simuladorteoriacolas.items.ItemDTO;
import es.tfg.simuladorteoriacolas.items.Semaphores.SemaphoreAsignation;
import es.tfg.simuladorteoriacolas.items.Semaphores.SemaphoresTypes;
import es.tfg.simuladorteoriacolas.items.Semaphores.SmallestQueueDecision;
import es.tfg.simuladorteoriacolas.items.connections.Connection;
import es.tfg.simuladorteoriacolas.items.products.Product;
import es.tfg.simuladorteoriacolas.items.types.*;
import es.tfg.simuladorteoriacolas.simulation.Simulation;
import es.tfg.simuladorteoriacolas.simulation.SimulationService;
import org.apache.commons.math3.distribution.*;
import org.hibernate.sql.ast.tree.expression.CaseSimpleExpression;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

public class Algorithm implements Runnable {

    private SimpMessageSendingOperations simpMessageSendingOperations;

    private Integer simulationId;

    private List<ItemDTO> simulation;

    private SimulationService simulationService;

    //A list of booleans that controls the correct saved state of the simulation when it is interrupted
    private List<Boolean> interruptedAndSavedTheadsState = new ArrayList<>();

    private Integer interruptedAndSavedTheadsStateIndex = 0;

    //A semaphore that controls the correct saved state of the simulation when it is interrupted
    private Semaphore interruptedAndSavedTheadsStateSemaphore = new Semaphore(1, true);


    public Algorithm(Integer simulatonId, List<ItemDTO> simulation, SimulationService simulationService, SimpMessageSendingOperations simpMessageSendingOperations) {
        this.simulation = simulation;
        this.simulationId = simulatonId;
        this.simulationService = simulationService;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    private Random random = new Random();

    @Override
    public void run() {

        //Each item will store in a structure a series of traffic lights necessary for the correct functioning of the simulation.
        //And in this data structure, different groups of semaphores will be distinguished according to their purpose.
        //Not all items have the same semaphore groups.
        //Semaphore groups are classified as follows:
        //  -Control: This type of semaphore is responsible for the correct protection of the critical section and the correct use of the common resources.
        //  -InOut: Some items are divided into two (input and output) that work independently but are connected to each other. The way to connect and communicate is through this semaphore.
        //  -Capacity: Exclusive semaphore of the items that are queues, and control the entry of products.
        //  -Out y AccessIn: Set of semaphores that control the communication between two items (this type of semaphore is the outgoing communication) and that function as a handshake.
        //  -In y AccessOut: They are the same semaphores as Out and AccessIn but at the entrance of the other item.

        List<SemaphoreAsignation> semaphoreAsignationList = new ArrayList<>();

        //Creates the common semaphores of the items.
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

                    //If the connection between these elements is not already built up, the necessary semaphores are created to establish a connection.
                    if (!connectionAlreadyMade(diferentsOrigins, connection.getDestinationItem().getIdItem(), semaphoreAsignationList)) {
                        SemaphoresTypes newSemaphoresType = null;
                        SemaphoresTypes newSemaphoresType2 = null;
                        //Load the semaphores of this type if a connection already exists
                        for (SemaphoresTypes semaphoresType : semaphoreAsignation.getSemaphoresTypes()) {
                            if (semaphoresType.getType().equals("Out")) {
                                newSemaphoresType = semaphoresType;
                            }
                            if (semaphoresType.getType().equals("AccessIn")) {
                                newSemaphoresType2 = semaphoresType;
                            }
                        }
                        List<Semaphore> semaphores = null;
                        if (newSemaphoresType != null) {
                            semaphores = newSemaphoresType.getSemaphores();
                        }
                        List<Integer> destinationList;
                        List<Semaphore> semaphores2;
                        List<Integer> destinationList2;
                        List<Exchanger> exchangers;
                        //if it does not exist, it is created
                        if (semaphores == null) {
                            List<SemaphoresTypes> types = semaphoreAsignation.getSemaphoresTypes();

                            newSemaphoresType = new SemaphoresTypes();
                            newSemaphoresType.setType("Out");

                            newSemaphoresType2 = new SemaphoresTypes();
                            newSemaphoresType2.setType("AccessIn");

                            semaphores = new ArrayList<>();
                            destinationList = new ArrayList<>();
                            exchangers = new ArrayList<>();

                            semaphores2 = new ArrayList<>();
                            destinationList2 = new ArrayList<>();
                            types.add(newSemaphoresType);
                            types.add(newSemaphoresType2);

                        } else {
                            destinationList = newSemaphoresType.getIdDestinationItem();
                            exchangers = newSemaphoresType.getExchangers();

                            semaphores2 = newSemaphoresType2.getSemaphores();
                            destinationList2 = newSemaphoresType2.getIdDestinationItem();
                        }

                        //Exchangers and semaphores are assigned to the origin item.
                        Semaphore semaphoreOut = new Semaphore(0, true);
                        Semaphore accessInSemaphore = new Semaphore(0, true);
                        Exchanger<Product> echanger = new Exchanger<>();

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

                        //Exchangers and semaphores are assigned to the destination item.
                        newSemaphoresType = new SemaphoresTypes();
                        newSemaphoresType.setType("In");
                        exchangers = new ArrayList<>();
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

                        //If exists another item that has the same destination item in a connection, it assigns the same semaphores and exchangers to the other origin item
                        for (Integer id : diferentsOrigins) {
                            if (!id.equals(connection.getOriginItem().getIdItem())) {
                                semaphoreAsignation = semaphoreAsignationAlreadyExist(id, semaphoreAsignationList);
                                newSemaphoresType = new SemaphoresTypes();
                                newSemaphoresType.setType("Out");
                                exchangers = new ArrayList<>();
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
        List<Thread> totalThreads = new ArrayList<>();
        for (var i = 0; i < semaphoreAsignationList.size(); i++) {
            var item = simulation.get(i);
            var semaphoresItem = semaphoreAsignationList.get(i);
            switch (item.getItem().getDescription()) {
                case "Source":
                    int finalI = i;
                    var source = new Thread(() -> {
                        var sourceIn = new Thread(() -> {

                            //Initialise the semaphore that controls the correct state of the simulation when it is interrupted
                            interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                            interruptedAndSavedTheadsState.add(false);
                            var indexState = interruptedAndSavedTheadsState.size() - 1;
                            interruptedAndSavedTheadsStateIndex++;
                            interruptedAndSavedTheadsStateSemaphore.release();

                            Double sleep;
                            Integer max = null;


                            //Initialise the possible distributions
                            PoissonDistribution poissonDistribution = null;
                            TriangularDistribution triangularDistribution = null;
                            LogNormalDistribution logNormalDistribution = null;
                            BinomialDistribution binomialDistribution = null;
                            NormalDistribution normalDistribution = null;
                            LogisticDistribution logisticDistribution = null;
                            BetaDistribution betaDistribution = null;
                            GammaDistribution gammaDistribution = null;
                            UniformIntegerDistribution uniformIntegerDistribution = null;
                            WeibullDistribution weibullDistribution = null;
                            ExponentialDistribution exponentialDistribution = null;

                            //Gets the formatted generation strategy of products
                            var strategyTime = getTimeStrategy(item.getSource().getInterArrivalTime());

                            //Creates the selected distribution
                            switch ((String) strategyTime.keySet().toArray()[0]) {
                                case "NegExp":
                                    var numbers = strategyTime.get("NegExp");
                                    exponentialDistribution = new ExponentialDistribution(numbers.get(0));
                                    break;
                                case "Poisson":
                                    numbers = strategyTime.get("Poisson");
                                    poissonDistribution = new PoissonDistribution(numbers.get(0));
                                    break;
                                case "Triangular":
                                    numbers = strategyTime.get("Triangular");
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

                            //Initialises the semaphores of the source
                            var inOutSemaphoreType = getSemaphore(semaphoresItem, "InOut");
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            Semaphore inOutSemaphore = null;
                            Semaphore controlSemaphore = null;
                            if (inOutSemaphoreType != null) {
                                inOutSemaphore = inOutSemaphoreType.getSemaphores().get(0);
                            }
                            var isInfinite = item.getSource().getNumberProducts().equals("Ilimitados");
                            var numberProducts = isInfinite ? Double.POSITIVE_INFINITY : Double.parseDouble(item.getSource().getNumberProducts());
                            try {
                                while (isInfinite || numberProducts > 0 || !Thread.interrupted()) {
                                    if (!isInfinite) {
                                        numberProducts--;
                                    }
                                    switch ((String) strategyTime.keySet().toArray()[0]) {
                                        case "NegExp":
                                            sleep = exponentialDistribution.sample() * 1000.0;
                                            break;
                                        case "Poisson":
                                            sleep = poissonDistribution.sample() * 1000.0;
                                            break;
                                        case "Triangular":
                                            sleep = triangularDistribution.sample() * 1000.0;
                                            break;
                                        case "LogNormal":
                                            sleep = logNormalDistribution.sample() * 1000.0;
                                            break;
                                        case "Binomial":
                                            sleep = (double) binomialDistribution.sample() * 1000.0;
                                            break;
                                        case "Max":
                                            if (strategyTime.keySet().toArray()[1].equals("Normal")) {
                                                sleep = normalDistribution.sample();
                                                if (sleep < max) {
                                                    sleep = (double) max * 1000.0;
                                                } else {
                                                    sleep = sleep * 1000.0;
                                                }
                                            } else {
                                                sleep = logisticDistribution.sample();
                                                if (sleep < max) {
                                                    sleep = (double) max * 1000.0;
                                                } else {
                                                    sleep = sleep * 1000.0;
                                                }
                                            }
                                            break;
                                        case "Beta":
                                            sleep = betaDistribution.sample() * 1000.0;
                                            sleep = sleep * max;
                                            break;
                                        case "Gamma":
                                            sleep = gammaDistribution.sample() * 1000.0;
                                            break;
                                        case "Uniform":
                                            sleep = (double) uniformIntegerDistribution.sample() * 1000.0;
                                            break;
                                        case "Weibull":
                                            sleep = weibullDistribution.sample() * 1000.0;
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
                                    //This simulates the generation of a product
                                    Thread.sleep(sleep.longValue());
                                    //Then it releases and sends to the sourceOut
                                    inOutSemaphore.release();
                                }
                            } catch (InterruptedException e) {
                                //When is interrupted it saves the state of the simulation
                                interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                                interruptedAndSavedTheadsState.set(indexState, true);
                                interruptedAndSavedTheadsStateSemaphore.release();
                            }
                        });

                        var sourceOut = new Thread(() -> {

                            //Initialise the semaphore that controls the correct state of the simulation when it is interrupted
                            interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                            interruptedAndSavedTheadsState.add(false);
                            var indexState = interruptedAndSavedTheadsState.size() - 1;
                            interruptedAndSavedTheadsStateIndex++;
                            interruptedAndSavedTheadsStateSemaphore.release();

                            //Gets the formatted sending strategy of products
                            var sendToStrategy = item.getItem().getSendToStrategy();
                            List<Double> percentages = getPercentages(item);

                            //Initialises the semaphores of the sourceOut
                            List<Semaphore> outSemaphores = null;
                            List<Semaphore> accessInSemaphores = null;
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            var outSemaphoresType = getSemaphore(semaphoresItem, "Out");
                            var accessInSemaphoresType = getSemaphore(semaphoresItem, "AccessIn");
                            var outExchangers = getExchangers(semaphoresItem, "Out");

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
                            var numProducts = 0;

                            try {
                                while (true) {
                                    Integer sendTo;
                                    //Gets the product from the SourceIn
                                    inOutSemaphore.acquire();
                                    //And prepares to send it to the correct queue
                                    Product product = new Product("Standar", null, null);
                                    switch (sendToStrategy) {
                                        case "Aleatorio (lo manda independientemente de si hay hueco o no)":
                                            //Selects a random channel (semaphore) to send the product
                                            sendTo = random.nextInt(outSemaphores.size());
                                            if (!accessInSemaphores.get(sendTo).tryAcquire() && !Thread.currentThread().isInterrupted()) {
                                                //If the channel is not available, it is "sent" but "lost"
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                controlSemaphore.release();
                                            } else {
                                                //If the channel is available, it sends the product
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                controlSemaphore.release();
                                                outExchangers.get(sendTo).exchange(product);
                                                outSemaphores.get(sendTo).release();
                                            }
                                            break;
                                        case "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                            //Selects a random channel (semaphore) to send the product
                                            sendTo = random.nextInt(outSemaphores.size());
                                            //Waits for the channel to be available to send the product
                                            accessInSemaphores.get(sendTo).acquire();
                                            controlSemaphore.acquire();
                                            numProducts++;
                                            item.getSource().setOutSource(numProducts);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(product);
                                            outSemaphores.get(sendTo).release();
                                            break;
                                        case "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)":
                                            sendTo = 0;
                                            //Stays in this loop until there is a spot in a queue
                                            while (!accessInSemaphores.get(sendTo).tryAcquire() && !Thread.currentThread().isInterrupted()) {
                                                if (outSemaphores.size() > (sendTo + 1)) {
                                                    sendTo++;
                                                } else {
                                                    sendTo = 0;
                                                }
                                            }
                                            //Sends it
                                            controlSemaphore.acquire();
                                            numProducts++;
                                            item.getSource().setOutSource(numProducts);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(product);
                                            outSemaphores.get(sendTo).release();
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
                                            //If the channel is not available, it is "sent" but "lost"
                                            if (!accessInSemaphores.get(sendTo).tryAcquire() && !Thread.currentThread().isInterrupted()) {
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                controlSemaphore.release();
                                            } else {
                                                //If the channel is available, it sends the product
                                                controlSemaphore.acquire();
                                                numProducts++;
                                                item.getSource().setOutSource(numProducts);
                                                controlSemaphore.release();
                                                outExchangers.get(sendTo).exchange(product);
                                                outSemaphores.get(sendTo).release();
                                            }
                                            break;
                                        case "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                            randomNumber = random.nextInt(100) + 1;
                                            sendTo = 0;
                                            //Selects a random channel to send the product considering the percentages
                                            for (var index = 0; index < percentages.size(); index++) {
                                                if (randomNumber < percentages.get(index) && index == 0) {
                                                    sendTo = index;
                                                    break;
                                                } else if (randomNumber < percentages.get(index) && randomNumber > percentages.get(index - 1)) {
                                                    sendTo = index;
                                                    break;
                                                }
                                            }
                                            //Waits for the channel to be available to send the product
                                            accessInSemaphores.get(sendTo).acquire();
                                            controlSemaphore.acquire();
                                            numProducts++;
                                            item.getSource().setOutSource(numProducts);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(product);
                                            outSemaphores.get(sendTo).release();
                                            break;
                                        case "A la cola más pequeña (si está llena espera hasta que haya hueco)":
                                            var products = Double.POSITIVE_INFINITY;
                                            var queueProducts = 0;
                                            var smallestQueue = 0;
                                            Boolean filled=true;

                                            while (!Thread.currentThread().isInterrupted() && filled) {
                                                filled=true;
                                                for (var index = 0; index < queues.size(); index++) {
                                                    if (queues.get(index).getTypeItem().equals("Queue")) {
                                                        queues.get(index).getControlDestinationSemaphore().acquire();
                                                        queueProducts = simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue() == null ? 0 : simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue();
                                                        if (simulation.get(queues.get(index).getIdentifier()).getQueue().getCapacityQueue().equals("Ilimitados") || Integer.parseInt(simulation.get(queues.get(index).getIdentifier()).getQueue().getCapacityQueue())>queueProducts){
                                                            if (queueProducts < products) {
                                                                products = queueProducts;
                                                                smallestQueue = index;
                                                                filled=false;
                                                            }
                                                        }
                                                        queues.get(index).getControlDestinationSemaphore().release();
                                                    }
                                                }
                                            }

                                            accessInSemaphores.get(smallestQueue).acquire();
                                            controlSemaphore.acquire();
                                            numProducts++;
                                            item.getSource().setOutSource(numProducts);
                                            controlSemaphore.release();
                                            outExchangers.get(smallestQueue).exchange(product);
                                            outSemaphores.get(smallestQueue).release();
                                            break;
                                    }
                                }
                            } catch (InterruptedException e) {
                                //When is interrupted it saves the state of the simulation
                                interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                                interruptedAndSavedTheadsState.set(indexState, true);
                                item.getSource().setOutSource(numProducts);
                                interruptedAndSavedTheadsStateSemaphore.release();
                            }
                        });
                        sourceIn.start();
                        sourceOut.start();
                        totalThreads.add(sourceIn);
                        totalThreads.add(sourceOut);
                    });
                    itemList.add(source);
                    totalThreads.add(source);
                    break;
                case "Queue":
                    finalI = i;
                    var queue = new Thread(() -> {
                        List<Product> productList = new ArrayList<>();
                        var queueIn = new Thread(() -> {

                            //Initialise the semaphore that controls the correct state of the simulation when it is interrupted
                            interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                            interruptedAndSavedTheadsState.add(false);
                            var indexState = interruptedAndSavedTheadsState.size() - 1;
                            interruptedAndSavedTheadsStateIndex++;
                            interruptedAndSavedTheadsStateSemaphore.release();

                            var total = 0;
                            var in = false;
                            Product inProduct;

                            //Initialises the semaphores of the queueIn
                            var inSemaphoreType = getSemaphore(semaphoresItem, "In");
                            var accessOutType = getSemaphore(semaphoresItem, "AccessOut");
                            var capacitySemaphoreType = getSemaphore(semaphoresItem, "Capacity");
                            var inOutSemaphoreType = getSemaphore(semaphoresItem, "InOut");
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            var inExchanger = getExchangers(semaphoresItem, "In").get(0);

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

                            try {
                                //This loop will be executing until the simulation stops
                                while (!Thread.currentThread().isInterrupted()) {
                                    //If there are empty places in the queue, it accepts products
                                    if (capacitySemaphore.availablePermits() > 0) {
                                        accessOutSemaphore.release();
                                        inProduct = (Product) inExchanger.exchange(null);
                                        //Sets the arrival time for later statistics
                                        inProduct.setArrivalTime((double) System.currentTimeMillis());
                                        inSemaphore.acquire();
                                        capacitySemaphore.acquire();
                                        controlSemaphore.acquire();
                                        //New product added to the queue
                                        productList.add(inProduct);
                                        total = item.getQueue().getInQueue() == null ? 0 : item.getQueue().getInQueue();
                                        total++;
                                        item.getQueue().setInQueue(total);
                                        controlSemaphore.release();
                                        //Informs to the queueOut about a new product
                                        inOutSemaphore.release();

                                        //If there is unlimited space in the queue, it frees up the occupied space
                                        if (item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                            capacitySemaphore.release();
                                        }
                                    }

                                }
                                //If the code is interrupted in the while loop check, it saves the state of the simulation there
                                if (Thread.currentThread().isInterrupted()) {
                                    interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                                    total = item.getQueue().getInQueue() == null ? 0 : item.getQueue().getInQueue();
                                    item.getQueue().setInQueue(total);
                                    interruptedAndSavedTheadsState.set(indexState, true);
                                    interruptedAndSavedTheadsStateSemaphore.release();
                                }
                            } catch (InterruptedException e) {
                                //When is interrupted it saves the state of the simulation
                                interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                                total = item.getQueue().getInQueue() == null ? 0 : item.getQueue().getInQueue();
                                item.getQueue().setInQueue(total);
                                interruptedAndSavedTheadsState.set(indexState, true);
                                interruptedAndSavedTheadsStateSemaphore.release();
                            }
                        });

                        var queueOut = new Thread(() -> {

                            //Initialise the semaphore that controls the correct state of the simulation when it is interrupted
                            interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                            interruptedAndSavedTheadsState.add(false);
                            var indexState = interruptedAndSavedTheadsState.size() - 1;
                            interruptedAndSavedTheadsStateIndex++;
                            interruptedAndSavedTheadsStateSemaphore.release();

                            var sendToStrategy = item.getItem().getSendToStrategy();
                            List<Double> percentages = getPercentages(item);
                            var totalIn = 0;
                            var totalOut = 0;
                            Product outProduct = null;

                            //Initialises the semaphores of the queueOut
                            var inOutSemaphoreType = getSemaphore(semaphoresItem, "InOut");
                            var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                            var outSemaphoreType = getSemaphore(semaphoresItem, "Out");
                            var accessInSemaphoreType = getSemaphore(semaphoresItem, "AccessIn");
                            var capacitySemaphoreType = getSemaphore(semaphoresItem, "Capacity");
                            var outExchangers = getExchangers(semaphoresItem, "Out");

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

                            try {
                                //This loop will be executing until the simulation stops
                                while (true) {
                                    switch (sendToStrategy) {
                                        case "Aleatorio":
                                            //Selects a random channel to send the product
                                            var sendTo = random.nextInt(outSemaphore.size());
                                            inOutSemaphore.acquire();
                                            accessInSemaphore.get(sendTo).acquire();
                                            controlSemaphore.acquire();
                                            //Selects a product to send depending on the sending strategy. And removes it from the list
                                            switch (item.getQueue().getDisciplineQueue()) {
                                                case "Fifo":
                                                    outProduct = productList.remove(0);
                                                    break;
                                                case "Lifo":
                                                    outProduct = productList.remove(productList.size() - 1);
                                                    break;
                                                case "Random":
                                                    var indexProduct = random.nextInt(productList.size());
                                                    outProduct = productList.remove(indexProduct);
                                            }
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(outProduct);
                                            controlSemaphore.acquire();
                                            totalIn = item.getQueue().getInQueue();
                                            totalIn--;
                                            totalOut++;
                                            item.getQueue().setInQueue(totalIn);
                                            item.getQueue().setOutQueue(totalOut);
                                            controlSemaphore.release();
                                            outSemaphore.get(sendTo).release();
                                            //If the queue has not unlimited capacity, it frees up a spot in the queue
                                            if (!item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                                capacitySemaphore.release();
                                            }
                                            break;
                                        case "Porcentaje":
                                            //Selects a random channel to send the product, considering the percentage of each channel
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
                                            inOutSemaphore.acquire();
                                            accessInSemaphore.get(sendTo).acquire();
                                            controlSemaphore.acquire();
                                            //Selects a product to send depending on the sending strategy. And removes it from the list
                                            switch (item.getQueue().getDisciplineQueue()) {
                                                case "Fifo":
                                                    outProduct = productList.remove(0);
                                                    break;
                                                case "Lifo":
                                                    outProduct = productList.remove(productList.size() - 1);
                                                    break;
                                                case "Random":
                                                    var indexProduct = random.nextInt(productList.size());
                                                    outProduct = productList.remove(indexProduct);
                                            }
                                            totalIn = item.getQueue().getInQueue();
                                            totalIn--;
                                            totalOut++;
                                            item.getQueue().setInQueue(totalIn);
                                            item.getQueue().setOutQueue(totalOut);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(outProduct);
                                            outSemaphore.get(sendTo).release();
                                            //If the queue has not unlimited capacity, it frees up a spot in the queue
                                            if (!item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                                capacitySemaphore.release();
                                            }
                                            break;
                                        case "Primera conexión disponible":
                                            sendTo = 0;
                                            inOutSemaphore.acquire();
                                            //Stays in this loop until there is a slot in the next item
                                            while (!accessInSemaphore.get(sendTo).tryAcquire() && !Thread.currentThread().isInterrupted()) {
                                                if (outSemaphore.size() > (sendTo + 1)) {
                                                    sendTo++;
                                                } else {
                                                    sendTo = 0;
                                                }
                                            }
                                            controlSemaphore.acquire();
                                            //Selects a product to send depending on the sending strategy. And removes it from the list
                                            switch (item.getQueue().getDisciplineQueue()) {
                                                case "Fifo":
                                                    outProduct = productList.remove(0);
                                                    break;
                                                case "Lifo":
                                                    outProduct = productList.remove(productList.size() - 1);
                                                    break;
                                                case "Random":
                                                    var indexProduct = random.nextInt(productList.size());
                                                    outProduct = productList.remove(indexProduct);
                                            }
                                            totalIn = item.getQueue().getInQueue();
                                            totalIn--;
                                            totalOut++;
                                            item.getQueue().setInQueue(totalIn);
                                            item.getQueue().setOutQueue(totalOut);
                                            controlSemaphore.release();
                                            outExchangers.get(sendTo).exchange(outProduct);
                                            outSemaphore.get(sendTo).release();
                                            //If the queue has not unlimited capacity, it frees up a spot in the queue
                                            if (!item.getQueue().getCapacityQueue().equals("Ilimitados")) {
                                                capacitySemaphore.release();
                                            }
                                            break;
                                    }
                                }
                            } catch (InterruptedException e) {
                                //When is interrupted it saves the state of the simulation
                                interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                                item.getQueue().setInQueue(totalIn);
                                item.getQueue().setOutQueue(totalOut);
                                interruptedAndSavedTheadsState.set(indexState, true);
                                interruptedAndSavedTheadsStateSemaphore.release();
                            }
                        });
                        queueIn.start();
                        queueOut.start();
                        totalThreads.add(queueIn);
                        totalThreads.add(queueOut);
                    });
                    itemList.add(queue);
                    totalThreads.add(queue);
                    break;
                case "Server":
                    finalI = i;
                    var server = new Thread(() -> {

                        //Initialise the semaphore that controls the correct state of the simulation when it is interrupted
                        interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                        interruptedAndSavedTheadsState.add(false);
                        var indexState = interruptedAndSavedTheadsState.size() - 1;
                        interruptedAndSavedTheadsStateIndex++;
                        interruptedAndSavedTheadsStateSemaphore.release();

                        //Initialise some variables needed to control the server item
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
                        Product product;
                        var queues = semaphoresItem.getSmallestQueueDecisions() == null ? new ArrayList<SmallestQueueDecision>() : semaphoresItem.getSmallestQueueDecisions();
                        var total = 0;
                        var in = 0;
                        List<Double> percentages = getPercentages(item);
                        var sendToStrategy = item.getItem().getSendToStrategy();

                        //Initialise the possible distributions
                        PoissonDistribution poissonDistribution = null;
                        ExponentialDistribution exponentialDistribution = null;
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

                        //Creates the selected distribution
                        var strategyTime = getTimeStrategy(item.getServer().getCicleTime());
                        switch ((String) strategyTime.keySet().toArray()[0]) {
                            case "NegExp":
                                var numbers = strategyTime.get("NegExp");
                                exponentialDistribution = new ExponentialDistribution(numbers.get(0));
                                break;
                            case "Poisson":
                                numbers = strategyTime.get("Poisson");
                                poissonDistribution = new PoissonDistribution(numbers.get(0));
                                break;
                            case "Triangular":
                                numbers = strategyTime.get("Triangular");
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

                        //Initialises the semaphores of the server
                        var inSemaphoreType = getSemaphore(semaphoresItem, "In");
                        var accessOutSemaphoreType = getSemaphore(semaphoresItem, "AccessOut");
                        var outSemaphoresType = getSemaphore(semaphoresItem, "Out");
                        var accessInSemaphoreType = getSemaphore(semaphoresItem, "AccessIn");
                        var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                        var inExchanger = getExchangers(semaphoresItem, "In").get(0);
                        var outExchanger = getExchangers(semaphoresItem, "Out");

                        List<Semaphore> outSemaphores = null;
                        List<Semaphore> accessInSemaphores = null;
                        Semaphore accessOutSemaphore = null;
                        Semaphore inSemaphore = null;
                        Semaphore controlSemaphore = null;

                        if (outSemaphoresType != null) {
                            accessInSemaphores = accessInSemaphoreType.getSemaphores();
                            outSemaphores = outSemaphoresType.getSemaphores();
                            accessOutSemaphore = accessOutSemaphoreType.getSemaphores().get(0);
                            inSemaphore = inSemaphoreType.getSemaphores().get(0);
                            controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                        }

                        try {
                            Double setUpTimeServer = Double.parseDouble(item.getServer().getSetupTime()) * 1000.0;

                            //This loop will be executing until the simulation stops
                            while (true) {
                                switch ((String) strategyTime.keySet().toArray()[0]) {
                                    case "NegExp":
                                        sleep = exponentialDistribution.sample() * 1000.0;
                                        break;
                                    case "Poisson":
                                        sleep = poissonDistribution.sample() * 1000.0;
                                        break;
                                    case "Triangular":
                                        sleep = triangularDistribution.sample() * 1000.0;
                                        break;
                                    case "LogNormal":
                                        sleep = logNormalDistribution.sample() * 1000.0;
                                        break;
                                    case "Binomial":
                                        sleep = (double) binomialDistribution.sample() * 1000.0;
                                        break;
                                    case "Max":
                                        if (strategyTime.keySet().toArray()[1].equals("Normal")) {
                                            sleep = normalDistribution.sample();
                                            if (sleep < max) {
                                                sleep = (double) max * 1000.0;
                                            } else {
                                                sleep = sleep * 1000.0;
                                            }
                                        } else {
                                            sleep = logisticDistribution.sample();
                                            if (sleep < max) {
                                                sleep = (double) max * 1000.0;
                                            } else {
                                                sleep = sleep * 1000.0;
                                            }
                                        }
                                        break;
                                    case "Beta":
                                        sleep = betaDistribution.sample() * 1000.0;
                                        sleep = sleep * max;
                                        break;
                                    case "Gamma":
                                        sleep = gammaDistribution.sample() * 1000.0;
                                        break;
                                    case "Uniform":
                                        sleep = (double) uniformIntegerDistribution.sample() * 1000.0;
                                        break;
                                    case "Weibull":
                                        sleep = weibullDistribution.sample() * 1000.0;
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
                                //Receives the product
                                product = (Product) inExchanger.exchange(null);
                                inSemaphore.acquire();
                                controlSemaphore.acquire();
                                //Once the product is in but is not being processed, the time is running and is considered iddle time
                                in++;
                                //Calculates the busy and idle time.
                                pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                pctBusy = Double.isNaN(pctBusy) ? 0 : pctBusy;
                                pctBusy = Math.round(pctBusy * 100.0) / 100.0;
                                item.getServer().setPctBusyTime(pctBusy);
                                item.getServer().setInServer(in);
                                controlSemaphore.release();

                                //Simulates the setup time the server needs to process a product
                                Thread.sleep(setUpTimeServer.longValue());

                                controlSemaphore.acquire();
                                //The set up time is considered as idle time, so it recalculates the total idle time
                                now = (double) System.currentTimeMillis();
                                totalIdle = totalIdle + (now - currentIdle);
                                currentBusy = now;

                                //Once the server is already setup, the server will process the product, so we change the control variables
                                idleOrBusy = 1;
                                item.getServer().setIdleOrBusy(idleOrBusy);
                                item.getServer().setLastTimeBusy(now);
                                item.getServer().setTotalIdle(totalIdle);
                                controlSemaphore.release();

                                //Simulates the processing time
                                Thread.sleep(sleep.longValue());

                                //The product has been processed, so it recalculates the total busy time
                                controlSemaphore.acquire();
                                now = (double) System.currentTimeMillis();
                                totalBusy = totalBusy + (now - currentBusy);

                                //Once the server has processed the product, we change the control variables to iddle mode
                                currentIdle = now;
                                idleOrBusy = 0;
                                item.getServer().setTotalBusy(totalBusy);
                                item.getServer().setIdleOrBusy(idleOrBusy);
                                item.getServer().setLastTimeIdle(now);
                                controlSemaphore.release();

                                switch (sendToStrategy) {
                                    case "Aleatorio (lo manda independientemente de si hay hueco o no)":
                                        //Selects a random channel to send the product
                                        var sendTo = random.nextInt(outSemaphores.size());
                                        //If the channel is not available, it is "sent" but "lost"
                                        if (!accessInSemaphores.get(sendTo).tryAcquire() && !Thread.currentThread().isInterrupted()) {
                                            controlSemaphore.acquire();
                                            in--;
                                            total++;
                                            item.getServer().setInServer(in);
                                            item.getServer().setOutServer(total);
                                            controlSemaphore.release();
                                        } else {
                                            //If the channel is available, it sends the product
                                            outExchanger.get(sendTo).exchange(product);
                                            controlSemaphore.acquire();
                                            in--;
                                            total++;
                                            item.getServer().setInServer(in);
                                            item.getServer().setOutServer(total);
                                            controlSemaphore.release();
                                            outSemaphores.get(sendTo).release();
                                        }
                                        break;
                                    case "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                        var randomNumber = random.nextInt(100) + 1;
                                        sendTo = 0;
                                        //Selects a random channel to send the product considering the percentages
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
                                        outExchanger.get(sendTo).exchange(product);
                                        controlSemaphore.acquire();
                                        in--;
                                        total++;
                                        item.getServer().setInServer(in);
                                        item.getServer().setOutServer(total);
                                        controlSemaphore.release();
                                        outSemaphores.get(sendTo).release();
                                        break;
                                    case "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)":
                                        //Selects a random channel to send the product
                                        sendTo = random.nextInt(outSemaphores.size());
                                        //Waits for the channel to be available to send the product
                                        accessInSemaphores.get(sendTo).acquire();
                                        outExchanger.get(sendTo).exchange(product);
                                        controlSemaphore.acquire();
                                        in--;
                                        total++;
                                        item.getServer().setInServer(in);
                                        item.getServer().setOutServer(total);
                                        controlSemaphore.release();
                                        outSemaphores.get(sendTo).release();
                                        break;
                                    case "Primera conexión disponible":
                                        sendTo = 0;
                                        //Stays in this loop until there is a slot in the next item
                                        while (!accessInSemaphores.get(sendTo).tryAcquire() && !Thread.currentThread().isInterrupted()) {
                                            if (outSemaphores.size() > (sendTo + 1)) {
                                                sendTo++;
                                            } else {
                                                sendTo = 0;
                                            }
                                        }
                                        outExchanger.get(sendTo).exchange(product);
                                        controlSemaphore.acquire();
                                        in--;
                                        total++;
                                        item.getServer().setInServer(in);
                                        item.getServer().setOutServer(total);
                                        controlSemaphore.release();
                                        outSemaphores.get(sendTo).release();
                                        break;
                                    case "A la cola más pequeña (si está llena espera hasta que haya hueco)":
                                        var products = Double.POSITIVE_INFINITY;
                                        var queueProducts = 0;
                                        var smallestQueue = 0;
                                        Boolean filled=true;

                                        while (!Thread.currentThread().isInterrupted() && filled) {
                                            filled=true;
                                            for (var index = 0; index < queues.size(); index++) {
                                                if (queues.get(index).getTypeItem().equals("Queue")) {
                                                    queues.get(index).getControlDestinationSemaphore().acquire();
                                                    queueProducts = simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue() == null ? 0 : simulation.get(queues.get(index).getIdentifier()).getQueue().getInQueue();
                                                    if (simulation.get(queues.get(index).getIdentifier()).getQueue().getCapacityQueue().equals("Ilimitados") || Integer.parseInt(simulation.get(queues.get(index).getIdentifier()).getQueue().getCapacityQueue())>queueProducts){
                                                        if (queueProducts < products) {
                                                            products = queueProducts;
                                                            smallestQueue = index;
                                                            filled=false;
                                                        }
                                                    }
                                                    queues.get(index).getControlDestinationSemaphore().release();
                                                }
                                                else if (queues.get(index).getTypeItem().equals("Sink")) {
                                                    smallestQueue = index;
                                                    products=0;
                                                    filled=false;
                                                    break;
                                                }
                                            }
                                        }

                                        accessInSemaphores.get(smallestQueue).acquire();
                                        outExchanger.get(smallestQueue).exchange(product);
                                        controlSemaphore.acquire();
                                        in--;
                                        total++;
                                        item.getServer().setInServer(in);
                                        item.getServer().setOutServer(total);
                                        controlSemaphore.release();
                                        outSemaphores.get(smallestQueue).release();
                                        break;
                                }
                            }
                        } catch (InterruptedException e) {
                            //When is interrupted it saves the state of the simulation
                            interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                            //Calculates the total busy and idle time when the simulation has been interrupted
                            totalIdle = item.getServer().getTotalIdle() != null ? item.getServer().getTotalIdle() : 0.0;
                            totalBusy = item.getServer().getTotalBusy() != null ? item.getServer().getTotalBusy() : 0.0;
                            var lastTimeIdle = item.getServer().getLastTimeIdle() != null ? item.getServer().getLastTimeIdle() : 0;
                            var lastTimeBusy = item.getServer().getLastTimeBusy() != null ? item.getServer().getLastTimeBusy() : 0;
                            pctBusy = 0.0;
                            if (totalBusy != 0) {
                                if (item.getServer().getIdleOrBusy() == 0) {
                                    totalIdle = totalIdle + ((double) System.currentTimeMillis() - lastTimeIdle);
                                    pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                } else {
                                    totalBusy = totalBusy + ((double) System.currentTimeMillis() - lastTimeBusy);
                                    pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                }
                                pctBusy = Math.round(pctBusy * 100.0) / 100.0;
                                item.getServer().setPctBusyTime(pctBusy);
                            }
                            interruptedAndSavedTheadsState.set(indexState, true);
                            interruptedAndSavedTheadsStateSemaphore.release();
                        }
                    });
                    itemList.add(server);
                    totalThreads.add(server);
                    break;
                case "Sink":
                    finalI = i;
                    var sink = new Thread(() -> {
                        //Initialise the semaphore that controls the correct state of the simulation when it is interrupted
                        interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                        interruptedAndSavedTheadsState.add(false);
                        var indexState = interruptedAndSavedTheadsState.size() - 1;
                        interruptedAndSavedTheadsStateIndex++;
                        interruptedAndSavedTheadsStateSemaphore.release();

                        var productsInSink = 0;

                        //Initialises the semaphores of the sink
                        var inSemaphoreType = getSemaphore(semaphoresItem, "In");
                        var accessOutType = getSemaphore(semaphoresItem, "AccessOut");
                        var controlSemaphoreType = getSemaphore(semaphoresItem, "Control");
                        var inExchanger = getExchangers(semaphoresItem, "In").get(0);

                        Semaphore inSemaphore = null;
                        Semaphore controlSemaphore = null;
                        Semaphore accessOutSemaphore = null;

                        if (inSemaphoreType != null) {
                            accessOutSemaphore = accessOutType.getSemaphores().get(0);
                            inSemaphore = inSemaphoreType.getSemaphores().get(0);
                            controlSemaphore = controlSemaphoreType.getSemaphores().get(0);
                        }

                        try {
                            //This loop will be executing until the simulation stops
                            while (true) {
                                //Receives all objects from the server
                                accessOutSemaphore.release();
                                inExchanger.exchange(null);
                                inSemaphore.acquire();
                                controlSemaphore.acquire();
                                productsInSink++;
                                item.getSink().setInSink(productsInSink);
                                controlSemaphore.release();
                            }
                        } catch (InterruptedException e) {
                            //When is interrupted it saves the state of the simulation
                            interruptedAndSavedTheadsStateSemaphore.acquireUninterruptibly();
                            item.getSink().setInSink(productsInSink);
                            interruptedAndSavedTheadsState.set(indexState, true);
                            interruptedAndSavedTheadsStateSemaphore.release();
                        }
                    });
                    itemList.add(sink);
                    totalThreads.add(sink);
            }
        }

        //We start all threads that has been stored that together they form the simulation
        for (Thread hilo : itemList) {
            hilo.start();
        }

        //This thread will be the responsible for updating and sending the data of the items (the simulation) through the open websocket
        var daemonThread = new Thread(() -> {
            try {
                //Get the status of the simulation
                String simulating = simulationService.findById(simulationId).get().getStatusSimulation();

                List<Semaphore> semaphoreList = new ArrayList<>();

                //Gets all the control semaphores of the items
                for (SemaphoreAsignation semaphoreAsignation : semaphoreAsignationList) {
                    semaphoreList.add(getSemaphore(semaphoreAsignation, "Control").getSemaphores().get(0));
                }

                //As long as the user does not stop the simulation this thread keeps sending and updating the data
                while (simulating.equals("1")) {
                    for (var i = 0; i < simulation.size(); i++) {
                        var item = simulation.get(i);
                        semaphoreList.get(i).acquire();
                        //Updates the total busy and idle time of the servers of the simulation
                        if (simulation.get(i).getItem().getDescription().equals("Server")) {
                            var totalIdle = item.getServer().getTotalIdle() != null ? item.getServer().getTotalIdle() : 0.0;
                            var totalBusy = item.getServer().getTotalBusy() != null ? item.getServer().getTotalBusy() : 0.0;
                            var lastTimeIdle = item.getServer().getLastTimeIdle() != null ? item.getServer().getLastTimeIdle() : 0;
                            var lastTimeBusy = item.getServer().getLastTimeBusy() != null ? item.getServer().getLastTimeBusy() : 0;
                            var pctBusy = 0.0;
                            if (lastTimeBusy != 0) {
                                if (item.getServer().getIdleOrBusy() == 0) {
                                    totalIdle = totalIdle + ((double) System.currentTimeMillis() - lastTimeIdle);
                                    pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                } else {
                                    totalBusy = totalBusy + ((double) System.currentTimeMillis() - lastTimeBusy);
                                    pctBusy = (totalBusy / (totalBusy + totalIdle)) * 100.0;
                                }
                                pctBusy = Math.round(pctBusy * 100.0) / 100.0;
                                item.getServer().setPctBusyTime(pctBusy);
                            }
                        }
                        semaphoreList.get(i).release();
                    }
                    //Sends the data through the websocket
                    simpMessageSendingOperations.convertAndSend("/simulationInfo/" + simulationId, simulation);

                    //Waits 0,5 seconds to execute the code again to avoid an overload
                    Thread.sleep(500);

                    //Checks if the user did not stop the simulation
                    simulating = simulationService.findById(simulationId).get().getStatusSimulation();
                }

                //If the simulation has been interrupted, we interrupt all the active threads of the simulation
                for (Thread thread : totalThreads) {
                    thread.interrupt();
                }

                //It waits until all threads has been properly interrupted
                Boolean allStopped = false;
                while (!allStopped) {
                    interruptedAndSavedTheadsStateSemaphore.acquire();
                    for (Boolean bool : interruptedAndSavedTheadsState) {
                        if (bool) {
                            allStopped = true;
                        } else {
                            allStopped = false;
                            break;
                        }
                    }
                    interruptedAndSavedTheadsStateSemaphore.release();
                    Thread.sleep(200);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        daemonThread.start();

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
        } else if (timeStrategy.length() > 8 && timeStrategy.substring(0, 7).equals("Poisson")) {
            var numbers = timeStrategy.substring(8, timeStrategy.length() - 1);
            var numberList = new ArrayList<Integer>();
            numberList.add(Integer.valueOf(numbers));
            timeStrategyMap.put("Poisson", numberList);
            return timeStrategyMap;
        } else if (timeStrategy.length() > 7 && timeStrategy.substring(0, 6).equals("NegExp")) {
            var numbers = timeStrategy.substring(7, timeStrategy.length() - 1);
            var numberList = new ArrayList<Integer>();
            numberList.add(Integer.valueOf(numbers));
            timeStrategyMap.put("NegExp", numberList);
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

    private List<Double> getPercentages(ItemDTO item) {
        List<Double> percentagesList = new ArrayList<>();
        Double cumulativePercentage = 0.0;
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
