import { AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { Collection, Track, User } from 'src/app/models/models';
import { CollectionsService } from 'src/app/services/collections.service';
import { PlayerService } from 'src/app/services/player.service';
import { getAvatarUrlByMongoRef } from 'src/app/shared/functions';
import { IconComponent } from '../icon/icon.component';
import { UserService } from "../../services/user.service";

@Component({
  selector: 'app-spoti-table',
  templateUrl: './spoti-table.component.html',
})
export class SpotiTableComponent implements OnInit, OnDestroy, AfterViewInit {

  @Input() tableType!: string;
  @Input() data: any;

  @ViewChildren('controlButton') controlButtons!: QueryList<ElementRef>;
  @ViewChildren('controlButtonIcon') controlButtonsIcons!: QueryList<IconComponent>;

  public columns: string[] = [];
  public getAvatarUrlByMongoRef = getAvatarUrlByMongoRef;

  public currentlySelectedTrackId?: number;
  public currentlyHoveredTrackId?: number;
  public isPlaying = false;
  public userFavorites?: Collection;
  public isFavoritesLoaded = false;

  private destroy = new Subject<void>();
  private firstPlay: boolean = false;
  private user?: User;
  private subs = new Subscription();

  constructor(
    private readonly collectionService: CollectionsService,
    public readonly playerService: PlayerService,
    private readonly userService: UserService
  ) {}

  ngOnInit(): void {
    this.user = this.userService.currentUser()!;
    if (this.tableType === 'collections') {
      this.columns = ['title', 'type', 'plays', 'duration', 'published'];
      this.data = this.data as Collection[];
      console.log('collections: ', this.data);
      this.subs.add(this.collectionService.updateTable.subscribe((_) => {
        this.collectionService.getCollections().subscribe((data) => {
          this.data = data;
          console.log(data);
        });
      }));
    } else {
      this.data = this.data as Collection;
      this.columns = ['title', 'plays', 'Duration', 'published', ''];
      this.collectionService.getFavorites().subscribe(response => {
        this.userFavorites = response;
        this.isFavoritesLoaded = true;
      });
    }

  }

  ngAfterViewInit(): void {
    console.log(1)
    // this.playerService.playingSound.pipe(
    //   takeUntil(this.destroy),
    //   filter(playingEvent => playingEvent !== null)
    // ).subscribe(playingEvent => {
    //   if(this.tableType !== "collections" && playingEvent.collectionId === this.data.id) {
    //     this.isPlaying = true;
    //   }
    // })
    //
    // this.playerService.pausingSong.pipe(
    //   takeUntil(this.destroy),
    //   filter(pausingEvent => pausingEvent !== null)
    // ).subscribe(playingEvent => {
    //   if(this.tableType !== "collections" && playingEvent.collectionId === this.data.id) {
    //     this.isPlaying = false;
    //   }
    // })
    //
    // this.collectionService.playingCollection.pipe(
    //   takeUntil(this.destroy),
    //   filter(event => event !== null)
    // ).subscribe(event => {
    //   if(this.data.tracks.length > 0 && event.collectionId === this.data.id) {
    //     this.togglePlay(this.data.tracks[0].id, 0);
    //   }
    // });
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
    this.destroy.next();
    this.destroy.complete();
  }

  msToHMS(ms: number): string {
    // 1- Convert to seconds:
    let seconds = ms / 1000;
    // 2- Extract hours:
    let hours = Math.floor(seconds / 3600); // 3,600 seconds in 1 hour
    seconds = seconds % 3600; // seconds remaining after extracting hours
    // 3- Extract minutes:
    let minutes = Math.floor(seconds / 60); // 60 seconds in 1 minute
    // 4- Keep only seconds not extracted to minutes:
    seconds = Math.floor(seconds % 60);


    if (hours === 0) {
      return this.formatNumber(minutes) + ':' + this.formatNumber(seconds);
    }
    return this.formatNumber(hours) + ':' + this.formatNumber(minutes) + ':' + this.formatNumber(seconds);
  }

  formatNumber(n: number){
    if(Math.floor(n / 10) === 0){
      return "0" + (n.toString()).slice(-2);
    }
    return n;
  }

  convertDate(dateString: string): string {
    return new Date(dateString).toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
    });
  }

  artistsToText(users: User[]): string {
    return users.map(u => u.displayName).join(', ');
  }

  deleteBtn(trackId: number) {
    this.collectionService.deleteTrackFromCollection(this.data.id, trackId.toString())
      .subscribe(response => {
        this.data.tracks = this.data.tracks
              .filter((track: Track) => track.id !== trackId)
      })
  }

  togglePlay(trackId: number, index: number) {
    if((this.currentlySelectedTrackId !== trackId || this.currentlySelectedTrackId === undefined)) {
      this.currentlySelectedTrackId = trackId;
      this.collectionService.announceSoundSelection({
        collection: this.data,
        selectedTrackId: trackId,
        selectedTrackIndex: index
      })
    } else if(this.currentlySelectedTrackId === trackId && this.isPlaying === false) {
      this.collectionService.announcePlaySongFromCollection({
        collectionId: this.data.id,
        selectedTrackId: this.currentlySelectedTrackId
      })
    } else {
      this.collectionService.announcePauseSongFromCollection({
        collectionId: this.data.id,
        selectedTrackId: this.currentlySelectedTrackId!
      })
    }

  }

  isOwner(): boolean {
    return this.data.owner.id === this.user?.id;
  }

  isControlButtonShouldBeHide(trackId: number): boolean {
    if(trackId === this.currentlySelectedTrackId || trackId === this.currentlyHoveredTrackId) {
      return false;
    }
    return true;
  }

  getControlButtonType(trackId: number) {
    if(trackId === this.currentlySelectedTrackId && this.isPlaying) {
      return 'pause';
    }
    return 'play';
  }

  plej(track: Track) {
    console.log("plej")
    const idx = this.data.tracks.findIndex((t: any) => t.id == track.id)
    this.playerService.playCollection(this.data, idx);
  }

  isTrackActive(track: any) {
    return this.playerService.currentTrack?.id == track.id;
  }

  isTrackBeingPlayed(track: any) {
    return this.playerService.currentTrack?.id == track.id && !this.playerService.isPaused();
  }

  isHovered(track: any) {
    return this.currentlyHoveredTrackId === track.id;
  }
}
