import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Collection } from 'src/app/models/models';
import { CollectionsService } from 'src/app/services/collections.service';

@Component({
  selector: 'app-collections',
  templateUrl: './collections.component.html'
})
export class CollectionsComponent implements OnInit {

  collections: Collection[] = [];
  isLoaded: boolean = false;

  @Output() collectionPopout: EventEmitter<boolean> = new EventEmitter();

  constructor(
    private readonly collectionsService: CollectionsService
  ) {

  }

  ngOnInit(): void {

    this.collectionsService.getCollections().subscribe(response => {
      this.isLoaded = true;
      this.collections = response;
    })
  }

  emitPopout() {
    this.collectionPopout.emit(true);
  }


}
