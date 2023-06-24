import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ItemContainerModel} from "./Items/itemContainer.model";

@Injectable()
export class SimulationService {
  constructor(private httpClient: HttpClient) {
  }

  getItems(idSimulation: number): Observable<any> {
    return this.httpClient.get('/api/simulations/' + idSimulation + '/items', {withCredentials: true}) as Observable<any>;
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
}
