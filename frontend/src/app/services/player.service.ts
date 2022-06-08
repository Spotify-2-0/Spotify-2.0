import { Injectable } from '@angular/core';
import { BehaviorSubject, fromEvent, Subject } from 'rxjs';
import { Collection, PlayerEvent, PlayMode, Track } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  public readonly native: HTMLAudioElement;


  currentTrack?: Track;
  currentCollection?: Collection;
  onEvent = new Subject<PlayerEvent>();

  public playMode: PlayMode = PlayMode.Default;
  public volume: number;

  constructor() {
    this.volume = Number(localStorage.getItem('volume'));
    this.native = new Audio();
    this.native.preload = 'none';
    this.native.volume = this.volume;

    fromEvent(this.native, 'ended').subscribe(event => {
      if (PlayMode.Single === this.playMode) {
        this.native.currentTime = 0;
        this.native.play();
        return;
      }
      this.queueNextFromCollection();
    });
  }

  public queuePreviousFromCollection() {
    const cc = this.currentCollection!;
    const idx = cc.tracks.findIndex(t => t.id === this.currentTrack?.id) - 1 ?? 0;
    const track = cc.tracks[idx];
    this.playTrack(track);
  }

  public queueNextFromCollection() {
    const cc = this.currentCollection!;
    let idx = cc.tracks.findIndex(t => t.id === this.currentTrack?.id) + 1 ?? 0;
    if (idx === cc.tracks.length) {
      if (PlayMode.Playlist === this.playMode) {
        idx = 0;
      } else {
        this.pause();
        return;
      }
    }
    const track = cc.tracks[idx];
    this.playTrack(track);
  }

  public playTrack(track: Track): void {
    const native = this.native;
    native.src = `http://localhost:8080/track/${track.id}?token=${localStorage.getItem('access_token')}`;
    native.load();
    native.currentTime = 0;
    native.play()
      .then(() => {
        this.currentTrack = track;
      })
  }

  public play(): void {
    this.native.play();
  }

  public pause(): void {
    this.native.pause();
  }

  public jump(second: number): void {
    this.native.currentTime = second;
  }

  public setMode(mode: PlayMode): void {
    this.playMode = mode;
  }

  public changeVolume(volume: number): void {
    const player = this.native;
    this.volume = volume;
    player.volume = this.volume;
    localStorage.setItem('volume', String(this.volume));
  }

  public playCollection(collection: Collection, start: number = 0): void {
    const native = this.native;
    const track = collection.tracks[start];
    native.src = `http://localhost:8080/track/${track.id}?token=${localStorage.getItem('access_token')}`;
    native.load();
    native.currentTime = 0;
    native.play()
      .then(_ => {
        this.currentTrack = track;
        this.currentCollection = collection;
      });
  }

  public isPaused(): boolean {
    return this.native.paused;
  }

  dispatchEvent(event: string, data: any = undefined) {
    this.onEvent.next({
      event: event,
      data: data
    });
  }
}
