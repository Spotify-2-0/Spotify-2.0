import { AfterViewInit, Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';
import { Collection } from 'src/app/models/models';
import { CollectionsService } from 'src/app/services/collections.service';
import { getUserProfileUrl } from 'src/app/shared/functions';
import { getAvatarUrlByMongoRef } from 'src/app/shared/functions';
import { PlayerService } from "../../../../services/player.service";

@Component({
  selector: 'app-single-collection',
  templateUrl: './single-collection.component.html'
})
export class SingleCollectionComponent implements OnInit {

  public collection: Collection = null as any;
  public isLoaded: boolean = false;
  public isFollowing: boolean = false;
  public getAvatarUrlByMongoRef = getAvatarUrlByMongoRef;
  public getUserProfileUrl = getUserProfileUrl;

  @Output() addTrackPopOut: EventEmitter<boolean> = new EventEmitter();

  private collectionId: string = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private collectionsService: CollectionsService,
    private player: PlayerService
  ) { }

  ngOnInit(): void {
    this.route.paramMap
    .pipe(
    map(paramMap => paramMap.get('id'))
    ).subscribe(collectionId => {
      this.collectionId = collectionId as string;
      this.collectionsService.getCollectionById(collectionId as string)
      .subscribe(collection => {
        this.collection = collection;
        this.isLoaded = true;
      });

      this.collectionsService.getUserCollections().subscribe(collections => {
        collections.forEach(collection => {
          if(collection.id === this.collection?.id) {
            this.isFollowing = true;
            return;
          }
        })
      });
    });
  }

  emitPopout() {
    this.addTrackPopOut.emit(true);
  }

  msToHMS(ms: number): string {
    let seconds = ms / 1000;
    // 2- Extract hours:
    let hours = Math.floor(seconds / 3600);
    seconds = seconds % 3600;
    // 3- Extract minutes:
    let minutes = Math.floor(seconds / 60);
    // 4- Keep only seconds not extracted to minutes:
    seconds = Math.floor(seconds % 60);


    if (hours === 0) {
      return minutes + " min " + seconds + " sec";
    }
    return hours + " hour " + minutes + " min " + seconds + " sec";
  }

  toggleFollow() {
    if(this.isFollowing) {
      this.collectionsService.unfollowCollection(this.collection.id.toString()).subscribe(res => {
        this.isFollowing = false;
      })
    } else {
      this.collectionsService.followCollection(this.collection.id.toString()).subscribe(res => {
        this.isFollowing = true;
      })
    }
  }

  playCollection() {
    this.player.playCollection(this.collection)
  }

  deleteCollection() {
    this.collectionsService.deleteCollection(this.collection.id.toString()).subscribe(res => {
      this.router.navigate(['/app'], {queryParams: {'id': null}, queryParamsHandling: 'merge'});
    })
  }

}
