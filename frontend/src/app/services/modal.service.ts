import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  private modals: any[] = [];

  constructor() { }

  public add = (modal: any) =>
    this.modals.push(modal);

  public remove = (id: string) =>
    this.modals = this.modals.filter(x => x.id !== id);

  public open = (id: string) =>
    this.modals.find(x => x.id === id).open();

  public close = (id: string) =>
    this.modals.find(x => x.id === id).close();

}
