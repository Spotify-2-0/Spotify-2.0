import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs/operators';
import { Collection } from 'src/app/models/models';
import { CollectionsService } from 'src/app/services/collections.service';

@Component({
  selector: 'app-single-collection',
  templateUrl: './single-collection.component.html'
})
export class SingleCollectionComponent implements OnInit {

  public collection: Collection = null as any;
  public isLoaded: boolean = false;
  private collectionId: string = "";

  constructor(private route: ActivatedRoute, private collectionsService: CollectionsService) { }

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
      });
  }

}
