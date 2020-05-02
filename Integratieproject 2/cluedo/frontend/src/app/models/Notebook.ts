import { NotationSymbol } from './enums';

export interface Notebook {
  notebookId: number,
  characters: NoteLine[],
  weapons: NoteLine[],
  rooms: NoteLine[],
}

export interface NoteLine {
  noteLineId: number,
  card: string,
  crossed: boolean,
  columns: NotationSymbol[]
}