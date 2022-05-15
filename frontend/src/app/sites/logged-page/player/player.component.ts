import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { fromEvent, Subject } from "rxjs";
import { filter, takeUntil } from 'rxjs/operators';
import { CollectionsService } from 'src/app/services/collections.service';
import { PlayerService } from 'src/app/services/player.service';
import { getAvatarUrlByMongoRef } from 'src/app/shared/functions';
import { Collection, PlayMode, User } from "../../../models/models";
import { UserService } from "../../../services/user.service";

@Component({
  selector: 'app-player',
  templateUrl: './player.component.html',
})
export class PlayerComponent implements AfterViewInit, OnInit, OnDestroy {
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
  audioTrackUrl?: string;
  trackName: string = "no song selected";
  trackAuthors: string = "no song selected";
  collectionAvatarUrl: string = "https://images.genius.com/d95e4b9e5949a632ba9486d3b5404ec1.1000x1000x1.jpg";

  private destroy = new Subject<void>();
  private currentlyTrackId?: number;
  private currentlyCollection?: Collection;
  private currentlyTrackIndex!: number;

  constructor(
    private readonly userService: UserService,
    private readonly collectionService: CollectionsService,
    private readonly playerService: PlayerService
  ) {
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

    this.collectionService.selectedSongInCollection.pipe(
        takeUntil(this.destroy),
        filter(selectedEvent => selectedEvent !== null)
      ).subscribe(selectedEvent => {
        console.log("selected event: ", selectedEvent)
      const newAudioTrackId = selectedEvent.collection.tracks[selectedEvent.selectedTrackIndex].id;
      this.audioTrackUrl = `http://localhost:8080/track/${newAudioTrackId}?token=${localStorage.getItem('access_token')}`;
      this.currentlyCollection = selectedEvent.collection;
      this.currentlyTrackId = selectedEvent.selectedTrackId;
      this.currentlyTrackIndex = selectedEvent.selectedTrackIndex;

      this.collectionAvatarUrl = getAvatarUrlByMongoRef(selectedEvent.collection.imageMongoRef);
      this.trackName = selectedEvent.collection.tracks[selectedEvent.selectedTrackIndex].name;
      this.authorsText();
      this.player.nativeElement.load();
      this.player.nativeElement.currentTime = 0;
      this.isPlaying = true;
      this.player.nativeElement.play();
      this.playerService.announcePlaySound({collectionId: this.currentlyCollection.id, selectedTrackId: this.currentlyTrackId});
    })

    this.collectionService.playSongFromCollection.pipe(
      takeUntil(this.destroy),
      filter(event => event !== null)
    ).subscribe(event => {
      this.isPlaying = true;
      this.player.nativeElement.play();
      if(this.currentlyCollection != undefined && this.currentlyTrackId != undefined) {
        this.playerService.announcePlaySound({collectionId: this.currentlyCollection.id, selectedTrackId: this.currentlyTrackId});
      }
    });

    this.collectionService.pauseSongFromCollection.pipe(
      takeUntil(this.destroy),
      filter(event => event !== null)
    ).subscribe(event => {
      this.isPlaying = false;
      this.player.nativeElement.pause();
      if(this.currentlyCollection != undefined && this.currentlyTrackId != undefined) {
        this.playerService.announcePauseSound({collectionId: this.currentlyCollection.id, selectedTrackId: this.currentlyTrackId});
      }
    });

  }

  public formatPlayerTime(total: number | undefined): string {
    if (!total) total = 0;
    const minutes = Math.floor(total / 60);
    const seconds = Math.floor(total % 60);
    return `${this.formatNumber(minutes)}:${this.formatNumber(seconds)}`;
  }

  private authorsText() {
    let newText = ``;
    let artists = this.currentlyCollection?.tracks[this.currentlyTrackIndex].artists!;
    for(let i = 0; i < artists.length; i++) {
      if(i === artists.length - 1) {
        newText += `${artists[i].firstName} ${artists[i].lastName}`;
      } else {
        newText += `${artists[i].firstName} ${artists[i].lastName}, `;
      }
    }

    this.trackAuthors = newText;
  }

  private registerPlayerEvents() {
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

  private formatNumber(number: number): string {
    return ("0" + number).slice(-2);
  }

  public togglePlay(): void {
    if (this.isPlaying) {
      this.player.nativeElement.pause();
      this.isPlaying = false;
      if(this.currentlyCollection != undefined && this.currentlyTrackId != undefined) {
        this.playerService.announcePauseSound({collectionId: this.currentlyCollection.id, selectedTrackId: this.currentlyTrackId});
      }
    } else {
      this.player.nativeElement.play();
      this.isPlaying = true;
      if(this.currentlyCollection != undefined && this.currentlyTrackId != undefined) {
        this.playerService.announcePlaySound({collectionId: this.currentlyCollection.id, selectedTrackId: this.currentlyTrackId});
      }
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
