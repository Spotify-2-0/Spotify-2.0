import { HttpClient } from '@angular/common/http';
import { environment } from "../../environments/environment";
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Collection } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class CollectionsService {

  constructor(private readonly http: HttpClient) { }

  public getCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${environment.serverURL}/collection`)
  }
}
