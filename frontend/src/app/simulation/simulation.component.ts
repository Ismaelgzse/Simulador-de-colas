import {AfterViewInit, Component, ElementRef, Inject, NgZone, OnDestroy, OnInit, ViewChild} from "@angular/core";
import {HomeService} from "../home/home.service";
import {DOCUMENT} from "@angular/common";
import {ItemContainerModel} from "./Items/itemContainer.model";
import {SimulationService} from "./simulation.service";
import {ActivatedRoute, NavigationEnd, NavigationStart, Route, Router} from "@angular/router";
import {QueueModel} from "./Items/queue.model";
import {SinkModel} from "./Items/sink.model";
import {SourceModel} from "./Items/source.model";
import {ServerModel} from "./Items/server.model";
import {ItemModel} from "./Items/item.model";
import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ConnectionModel} from "./Connection/connection.model";
import {refresh} from "../app.component";
import {quickSimulationFormDTOModel} from "./QuickSimulationFormDTO/quickSimulationFormDTO.model";


function totalAmountQueue(max: number, component: any): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    if (component) {
      if (component.editQueueForm) {
        if (component.editQueueForm.controls) {
          //Checks if we need to verify the total amount of the percentages
          if (component.editQueueForm.controls.sendToStrategyQueue.value === "Porcentaje") {
            let total = 0;

            //Number of fields of connections
            let lengthControl = Object.keys(control.value)
              .map(key => 0)
              .reduce((a, b) => a + 1, 0);

            //Won't let the user put a number under 0 or over 100
            for (let i = 0; i < lengthControl; i++) {
              if (Number(control.value[i])) {
                if (parseFloat(control.value[i]) > 100 || parseFloat(control.value[i]) < 0) {
                  return {'invalid': true};
                }
              } else {
                return {'invalid': true};
              }
            }

            //The total amount need to be 100 in total
            const totalAmount = Object.keys(control.value)
              .map(key => parseFloat(control.value[key]) || 0)
              .reduce((a, b) => a + b, 0);

            //We help the user to reach 100
            if (totalAmount < 100 && lengthControl > 1) {
              let final = control.value[lengthControl - 1]
              final = totalAmount - final
              component.editQueueForm.controls['percentagesQueue'].controls[lengthControl - 1].setValue(max - final)
            }
            if (totalAmount > 100) {
              let final = control.value[lengthControl - 1]
              final = totalAmount - final
              component.editQueueForm.controls['percentagesQueue'].controls[lengthControl - 1].setValue(max - final)
            }

            if (totalAmount != 100 && lengthControl === 1) {
              return {'invalid': true};
            }

            //if everything is correct we sets the error off
            component.editQueueForm.controls['sendToStrategyQueue'].setErrors(null);
            return null;
          }
        }
      }
    }
    return null;
  };
}

function totalAmountStrategyQueue(max: number, component: any): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    let input = control.value;
    //If the strategy is percentage-based, we allow the user the possibility of choosing the percentages
    if (input === "Porcentaje") {
      component.showConnections = true;
      if (component) {
        if (component.editQueueForm) {
          if (component.editQueueForm.controls) {
            if (component.editQueueForm.controls.percentagesQueue.value) {
              const totalAmount = Object.keys(component.editQueueForm.controls.percentagesQueue.value)
                .map(key => parseFloat(component.editQueueForm.controls.percentagesQueue.value[key]) || 0)
                .reduce((a, b) => a + b, 0);

              return totalAmount != max ? {'invalid': true} : null;
            }
          }
        }
      }
    } else {
      //If the strategy is not percentage-based, we set the errors to null
      component.showConnections = false;
      if (component) {
        if (component.editQueueForm) {
          if (component.editQueueForm.controls) {
            if (component.editQueueForm.controls.percentagesQueue.value) {
              component.editQueueForm.controls['percentagesQueue'].setErrors(null);
            }
          }
        }
      }
    }
    return null;
  }
}


function totalAmountServer(max: number, component: any): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    if (component) {
      if (component.editServerForm) {
        if (component.editServerForm.controls) {
          //Checks if we need to verify the total amount of the percentages
          if (component.editServerForm.controls.sendToStrategyServer.value === "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)") {
            let total = 0;

            //Number of fields of connections
            let lengthControl = Object.keys(control.value)
              .map(key => 0)
              .reduce((a, b) => a + 1, 0);

            //Won't let the user put a number under 0 or over 100
            for (let i = 0; i < lengthControl; i++) {
              if (Number(control.value[i])) {
                if (parseFloat(control.value[i]) > 100 || parseFloat(control.value[i]) < 0) {
                  return {'invalid': true};
                }
              } else {
                return {'invalid': true};
              }
            }

            //The total amount need to be 100 in total
            const totalAmount = Object.keys(control.value)
              .map(key => parseFloat(control.value[key]) || 0)
              .reduce((a, b) => a + b, 0);

            //We help the user to reach 100
            if (totalAmount < 100 && lengthControl > 1) {
              let final = control.value[lengthControl - 1]
              final = totalAmount - final
              component.editServerForm.controls['percentagesServer'].controls[lengthControl - 1].setValue(max - final)
            }
            if (totalAmount > 100) {
              let final = control.value[lengthControl - 1]
              final = totalAmount - final
              component.editServerForm.controls['percentagesServer'].controls[lengthControl - 1].setValue(max - final)
            }

            if (totalAmount != 100 && lengthControl === 1) {
              return {'invalid': true};
            }

            //if everything is correct we sets the error off
            component.editServerForm.controls['sendToStrategyServer'].setErrors(null);
            return null;
          }
        }
      }
    }
    return null;
  };
}

function totalAmountStrategyServer(max: number, component: any): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    let input = control.value;

    //If the strategy is percentage-based, we allow the user the possibility of choosing the percentages
    if (input === "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)") {
      component.showConnections = true;
      if (component) {
        if (component.editServerForm) {
          if (component.editServerForm.controls) {
            if (component.editServerForm.controls.percentagesServer.value) {
              const totalAmount = Object.keys(component.editServerForm.controls.percentagesServer.value)
                .map(key => parseFloat(component.editServerForm.controls.percentagesServer.value[key]) || 0)
                .reduce((a, b) => a + b, 0);

              return totalAmount != max ? {'invalid': true} : null;
            }
          }
        }
      }
    } else {
      //If the strategy is not percentage-based, we set the errors to null
      component.showConnections = false;
      if (component) {
        if (component.editServerForm) {
          if (component.editServerForm.controls) {
            if (component.editServerForm.controls.percentagesServer.value) {
              component.editServerForm.controls['percentagesServer'].setErrors(null);
            }
          }
        }
      }
    }
    return null;
  }
}


function totalAmountSource(max: number, component: any): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    if (component) {
      if (component.editSourceForm) {
        if (component.editSourceForm.controls) {
          //Checks if we need to verify the total amount of the percentages
          if (component.editSourceForm.controls.sendToStrategySource.value === "Porcentaje (si no hay hueco se envia aunque se pierda)" || component.editSourceForm.controls.sendToStrategySource.value === "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)") {
            let total = 0;

            //Number of fields of connections
            let lengthControl = Object.keys(control.value)
              .map(key => 0)
              .reduce((a, b) => a + 1, 0);

            //Won't let the user put a number under 0 or over 100
            for (let i = 0; i < lengthControl; i++) {
              if (Number(control.value[i])) {
                if (parseFloat(control.value[i]) > 100 || parseFloat(control.value[i]) < 0) {
                  return {'invalid': true};
                }
              } else {
                return {'invalid': true};
              }
            }

            //The total amount need to be 100 in total
            const totalAmount = Object.keys(control.value)
              .map(key => parseFloat(control.value[key]) || 0)
              .reduce((a, b) => a + b, 0);

            //We help the user to reach 100
            if (totalAmount < 100 && lengthControl > 1) {
              let final = control.value[lengthControl - 1]
              final = totalAmount - final
              component.editSourceForm.controls['percentagesSource'].controls[lengthControl - 1].setValue(max - final)
            }
            if (totalAmount > 100) {
              let final = control.value[lengthControl - 1]
              final = totalAmount - final
              component.editSourceForm.controls['percentagesSource'].controls[lengthControl - 1].setValue(max - final)
            }

            if (totalAmount != 100 && lengthControl === 1) {
              return {'invalid': true};
            }

            //if everything is correct we sets the error off
            component.editSourceForm.controls['sendToStrategySource'].setErrors(null);
            return null;
          }
        }
      }
    }
    return null;
  };
}

function totalAmountStrategySource(max: number, component: any): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    let input = control.value;
    //If the strategy is percentage-based, we allow the user the possibility of choosing the percentages
    if (input === "Porcentaje (si no hay hueco se envia aunque se pierda)" || input === "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)") {
      component.showConnections = true;
      if (component) {
        if (component.editSourceForm) {
          if (component.editSourceForm.controls) {
            if (component.editSourceForm.controls.percentagesSource.value) {
              const totalAmount = Object.keys(component.editSourceForm.controls.percentagesSource.value)
                .map(key => parseFloat(component.editSourceForm.controls.percentagesSource.value[key]) || 0)
                .reduce((a, b) => a + b, 0);

              return totalAmount != max ? {'invalid': true} : null;
            }
          }
        }
      }
    } else {
      //If the strategy is not percentage-based, we set the errors to null
      component.showConnections = false;
      if (component) {
        if (component.editSourceForm) {
          if (component.editSourceForm.controls) {
            if (component.editSourceForm.controls.percentagesSource.value) {
              component.editSourceForm.controls['percentagesSource'].setErrors(null);
            }
          }
        }
      }
    }
    return null;
  }
}


@Component({
  selector: 'app-simulation',
  templateUrl: './simulation.component.html',
  styleUrls: ['../../assets/css/home.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css', '../../assets/css/simulation.css'
  ],
  providers: [SimulationService, HomeService]
})


export class SimulationComponent implements AfterViewInit, OnInit, OnDestroy {
  numConnections: number;
  showConnections: boolean;
  inputControls: FormControl[] = [];

  //List of strategies avaliable for each item
  listSendToStrategiesQueue = ["Aleatorio", "Primera conexión disponible", "Porcentaje"];
  listSendToStrategiesSource = ["Aleatorio (lo manda independientemente de si hay hueco o no)", "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)", "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)", "Porcentaje (si no hay hueco se envia aunque se pierda)", "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)", "A la cola más pequeña (si está llena espera hasta que haya hueco)"];
  listSendToStrategiesServer = ["Aleatorio (lo manda independientemente de si hay hueco o no)", "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)", "Primera conexión disponible", "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)", "A la cola más pequeña (si está llena espera hasta que haya hueco)"];

  //Variable related to connection messages
  //0: no error
  //1: itself
  //2: already exist
  //3: invalid simulation structure
  errorConnection: number;

  simulating: boolean;
  quickSimulating: boolean;

  referenceModal: any;
  loading:boolean;
  stageQuickSimulating: number;
  connectionModal: ConnectionModel;
  listConnections: ConnectionModel[]
  listConnectionsBackUp: ConnectionModel[]
  connectionInfo: ConnectionModel;
  correctSinkShown: boolean;
  correctSourceSown: boolean;
  correctServerShown: boolean;
  correctQueueShown: boolean;
  sameElement: string
  blackScreen: boolean;

  //Stores a pair of item that makes a connection
  listItemConnection: ItemModel[];

  startSimulation: number;
  intervalId: number;

  typeInterArrivalTime: number;
  typeServiceTime: number;

  sourceItemAuxForm: ItemModel;
  queueItemAuxForm: ItemModel;
  sinkItemAuxForm: ItemModel;

  quickSimulationDTO: quickSimulationFormDTOModel;

  listItemsTemplate: ItemContainerModel[];

  countDownText: string;
  percentageCountDownText: string;
  countDownTimeQuickSimulation: number;

  listItems: ItemContainerModel[]
  id: number;
  simulationTitle: string;
  queueInfo: QueueModel;
  sinkInfo: SinkModel;
  sourceInfo: SourceModel;
  serverInfo: ServerModel;
  itemInfo: ItemModel;
  itemContainerInfo: ItemContainerModel;
  itemContainerModal: ItemContainerModel;
  listNames: string[];
  timer: string;
  listInterArrivalTypes = ["Determinista", "Exponencial", "General"];
  listProbDeterministicFunc = ["10", "mins(10)", "hr(0.5)"];
  listProbExpFunc = ["NegExp(10)"];
  listProbGeneralFunc = ["Poisson(10)", "Triangular(5,19,10)", "LogNormal(10,2)", "Binomial(5,0.15)", "Max(0,Normal(10,1))",
    "Beta(10,1,1)", "Gamma(10,2)", "Max(0,Logistic(10,1))", "Uniform(5,15)", "Weibull(10,2)"];

  listProbFunc = ["NegExp(10)", "Poisson(10)", "Triangular(5,19,10)", "LogNormal(10,2)", "Binomial(5,0.15)", "Max(0,Normal(10,1))",
    "Beta(10,1,1)", "Gamma(10,2)", "Max(0,Logistic(10,1))", "Uniform(5,15)", "Weibull(10,2)",
    "10", "mins(10)", "hr(0.5)"];

  listProbFuncGuide = ["NegExp(Media)", "Poisson(Lambda)", "Triangular(Límite inferior,Modo,Límite superior)", "LogNormal(Escala,Forma)", "Binomial(Ensayos,p)", "Max(0,Normal(Media,Desviación típica))",
    "Beta(Alfa,Beta,Max. valor)", "Gamma(Escala,Forma)", "Max(0,Logistic(mu,s))", "Uniform(Min. valor,Max. valor)", "Weibull(Alfa,Beta)",
    "Valor entero (segundos)", "mins(Número minutos)", "hr(Número de horas)"];

  quickSimulationForm = new FormGroup({
    timeSimulation: new FormControl(0, Validators.compose([Validators.required, Validators.min(1), Validators.max(180)])),
    numberSimulations: new FormControl(0, Validators.compose([Validators.required, Validators.min(1), Validators.max(5), Validators.pattern("^[0-9]*$")])),
    pdfFormat: new FormControl(false),
    csvFormat: new FormControl(false)
  })

  editSourceForm = new FormGroup({
    nameSource: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), (control) => this.validateName(control, this.listNames)])),
    interArrivalTimeSource: new FormControl('', Validators.compose([Validators.required, (control) => this.validateProbFunc(control, this)])),
    numberProductsSource: new FormControl('', Validators.compose([Validators.required, this.validateFormatNumberProducts])),
    sendToStrategySource: new FormControl('', Validators.compose([Validators.required, totalAmountStrategySource(100, this)])),
    percentagesSource: new FormGroup({}, totalAmountSource(100, this))
  })

  editServerForm = new FormGroup({
    nameServer: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), (control) => this.validateName(control, this.listNames)])),
    setUpTimeServer: new FormControl('', Validators.compose([Validators.required, Validators.min(0), Validators.pattern("^[0-9]*$")])),
    cycletimeServer: new FormControl('', Validators.compose([Validators.required, (control) => this.validateProbFunc(control, this)])),
    sendToStrategyServer: new FormControl('', Validators.compose([Validators.required, totalAmountStrategyServer(100, this)])),
    percentagesServer: new FormGroup({}, totalAmountServer(100, this))
  })

  editQueueForm = new FormGroup({
    nameQueue: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), (control) => this.validateName(control, this.listNames)])),
    capacityQueue: new FormControl('', Validators.compose([Validators.required, this.validateFormatNumberProducts])),
    queueDiscipline: new FormControl('', Validators.compose([Validators.required, this.validateQueueDiscipline])),
    sendToStrategyQueue: new FormControl('', Validators.compose([Validators.required, totalAmountStrategyQueue(100, this)])),
    percentagesQueue: new FormGroup({}, totalAmountQueue(100, this))
  })

  editSinkForm = new FormGroup({
    nameSink: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), , (control) => this.validateName(control, this.listNames)]))
  })

  customTemplateForm = new FormGroup({
    interArrivalTimeSourceType: new FormControl('', Validators.compose([Validators.required])),
    interArrivalTimeSourceCkeck: new FormControl('', Validators.compose([Validators.required, (control) => this.validateProbFunc(control, this)])),
    serviceTimeType: new FormControl('', Validators.compose([Validators.required])),
    serviceTime: new FormControl('', Validators.compose([Validators.required, (control) => this.validateProbFunc(control, this)])),
    numberOfServers: new FormControl('', Validators.compose([Validators.required, Validators.min(1), Validators.max(5), Validators.pattern("^[0-9]*$")])),
    capacityQueueCheck: new FormControl('', Validators.compose([Validators.required, this.validateFormatNumberProducts])),
    queueDisciplineCheck: new FormControl('', Validators.compose([Validators.required, this.validateQueueDiscipline]))
  })


  constructor(private modalService: NgbModal, @Inject(DOCUMENT) document: Document, public homeService: HomeService, public simulationService: SimulationService, private router: Router, private route: ActivatedRoute, private ngZone: NgZone) {
  }

  //When the user exits the component, this method is necessary to take the necessary steps to control the state of the simulation
  ngOnDestroy(): void {
    this.homeService.isAuthenticated().subscribe({
      next: (success) => {
        if (success) {
          this.simulationService.getStatusSimulation(this.id).subscribe({
            //If a simulation is running, it stops it
            next: (status) => {
              if (status) {
                this.simulationService.sendMessage(this.id.toString(), "stop")
              }
              this.simulationService.closeConnection();
            },
            error: (err) => {
              if (this.simulating) {
                this.simulationService.sendMessage(this.id.toString(), "stop");
                this.simulationService.closeConnection();
              }
            }
          })
        }
      }
    })
  }

  ngOnInit(): void {
    this.percentageCountDownText = "0%"
    this.loading=false;
    //Resets the error message and the alert
    let alertErrorMessage = document.getElementById("cancelConnect");
    // @ts-ignore
    let listClasses = alertErrorMessage.classList;
    if (listClasses.length > 1) {
      // @ts-ignore
      alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
      this.errorConnection = 0;
    }
    //Resets all variables
    this.quickSimulationDTO = {
      timeSimulation: 0,
      numberSimulations: 0,
      pdfFormat: false,
      csvFormat: false
    }
    this.listItemsTemplate = []
    this.typeServiceTime = -1;
    this.typeInterArrivalTime = -1;
    this.intervalId = 0;
    this.startSimulation = 0;
    this.timer = "0:00"
    this.simulating = false;
    this.quickSimulating = false;
    this.stageQuickSimulating = 0;
    this.showConnections = true
    this.errorConnection = 0;
    this.correctSinkShown = false;
    this.correctSourceSown = false;
    this.correctServerShown = false;
    this.correctQueueShown = false;
    this.sameElement = '';
    this.blackScreen = false;

    //The main variables and objects are initialised
    this.listItemConnection = [];
    this.listConnections = []
    this.queueInfo = {
      outQueue: 0,
      capacityQueue: "",
      disciplineQueue: "",
      inQueue: 0
    };
    this.serverInfo = {
      cicleTime: "",
      outServer: 0,
      setupTime: "",
      pctBusyTime: 0,
      inServer: 0
    };
    this.sourceInfo = {
      interArrivalTime: "",
      numberProducts: "",
      outSource: 0
    };
    this.sinkInfo = {
      inSink: 0
    };
    this.itemInfo = {
      description: "",
      name: "",
      positionX: 0,
      positionY: 0,
      sendToStrategy: "Random"
    };
    this.connectionInfo = {
      originItem: this.itemInfo,
      destinationItem: this.itemInfo,
      percentage: 0
    }
    this.connectionModal = this.connectionInfo
    this.itemContainerInfo = {
      item: this.itemInfo
    };
    this.itemContainerModal = {
      item: this.itemInfo,
      queue: this.queueInfo,
      server: this.serverInfo,
      sink: this.sinkInfo,
      source: this.sourceInfo
    }
    this.listNames = []
    this.listItems = [];

    this.route.params.subscribe({
      next: (params) => {
        this.loading=true;
        this.id = params['id'];
        //If the user refresh the component, it checks if the simulation was running
        if (refresh) {
          this.simulationService.getStatusSimulation(this.id).subscribe({
            next: (status) => {
              if (status) {
                //If was running, it stops it
                this.simulationService.connectAlt(this.id.toString(), "stop");
                this.simulating = false;
              } else {
                //If not it connects it
                this.simulationService.connect(this.id.toString());
              }
              //The simulation and its items and connections are loaded
              this.simulationService.getSimulationInfo(this.id).subscribe({
                next: (simulation) => {
                  this.simulationTitle = simulation.title;
                  this.simulationService.getItems(this.id).subscribe({
                    next: (items) => {
                      this.listItems = items;
                      for (let i = 0; i < this.listItems.length; i++) {
                        // @ts-ignore
                        for (let j = 0; j < this.listItems[i].connections.length; j++) {
                          // @ts-ignore
                          this.listConnections.push(this.listItems[i].connections[j]);
                        }
                      }
                      this.listConnectionsBackUp = this.listConnections;
                      this.loading=false;
                    },
                    error: (err) => {
                      this.router.navigate(['error403']);
                    }
                  })
                },
                error: (err) => {
                  this.router.navigate(['error403']);
                }
              })
            },
            error: (err) => {
              this.router.navigate(['error500']);
            }
          })
        } else {
          this.simulationService.connect(this.id.toString());

          //The simulation and its items and connections are loaded
          this.simulationService.getSimulationInfo(this.id).subscribe({
            next: (simulation) => {
              this.simulationTitle = simulation.title;
              this.simulationService.getItems(this.id).subscribe({
                next: (items) => {
                  this.listItems = items;
                  for (let i = 0; i < this.listItems.length; i++) {
                    // @ts-ignore
                    for (let j = 0; j < this.listItems[i].connections.length; j++) {
                      // @ts-ignore
                      this.listConnections.push(this.listItems[i].connections[j]);
                    }
                  }
                  this.listConnectionsBackUp = this.listConnections;
                  this.loading=false
                },
                error: (err) => {
                  this.router.navigate(['error403']);
                }
              })
            },
            error: (err) => {
              this.router.navigate(['error403']);
            }
          })
        }
      },
      error: (err) => {
        this.router.navigate(['error403']);
      }
    })
  }


  ngAfterViewInit(): void {
    //We assign to the canvas element of the html some events
    let destinationElement = document.getElementById("canvas")
    // @ts-ignore
    if (destinationElement !== null) {
      destinationElement.addEventListener("dragover", this.dragOver);
      //When we drop an element over canvas we execute a custom function
      // @ts-ignore
      destinationElement.addEventListener("drop", (event) => this.newElement(event, this));
    }
    //We assign to the images element of the html some events
    let imagenes = document.querySelectorAll(".image")
    for (var i = 0; i < imagenes.length; i++) {
      // @ts-ignore
      imagenes[i].addEventListener("dragstart", this.drag);
    }
  }

  drag(event: DragEvent) {
    // @ts-ignore
    event.dataTransfer.setData("text", event.target.id);
  }

  dragOver(event: DragEvent) {
    event.preventDefault()
  }

  resetErrors(){
    let alertErrorMessage = document.getElementById("cancelConnect");
    // @ts-ignore
    let listClasses = alertErrorMessage.classList;
    if (listClasses.length > 1) {
      // @ts-ignore
      alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
    }
    this.errorConnection=0;
  }

  newElement(event: DragEvent, simulationComponent: any) {
    if (event != undefined) {
      event.preventDefault();
      if (event.dataTransfer !== null) {
        this.resetErrors();
        //Get the data from the object that triggered the event
        var data = event.dataTransfer.getData("text")
        // @ts-ignore
        var parentClass = document.getElementById(data).parentElement.className;
        var element = document.getElementById(data)

        //If the object is from the side menu then we will create a new object
        if (parentClass === "dragItemContainer") {
          this.loading=true;
          let type = data.substring(0, 11)
          switch (type) {
            case "Fuennnnnnnn":
              this.sourceInfo.outSource = 0;
              this.sourceInfo.numberProducts = 'Ilimitados';
              this.sourceInfo.interArrivalTime = '10';
              this.itemInfo.name = '';
              this.itemInfo.description = 'Source';
              this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)";
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.source = this.sourceInfo;
              break;
            case "Colaaaaaaaa":
              this.queueInfo.capacityQueue = 'Ilimitados';
              this.queueInfo.disciplineQueue = 'Fifo';
              this.queueInfo.inQueue = 0;
              this.queueInfo.outQueue = 0;
              this.itemInfo.name = '';
              this.itemInfo.description = 'Queue';
              this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible";
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.queue = this.queueInfo;
              break;
            case "Procccccccc":
              this.serverInfo.outServer = 0;
              this.serverInfo.cicleTime = '10'
              this.serverInfo.setupTime = '0'
              this.itemInfo.name = '';
              this.itemInfo.description = 'Server';
              this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible";
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.server = this.serverInfo;
              break;
            case "Sumiiiiiiii":
              this.sinkInfo.inSink = 0;
              this.itemInfo.name = '';
              this.itemInfo.description = 'Sink';
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.sink = this.sinkInfo;
              break;
          }
          //Gets the position where the user dropped it
          // @ts-ignore
          this.itemContainerInfo.item.positionX = event.pageX - document.getElementById(data).offsetWidth * 1.7;
          // @ts-ignore
          this.itemContainerInfo.item.positionY = event.pageY - document.getElementById(data).offsetHeight * 2.4;

          //Saves the new item
          // @ts-ignore
          this.simulationService.newItem(this.id, this.itemContainerInfo).subscribe({
            next: (item) => {
              item.connections = []
              this.listItems.push(item);
              this.loading=false;
            },
            error: (err) => {
              this.router.navigate(['error500']);
            }
          });
          //If the item is not from the side menu, it means that it already exists and the user wants to move it
        } else {
          for (let i = 0; i < this.listItems.length; i++) {
            if (data === this.listItems[i].item.name) {
              this.itemContainerInfo = this.listItems[i];
            }
          }
          //Gets the position where the user dropped it
          // @ts-ignore
          this.itemContainerInfo.item.positionX = event.pageX - document.getElementById(data).offsetWidth * 1.5;
          // @ts-ignore
          this.itemContainerInfo.item.positionY = event.pageY - document.getElementById(data).offsetHeight * 2;

          if (this.itemContainerInfo.item.positionX < -30) {
            this.itemContainerInfo.item.positionX = -15
          }
          if (this.itemContainerInfo.connections === null) {
            this.itemContainerInfo.connections = [];
          }
          //Saves the existing item
          if (this.itemContainerInfo.item.idItem) {
            this.simulationService.updateItem(this.id, this.itemContainerInfo.item.idItem, this.itemContainerInfo).subscribe({
              next: (itemContainer) => {
                this.replaceItemByItsId(itemContainer);
                for (let i = 0; i < this.listConnections.length; i++) {
                  if (this.listConnections[i].originItem.idItem === itemContainer.item.idItem) {
                    this.listConnections[i].originItem.positionX = itemContainer.item.positionX;
                    this.listConnections[i].originItem.positionY = itemContainer.item.positionY;
                  } else if (this.listConnections[i].destinationItem.idItem === itemContainer.item.idItem) {
                    this.listConnections[i].destinationItem.positionX = itemContainer.item.positionX;
                    this.listConnections[i].destinationItem.positionY = itemContainer.item.positionY;
                  }
                }
              },
              error: (err) => {
                this.router.navigate(['error500']);
              }
            });
          }
        }
      }
    }
  }

  editItem() {
    this.resetErrors();
    this.loading=true;
    if (this.itemContainerModal.item.idItem) {
      switch (this.itemContainerModal.item.description) {
        //Gets the data of the item from the modal
        case "Source":
          this.itemContainerModal.item.name = <string>this.editSourceForm.value.nameSource;
          // @ts-ignore
          this.itemContainerModal.source.numberProducts = this.editSourceForm.value.numberProductsSource;
          // @ts-ignore
          this.itemContainerModal.source.interArrivalTime = this.editSourceForm.value.interArrivalTimeSource;
          // @ts-ignore
          this.itemContainerModal.item.sendToStrategy = this.editSourceForm.value.sendToStrategySource;
          if (this.itemContainerModal.connections) {
            for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
              // @ts-ignore
              this.itemContainerModal.connections[i].percentage = this.editSourceForm.value.percentagesSource[i];
              //Once the connection data has been stored, the Controls are removed from the FormGroup that is responsible for verifying the correct formatting of the percentages.
              (this.editSourceForm.get('percentagesSource') as FormGroup).removeControl(i.toString());
            }
          }
          break;
        case "Sink":
          // @ts-ignore
          this.itemContainerModal.item.name = this.editSinkForm.value.nameSink;
          break;
        case "Server":
          // @ts-ignore
          this.itemContainerModal.item.name = this.editServerForm.value.nameServer;
          // @ts-ignore
          this.itemContainerModal.server.setupTime = this.editServerForm.value.setUpTimeServer;
          // @ts-ignore
          this.itemContainerModal.server.cicleTime = this.editServerForm.value.cycletimeServer;
          // @ts-ignore
          this.itemContainerModal.item.sendToStrategy = this.editServerForm.value.sendToStrategyServer;
          if (this.itemContainerModal.connections) {
            for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
              // @ts-ignore
              this.itemContainerModal.connections[i].percentage = this.editServerForm.value.percentagesServer[i];
              //Once the connection data has been stored, the Controls are removed from the FormGroup that is responsible for verifying the correct formatting of the percentages.
              (this.editServerForm.get('percentagesServer') as FormGroup).removeControl(i.toString());
            }
          }
          break;
        case "Queue":
          // @ts-ignore
          this.itemContainerModal.item.name = this.editQueueForm.value.nameQueue;
          // @ts-ignore
          this.itemContainerModal.queue.disciplineQueue = this.editQueueForm.value.queueDiscipline;
          // @ts-ignore
          this.itemContainerModal.queue.capacityQueue = this.editQueueForm.value.capacityQueue;
          // @ts-ignore
          this.itemContainerModal.item.sendToStrategy = this.editQueueForm.value.sendToStrategyQueue;
          if (this.itemContainerModal.connections) {
            for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
              // @ts-ignore
              this.itemContainerModal.connections[i].percentage = this.editQueueForm.value.percentagesQueue[i];
              //Once the connection data has been stored, the Controls are removed from the FormGroup that is responsible for verifying the correct formatting of the percentages.
              (this.editQueueForm.get('percentagesQueue') as FormGroup).removeControl(i.toString());
            }
          }
      }
      //Saves the item updated
      this.simulationService.updateItem(this.id, this.itemContainerModal.item.idItem, this.itemContainerModal).subscribe({
        next: (itemContainer) => {
          this.replaceItemByItsId(itemContainer);
          this.loading=false;
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  //Resets these type of items
  resetServer() {
    this.serverInfo = {
      cicleTime: "",
      outServer: 0,
      setupTime: "",
      pctBusyTime: 0,
      inServer: 0
    };
    this.itemInfo = {
      description: "",
      name: "",
      positionX: 0,
      positionY: 0,
      sendToStrategy: "Primera conexión disponible"
    };
    this.itemContainerModal = {
      item: this.itemInfo,
      queue: this.queueInfo,
      server: this.serverInfo,
      sink: this.sinkInfo,
      source: this.sourceInfo
    }
  }

  //Resets these type of items
  resetSource() {
    this.sourceInfo = {
      interArrivalTime: "",
      numberProducts: "",
      outSource: 0
    };
    this.itemInfo = {
      description: "",
      name: "",
      positionX: 0,
      positionY: 0,
      sendToStrategy: "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)"
    };
    this.itemContainerModal = {
      item: this.itemInfo,
      queue: this.queueInfo,
      server: this.serverInfo,
      sink: this.sinkInfo,
      source: this.sourceInfo
    }
  }

  //Resets these type of items
  resetQueue() {
    this.queueInfo = {
      outQueue: 0,
      capacityQueue: "",
      disciplineQueue: "",
      inQueue: 0
    };
    this.itemInfo = {
      description: "",
      name: "",
      positionX: 0,
      positionY: 0,
      sendToStrategy: "Primera conexión disponible"
    };
    this.itemContainerModal = {
      item: this.itemInfo,
      queue: this.queueInfo,
      server: this.serverInfo,
      sink: this.sinkInfo,
      source: this.sourceInfo
    }
  }

  //Resets these type of items
  resetSink() {
    this.sinkInfo = {
      inSink: 0
    };
    this.itemInfo = {
      description: "",
      name: "",
      positionX: 0,
      positionY: 0,
      sendToStrategy: "Random"
    };
    this.itemContainerModal = {
      item: this.itemInfo,
      queue: this.queueInfo,
      server: this.serverInfo,
      sink: this.sinkInfo,
      source: this.sourceInfo
    }
  }

  customTemplateFormSubmit() {
    this.loading=true;
    this.sourceInfo.outSource = 0;
    this.sourceInfo.numberProducts = 'Ilimitados';
    // @ts-ignore
    this.sourceInfo.interArrivalTime = this.customTemplateForm.value.interArrivalTimeSourceCkeck;
    this.itemInfo.name = '';
    this.itemInfo.description = 'Source';
    this.itemContainerInfo.item = this.itemInfo;
    this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)";
    this.itemContainerInfo.source = this.sourceInfo;
    //Creates the source
    this.simulationService.newItem(this.id, this.itemContainerInfo).subscribe({
      next: (item) => {
        item.connections=[]
        this.listItemsTemplate.push(item);
        this.sourceItemAuxForm = item.item;
        this.resetSource();
        // @ts-ignore
        this.queueInfo.capacityQueue = this.customTemplateForm.value.capacityQueueCheck;
        // @ts-ignore
        this.queueInfo.disciplineQueue = this.customTemplateForm.value.queueDisciplineCheck;
        this.queueInfo.inQueue = 0;
        this.queueInfo.outQueue = 0;
        this.itemInfo.name = '';
        this.itemInfo.description = 'Queue';
        this.itemContainerInfo.item = this.itemInfo;
        this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible";
        this.itemContainerInfo.queue = this.queueInfo;
        //Creates the queue
        this.simulationService.newItem(this.id, this.itemContainerInfo).subscribe({
          next: (item) => {
            this.listItemsTemplate.push(item)
            this.queueItemAuxForm = item.item;
            this.connectionInfo.originItem = this.sourceItemAuxForm;
            this.connectionInfo.destinationItem = this.queueItemAuxForm;
            //Connects the source and the queue
            this.simulationService.newConnection(this.connectionInfo).subscribe({
              next: (connection) => {
                this.resetQueue();
                this.sinkInfo.inSink = 0;
                this.itemInfo.name = '';
                this.itemInfo.description = 'Sink';
                this.itemContainerInfo.item = this.itemInfo;
                this.itemContainerInfo.sink = this.sinkInfo;
                //Creates the sink
                this.simulationService.newItem(this.id, this.itemContainerInfo).subscribe({
                  next: async (item) => {
                    this.listItemsTemplate.push(item);
                    this.sinkItemAuxForm = item.item;
                    this.resetSink();
                    //Creates the necessary number of servers and connects them to the queue and sink
                    // @ts-ignore
                    for (let i = 0; i < this.customTemplateForm.value.numberOfServers; i++) {
                      this.serverInfo.outServer = 0;
                      // @ts-ignore
                      this.serverInfo.cicleTime = this.customTemplateForm.value.serviceTime;
                      this.serverInfo.setupTime = '0'
                      this.itemInfo.name = '';
                      this.itemInfo.description = 'Server';
                      this.itemContainerInfo.item = this.itemInfo;
                      this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible";
                      this.itemContainerInfo.server = this.serverInfo;

                      await new Promise<void>((resolve) => {
                        this.simulationService.newItem(this.id, this.itemContainerInfo).subscribe({
                          next: (item) => {
                            this.listItemsTemplate.push(item);
                            this.connectionInfo.originItem = item.item;
                            this.connectionInfo.destinationItem = this.sinkItemAuxForm;
                            this.simulationService.newConnection(this.connectionInfo).subscribe({
                              next: (connection) => {
                                this.connectionInfo.destinationItem = this.connectionInfo.originItem;
                                this.connectionInfo.originItem = this.queueItemAuxForm;
                                this.simulationService.newConnection(this.connectionInfo).subscribe({
                                  next: (connection) => {
                                    this.resetServer();
                                    resolve();
                                  }
                                })
                              }
                            })
                          }
                        });
                      });
                    }
                    //It places the positions of the items
                    this.resetPositionsTemplate(this.listItemsTemplate);
                    //Once the creation of the items is completed, the form is restarted
                    this.customTemplateForm.patchValue({
                      interArrivalTimeSourceType: '',
                      interArrivalTimeSourceCkeck: '',
                      numberOfServers: '',
                      serviceTime: '',
                      serviceTimeType: '',
                      queueDisciplineCheck: '',
                      capacityQueueCheck: ''
                    })
                  },
                  error: (err) => {
                    this.router.navigate(['error500']);
                  }
                })
              }
            })
          },
          error: (err) => {
            this.router.navigate(['error500']);
          }
        })
      },
      error: (err) => {
        this.router.navigate(['error500']);
      }
    })
  }

  //Method responsible for positioning the items in the canvas
  resetPositionsTemplate(listItemsTemplate: ItemContainerModel[]) {
    let x = -15;
    let y = 30;

    for (let i = 0; i < listItemsTemplate.length; i++) {
      if (listItemsTemplate[i].item.description === "Sink") {
        let x2 = x + 200
        listItemsTemplate[i].item.positionX = x2;
        listItemsTemplate[i].item.positionY = y;
      } else if (listItemsTemplate[i].item.description === "Server") {
        listItemsTemplate[i].item.positionX = x;
        listItemsTemplate[i].item.positionY = y;
        y = y + 150;
      } else {
        listItemsTemplate[i].item.positionX = x;
        listItemsTemplate[i].item.positionY = y;
        x = x + 200
      }
    }
    //When we already placed the positions of the items, we save and update them
    this.simulationService.updateAllItems(this.id, listItemsTemplate).subscribe({
      next: (listItemsTemplateUpdated) => {
        this.ngOnInit();
      },
      error: (err) => {
        this.router.navigate(['error500']);
      }
    })
  }

  //Method responsible for stopping the active simulation
  stopSimulating() {
    if (this.simulating) {
      //Go back to the normal screen, removing the blackscreen
      let blackCanvas = document.getElementById('blackScreen')
      // @ts-ignore
      blackCanvas.classList.toggle("showScreen")
      this.blackScreen = false;
      this.simulating = false;
      let timerElement = document.getElementById('timer')
      // @ts-ignore
      timerElement.classList.toggle("showTimer");
      //Stops the timer function
      clearInterval(this.intervalId);
      this.startSimulation = 0;
      //Sends a message through the websocket to stop the active simulation
      this.simulationService.sendMessage(this.id.toString(), "stop");
    }
  }

  //Method responsible for displaying the countdown of the quick simulation
  countDown(seconds: number) {
    let interval = 500;
    let timeRemaining = seconds;

    const timer = setInterval(() => {
      if (timeRemaining <= 0) {
        clearInterval(timer)
      } else {
        let remainingMin = Math.floor(timeRemaining / 60);
        let remainingSec = Math.trunc(timeRemaining % 60);
        timeRemaining = timeRemaining - 0.5;
        //The countdown text
        this.countDownText = remainingMin.toString().padStart(2, '0') + ":" + remainingSec.toString().padStart(2, '0');
        this.ngZone.run(() => {
          //It shows the progress bar progression
          this.percentageCountDownText = (((this.countDownTimeQuickSimulation - timeRemaining) * 100) / this.countDownTimeQuickSimulation).toFixed(2).toString() + "%";
        });
      }
    }, interval);
  }

  //Controls the quick simulation funcionality
  quickSimulationFunc() {
    //Asigns the values of the modal to the DTO responsible for the quick simulation
    // @ts-ignore
    this.quickSimulationDTO.timeSimulation = this.quickSimulationForm.value.timeSimulation;
    // @ts-ignore
    this.quickSimulationDTO.numberSimulations = this.quickSimulationForm.value.numberSimulations;
    // @ts-ignore
    this.quickSimulationDTO.pdfFormat = this.quickSimulationForm.value.pdfFormat;
    // @ts-ignore
    this.quickSimulationDTO.csvFormat = this.quickSimulationForm.value.csvFormat;

    //Clone and copy the existing simulation to simulate the different experiments in the backend
    let listSimulations = this.cloneSimulations(this.listItems, this.quickSimulationDTO.numberSimulations);
    this.quickSimulationDTO.listSimulations = listSimulations;

    //Calculate the time needed to simulate the experiment
    let timeSimulation = this.quickSimulationDTO.timeSimulation;
    // @ts-ignore
    if (timeSimulation !== undefined && timeSimulation < 30) {
      this.countDownTimeQuickSimulation = timeSimulation * 0.15 * 60;
    } else if (timeSimulation !== undefined && timeSimulation >= 30 && timeSimulation < 60) {
      this.countDownTimeQuickSimulation = timeSimulation * 0.1 * 60;
    } else {
      // @ts-ignore
      this.countDownTimeQuickSimulation = timeSimulation * 0.07 * 60;
    }

    //First check if there is an active simulation or an active quick simulation
    this.simulationService.getStatusQuickSimulation(this.id).subscribe({
      next: (statusQuickSimulation) => {
        this.simulationService.getStatusSimulation(this.id).subscribe({
          next: (status) => {
            //If no simulation is active, the countdown starts
            if (status === false && statusQuickSimulation === false) {
              this.countDown(this.countDownTimeQuickSimulation);

              //Whe sets the visuals needed
              if (!this.blackScreen) {
                let blackCanvas = document.getElementById('blackScreen')
                // @ts-ignore
                blackCanvas.classList.toggle("showScreen")
                this.blackScreen = true;
                this.quickSimulating = true;
              }
              let timerElement = document.getElementById('waitingExportationData')
              // @ts-ignore
              timerElement.classList.toggle("showExportScreen");

              //Starts the quick simulation
              this.stageQuickSimulating = 1;
              this.simulationService.quickSimulation(this.id, this.quickSimulationDTO).subscribe({
                next: (listSimulations) => {
                  if (listSimulations !== null) {
                    //When the quick simulation is finished, the data exportation starts
                    if (this.quickSimulationDTO.pdfFormat) {
                      this.stageQuickSimulating = 2;
                      this.simulationService.generatePDF(this.id, listSimulations).subscribe({
                        next: (pdf) => {
                          //The pdf is opened in a new tab
                          const blob = new Blob([pdf], {type: 'application/pdf'});
                          const url = window.URL.createObjectURL(blob);
                          window.open(url);

                          if (this.quickSimulationDTO.csvFormat) {
                            this.simulationService.generateExcel(this.id, listSimulations).subscribe({
                              next: (excel) => {
                                //The excel is downloaded
                                const url = window.URL.createObjectURL(excel);
                                const a = document.createElement('a');
                                a.href = url;
                                a.download = this.simulationTitle + '.xlsx';
                                document.body.appendChild(a);
                                a.click();
                                document.body.removeChild(a);
                                window.URL.revokeObjectURL(url);

                                //Visual effects are reset
                                if (this.blackScreen) {
                                  //Go back tho the normal screen, removing the blackscreen
                                  let blackCanvas = document.getElementById('blackScreen')
                                  // @ts-ignore
                                  blackCanvas.classList.toggle("showScreen")
                                  this.blackScreen = false;
                                  this.quickSimulating = false;
                                }
                                let timerElement = document.getElementById('waitingExportationData')
                                // @ts-ignore
                                timerElement.classList.toggle("showExportScreen");
                                this.stageQuickSimulating = 0;
                              }
                            })
                          } else {
                            //Visual effects are reset
                            if (this.blackScreen) {
                              //Go back tho the normal screen, removing the blackscreen
                              let blackCanvas = document.getElementById('blackScreen')
                              // @ts-ignore
                              blackCanvas.classList.toggle("showScreen")
                              this.blackScreen = false;
                              this.quickSimulating = false;
                            }
                            let timerElement = document.getElementById('waitingExportationData')
                            // @ts-ignore
                            timerElement.classList.toggle("showExportScreen");
                            this.stageQuickSimulating = 0;
                          }
                        }
                      })
                    } else if (this.quickSimulationDTO.csvFormat) {
                      this.simulationService.generateExcel(this.id, listSimulations).subscribe({
                        next: (excel) => {
                          //The excel is downloaded
                          const url = window.URL.createObjectURL(excel);
                          const a = document.createElement('a');
                          a.href = url;
                          a.download = this.simulationTitle + '.xlsx';
                          document.body.appendChild(a);
                          a.click();
                          document.body.removeChild(a);
                          window.URL.revokeObjectURL(url);

                          //Visual effects are reset
                          if (this.blackScreen) {
                            //Go back tho the normal screen, removing the blackscreen
                            let blackCanvas = document.getElementById('blackScreen')
                            // @ts-ignore
                            blackCanvas.classList.toggle("showScreen")
                            this.blackScreen = false;
                            this.quickSimulating = false;
                          }
                          let timerElement = document.getElementById('waitingExportationData')
                          // @ts-ignore
                          timerElement.classList.toggle("showExportScreen");
                          this.stageQuickSimulating = 0;
                        }
                      })
                    }
                  }
                },
                error: (err) => {
                  this.router.navigate(['error500']);
                }
              })
            }
            //If there is an active simulation, a modal is displayed
            else {
              this.modalService.open(this.referenceModal, {centered: true});
            }
          }
        })
      }
    })
  }

  simulate(content: any) {
    //Stores the reference of the modal
    this.referenceModal = content;

    this.loading=true;

    //checks if the simulation structure is valid
    if (this.checkSimulationStructure(this.listItems)) {
      //Checks if the simulation is already running
      this.simulationService.getStatusSimulation(this.id).subscribe({
        next: (status:boolean) => {
          this.simulationService.getStatusQuickSimulation(this.id).subscribe({
            next: (statusQuickSimulation) => {
              if (status === false && statusQuickSimulation === false) {

                //Whe sets the visuals needed
                if (!this.blackScreen) {
                  let blackCanvas = document.getElementById('blackScreen')
                  // @ts-ignore
                  blackCanvas.classList.toggle("showScreen")
                  this.blackScreen = true;
                  this.simulating = true;
                }
                let timerElement = document.getElementById('timer')
                // @ts-ignore
                timerElement.classList.toggle("showTimer");

                //Sets the timer
                if (this.simulating) {
                  this.startSimulation = Date.now() - (this.startSimulation > 0 ? this.startSimulation : 0);
                  // @ts-ignore
                  this.intervalId = setInterval(() => {
                    this.updateTimer();
                  }, 1000);
                }
                this.loading=false;
                //Sends a message through the websocket to start the active simulation
                this.simulationService.sendMessage(this.id.toString(), "start");

              } else {
                this.loading=false;
                //If there is an active simulation, a modal is displayed
                this.modalService.open(this.referenceModal, {centered: true});
              }
              if (status === true) {
                this.loading=false;
                //If there is an active simulation, sends a message through the websocket to stop the active simulation
                this.simulationService.sendMessage(this.id.toString(), "stop");
              }
            }
          })
        }
      })
    } else {
      this.loading=false;

      //if the simulation structure isn't valid, an error message is displayed
      let alertErrorMessage = document.getElementById("cancelConnect");
      // @ts-ignore
      let listClasses = alertErrorMessage.classList;
      if (listClasses.length <= 1) {
        // @ts-ignore
        alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
      }
      this.errorConnection = 3;
    }
  }

  //Updates the timer
  updateTimer() {
    let currentTime = Date.now() - this.startSimulation;
    let minutes = Math.floor(currentTime / (60 * 1000));
    let seconds = (Math.floor(currentTime / 1000) % 60);
    this.timer = `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }


  //Depth-first search algorithm
  checkSimulationStructure(listItems: ItemContainerModel[]) {
    //Gets all sinks, the project must have at least one sink to be a valid simulation
    let sinks = this.getSinks(listItems);
    //Inicialises a list of all visited items
    let visitedList = this.getVisitedList(listItems)
    if (sinks.length >= 1) {
      for (let i = 0; i < listItems.length; i++) {
        if (visitedList[i] === false && listItems[i].item.description === "Source") {
          this.checkSimulationStructureRec(listItems, visitedList, i)
        }
      }
      //Checks if all items have been visited
      if (this.checkAllVisited(visitedList)) {
        return true;
      }
    }
    return false;
  }

  //Checks if all items are visited
  private checkAllVisited(visitedList: any[]) {
    for (let i = 0; i < visitedList.length; i++) {
      if (visitedList[i] !== true) {
        return false;
      }
    }
    return true;
  }

  private checkSimulationStructureRec(listItems: ItemContainerModel[], visitedList: any[], i: number) {
    if (listItems[i].item.description === "Sink") {
      visitedList[i] = true;
    }
    //If the item has not connections and is not a sink, it means the structure of the simulation is invalid.
    if (listItems[i].connections !== undefined) {
      // @ts-ignore
      if (listItems[i].connections.length > 0) {
        visitedList[i] = true;
        // @ts-ignore
        for (let index = 0; index < listItems[i].connections.length; index++) {
          // @ts-ignore
          let indexOfElement = this.getIndexOfElement(listItems[i].connections[index].destinationItem.idItem, listItems)
          // @ts-ignore
          if (visitedList[indexOfElement] === false && indexOfElement !== null) {
            this.checkSimulationStructureRec(listItems, visitedList, indexOfElement)
          }
        }
      }
    }
  }

  //Gets the index of an item by its id
  private getIndexOfElement(idItem: number, listItems: ItemContainerModel[]) {
    for (let i = 0; i < listItems.length; i++) {
      if (listItems[i].item.idItem === idItem) {
        return i;
      }
    }
    return null;
  }

  //Inicialises a list of all visited items
  private getVisitedList(listItems: ItemContainerModel[]) {
    let visitedList = [];
    for (let i = 0; i < listItems.length; i++) {
      visitedList.push(false);
    }
    return visitedList;
  }

  //Returns a list of the sinks
  private getSinks(listItems: ItemContainerModel[]) {
    let sinkList = [];
    for (let index = 0; index < listItems.length; index++) {
      if (listItems[index].item.description === "Sink") {
        sinkList.push(listItems[index]);
      }
    }
    return sinkList;
  }

//When the user cancels the procces of adding a new connection between two items
  cancelNewConnection() {
    if (this.blackScreen) {

      //Go back to the previous state
      this.listConnections = this.listConnectionsBackUp;
      this.blackScreen = false;
      this.correctQueueShown = false;
      this.correctSinkShown = false;
      this.correctServerShown = false;
      this.listItemConnection = [];

      //Go back tho the normal screen, removing the blackscreen
      let blackCanvas = document.getElementById('blackScreen')
      // @ts-ignore
      if (blackCanvas.classList.length > 1) {
        // @ts-ignore
        blackCanvas.classList.toggle("showScreen")
      }
      let alertMessage = document.getElementById("newConnect");
      // @ts-ignore
      alertMessage.classList.toggle('alertNewConnectionAlt')
      let alertErrorMessage = document.getElementById("cancelConnect");
      //If the error message was being displayed, it is removed.
      // @ts-ignore
      let listClasses = alertErrorMessage.classList;
      if (listClasses.length > 1) {
        // @ts-ignore
        alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
      }

    }
  }

//Returns the connections of a item
  connectionsOfSelectedItem(itemContainer: ItemContainerModel) {
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.idItem === itemContainer.item.idItem && this.listItems[i].item.name === itemContainer.item.name) {
        return this.listItems[i].connections;
      }
    }
    return []
  }

//Returns true if the items are already connected, false otherwise
  alreadyConnected(origin: ItemModel, destination: ItemContainerModel) {
    let originItem: ItemContainerModel;
    for (let i = 0; i < this.listItems.length; i++) {
      // @ts-ignore
      if (this.listItems[i].item.idItem === origin.idItem && this.listItems[i].item.name === origin.name) {
        originItem = this.listItems[i];
        break;
      }
    }
    // @ts-ignore
    let connections = this.connectionsOfSelectedItem(originItem)
    // @ts-ignore
    for (let i = 0; i < connections.length; i++) {
      // @ts-ignore
      if (connections[i].destinationItem.idItem === destination.item.idItem && connections[i].destinationItem.name === destination.item.name) {
        return true;
      }
    }
    return false;
  }

  newConnection(event: Event, itemContainer: ItemContainerModel) {
    this.loading=true;

    //When the user presses the button to add a new connection we add a blackscreen to focus
    this.blackScreen = true;
    // @ts-ignore
    if (event.currentTarget.nodeName != "DIV") {
      let alertErrorMessage = document.getElementById("cancelConnect");
      // @ts-ignore
      let listClasses = alertErrorMessage.classList;
      if (listClasses.length > 1) {
        // @ts-ignore
        alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
      }
      this.errorConnection = 0;

      this.listConnectionsBackUp = this.listConnections;
      //We show the new connection message and store the connections and name of the first selected item
      // @ts-ignore
      this.listConnections = this.connectionsOfSelectedItem(itemContainer);
      let alertMessage = document.getElementById("newConnect");
      // @ts-ignore
      alertMessage.classList.toggle('alertNewConnectionAlt')
      // @ts-ignore
      let currentNameElement = event.currentTarget.offsetParent.offsetParent.id;
      this.sameElement = currentNameElement;
    }

    if (this.listItemConnection.length != 0) {
      if (itemContainer.item.name === this.listItemConnection[0].name && itemContainer.item.idItem === this.listItemConnection[0].idItem) {
        //If the selected item is itself, we show an alert to the user
        // @ts-ignore
        if (this.errorConnection != 1 || this.errorConnection != 2) {
          //Mostrar error de que no es posible unirse a si mismo
          this.errorConnection = 1;
          let alertErrorMessage = document.getElementById("cancelConnect");
          // @ts-ignore
          let listClasses = alertErrorMessage.classList;
          if (listClasses.length <= 1) {
            // @ts-ignore
            alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
          }
          //If the selected item is already connected, we show another alert
        } else if (this.errorConnection === 2) {
          this.errorConnection = 1;
        }
        //If the selected item is already connected
      } else if (this.alreadyConnected(this.listItemConnection[0], itemContainer)) {
        //We show the correct alert, depending on the previous error value
        // @ts-ignore
        if (this.errorConnection != 1 || this.errorConnection != 2) {
          this.errorConnection = 2;
          let alertErrorMessage = document.getElementById("cancelConnect");
          // @ts-ignore
          let listClasses = alertErrorMessage.classList;
          if (listClasses.length <= 1) {
            // @ts-ignore
            alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
          }
        } else if (this.errorConnection === 1) {
          this.errorConnection = 2;
        }
        //If the item selected is not itself and is not already connected, we create and save a new connection
      } else {
        this.listItemConnection.push(itemContainer.item)
        this.connectionInfo.originItem = this.listItemConnection[0];
        this.connectionInfo.destinationItem = this.listItemConnection[1];
        this.simulationService.newConnection(this.connectionInfo).subscribe({
          next: (connection) => {
            //Reset the error values, the black screen and alerts
            let blackCanvas = document.getElementById('blackScreen')
            // @ts-ignore
            if (blackCanvas.classList.length > 1) {
              // @ts-ignore
              blackCanvas.classList.toggle("showScreen")
            }
            let alertMessage = document.getElementById("newConnect");
            // @ts-ignore
            alertMessage.classList.toggle('alertNewConnectionAlt');
            let alertErrorMessage = document.getElementById("cancelConnect");
            // @ts-ignore
            let listClasses = alertErrorMessage.classList;
            if (listClasses.length > 1) {
              // @ts-ignore
              alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
            }
            this.blackScreen = false;
            this.sameElement = ''
            this.listItemConnection = []
            this.listConnections = this.listConnectionsBackUp;
            this.correctQueueShown = false;
            this.correctSinkShown = false;
            this.correctServerShown = false;
            this.listConnections.push(connection);
            this.addConnectionToItem(connection);
            this.loading=false;
          },
          error: (err) => {
            this.router.navigate(['error500'])
          }
        })
      }
    } else {
      //Depending on the type of item, we show the compatible objects that can be linked to the selected item
      let typeElement = itemContainer.item.description;
      switch (typeElement) {
        case "Source":
          this.correctQueueShown = true;
          break;
        case "Queue":
          this.correctServerShown = true;
          this.correctQueueShown = true;
          break;
        case "Server":
          this.correctQueueShown = true;
          this.correctSinkShown = true;
          break;
      }
      this.listItemConnection.push(itemContainer.item)
      let blackCanvas = document.getElementById('blackScreen')
      // @ts-ignore
      blackCanvas.classList.toggle("showScreen")
      this.loading=false;
    }
  }

  deleteItemFunction() {
    this.resetErrors();
    this.loading=true;

    //If the item is not null, we delete the selected item
    if (this.itemContainerModal.item.idItem) {
      this.simulationService.deleteItem(this.id, this.itemContainerModal.item.idItem).subscribe({
        next: (item) => {
          //this.deleteConnectionsByItsItem(item);
          //this.deleteItemByItsId(item);
          this.loading=false;
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  deleteConnectionFunction() {
    this.resetErrors();
    this.loading=true;

    //If the connection is not null, we delete the selected connection
    if (this.connectionModal.idConnect) {
      this.simulationService.deleteConnection(this.connectionModal.idConnect).subscribe({
        next: (connection) => {
          this.deleteConnectionByItsId(connection);
          this.deleteConnectionOfItem(connection)
          this.loading=false;
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

//When the user clicks the delete button, we store the connection data, and in case the user confirms the deletion, we will delete the connection
  openModalDeleteConnection(content: any, connection: ConnectionModel) {
    this.connectionModal = connection;
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

//When the user clicks the delete button, we store the item data, and in case the user confirms the deletion, we will delete the item
  openModalDeleteItem(content: any, itemContainer: ItemContainerModel) {
    this.itemContainerModal = itemContainer;
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  //The modal is displayed if the structure of the simulation is valid
  openModalQuickSimulation(content: any, content2: any) {
    this.referenceModal = content2;
    let isCorrect = this.checkSimulationStructure(this.listItems);
    if (isCorrect) {
      this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
    } else {
      let alertErrorMessage = document.getElementById("cancelConnect");
      // @ts-ignore
      let listClasses = alertErrorMessage.classList;
      if (listClasses.length <= 1) {
        // @ts-ignore
        alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
      }
      this.errorConnection = 3;
    }
  }

  openHelpModal(content: any) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', size: 'lg', scrollable: true});
  }

  openModalCustomTemplate(content: any) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', size: 'xl', scrollable: true});
  }

  addInput(i: number, description: string) {
    const control = new FormControl('');
    switch (description) {
      case "Source":
        (this.editSourceForm.get('percentagesSource') as FormGroup).addControl(i.toString(), control);
        break;
      case "Server":
        (this.editServerForm.get('percentagesServer') as FormGroup).addControl(i.toString(), control);
        break;
      case "Queue":
        (this.editQueueForm.get('percentagesQueue') as FormGroup).addControl(i.toString(), control);
        break;
    }
    this.inputControls.push(control)
  }

  deleteInput(i: number) {
    this.inputControls.splice(0, 1);
  }

  openModalEdit(content: any, itemContainer: ItemContainerModel) {
    //When the user clicks the edit button, we store the item data and its connections
    this.itemContainerModal = itemContainer;
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.name != this.itemContainerModal.item.name) {
        this.listNames.push(this.listItems[i].item.name)
      }
    }
    if (this.itemContainerModal.connections?.length) {
      this.numConnections = this.itemContainerModal.connections?.length;
    }

    //We delete all previously created controls that manage the porcentages FormGroup
    let lengthControlSource = Object.keys((this.editSourceForm.get('percentagesSource') as FormGroup).controls)
      .map(key => 0)
      .reduce((a, b) => a + 1, 0);
    for (let i = 0; i < lengthControlSource; i++) {
      (this.editSourceForm.get('percentagesSource') as FormGroup).removeControl(i.toString());
    }
    let lengthControlServer = Object.keys((this.editServerForm.get('percentagesServer') as FormGroup).controls)
      .map(key => 0)
      .reduce((a, b) => a + 1, 0);
    for (let i = 0; i < lengthControlServer; i++) {
      (this.editServerForm.get('percentagesServer') as FormGroup).removeControl(i.toString());
    }
    let lengthControlQueue = Object.keys((this.editQueueForm.get('percentagesQueue') as FormGroup).controls)
      .map(key => 0)
      .reduce((a, b) => a + 1, 0);
    for (let i = 0; i < lengthControlQueue; i++) {
      (this.editQueueForm.get('percentagesQueue') as FormGroup).removeControl(i.toString());
    }
    let lengthInputsControls = this.inputControls.length;
    // @ts-ignore
    for (let i = 0; i < lengthInputsControls; i++) {
      this.deleteInput(i);
    }
    //Depending on the item type we fill the corresponding form with the item information
    switch (this.itemContainerModal.item.description) {
      case "Source":
        this.editSourceForm.patchValue({
          nameSource: this.itemContainerModal.item.name,
          // @ts-ignore
          numberProductsSource: this.itemContainerModal.source?.numberProducts,
          interArrivalTimeSource: this.itemContainerModal.source?.interArrivalTime,
          sendToStrategySource: this.itemContainerModal.item.sendToStrategy
        });
        if (itemContainer.connections && itemContainer.connections.length > 0) {
          //Add the number of controls needed for each of the connections and store them
          // @ts-ignore
          for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
            this.addInput(i, "Source");
            // @ts-ignore
          }
          // @ts-ignore
          for (let i = this.itemContainerModal.connections?.length-1; i >= 0; i--) {
            // @ts-ignore
            this.editSourceForm.controls['percentagesSource'].controls[i].setValue(this.itemContainerModal.connections[i].percentage)
          }
        }
        break;
      case "Sink":
        this.editSinkForm.patchValue({
          nameSink: this.itemContainerModal.item.name
        });
        break;
      case "Server":
        this.editServerForm.patchValue({
          nameServer: this.itemContainerModal.item.name,
          setUpTimeServer: this.itemContainerModal.server?.setupTime,
          cycletimeServer: this.itemContainerModal.server?.cicleTime,
          sendToStrategyServer: this.itemContainerModal.item.sendToStrategy
        });
        //Add the number of controls needed for each of the connections and store them
        if (itemContainer.connections && itemContainer.connections.length > 0) {
          // @ts-ignore
          for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
            this.addInput(i, "Server");
          }
          // @ts-ignore
          for (let i = this.itemContainerModal.connections?.length-1; i >= 0; i--) {
            // @ts-ignore
            this.editServerForm.controls['percentagesServer'].controls[i].setValue(this.itemContainerModal.connections[i].percentage)
          }
        }
        break;
      case "Queue":
        this.editQueueForm.patchValue({
          nameQueue: this.itemContainerModal.item.name,
          capacityQueue: this.itemContainerModal.queue?.capacityQueue,
          queueDiscipline: this.itemContainerModal.queue?.disciplineQueue,
          sendToStrategyQueue: this.itemContainerModal.item.sendToStrategy
        })
        //Add the number of controls needed for each of the connections and store them
        if (itemContainer.connections && itemContainer.connections.length > 0) {
          // @ts-ignore
          for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
            // @ts-ignore
            this.addInput(i, "Queue");
          }
          // @ts-ignore
          for (let i = this.itemContainerModal.connections?.length-1; i >= 0; i--) {
            // @ts-ignore
            this.editQueueForm.controls['percentagesQueue'].controls[i].setValue(this.itemContainerModal.connections[i].percentage)
          }
        }
        break;
    }


    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  resetListNames() {
    this.listNames = []
    this.numConnections = 0
  }

//When the user resets the position of the item we place the items in controled positions, in order to be visible
  resetPositions() {
    this.loading=true;

    let x = -15;
    let y = 30;
    for (let i = 0; i < this.listItems.length; i++) {
      if (x > 2000) {
        x = -15;
        y = y + 110;
      }
      this.listItems[i].item.positionX = x;
      this.listItems[i].item.positionY = y;
      x = x + 200
    }
    //When we already placed the positions of the items we save and update them
    this.simulationService.updateAllItems(this.id, this.listItems).subscribe({
      next: (listaItems) => {
        this.listConnections = [];
        this.listItems = listaItems;
        for (let i = 0; i < this.listItems.length; i++) {
          // @ts-ignore
          for (let j = 0; j < this.listItems[i].connections.length; j++) {
            // @ts-ignore
            this.listConnections.push(this.listItems[i].connections[j]);
          }
        }
        this.listConnectionsBackUp = this.listConnections;
        this.loading=false;
      },
      error: (err) => {
        this.router.navigate(['error500']);
      }
    })
  }

  get timeSimulation() {
    return this.quickSimulationForm.get('timeSimulation');
  }

  get numberSimulations() {
    return this.quickSimulationForm.get('numberSimulations');
  }

  get pdfFormat() {
    return this.quickSimulationForm.get('pdfFormat');
  }

  get csvFormat() {
    return this.quickSimulationForm.get('csvFormat');
  }

//Form edit source
  get nameSource() {
    return this.editSourceForm.get('nameSource');
  }

  get numberProductsSource() {
    return this.editSourceForm.get('numberProductsSource');
  }

  get interArrivalTimeSource() {
    return this.editSourceForm.get('interArrivalTimeSource');
  }

  get percentagesSource() {
    return this.editSourceForm.get('percentagesSource')
  }

  get sendToStrategySource() {
    return this.editSourceForm.get('sendToStrategySource');
  }

//Form edit sink
  get nameSink() {
    return this.editSinkForm.get('nameSink');
  }

//Form edit server
  get nameServer() {
    return this.editServerForm.get('nameServer');
  }

  get setUpTimeServer() {
    return this.editServerForm.get('setUpTimeServer');
  }

  get cycletimeServer() {
    return this.editServerForm.get('cycletimeServer');
  }

  get sendToStrategyServer() {
    return this.editServerForm.get('sendToStrategyServer');
  }

  get percentagesServer() {
    return this.editServerForm.get('percentagesServer');
  }

//Form edit queue
  get nameQueue() {
    return this.editQueueForm.get('nameQueue');
  }

  get capacityQueue() {
    return this.editQueueForm.get('capacityQueue');
  }

  get queueDiscipline() {
    return this.editQueueForm.get('queueDiscipline');
  }

  get percentagesQueue() {
    return this.editQueueForm.get('percentagesQueue');
  }

  get sendToStrategyQueue() {
    return this.editQueueForm.get('sendToStrategyQueue');
  }

  setNumberProducts(option: string) {
    if (option == "Ilimitados") {
      this.editSourceForm.patchValue({
        numberProductsSource: "Ilimitados"
      })
    } else {
      this.editSourceForm.patchValue({
        numberProductsSource: "1"
      })
    }
  }

  setinterArrivalTime(option: string) {
    this.editSourceForm.patchValue({
      interArrivalTimeSource: option
    });
  }

//Form edit Server
  setcycletimeServer(option: string) {
    this.editServerForm.patchValue({
      cycletimeServer: option
    })
  }

  setCapacityQueue(option: string) {
    if (option == "Ilimitados") {
      this.editQueueForm.patchValue({
        capacityQueue: "Ilimitados"
      })
    } else {
      this.editQueueForm.patchValue({
        capacityQueue: "1"
      })
    }
  }

  validateQueueDiscipline(control: AbstractControl) {
    switch (control.value) {
      case "Fifo":
        return null;
      case "Lifo":
        return null;
      case "Random":
        return null;
      default:
        return {invalidFormat: true}
    }
  }

  setQueueDiscipline(option: string) {
    switch (option) {
      case "Fifo":
        this.editQueueForm.patchValue({
          queueDiscipline: "Fifo"
        });
        break;
      case "Lifo":
        this.editQueueForm.patchValue({
          queueDiscipline: "Lifo"
        });
        break;
      case "Random":
        this.editQueueForm.patchValue({
          queueDiscipline: "Random"
        })
        break;
    }
  }

  //Custom Template form
  setTypeInterArrivalTime(option: any) {
    this.customTemplateForm.patchValue({
      interArrivalTimeSourceType: this.listInterArrivalTypes[option]
    })
    this.typeInterArrivalTime = option;
    switch (this.listInterArrivalTypes[option]) {
      case "Determinista":
        // @ts-ignore
        if (!this.listProbDeterministicFunc.includes(this.customTemplateForm.value.interArrivalTimeSourceCkeck)) {
          this.customTemplateForm.patchValue({
            interArrivalTimeSourceCkeck: ''
          })
        }
        break;
      case "Exponencial":
        // @ts-ignore
        if (!this.listProbExpFunc.includes(this.customTemplateForm.value.interArrivalTimeSourceCkeck)) {
          this.customTemplateForm.patchValue({
            interArrivalTimeSourceCkeck: ''
          })
        }
        break;
      case "General":
        // @ts-ignore
        if (!this.listProbGeneralFunc.includes(this.customTemplateForm.value.interArrivalTimeSourceCkeck)) {
          this.customTemplateForm.patchValue({
            interArrivalTimeSourceCkeck: ''
          })
        }
        break;
    }
  }

  setInterArrivalTimeSourceCkeck(option: string) {
    this.customTemplateForm.patchValue({
      interArrivalTimeSourceCkeck: option
    })
  }

  setServiceTime(option: string) {
    this.customTemplateForm.patchValue({
      serviceTime: option
    })
  }

  setServiceTimeType(option: any) {
    this.customTemplateForm.patchValue({
      serviceTimeType: this.listInterArrivalTypes[option]
    })
    this.typeServiceTime = option;
    switch (this.listInterArrivalTypes[option]) {
      case "Determinista":
        // @ts-ignore
        if (!this.listProbDeterministicFunc.includes(this.customTemplateForm.value.serviceTime)) {
          this.customTemplateForm.patchValue({
            serviceTime: ''
          })
        }
        break;
      case "Exponencial":
        // @ts-ignore
        if (!this.listProbExpFunc.includes(this.customTemplateForm.value.serviceTime)) {
          this.customTemplateForm.patchValue({
            serviceTime: ''
          })
        }
        break;
      case "General":
        // @ts-ignore
        if (!this.listProbGeneralFunc.includes(this.customTemplateForm.value.serviceTime)) {
          this.customTemplateForm.patchValue({
            serviceTime: ''
          })
        }
        break;
    }
  }

  setCapacityQueueCheck(option: string) {
    if (option == "Ilimitados") {
      this.customTemplateForm.patchValue({
        capacityQueueCheck: "Ilimitados"
      })
    } else {
      this.customTemplateForm.patchValue({
        capacityQueueCheck: "1"
      })
    }
  }

  setDisciplineQueueCheck(option: string) {
    switch (option) {
      case "Fifo":
        this.customTemplateForm.patchValue({
          queueDisciplineCheck: "Fifo"
        });
        break;
      case "Lifo":
        this.customTemplateForm.patchValue({
          queueDisciplineCheck: "Lifo"
        });
        break;
      case "Random":
        this.customTemplateForm.patchValue({
          queueDisciplineCheck: "Random"
        })
        break;
    }
  }

  get interArrivalTimeSourceType() {
    return this.customTemplateForm.get('interArrivalTimeSourceType');
  }

  get interArrivalTimeSourceCkeck() {
    return this.customTemplateForm.get('interArrivalTimeSourceCkeck');
  }

  get serviceTimeType() {
    return this.customTemplateForm.get('serviceTimeType');
  }

  get serviceTime() {
    return this.customTemplateForm.get('serviceTime');
  }

  get numberOfServers() {
    return this.customTemplateForm.get('numberOfServers');
  }

  get capacityQueueCheck() {
    return this.customTemplateForm.get('capacityQueueCheck');
  }

  get queueDisciplineCheck() {
    return this.customTemplateForm.get('queueDisciplineCheck');
  }


  validateFormatNumberProducts(control: AbstractControl) {
    let numberProducts = control.value;
    if (numberProducts === "Ilimitados" || !isNaN(numberProducts)) {
      if (numberProducts === "Ilimitados") {
        return null
      } else {
        if (Number(numberProducts)) {
          let number = Number(numberProducts)
          if (Number.isInteger(number) && number > 0)
            return null
        }
      }
    }
    return {invalidFormat: true}
  }

  validateName(control: AbstractControl, listNames: string[]) {
    let name = control.value;
    //The name of the item need to be unique in the simulation
    if (name != '' && name != undefined && this.listNames.length != 0) {
      if (this.listNames.includes(name)) {
        return {invalidFormat: true}
      }
    }
    return null
  }

//Validate the format of the functions
  validateNumbers(numbers: string, func: string):
    boolean {
    let posComa = 0;
    for (let i = 0; i < numbers.length; i++) {
      if (numbers.charAt(i) === ',') {
        posComa = i;
        break;
      }
    }
    if (posComa !== 0) {
      let firstNumber = numbers.substring(0, posComa);
      let secondNumber = numbers.substring(posComa + 1);
      let firstNumberInt = Number(firstNumber)
      let secondNumberInt = Number(secondNumber);
      if (!isNaN(firstNumberInt) && !isNaN(secondNumberInt) && firstNumber.length > 0 && secondNumber.length > 0) {
        switch (func) {
          case "LogNormal":
            if (secondNumberInt > 0) {
              return true;
            }
            return false;
            break;
          case "Binomial":
            if (firstNumberInt > 0 && Number.isInteger(firstNumberInt) && secondNumberInt > 0 && secondNumberInt < 1) {
              return true;
            }
            return false;
            break;
          case "Normal":
            if (secondNumberInt > 0) {
              return true;
            }
            return false;
            break;
          case "Logistic":
            if (secondNumberInt > 0) {
              return true;
            }
            return false;
            break;
          case "Gamma":
            if (firstNumberInt > 0 && secondNumberInt > 0) {
              return true;
            }
            return false;
            break;
          case "Weibull":
            if (firstNumberInt > 0 && secondNumberInt > 0) {
              return true;
            }
            return false;
            break;
          case "Uniform":
            if (firstNumberInt > 0 && firstNumberInt < secondNumberInt) {
              return true;
            }
            return false;
            break;
        }
        return true;
      }
    }
    return false;
  }

  validatePercentages(control: AbstractControl, itemContainer: ItemContainerModel) {
    let input = control.value;
    if (itemContainer) {
      if (itemContainer.connections && itemContainer.connections.length > 0) {
        let totalAmount = 0;
        for (let i = 0; i < itemContainer.connections.length; i++) {
          totalAmount = totalAmount + itemContainer.connections[i].percentage;
        }
        totalAmount = totalAmount + input
        if (totalAmount != 100) {
          return {invalidFormat: true};
        }
        control.setValue(0)

      }
      return null;
    }
    return null;
  }

  validateProbFunc(control: AbstractControl, component: any) {
    let input = control.value;
    if (input.substring(0, 6) === "NegExp") {
      if (input.substring(6, 7) === "(" && input.substring(input.length - 1) === ")") {
        let number = input.substring(7, input.length - 1);
        if (number.length !== 0) {
          let numberInt = Number(number);
          if (!isNaN(numberInt) && numberInt >= 0) {
            return null;
          }
        }
        return {invalidFormat: true};
      }
    }
    if (input.substring(0, 7) === "Poisson") {
      if (input.substring(7, 8) === "(" && input.substring(input.length - 1) === ")") {
        let number = input.substring(8, input.length - 1);
        if (number.length !== 0) {
          let numberInt = Number(number);
          if (!isNaN(numberInt) && numberInt >= 0) {
            return null;
          }
        }
        return {invalidFormat: true};
      }
    }
    if (input.substring(0, 10) === "Triangular") {
      if (input.substring(10, 11) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(11, input.length - 1)
        let posComa1 = -1;
        let posComa2 = -1;
        for (let i = 0; i < numbers.length; i++) {
          if (numbers.charAt(i) === ',') {
            if (posComa1 === -1) {
              posComa1 = i;
            } else {
              posComa2 = i;
              break;
            }
          }
        }
        let firstNumber = numbers.substring(0, posComa1);
        let secondNumber = numbers.substring(posComa1 + 1, posComa2);
        let thirdNumber = numbers.substring(posComa2 + 1);
        let firstNumberInt = Number(firstNumber);
        let secondNumberInt = Number(secondNumber);
        let thirdNumberInt = Number(thirdNumber)
        if (!isNaN(firstNumberInt) && !isNaN(secondNumberInt) && !isNaN(thirdNumberInt) && firstNumberInt > 0 && secondNumberInt > firstNumberInt && thirdNumberInt > firstNumberInt && secondNumberInt > thirdNumberInt) {
          return null;
        }
        return {invalidFormat: true};
      }
    }
    if (input.substring(0, 9) === "LogNormal") {
      if (input.substring(9, 10) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(10, input.length - 1)
        if (component.validateNumbers(numbers, "LogNormal")) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 8) === "Binomial") {
      if (input.substring(8, 9) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(9, input.length - 1)
        if (component.validateNumbers(numbers, "Binomial")) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 3) === "Max") {
      if (input.substring(3, 4) === "(" && input.substring(input.length - 1) === ")") {
        let subInput = input.substring(4, input.length - 1)
        let posComa = -1;
        for (let i = 0; i < subInput.length; i++) {
          if (subInput.charAt(i) === ',') {
            posComa = i;
            break;
          }
        }
        let firstNumber = subInput.substring(0, posComa);
        let secondSubInput = subInput.substring(posComa + 1);
        let firstNumberInt = Number(firstNumber);
        if (!isNaN(firstNumberInt) && firstNumberInt >= 0 && firstNumber.length > 0) {
          if (secondSubInput.substring(0, 6) === "Normal" && secondSubInput.substring(6, 7) === "(" && secondSubInput.substring(secondSubInput.length - 1) === ")") {
            let numbers = secondSubInput.substring(7, secondSubInput.length - 1)
            if (component.validateNumbers(numbers, "Normal")) {
              return null;
            } else {
              return {invalidFormat: true};
            }
          }
          if (secondSubInput.substring(0, 8) === "Logistic" && secondSubInput.substring(8, 9) === "(" && secondSubInput.substring(secondSubInput.length - 1) === ")") {
            let numbers = secondSubInput.substring(9, secondSubInput.length - 1)
            if (component.validateNumbers(numbers, "Logistic")) {
              return null;
            } else {
              return {invalidFormat: true};
            }
          }
        }

      }
    }
    if (input.substring(0, 4) === "Beta") {
      if (input.substring(4, 5) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(5, input.length - 1)
        let posComa1 = -1;
        let posComa2 = -1;
        for (let i = 0; i < numbers.length; i++) {
          if (numbers.charAt(i) === ',') {
            if (posComa1 === -1) {
              posComa1 = i;
            } else {
              posComa2 = i;
              break;
            }
          }
        }
        let firstNumber = numbers.substring(0, posComa1);
        let secondNumber = numbers.substring(posComa1 + 1, posComa2);
        let thirdNumber = numbers.substring(posComa2 + 1);
        let firstNumberInt = Number(firstNumber);
        let secondNumberInt = Number(secondNumber);
        let thirdNumberInt = Number(thirdNumber)
        if (!isNaN(firstNumberInt) && !isNaN(secondNumberInt) && !isNaN(thirdNumberInt) && firstNumberInt > 0 && secondNumberInt > 0 && thirdNumberInt > 0) {
          return null;
        }
        return {invalidFormat: true};
      }
    }
    if (input.substring(0, 5) === "Gamma") {
      if (input.substring(5, 6) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(6, input.length - 1)
        if (component.validateNumbers(numbers, "Gamma")) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 7) === "Uniform") {
      if (input.substring(7, 8) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(8, input.length - 1)
        if (component.validateNumbers(numbers, "Uniform")) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 7) === "Weibull") {
      if (input.substring(7, 8) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(8, input.length - 1)
        if (component.validateNumbers(numbers, "Weibull")) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 4) === "mins") {
      if (input.substring(4, 5) === "(" && input.substring(input.length - 1) === ")") {
        if (Number(input.substring(5, input.length - 1))) {
          return null;
        }
      }
      return {invalidFormat: true};
    }
    if (input.substring(0, 2) === "hr") {
      if (input.slice(2, 3) === "(" && input.substring(input.length - 1) === ")") {
        if (Number(input.slice(3, input.length - 1))) {
          return null;
        }
      }
      return {invalidFormat: true};
    }
    if (Number(input.substring(0)) || input === "0") {
      if (!Number(input) && input != "0") {
        return {invalidFormat: true};
      }
      return null;
    }
    return {invalidFormat: true};
  }

  progressValue(itemContainer: ItemContainerModel) {
    if (itemContainer.queue !== undefined) {
      if (itemContainer.queue.capacityQueue && itemContainer.queue.inQueue) {
        if (itemContainer.queue.inQueue !== null && itemContainer.queue.capacityQueue !== "Ilimitados") {
          let result = (itemContainer.queue.inQueue / parseInt(itemContainer.queue.capacityQueue)) * 100.0;
          return result.toString() + "%"
        }
      }
    }
    return "0%";
  }

  getColor(itemContainer: ItemContainerModel) {
    if (itemContainer.queue !== undefined) {
      if (itemContainer.queue.capacityQueue && itemContainer.queue.inQueue) {
        if (itemContainer.queue.inQueue !== null && itemContainer.queue.capacityQueue !== "Ilimitados") {
          let ratio = (itemContainer.queue.inQueue / parseInt(itemContainer.queue.capacityQueue)) * 100.0;
          if (ratio < 25) {
            return 'progress-bar progress-bar-striped active successProgressBar';
          } else if (ratio < 75) {
            return 'progress-bar progress-bar-striped active warningProgressBar';
          } else {
            return 'progress-bar progress-bar-striped active dangerProgressBar';
          }
        }
      }
    }
    return 'progress-bar progress-bar-striped active successProgressBar';
  }

  cloneSimulations(listItems: ItemContainerModel[], numberSimulations: number | undefined) {
    let listSimulations = []
    // @ts-ignore
    for (let i = 0; i < numberSimulations; i++) {
      listSimulations.push(listItems);
    }
    return listSimulations;
  }

  replaceItemByItsId(itemContainer: ItemContainerModel) {
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.idItem === itemContainer.item.idItem) {
        this.listItems.splice(i, 1, itemContainer);
      }
      // @ts-ignore
      for (let j = 0; j < this.listItems[i].connections.length; j++) {
        // @ts-ignore
        if (this.listItems[i].connections[j].originItem.idItem === itemContainer.item.idItem) {
          // @ts-ignore
          this.listItems[i].connections[j].originItem.name=itemContainer.item.name;
          // @ts-ignore
          this.listItems[i].connections[j].originItem.positionX = itemContainer.item.positionX;
          // @ts-ignore
          this.listItems[i].connections[j].originItem.positionY = itemContainer.item.positionY;
        }
        // @ts-ignore
        if (this.listItems[i].connections[j].destinationItem.idItem === itemContainer.item.idItem) {
          // @ts-ignore
          this.listItems[i].connections[j].destinationItem.name=itemContainer.item.name;
          // @ts-ignore
          this.listItems[i].connections[j].destinationItem.positionX = itemContainer.item.positionX;
          // @ts-ignore
          this.listItems[i].connections[j].destinationItem.positionY = itemContainer.item.positionY;
        }
      }
    }
  }

  deleteConnectionByItsId(connection: ConnectionModel) {
    for (let i = 0; i < this.listConnections.length; i++) {
      if (connection.idConnect === this.listConnections[i].idConnect) {
        this.listConnections.splice(i, 1);
        break;
      }
    }
  }

  deleteConnectionsByItsItem(itemContainer: ItemContainerModel) {
    let lengthConnections=this.listConnections.length;
    for (let i = lengthConnections-1; i >= 0; i--) {
      if (itemContainer.item.idItem === this.listConnections[i].originItem.idItem || itemContainer.item.idItem === this.listConnections[i].destinationItem.idItem) {
        this.listConnections.splice(i, 1);
      }
    }

    for (let i=0;i<this.listItems.length;i++){
      // @ts-ignore
      for (let j=0;j<this.listItems[i].connections.length;j++){
        // @ts-ignore
        if (this.listItems[i].connections[j].destinationItem.idItem===itemContainer.item.idItem){
          // @ts-ignore
          this.listItems[i].connections.splice(j,1);
        }
      }
    }

  }

  deleteItemByItsId(itemContainer: ItemContainerModel) {
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.idItem === itemContainer.item.idItem) {
        this.listItems.splice(i, 1)
        break;
      }
    }
  }

  addConnectionToItem(connection: ConnectionModel) {
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.idItem === connection.originItem.idItem) {
        // @ts-ignore
        this.listItems[i].connections.push(connection);
      }
    }
  }

  deleteConnectionOfItem(connection: ConnectionModel) {
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.idItem === connection.originItem.idItem) {
        // @ts-ignore
        for (let j = 0; this.listItems[i].connections.length; j++) {
          // @ts-ignore
          if (this.listItems[i].connections[j].originItem.idItem == connection.originItem.idItem) {
            // @ts-ignore
            this.listItems[i].connections.splice(j, 1);
          }
        }
      }
    }
  }
}
