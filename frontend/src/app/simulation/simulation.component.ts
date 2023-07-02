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

@Component({
  selector: 'app-simulation',
  templateUrl: './simulation.component.html',
  styleUrls: ['../../assets/css/home.css', '../../assets/css/simulation.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [SimulationService]
})

export class SimulationComponent implements AfterViewInit, OnInit {
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
  listProbFunc = ["Erlang(10,2)", "LogNormal(10,2)", "Bernouilli(50,5,15)", "Max(0,Normal(10,1))",
    "Beta(10,1,1)", "Gamma(10,2)", "Max(0,Logistic(10,1))", "Uniform(5,15)", "Weibull(10,2)",
    "10", "mins(10)", "hr(0.5)"]

  quickSimulationForm = new FormGroup({
    timeSimulation: new FormControl('', Validators.compose([Validators.required, Validators.min(0.1), Validators.max(20)])),
    numberSimulations: new FormControl('', Validators.compose([Validators.required, Validators.min(1), Validators.max(5)])),
    pdfFormat: new FormControl(false),
    csvFormat: new FormControl(false)
  })

  editSourceForm = new FormGroup({
    nameSource: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), (control) => this.validateName(control, this.listNames)])),
    interArrivalTimeSource: new FormControl('', Validators.compose([Validators.required, (control) => this.validateProbFunc(control, this)])),
    numberProductsSource: new FormControl('', Validators.compose([Validators.required, this.validateFormatNumberProducts]))
  })

  editServerForm = new FormGroup({
    nameServer: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), (control) => this.validateName(control, this.listNames)])),
    setUpTimeServer: new FormControl('', Validators.compose([Validators.required, Validators.min(0)])),
    cycletimeServer: new FormControl('', Validators.compose([Validators.required, (control) => this.validateProbFunc(control, this)]))
  })

  editQueueForm = new FormGroup({
    nameQueue: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), (control) => this.validateName(control, this.listNames)])),
    capacityQueue: new FormControl('', Validators.compose([Validators.required, this.validateFormatNumberProducts])),
    queueDiscipline: new FormControl('', Validators.compose([Validators.required, this.validateQueueDiscipline]))
  })

  editSinkForm = new FormGroup({
    nameSink: new FormControl('', Validators.compose([Validators.required, Validators.minLength(1), Validators.maxLength(10), , (control) => this.validateName(control, this.listNames)]))
  })


  constructor(private modalService: NgbModal, @Inject(DOCUMENT) document: Document, private simulationService: SimulationService, private router: Router, private route: ActivatedRoute) {
    document.getElementById("canvas")
  }

  ngOnInit(): void {
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
      positionY: 0
    };
    this.itemContainerInfo = {
      item: this.itemInfo
    };
    this.itemContainerModal = {
      item: this.itemInfo,
      queue:this.queueInfo,
      server:this.serverInfo,
      sink:this.sinkInfo,
      source:this.sourceInfo
    }
    this.listNames = []
    this.listItems = [];
    this.route.params.subscribe(
      (params => {
        this.id = params['id'];
        this.simulationService.getSimulationInfo(this.id).subscribe(
          (simulation => {
            this.simulationTitle = simulation.title;
            this.simulationService.getItems(this.id).subscribe(
              (items => {
                this.listItems = items;
              }),
              (error => this.router.navigate(['error403']))
            )
          })
        )
      })
    )

    /*this.simulationService.getItems().subscribe(
      (items=>{

      }),
      (error => //this.router.navigate(['error403'])
                console.log("a") )
    )

     */

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
              this.itemContainerInfo.item = this.itemInfo;
              this.itemContainerInfo.queue = this.queueInfo;
              break;
            case "Proc":
              this.serverInfo.outServer = 0;
              this.serverInfo.cicleTime = '10'
              this.serverInfo.setupTime = '0'
              this.itemInfo.name = '';
              this.itemInfo.description = 'Server';
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
          this.simulationService.newItem(this.id, this.itemContainerInfo).subscribe(
            // @ts-ignore
            (item => {
              this.ngOnInit();
            }));
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
            this.simulationService.updateItem(this.id, this.itemContainerInfo.item.idItem, this.itemContainerInfo).subscribe(
              (itemContainer => {
                this.ngOnInit();
              })
            );
          }
        }
      }
    }
  }

  simulate() {
    console.log(this.quickSimulationForm)
  }

  deleteItemFunction() {
    if (this.itemContainerModal.item.idItem) {
      this.simulationService.deleteItem(this.id, this.itemContainerModal.item.idItem).subscribe(
        (item => {
          this.ngOnInit();
        }),
        (error => this.router.navigate(['error500']))
      )
    }
  }

  openModalDeleteItem(content: any, itemContainer: ItemContainerModel) {
    this.itemContainerModal = itemContainer;
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  openModalQuickSimulation(content: any) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'});
  }

  openModalEdit(content: any, itemContainer: ItemContainerModel) {
    this.itemContainerModal = itemContainer;
    for (let i = 0; i < this.listItems.length; i++) {
      if (this.listItems[i].item.name != this.itemContainerModal.item.name) {
        this.listNames.push(this.listItems[i].item.name)
      }
    }
    switch (this.itemContainerModal.item.description) {
      case "Source":
        this.editSourceForm.patchValue({
          nameSource: this.itemContainerModal.item.name,
          // @ts-ignore
          numberProductsSource: this.itemContainerModal.source?.numberProducts,
          interArrivalTimeSource: this.itemContainerModal.source?.interArrivalTime
        });
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
          cycletimeServer: this.itemContainerModal.server?.cicleTime
        })
        break;
      case "Queue":
        this.editQueueForm.patchValue({
          nameQueue: this.itemContainerModal.item.name,
          capacityQueue: this.itemContainerModal.queue?.capacityQueue,
          queueDiscipline: this.itemContainerModal.queue?.disciplineQueue
        })
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
    this.simulationService.updateAllItems(this.id, this.listItems).subscribe(
      (listaItems => {
        this.listItems = listaItems;
      }),
      (error => {
        this.router.navigate(['error500']);
      })
    )
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

  validateNumbers(numbers: string): boolean {
    let posComa = 0;
    for (let i = 0; i < numbers.length; i++) {
      if (numbers.charAt(i) === ',') {
        posComa = i;
        break;
      }
    }
    let firstNumber = numbers.substring(0, posComa);
    let secondNumber = numbers.substring(posComa + 1);
    let firstNumberInt = Number(firstNumber)
    let secondNumberInt = Number(secondNumber);
    if (Number.isInteger(firstNumberInt) && Number.isInteger(secondNumberInt) && firstNumberInt > 0 && secondNumberInt > 0) {
      return true;
    }
    return false;
  }

  validateProbFunc(control: AbstractControl, component: any) {
    let input = control.value;
    if (input.substring(0, 6) === "Erlang") {
      if (input.substring(6, 7) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(7, input.length - 1)
        if (component.validateNumbers(numbers)) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 9) === "LogNormal") {
      if (input.substring(9, 10) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(10, input.length - 1)
        if (component.validateNumbers(numbers)) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 10) === "Bernouilli") {
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
        if (Number.isInteger(firstNumberInt) && Number.isInteger(secondNumberInt) && Number.isInteger(thirdNumberInt) && firstNumberInt > 0 && secondNumberInt > 0 && thirdNumberInt > 0) {
          return null;
        }
        return {invalidFormat: true};
      }
    }
    if (input.substring(0, 3) === "Max") {
      if (input.substring(3,4)=== "(" && input.substring(input.length -1)=== ")"){
        let subInput= input.substring(4,input.length-1)
        let posComa = -1;
        for (let i = 0; i < subInput.length; i++) {
          if (subInput.charAt(i) === ',') {
            posComa=i;
            break;
          }
        }
        let firstNumber = subInput.substring(0, posComa);
        let secondSubInput=subInput.substring(posComa+1);
        let firstNumberInt = Number(firstNumber);
        if (Number.isInteger(firstNumberInt) && firstNumberInt >= 0){
          if (secondSubInput.substring(0,6)==="Normal"  && secondSubInput.substring(6, 7) === "(" && secondSubInput.substring(secondSubInput.length - 1) === ")"){
            let numbers = secondSubInput.substring(7, secondSubInput.length - 1)
            if (component.validateNumbers(numbers)) {
              return null;
            } else {
              return {invalidFormat: true};
            }
          }
          if (secondSubInput.substring(0,8)==="Logistic" && secondSubInput.substring(8, 9) === "(" && secondSubInput.substring(secondSubInput.length - 1) === ")"){
              let numbers = secondSubInput.substring(9, secondSubInput.length - 1)
            if (component.validateNumbers(numbers)) {
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
        if (Number.isInteger(firstNumberInt) && Number.isInteger(secondNumberInt) && Number.isInteger(thirdNumberInt) && firstNumberInt > 0 && secondNumberInt > 0 && thirdNumberInt > 0) {
          return null;
        }
        return {invalidFormat: true};
      }
    }
    if (input.substring(0, 5) === "Gamma") {
      if (input.substring(5, 6) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(6, input.length - 1)
        if (component.validateNumbers(numbers)) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 7) === "Uniform") {
      if (input.substring(7, 8) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(8, input.length - 1)
        if (component.validateNumbers(numbers)) {
          return null;
        } else {
          return {invalidFormat: true};
        }
      }
    }
    if (input.substring(0, 7) === "Weibull") {
      if (input.substring(7, 8) === "(" && input.substring(input.length - 1) === ")") {
        let numbers = input.substring(8, input.length - 1)
        if (component.validateNumbers(numbers)) {
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


  editItem() {
    if (this.itemContainerModal.item.idItem){
      switch (this.itemContainerModal.item.description){
        case "Source":
          this.itemContainerModal.item.name=<string>this.editSourceForm.value.nameSource;
          // @ts-ignore
          this.itemContainerModal.source.numberProducts=this.editSourceForm.value.numberProductsSource;
          // @ts-ignore
          this.itemContainerModal.source.interArrivalTime=this.editSourceForm.value.interArrivalTimeSource;
          break;
        case "Sink":
          // @ts-ignore
          this.itemContainerModal.item.name=this.editSinkForm.value.nameSink;
          break;
        case "Server":
          // @ts-ignore
          this.itemContainerModal.item.name=this.editServerForm.value.nameServer;
          // @ts-ignore
          this.itemContainerModal.server.setupTime=this.editServerForm.value.setUpTimeServer;
          // @ts-ignore
          this.itemContainerModal.server.cicleTime=this.editServerForm.value.cycletimeServer;
          break;
        case "Queue":
          // @ts-ignore
          this.itemContainerModal.item.name=this.editQueueForm.value.nameQueue;
          // @ts-ignore
          this.itemContainerModal.queue.disciplineQueue=this.editQueueForm.value.queueDiscipline;
          // @ts-ignore
          this.itemContainerModal.queue.capacityQueue=this.editQueueForm.value.capacityQueue
      }
      this.simulationService.updateItem(this.id,this.itemContainerModal.item.idItem,this.itemContainerModal).subscribe(
        (itemContainer=>{
          this.ngOnInit();
        }),
        (error => {
          this.router.navigate(['error500']);
        })
      )

    }
  }


}
