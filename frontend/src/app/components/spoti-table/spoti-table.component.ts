import { Component, ElementRef, Input, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
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
export class SpotiTableComponent implements OnInit, OnDestroy {

  @Input() tableType!: string;
  @Input() data: any;

  @ViewChildren('controlButton') controlButtons!: QueryList<ElementRef>;
  @ViewChildren('controlButtonIcon') controlButtonsIcons!: QueryList<IconComponent>;

  public columns: string[] = [];
  public getAvatarUrlByMongoRef = getAvatarUrlByMongoRef;

  public currentlySelectedTrackId?: number;
  public currentlyHoveredTrackId?: number;
  public userFavorites?: Collection;
  public isFavoritesLoaded = false;

  private destroy = new Subject<void>();
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
    }
  }

  isOwner(): boolean {
    return this.data.owner.id === this.user?.id;
  }

  play(track: Track) {
    const idx = this.data.tracks.findIndex((t: any) => t.id == track.id)
    this.playerService.playCollection(this.data, idx);
  }

  isTrackActive(track: any) {
    return this.playerService.currentTrack?.id == track.id;
  }

  isHovered(track: any) {
    return this.currentlyHoveredTrackId === track.id;
  }
}
