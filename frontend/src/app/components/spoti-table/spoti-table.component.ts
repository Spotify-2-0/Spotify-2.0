import { Component, Input, OnInit } from '@angular/core';
import { Collection } from 'src/app/models/models';
import { getAvatarUrlByMongoRef } from 'src/app/shared/functions';

@Component({
  selector: 'app-spoti-table',
  templateUrl: './spoti-table.component.html'
})
export class SpotiTableComponent implements OnInit {

  @Input() tableType!: string;
  @Input() data: any;

  public columns: string[] = [];
  public getAvatarUrlByMongoRef = getAvatarUrlByMongoRef;

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

  msToHMS(ms: number): string {
    // 1- Convert to seconds:
    let seconds = ms / 1000;
    // 2- Extract hours:
    const hours = Math.floor( seconds / 3600 ); // 3,600 seconds in 1 hour
    seconds = seconds % 3600; // seconds remaining after extracting hours
    // 3- Extract minutes:
    const minutes = Math.floor( seconds / 60 ); // 60 seconds in 1 minute
    // 4- Keep only seconds not extracted to minutes:
    seconds = Math.floor(seconds % 60);

    if(hours === 0) {
      return minutes+":"+seconds;  
    }
    return hours+":"+minutes+":"+seconds;
  }

  convertDate(dateString: string): string {
    return new Date(dateString).toLocaleString(undefined, {year: 'numeric', month: 'short', day: '2-digit'})
  }

}
