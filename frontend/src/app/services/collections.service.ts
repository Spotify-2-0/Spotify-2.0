import { HttpClient, HttpResponse, HttpResponseBase } from '@angular/common/http';
import { environment } from "../../environments/environment";
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Collection, CollectionRequest, IdAndNameDTO, pausingSongFromCollectionEvent, PlayCollectionEvent, playingSongFromCollectionEvent, SelectedSongInCollectionEvent } from '../models/models';

@Injectable({
  providedIn: 'root',
})
export class CollectionsService {
  updateTable: Subject<void> = new Subject();

  constructor(private readonly http: HttpClient) {
  }

  public emitUpdateTable() {
    this.updateTable.next();
  }

  public getCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${environment.serverURL}/collection`);
  }

  public getUserCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${environment.serverURL}/collection/me`);
  }

  public getLoggedUserColections(): Observable<IdAndNameDTO[]> {
    return this.http.get<IdAndNameDTO[]>(`${environment.serverURL}/collection/loggedUser`)
  }

  public getCollectionById(id: string): Observable<Collection> {
    return this.http.get<Collection>(`${environment.serverURL}/collection/${id}`);
  }

  public deleteTrackFromCollection(collectionId: string, trackId: string): Observable<void> {
    return this.http.delete<void>(`${environment.serverURL}/collection/${collectionId}/track/${trackId}`);
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

  public getCollectionTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.serverURL}/collection/types`);
  }

  public getFavorites(): Observable<Collection> {
    return this.http.get<Collection>(`${environment.serverURL}/collection/favourites`);
  }

  public addTrackToFavorites(trackId: string): Observable<Collection> {
    return this.http.post<Collection>(`${environment.serverURL}/collection/favourites/${trackId}`, null);
  }

  public removeTrackFromFavorites(trackId: string): Observable<void> {
    return this.http.delete<void>(`${environment.serverURL}/collection/favourites/${trackId}`);
  }

  public followCollection(collectionId: string): Observable<void> {
    return this.http.post<void>(`${environment.serverURL}/collection/${collectionId}/follow`, null);
  }

  public unfollowCollection(collectionId: string): Observable<void> {
    return this.http.post<void>(`${environment.serverURL}/collection/${collectionId}/unfollow`, null);
  }

  public addVisitToCollection(collectionId: string): Observable<void> {
    return this.http.post<void>(`${environment.serverURL}/collection/${collectionId}/addVisit`, null);
  }

  public deleteCollection(collectionId: string): Observable<void> {
    return this.http.delete<void>(`${environment.serverURL}/collection/${collectionId}`);
  }
}
