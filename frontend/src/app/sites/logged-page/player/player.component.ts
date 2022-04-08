import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { fromEvent } from "rxjs";
import { min, tap } from "rxjs/operators";
import { User } from "../../../models/models";
import { UserService } from "../../../services/user.service";

@Component({
  selector: 'app-player',
  templateUrl: './player.component.html',
})
export class PlayerComponent implements AfterViewInit, OnInit {
  @ViewChild('player') player!: ElementRef<HTMLAudioElement>;

  user?: User;
  volume?: number;
  progress: number = 0;
  currentTime?: number;
  maxTime?: number;
  isPlaying: boolean = false;

  constructor(
    private readonly userService: UserService,
  ) { }

  public ngOnInit(): void {
    this.user = this.userService.currentUser()!;
  }

  public getTrackURL(): string {
    return `http://localhost:8080/track/anyid?token=${localStorage.getItem('access_token')}`
  }

  public ngAfterViewInit(): void {

    fromEvent(this.player.nativeElement, 'loadeddata').subscribe(event => {
      this.maxTime = this.player.nativeElement.duration;
      this.currentTime = this.player.nativeElement.currentTime;
      this.volume = this.player.nativeElement.volume;
      console.log(this.volume)
    });

    fromEvent(this.player.nativeElement, 'timeupdate').subscribe(event => {
      console.log(this.player.nativeElement.duration)
      const current = this.player.nativeElement.currentTime;
      this.progress = current;
      this.currentTime = current;
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
    console.log("play")
    console.log(this.player)
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
  }

  jumpTo($event: Event) {
    const second = Number(($event.target as HTMLInputElement).value);
    this.player.nativeElement.currentTime = second;
    this.progress = second
  }
}
