import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { fromEvent } from "rxjs";
import { PlayMode, User } from "../../../models/models";
import { UserService } from "../../../services/user.service";

@Component({
  selector: 'app-player',
  templateUrl: './player.component.html',
})
export class PlayerComponent implements AfterViewInit, OnInit {
  @ViewChild('player') player!: ElementRef<HTMLAudioElement>;

  PlayMode = PlayMode;
  playMode: PlayMode = PlayMode.Default;
  user?: User;
  volume?: number;
  progress: number = 0;
  currentTime?: number;
  maxTime?: number;
  isPlaying: boolean = false;
  isHoldingDownPlayer: boolean = false;

  constructor(
    private readonly userService: UserService,
  ) { }

  public ngOnInit(): void {
    this.user = this.userService.currentUser()!;
    this.volume = Number(localStorage.getItem('volume'));
  }

  public getTrackURL(): string {
    return `http://localhost:8080/track/anyid?token=${localStorage.getItem('access_token')}`
  }

  public ngAfterViewInit(): void {
    this.player.nativeElement.volume = this.volume!;
    fromEvent(this.player.nativeElement, 'loadeddata').subscribe(event => {
      this.maxTime = this.player.nativeElement.duration;
      this.currentTime = this.player.nativeElement.currentTime;
      this.volume = this.player.nativeElement.volume;
    });

    fromEvent(this.player.nativeElement, 'timeupdate').subscribe(event => {
      const current = this.player.nativeElement.currentTime;
      if (!this.isHoldingDownPlayer) {
        this.progress = current;
        this.currentTime = current;
      }
    });

    fromEvent(this.player.nativeElement, 'ended').subscribe(event => {
      if (PlayMode.Single === this.playMode) {
        this.player.nativeElement.currentTime = 0;
        this.player.nativeElement.play();
      }
    });
  }

  public formatPlayerTime(total: number | undefined): string {
    if (!total) total = 0;
    const minutes = Math.floor(total / 60);
    const seconds = Math.floor(total % 60);
    return `${this.formatNumber(minutes)}:${this.formatNumber(seconds)}`;
  }

  private formatNumber(number: number): string {
    return ("0" + number).slice(-2);
  }

  public togglePlay(): void {
    if (this.isPlaying) {
      this.player.nativeElement.pause();
      this.isPlaying = false;
    } else {
      this.player.nativeElement.play();
      this.isPlaying = true;
    }

  }

  volumeChanged($event: Event) {
    this.volume = Number(($event.target as HTMLInputElement).value);
    this.player.nativeElement.volume = this.volume;
    localStorage.setItem('volume', String(this.volume));
  }

  jumpTo($event: Event) {
    const second = Number(($event.target as HTMLInputElement).value);
    this.player.nativeElement.currentTime = second;
    this.progress = second
  }

  progressInput($event: Event) {
    const second = Number(($event.target as HTMLInputElement).value);
    this.currentTime = second;
  }

  switchPlayMode() {
    switch (this.playMode) {
      case PlayMode.Default:  this.playMode = PlayMode.Single;   break;
      case PlayMode.Single:   this.playMode = PlayMode.Playlist; break;
      case PlayMode.Playlist: this.playMode = PlayMode.Default;  break;
    }
  }
}
