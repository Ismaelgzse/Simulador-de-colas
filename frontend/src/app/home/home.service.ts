import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Folder} from "./folder.model";
import {Simulation} from "./simulation.model";


@Injectable()
export class HomeService{
  constructor(private httpClient: HttpClient) {
  }

  getFolders():Observable<any>{
    return this.httpClient.get('/api/foldersAlt',{withCredentials: true}) as Observable<any>;
  }

  isAuthenticated():Observable<any>{
    return this.httpClient.get('api/isAuthenticated',{withCredentials: true}) as Observable<any>;
  }

  getPageSimulation(idFolder:number,page:number):Observable<any>{
    return this.httpClient.get('/api/folders/'+idFolder+'/simulations?page='+page,{withCredentials: true}) as Observable<any>;
  }

  getIdFolder(nameFolder:string):Observable<number>{
    return this.httpClient.get('/api/folder?name='+nameFolder) as Observable<number>;
  }

  deleteFolder(id:number): Observable<any>{
    return this.httpClient.delete('/api/folders/'+id,{withCredentials: true}) as Observable<any>;
  }

  deleteSimulation(idFolder:number,idSimulation:number): Observable<any>{
    return this.httpClient.delete('/api/folders/'+idFolder+'/simulations/'+idSimulation,{withCredentials: true}) as Observable<any>;
  }

  saveFolder(folder:Folder):Observable<Folder>{
    return this.httpClient.post('/api/folders',{"idFolder":folder.idFolder,"nameFolder":folder.nameFolder},{withCredentials: true}) as Observable<any>;
  }

  updateFolder(folder:Folder):Observable<Folder>{
    return this.httpClient.put('/api/folders/'+folder.idFolder,{"idFolder":folder.idFolder,"nameFolder":folder.nameFolder},{withCredentials: true}) as Observable<any>;
  }

  saveSimulation(simulation:Simulation):Observable<any>{
    return this.httpClient.post('/api/folders/'+simulation.folderId+'/simulations',simulation,{withCredentials: true}) as Observable<any>;
  }

  updateSimulation(simulation:Simulation):Observable<Simulation>{
    return this.httpClient.put('/api/folders/'+simulation.folderId+'/simulations/'+simulation.idSimulation,simulation,{withCredentials: true}) as Observable<any>;
  }

  updateImage(simulation:number,form:FormData):Observable<any>{
    return this.httpClient.put('/api/simulations/'+simulation+'/image',form,{withCredentials: true}) as Observable<any>;
  }

}
