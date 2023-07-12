import {ItemModel} from "./item.model";
import {QueueModel} from "./queue.model";
import {ServerModel} from "./server.model";
import {SinkModel} from "./sink.model";
import {SourceModel} from "./source.model";
import {ConnectionModel} from "../Connection/connection.model";

export interface ItemContainerModel{
  item:ItemModel,
  queue?:QueueModel,
  server?:ServerModel,
  sink?:SinkModel,
  source?:SourceModel,
  connections?:ConnectionModel[]
}
