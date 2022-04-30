import { Component, OnInit } from '@angular/core';
import { Collection } from 'src/app/models/models';
import { CollectionsService } from 'src/app/services/collections.service';

@Component({
  selector: 'app-collections',
  templateUrl: './collections.component.html'
})
export class CollectionsComponent implements OnInit {

  collections: Collection[] = [];
  isLoaded: boolean = false;

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

}
