import {Simulation} from "./simulation.model";

export interface Folder{
  idFolder?:number;
  nameFolder:String;
  simulations:Simulation[];
  isLastPage:boolean;
  page?:number;
}
