import { Component, Input, OnInit } from '@angular/core';
import { Collection } from 'src/app/models/models';
import { CollectionsService } from 'src/app/services/collections.service';

@Component({
  selector: 'app-heart-btn',
  templateUrl: './heart-btn.component.html'
})
export class HeartBtnComponent implements OnInit {

  @Input('trackId') trackId!: number;
  @Input('favorites') favorites!: Collection;

  public followed = false;

  constructor(private readonly collectionService: CollectionsService) { }

  ngOnInit(): void {
    for(let track of this.favorites.tracks) {
      if(track.id === this.trackId) {
        this.followed = true;
        break;
      }
    }
  }

  followOrUnfollow() {
    if(this.followed) {
      this.collectionService.removeTrackFromFavorites(this.trackId.toString())
        .subscribe(response => {
          this.followed = false;
        })
    } else {
      this.collectionService.addTrackToFavorites(this.trackId.toString())
        .subscribe(response => {
          this.followed = true;
        })
    }
  }

}
