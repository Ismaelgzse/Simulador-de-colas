import {ItemModel} from "../Items/item.model";

export interface ConnectionModel{
  idConnect?:number,
  percentage:number,
  originItem:ItemModel,
  destinationItem:ItemModel
}
