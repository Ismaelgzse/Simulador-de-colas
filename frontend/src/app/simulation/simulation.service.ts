import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ItemContainerModel} from "./Items/itemContainer.model";
import {ConnectionModel} from "./Connection/connection.model";
import {quickSimulationFormDTOModel} from "./QuickSimulationFormDTO/quickSimulationFormDTO.model";

// @ts-ignore
declare var SockJS;
// @ts-ignore
declare var Stomp;

@Injectable()
export class SimulationService {
  private stompClient;
  message: string
  listItemSimulation: ItemContainerModel[]


  constructor(private httpClient: HttpClient) {
    const ws = new SockJS("https://localhost:8443/ws");
    this.stompClient = Stomp.over(ws);
  }

  public connectAlt(simulationId: string) {

    // @ts-ignore
    this.stompClient.connect({}, (frame) => {
      // @ts-ignore
      this.stompClient.subscribe(`/simulationInfo/${simulationId}`, (message) => {
        this.listItemSimulation = JSON.parse(message.body);
      });
      this.stompClient.send('/wsAPI/simulateMessage/' + simulationId, {}, JSON.stringify('connect'))
    });
  }

  public connect(simulationId: string) {

    // @ts-ignore
    this.stompClient.connect({}, (frame) => {
      // @ts-ignore
      this.stompClient.subscribe(`/simulationInfo/${simulationId}`, (message) => {
        this.listItemSimulation = JSON.parse(message.body);
      });
    });
  }

  public sendMessage(simulationId: string) {
    this.stompClient.send('/wsAPI/simulateMessage/' + simulationId, {}, JSON.stringify('connect'))
  }

  public closeConnection() {
    this.stompClient.disconnect();
  }

  quickSimulation(idSimulation: number, quickSimulatonForm: quickSimulationFormDTOModel): Observable<any> {
    // @ts-ignore
    return this.httpClient.post('api/simulation/' + idSimulation + '/quickSimulation', quickSimulatonForm, {withCredentials: true}) as Observable<any>;
  }

  generatePDF(idSimulation: number, listSimulations: ItemContainerModel[][]): Observable<Blob> {
    const httpOptions = {
      responseType: 'blob' as 'json',
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    // @ts-ignore
    return this.httpClient.post(`/api/simulation/${idSimulation}/quickSimulation/pdf`, listSimulations, {
      ...httpOptions,
      withCredentials: true
    });
  }

  generateExcel(idSimulation: number, listSimulations: ItemContainerModel[][]): Observable<Blob> {
    const httpOptions = {
      responseType: 'blob' as 'json',
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    // @ts-ignore
    return this.httpClient.post(`/api/simulation/${idSimulation}/quickSimulation/excel`, listSimulations, {
      ...httpOptions,
      withCredentials: true
    });
  }

  getItems(idSimulation: number): Observable<any> {
    return this.httpClient.get('/api/simulations/' + idSimulation + '/items', {withCredentials: true}) as Observable<ItemContainerModel[]>;
  }

  getSimulationInfo(idSimulation: number): Observable<any> {
    return this.httpClient.get('/api/simulation/' + idSimulation, {withCredentials: true}) as Observable<any>;
  }

  newItem(idSimulation: number, item: ItemContainerModel): Observable<any> {
    return this.httpClient.post('/api/simulations/' + idSimulation + '/item', item, {withCredentials: true}) as Observable<any>;
  }

  updateItem(idSimulation: number, idItem: number, item: ItemContainerModel): Observable<any> {
    return this.httpClient.put('/api/simulations/' + idSimulation + '/item/' + idItem, item, {withCredentials: true}) as Observable<any>;
  }

  updateAllItems(idSimulation: number, lista: ItemContainerModel[]): Observable<any> {
    return this.httpClient.put('/api/simulations/' + idSimulation + '/item/all', lista, {withCredentials: true}) as Observable<any>;
  }

  deleteItem(idSimulation: number, idItem: number): Observable<any> {
    return this.httpClient.delete('/api/simulations/' + idSimulation + '/item/' + idItem, {withCredentials: true}) as Observable<any>;
  }

  newConnection(connection: ConnectionModel): Observable<any> {
    return this.httpClient.post('/api/connection', connection, {withCredentials: true}) as Observable<any>;
  }

  deleteConnection(idConnection: number): Observable<any> {
    return this.httpClient.delete('/api/connection/' + idConnection, {withCredentials: true}) as Observable<any>;

  }

  getStatusSimulation(idSimulation: number): Observable<any> {
    return this.httpClient.get('api/simulation/' + idSimulation + "/isRunning", {withCredentials: true}) as Observable<any>;
  }


}
