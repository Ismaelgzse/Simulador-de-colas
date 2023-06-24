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


  constructor(@Inject(DOCUMENT) document: Document, private simulationService: SimulationService, private router: Router, private route: ActivatedRoute) {
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
      // @ts-ignore
      imagenes[i].addEventListener("dragend", this.moveElement);
    }
  }

  drag(event: DragEvent) {
    // @ts-ignore
    event.dataTransfer.setData("text", event.target.id);
  }

  moveElement(event: DragEvent) {
    var element = event.target;
    if (element instanceof Element) {
      element.setAttribute("style", "left:" + event.pageX + "px");
      console.log(event.pageX)
      element.setAttribute("style", "top:" + event.pageY + "px");
    }
  }

  dragOver(event: DragEvent) {
    event.preventDefault()
  }

  getRoute() {
    return this.id
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
      /*var parentClass = document.getElementById(data).parentNode.className;
      //console.log(parentClass)
      var element = document.getElementById(data)
      if (parentClass === "dragItemContainer") {
        let type= data.substring(0,4)
        let strModal=null
        switch (type) {
          case "Fuen": strModal= "data-target=\"#modalEditFuente\""
            break
          case "Cola": strModal= "data-target=\"#modalEditCola\""
            break
          case "Proc": strModal= "data-target=\"#modalEditProc\""
            break
          case "Sumi": strModal= "data-target=\"#modalEditSumidero\""
            break
        }
        element = document.getElementById(data).cloneNode(true)
        element.draggable = true;
        element.addEventListener("dragstart", drag);
        element.style.position = "absolute";
        element.style.left = event.pageX - document.getElementById(data).offsetWidth*1.5 + "px";
        element.style.top = event.pageY - document.getElementById(data).offsetHeight*1.5 + "px";
        element.id = "Elemento" + contador
        contador = contador + 1
        var headerButtons= document.createElement("div")
        headerButtons.innerHTML="<div class='buttons'>"+
          "<i type='button' class='actionButton fa-regular fa-pen-to-square' data-toggle=\"modal\""+strModal+"></i>"+
          "<i type='button' class='actionButton fa-regular fa-trash-can' onclick=\"deleteElement2(\'"+element.id+"\')\"></i>"+
          "</div>"
        element.insertBefore(headerButtons,element.firstChild)
        event.target.appendChild(element);

       */
      /*var nuevoElemento = document.createElement();
      nuevoElemento.innerHTML = element2;
      event.target.replaceChild(nuevoElemento,element)*/
      /*
          } else {
            element.draggable = true;
            element.addEventListener("dragstart", drag);
            element.style.position = "absolute";
            element.style.left = event.pageX - document.getElementById(data).offsetWidth*1.5 + "px";
            element.style.top = event.pageY - document.getElementById(data).offsetHeight*1.5  + "px";
          }

       */
    }
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
      x=x+200
    }
    this.simulationService.updateAllItems(this.id,this.listItems).subscribe(
      (listaItems=>{
        this.listItems=listaItems;
      }),
      (error => {
        this.router.navigate(['error500']);
      })
    )
  }


}
