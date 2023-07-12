import {ItemModel} from "../Items/item.model";

export interface ConnectionModel{
  idConnection?:number;
  percentage:number;
  originItem:ItemModel;
  destinationItem:ItemModel;
}
