import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Collection } from '../models/models';
import { CollectionRequest } from '../models/models';

@Injectable({
  providedIn: 'root',
})
export class CollectionsService {
  updateTable: Subject<void> = new Subject();

  constructor(private readonly http: HttpClient) {}

  public emitUpdateTable() {
    this.updateTable.next();
  }

  public getCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${environment.serverURL}/collection`);
  }

  public addCollection(request: CollectionRequest) {
    const formData = new FormData();
    formData.append('name', request.name);
    if (request.image != null) {
      formData.append('image', request.image);
    }
    formData.append('type', request.type);

    this.http
      .post<any>(`${environment.serverURL}/collection`, formData)
      .subscribe(_ => this.emitUpdateTable());
  }

  getCollectionTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.serverURL}/collection/types`);
  }
}
