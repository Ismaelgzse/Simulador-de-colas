import {ItemContainerModel} from "../Items/itemContainer.model";

export interface quickSimulationFormDTOModel {
  timeSimulation?:number,
  numberSimulations?:number,
  pdfFormat?:boolean,
  csvFormat?:boolean,
  listSimulations?:ItemContainerModel[][]
}
