import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { fromEvent, Subject } from "rxjs";
import { CollectionsService } from 'src/app/services/collections.service';
import { PlayerService } from 'src/app/services/player.service';
import { Collection, PlayMode, User } from "../../../models/models";
import { UserService } from "../../../services/user.service";

@Component({
  selector: 'app-player',
  templateUrl: './player.component.html',
})
export class PlayerComponent implements AfterViewInit, OnInit, OnDestroy {
  PlayMode = PlayMode;
  user?: User;
  volume?: number;
  progress: number = 0;
  currentTime?: number;
  maxTime?: number;
  isHoldingDownPlayer: boolean = false;
  collectionAvatarUrl: string = "https://images.genius.com/d95e4b9e5949a632ba9486d3b5404ec1.1000x1000x1.jpg";

  private destroy = new Subject<void>();

  constructor(
    public readonly player: PlayerService,
    private readonly userService: UserService,
  ) {
  }

  public getCurrentTrackImageURL() {
    const cc = this.player.currentCollection;
    if (!cc) {
      return this.collectionAvatarUrl;
    }
    return `http://localhost:8080/collection/${cc.id}/avatar`
  }

  public ngOnInit(): void {
    this.user = this.userService.currentUser()!;
    this.volume = Number(localStorage.getItem('volume'));
  }

  public ngOnDestroy(): void {
    this.destroy.next();
    this.destroy.complete();
  }

  public getTrackURL(): string {
    return `http://localhost:8080/track/anyid?token=${localStorage.getItem('access_token')}`
  }

  public ngAfterViewInit(): void {
    this.registerPlayerEvents();
  }

  public formatPlayerTime(total: number | undefined): string {
    if (!total) total = 0;
    const minutes = Math.floor(total / 60);
    const seconds = Math.floor(total % 60);
    return `${this.formatNumber(minutes)}:${this.formatNumber(seconds)}`;
  }

  public authors() {
    return this.player.currentTrack?.artists
      .map(a => a.displayName)
      .join(', ');
  }

  private registerPlayerEvents() {
    fromEvent(this.player.native, 'loadeddata').subscribe(event => {
      this.maxTime = this.player.native.duration;
      this.currentTime = this.player.native.currentTime;
      this.volume = this.player.native.volume;
    });

    fromEvent(this.player.native, 'timeupdate').subscribe(event => {
      const current = this.player.native.currentTime;
      if (!this.isHoldingDownPlayer) {
        this.progress = current;
        this.currentTime = current;
      }
    });
  }

  private formatNumber(number: number): string {
    return ("0" + number).slice(-2);
  }

  public togglePlay(): void {
    if (this.player.isPaused()) {
      this.player.play();
    } else {
      this.player.pause();
    }
  }

  volumeChanged($event: Event) {
    const volume = Number(($event.target as HTMLInputElement).value);
    this.player.changeVolume(volume)
  }

  jumpTo($event: Event) {
    const second = Number(($event.target as HTMLInputElement).value);
    this.player.jump(second);
  }

  progressInput($event: Event) {
    const second = Number(($event.target as HTMLInputElement).value);
    this.currentTime = second;
  }

  switchPlayMode() {
    switch (this.player.playMode) {
      case PlayMode.Default:  this.player.setMode(PlayMode.Single); break;
      case PlayMode.Single:   this.player.setMode(PlayMode.Playlist); break;
      case PlayMode.Playlist: this.player.setMode(PlayMode.Default); break;
    }
  }
}
