import { Component, Input, OnInit } from '@angular/core';
import { Collection } from 'src/app/models/models';

@Component({
  selector: 'app-spoti-table',
  templateUrl: './spoti-table.component.html'
})
export class SpotiTableComponent implements OnInit {

  @Input() tableType!: string;
  @Input() data: any;

  public columns: string[] = [];

  constructor() { 
  }
  
  ngOnInit(): void {
    if(this.tableType === "collections") {
      this.columns = ['#', 'title', 'type', 'plays', 'duration', 'published'];
      this.data = this.data as Collection[];
      console.log("collections: ", this.data);
    } else {
      this.columns = ['#', 'title', 'plays', 'Duration', 'published', '', ''];
    }
  }

}
