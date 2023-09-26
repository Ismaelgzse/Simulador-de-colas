import {AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild} from "@angular/core";
import {HomeService} from "../home/home.service";
import {DOCUMENT} from "@angular/common";
import {ItemContainerModel} from "./Items/itemContainer.model";
import {SimulationService} from "./simulation.service";
import {ActivatedRoute, Route, Router} from "@angular/router";
import {QueueModel} from "./Items/queue.model";
import {SinkModel} from "./Items/sink.model";
import {SourceModel} from "./Items/source.model";
import {ServerModel} from "./Items/server.model";
import {ItemModel} from "./Items/item.model";
import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {isNumber} from "@ng-bootstrap/ng-bootstrap/util/util";
import {ConnectionModel} from "./Connection/connection.model";


function totalAmountQueue(max: number, component: any): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    if (component) {
      if (component.editQueueForm) {
        if (component.editQueueForm.controls) {
          if (component.editQueueForm.controls.sendToStrategyQueue.value === "Porcentaje") {
            let total = 0;
            let lengthControl = Object.keys(control.value)
              .map(key => 0)
              .reduce((a, b) => a + 1, 0);

            for (let i = 0; i < lengthControl; i++) {
              if (Number(control.value[i])) {
                if (parseFloat(control.value[i]) > 100 || parseFloat(control.value[i]) < 0) {
                  return {'invalid': true};
                }
              } else {
                return {'invalid': true};
              }
            }
            const totalAmount = Object.keys(control.value)
              .map(key => parseFloat(control.value[key]) || 0)
              .reduce((a, b) => a + b, 0);

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
          if (component.editServerForm.controls.sendToStrategyServer.value === "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)") {
            let total = 0;
            let lengthControl = Object.keys(control.value)
              .map(key => 0)
              .reduce((a, b) => a + 1, 0);

            for (let i = 0; i < lengthControl; i++) {
              if (Number(control.value[i])) {
                if (parseFloat(control.value[i]) > 100 || parseFloat(control.value[i]) < 0) {
                  return {'invalid': true};
                }
              } else {
                return {'invalid': true};
              }
            }
            const totalAmount = Object.keys(control.value)
              .map(key => parseFloat(control.value[key]) || 0)
              .reduce((a, b) => a + b, 0);

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
          if (component.editSourceForm.controls.sendToStrategySource.value === "Porcentaje (si no hay hueco se envia aunque se pierda)" || component.editSourceForm.controls.sendToStrategySource.value ==="Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)") {
            let total = 0;
            let lengthControl = Object.keys(control.value)
              .map(key => 0)
              .reduce((a, b) => a + 1, 0);

            for (let i = 0; i < lengthControl; i++) {
              if (Number(control.value[i])) {
                if (parseFloat(control.value[i]) > 100 || parseFloat(control.value[i]) < 0) {
                  return {'invalid': true};
                }
              } else {
                return {'invalid': true};
              }
            }
            const totalAmount = Object.keys(control.value)
              .map(key => parseFloat(control.value[key]) || 0)
              .reduce((a, b) => a + b, 0);

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
    if (input === "Porcentaje (si no hay hueco se envia aunque se pierda)" || input==="Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)") {
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
  providers: [SimulationService]
})

export class SimulationComponent implements AfterViewInit, OnInit {
  numConnections: number;
  showConnections: boolean;
  inputControls: FormControl[] = [];
  listSendToStrategiesQueue = ["Aleatorio", "Primera conexión disponible", "Porcentaje"];
  listSendToStrategiesSource = ["Aleatorio (lo manda independientemente de si hay hueco o no)", "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)", "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)", "Porcentaje (si no hay hueco se envia aunque se pierda)", "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)"];
  listSendToStrategiesServer = ["Aleatorio (lo manda independientemente de si hay hueco o no)", "Aleatorio (si está llena la cola seleccionada, espera hasta que haya hueco)", "Primera conexión disponible", "Porcentaje (si está llena la cola seleccionada, espera hasta que haya hueco)"];

  //0: no error
  //1: itself
  errorConnection: number;
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
  listItemConnection: ItemModel[];
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
  listProbFunc = ["Triangular(5,10,15)", "LogNormal(10,2)", "Binomial(5,0.15)", "Max(0,Normal(10,1))",
    "Beta(10,1,1)", "Gamma(10,2)", "Max(0,Logistic(10,1))", "Uniform(5,15)", "Weibull(10,2)",
    "10", "mins(10)", "hr(0.5)"];

  listProbFuncGuide = ["Triangular(Límite inferior,Modo,Límite superior)", "LogNormal(Escala,Forma)", "Binomial(Ensayos,p)", "Max(0,Normal(Media,Desviación típica))",
    "Beta(Alfa,Beta,Max. valor)", "Gamma(Escala,Forma)", "Max(0,Logistic(mu,s))", "Uniform(Min. valor,Max. valor)", "Weibull(Alfa,Beta)",
    "Valor entero (segundos)", "mins(Número minutos)", "hr(Número de horas)"];

  quickSimulationForm = new FormGroup({
    timeSimulation: new FormControl('', Validators.compose([Validators.required, Validators.min(0.1), Validators.max(20)])),
    numberSimulations: new FormControl('', Validators.compose([Validators.required, Validators.min(1), Validators.max(5)])),
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
    setUpTimeServer: new FormControl('', Validators.compose([Validators.required, Validators.min(0)])),
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


  constructor(private modalService: NgbModal, @Inject(DOCUMENT) document: Document, public simulationService: SimulationService, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.showConnections = true
    this.errorConnection = 0;
    this.correctSinkShown = false;
    this.correctSourceSown = false;
    this.correctServerShown = false;
    this.correctQueueShown = false;
    this.sameElement = '';
    this.blackScreen = false;
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
      setupTime: ""
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
        this.id = params['id'];
        this.simulationService.connect(this.id.toString());
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
        this.router.navigate(['error403']);
      }
    })
  }

  inicializeConnections(itemContainer: ItemContainerModel) {
    // @ts-ignore
    this.listConnections.concat(itemContainer.connections)
  }

  ngAfterViewInit(): void {
    let destinationElement = document.getElementById("canvas")
    // @ts-ignore
    if (destinationElement !== null) {
      destinationElement.addEventListener("dragover", this.dragOver);
      // @ts-ignore
      destinationElement.addEventListener("drop", (event) => this.newElement(event, this));
    }
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

  newElement(event: DragEvent, simulationComponent: any) {
    if (event != undefined) {
      event.preventDefault();
      if (event.dataTransfer !== null) {
        var data = event.dataTransfer.getData("text")
        // @ts-ignore
        var parentClass = document.getElementById(data).parentElement.className;
        var element = document.getElementById(data)
        if (parentClass === "dragItemContainer") {
          let type = data.substring(0, 4)
          switch (type) {
            case "Fuen":
              this.sourceInfo.outSource = 0;
              this.sourceInfo.numberProducts = 'Ilimitados';
              this.sourceInfo.interArrivalTime = '10';
              this.itemInfo.name = '';
              this.itemInfo.description = 'Source';
              this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible (si no hay hueco, espera hasta que lo haya)";
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.source = this.sourceInfo;
              break;
            case "Cola":
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
            case "Proc":
              this.serverInfo.outServer = 0;
              this.serverInfo.cicleTime = '10'
              this.serverInfo.setupTime = '0'
              this.itemInfo.name = '';
              this.itemInfo.description = 'Server';
              this.itemContainerInfo.item.sendToStrategy = "Primera conexión disponible";
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.server = this.serverInfo;
              break;
            case "Sumi":
              this.sinkInfo.inSink = 0;
              this.itemInfo.name = '';
              this.itemInfo.description = 'Sink';
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.sink = this.sinkInfo;
              break;
          }
          // @ts-ignore
          this.itemContainerInfo.item.positionX = event.pageX - document.getElementById(data).offsetWidth * 1.7;
          // @ts-ignore
          this.itemContainerInfo.item.positionY = event.pageY - document.getElementById(data).offsetHeight * 2.4;
          // @ts-ignore
          this.simulationService.newItem(this.id, this.itemContainerInfo).subscribe({
            next: (item) => {
              this.ngOnInit();
            },
            error: (err) => {
              this.router.navigate(['error403']);
            }
          });
        } else {
          for (let i = 0; i < this.listItems.length; i++) {
            if (data === this.listItems[i].item.name) {
              this.itemContainerInfo = this.listItems[i];
            }
          }
          // @ts-ignore
          this.itemContainerInfo.item.positionX = event.pageX - document.getElementById(data).offsetWidth * 1.5;
          // @ts-ignore
          this.itemContainerInfo.item.positionY = event.pageY - document.getElementById(data).offsetHeight * 2;
          if (this.itemContainerInfo.item.positionX < -30) {
            this.itemContainerInfo.item.positionX = -15
          }
          if (this.itemContainerInfo.item.idItem) {
            this.simulationService.updateItem(this.id, this.itemContainerInfo.item.idItem, this.itemContainerInfo).subscribe({
              next: (itemContainer) => {
                this.ngOnInit();
              },
              error: (err) => {
                this.router.navigate(['error403']);
              }
            });
          }
        }
      }
    }
  }

  editItem() {
    if (this.itemContainerModal.item.idItem) {
      switch (this.itemContainerModal.item.description) {
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
              (this.editQueueForm.get('percentagesQueue') as FormGroup).removeControl(i.toString());
            }
          }
      }
      this.simulationService.updateItem(this.id, this.itemContainerModal.item.idItem, this.itemContainerModal).subscribe({
        next: (itemContainer) => {
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  simulate() {
    this.simulationService.sendMessage(this.id.toString());
  }

  cancelNewConnection() {
    if (this.blackScreen === true) {
      this.listConnections = this.listConnectionsBackUp;
      this.blackScreen = false;
      this.listItemConnection = [];
      let blackCanvas = document.getElementById('blackScreen')
      // @ts-ignore
      blackCanvas.classList.toggle("showScreen")
      let alertMessage = document.getElementById("newConnect");
      // @ts-ignore
      alertMessage.classList.toggle('alertNewConnectionAlt')
      let alertErrorMessage = document.getElementById("cancelConnect");
      // @ts-ignore
      let listClasses = alertErrorMessage.classList;
      if (listClasses.length > 1) {
        // @ts-ignore
        alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
      }

    }
  }

  connectionsOfSelectedItem(itemContainer: ItemContainerModel) {
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.idItem === itemContainer.item.idItem && this.listItems[i].item.name === itemContainer.item.name) {
        return this.listItems[i].connections;
      }
    }
    return []
  }

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
    //cuando se pulsa por primera vez solo quedan las conexiones de las existentes con las posibles
    this.blackScreen = true;
    // @ts-ignore
    if (event.currentTarget.nodeName != "DIV") {
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
        // @ts-ignore
        if (this.errorConnection != 1 || this.errorConnection != 2) {
          //Mostrar error de que no es posible unirse a si mismo
          this.errorConnection = 1;
          let alertErrorMessage = document.getElementById("cancelConnect");
          // @ts-ignore
          alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
        } else if (this.errorConnection === 2) {
          this.errorConnection = 1;
        }
      } else if (this.alreadyConnected(this.listItemConnection[0], itemContainer)) {
        // @ts-ignore
        if (this.errorConnection != 1 || this.errorConnection != 2) {
          //Mostrar error de que no es posible unirse a si mismo
          this.errorConnection = 1;
          let alertErrorMessage = document.getElementById("cancelConnect");
          // @ts-ignore
          alertErrorMessage.classList.toggle('alertCancelConnectionAlt');
        } else if (this.errorConnection === 1) {
          this.errorConnection = 2;
        }
      } else {
        this.listItemConnection.push(itemContainer.item)
        this.connectionInfo.originItem = this.listItemConnection[0];
        this.connectionInfo.destinationItem = this.listItemConnection[1];
        this.simulationService.newConnection(this.connectionInfo).subscribe({
          next: (connection) => {
            let blackCanvas = document.getElementById('blackScreen')
            // @ts-ignore
            blackCanvas.classList.toggle("showScreen")
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
            this.ngOnInit();
          },
          error: (err) => {
            this.router.navigate(['error500'])
          }
        })
      }
    } else {
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
    }
  }

  deleteItemFunction() {
    if (this.itemContainerModal.item.idItem) {
      this.simulationService.deleteItem(this.id, this.itemContainerModal.item.idItem).subscribe({
        next: (item) => {
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  deleteConnectionFunction() {
    if (this.connectionModal.idConnect) {
      this.simulationService.deleteConnection(this.connectionModal.idConnect).subscribe({
        next: (connection) => {
          this.ngOnInit();
        },
        error: (err) => {
          this.router.navigate(['error500']);
        }
      })
    }
  }

  openModalDeleteConnection(content: any, connection: ConnectionModel) {
    this.connectionModal = connection;
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  openModalDeleteItem(content: any, itemContainer: ItemContainerModel) {
    this.itemContainerModal = itemContainer;
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  openModalQuickSimulation(content: any) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
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
    this.itemContainerModal = itemContainer;
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.name != this.itemContainerModal.item.name) {
        this.listNames.push(this.listItems[i].item.name)
      }
    }
    if (this.itemContainerModal.connections?.length) {
      this.numConnections = this.itemContainerModal.connections?.length;
    }
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
          // @ts-ignore
          for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
            this.addInput(i, "Source");
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
        if (itemContainer.connections && itemContainer.connections.length > 0) {
          // @ts-ignore
          for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
            this.addInput(i, "Server");
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
        if (itemContainer.connections && itemContainer.connections.length > 0) {
          // @ts-ignore
          for (let i = 0; i < this.itemContainerModal.connections?.length; i++) {
            // @ts-ignore
            this.addInput(i, "Queue");
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
  }

  resetPositions() {
    let x = -15;
    let y = 30;
    //2000
    for (let i = 0; i < this.listItems.length; i++) {
      if (x > 2000) {
        x = -15;
        y = y + 110;
      }
      this.listItems[i].item.positionX = x;
      this.listItems[i].item.positionY = y;
      x = x + 200
    }
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
    let opt = control.value.substring(0, 6).toLowerCase();
    switch (opt) {
      case "fifo":
        return null;
      case "lifo":
        return null;
      case "random":
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
    if (name != '' && name != undefined && this.listNames.length != 0) {
      if (this.listNames.includes(name)) {
        return {invalidFormat: true}
      }
    }
    return null
  }

  validateNumbers(numbers: string, func: string): boolean {
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
      if (!isNaN(firstNumberInt) && !isNaN(secondNumberInt)) {
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
        if (!isNaN(firstNumberInt) && !isNaN(secondNumberInt) && !isNaN(thirdNumberInt) && firstNumberInt > 0 && secondNumberInt > firstNumberInt && thirdNumberInt > firstNumberInt && thirdNumberInt > secondNumberInt) {
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
        if (!isNaN(firstNumberInt) && firstNumberInt >= 0) {
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
}
