import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { PausingSongEvent, PlayingSongEvent} from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  private play = new BehaviorSubject<PlayingSongEvent>(null as any);
  private pause = new BehaviorSubject<PausingSongEvent>(null as any);
  playingSound: Observable<PlayingSongEvent>;
  pausingSong: Observable<PausingSongEvent>;

  constructor() { 
    this.playingSound = this.play.asObservable();
    this.pausingSong = this.pause.asObservable();
  }

  announcePlaySound(playingEvent: PlayingSongEvent) {
    this.play.next(playingEvent);
  }

  announcePauseSound(pausingEvent: PausingSongEvent) {
    this.pause.next(pausingEvent);
  }

}
