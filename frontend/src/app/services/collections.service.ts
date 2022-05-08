import { HttpClient, HttpResponse, HttpResponseBase } from '@angular/common/http';
import { environment } from "../../environments/environment";
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Collection, pausingSongFromCollectionEvent, playingSongFromCollectionEvent, SelectedSongInCollectionEvent } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class CollectionsService {
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

  public getCollections(): Observable<Collection[]> {
    return this.http.get<Collection[]>(`${environment.serverURL}/collection`);
  }

  public getCollectionById(id: string): Observable<Collection> {
    return this.http.get<Collection>(`${environment.serverURL}/collection/${id}`);
  }

  public deleteTrackFromCollection(collectionId: string, trackId: string): Observable<void> {
    return this.http.delete<void>(`${environment.serverURL}/collection/${collectionId}/track/${trackId}`);
  }
}
