import { HttpClient, HttpResponse, HttpResponseBase } from '@angular/common/http';
import { environment } from "../../environments/environment";
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Collection, CollectionRequest, pausingSongFromCollectionEvent, playingSongFromCollectionEvent, SelectedSongInCollectionEvent } from '../models/models';

@Injectable({
  providedIn: 'root',
})
export class CollectionsService {
  updateTable: Subject<void> = new Subject();
  
  private selected = new BehaviorSubject<SelectedSongInCollectionEvent>(null as any);
  private playSound = new BehaviorSubject<playingSongFromCollectionEvent>(null as any);
  private pauseSound = new BehaviorSubject<pausingSongFromCollectionEvent>(null as any);
  selectedSongInCollection: Observable<SelectedSongInCollectionEvent>;
  playSongFromCollection: Observable<playingSongFromCollectionEvent>;
  pauseSongFromCollection: Observable<playingSongFromCollectionEvent>;

  constructor(private readonly http: HttpClient) { 
    this.selectedSongInCollection = this.selected.asObservable();
    this.playSongFromCollection = this.playSound.asObservable();
    this.pauseSongFromCollection = this.pauseSound.asObservable();
  }

  announceSoundSelection(selectedEvent: SelectedSongInCollectionEvent) {
    this.selected.next(selectedEvent);
  }

  announcePlaySongFromCollection(playingFromCollectionEvent: playingSongFromCollectionEvent) {
    this.playSound.next(playingFromCollectionEvent);
  }

  announcePauseSongFromCollection(pausingFromCollectionEvent: pausingSongFromCollectionEvent) {
    this.pauseSound.next(pausingFromCollectionEvent);
  }


  public emitUpdateTable() {
    this.updateTable.next();
  }

  public getCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${environment.serverURL}/collection`);
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

  getCollectionTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.serverURL}/collection/types`);
  }
}
