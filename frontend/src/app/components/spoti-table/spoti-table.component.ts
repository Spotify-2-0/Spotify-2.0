import {
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { Collection } from 'src/app/models/models';
import { CollectionsService } from 'src/app/services/collections.service';
import { getAvatarUrlByMongoRef } from 'src/app/shared/functions';

@Component({
  selector: 'app-spoti-table',
  templateUrl: './spoti-table.component.html',
})
export class SpotiTableComponent implements OnInit, OnDestroy {
  @Input() tableType!: string;
  @Input() data: any;

  public columns: string[] = [];
  public getAvatarUrlByMongoRef = getAvatarUrlByMongoRef;
  public updateTableSub!: Subscription;

  constructor(
    private readonly collectionService: CollectionsService
  ) {}

  ngOnInit(): void {
    if (this.tableType === 'collections') {
      this.columns = ['#', 'title', 'type', 'plays', 'duration', 'published'];
      this.data = this.data as Collection[];
      console.log('collections: ', this.data);
    } else {
      this.columns = ['#', 'title', 'plays', 'Duration', 'published', '', ''];
    }

    this.updateTableSub = this.collectionService.updateTable.subscribe((_) => {


      this.collectionService.getCollections().subscribe((data) => {
        this.data = data;
        console.log(data);
      });
    });
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

  ngOnDestroy(): void {
    this.updateTableSub.unsubscribe();
  }
}
