import {Simulation} from "./simulation.model";

export interface Folder{
  idFolder?:number;
  nameFolder:string;
  simulations:Simulation[];
  isLastPage:boolean;
  page?:number;
}
