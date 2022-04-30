import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { ActivityResponse } from 'src/app/models/models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-acitiity-log',
  templateUrl: './activity-log.component.html',
  providers: [DatePipe]
})
export class ActivityLogComponent implements OnInit {
  nOfPages: number = 0;
  itemsPerPage: number[] = [5, 10, 15, 20];
  page: number = 0;
  size: number = this.itemsPerPage[0];
  activities: ActivityResponse[] = [];
  leftClickable!: boolean;
  rightClickable!: boolean;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.updateItemsPerPage();
  }

  updateItemsPerPage() {
    this.userService
      .userActivity(this.page, this.size)
      .subscribe((response) => {
        response.content.forEach((activity) => (activity.hiddenIp = true));
        this.activities = response.content;
        this.nOfPages = response.totalPages;
        this.calculateLeftClickable();
        this.calculateRightClickable();
      });
  }

  calculateRightClickable() {
    if (this.page < this.nOfPages - 1) {
      this.rightClickable = true;
    } else {
      this.rightClickable = false;
    }
  }

  calculateLeftClickable() {
    if (this.page > 0) {
      this.leftClickable = true;
    } else {
      this.leftClickable = false;
    }
  }

  toRight() {
    if (this.rightClickable) {
      this.page++;
      this.updateItemsPerPage();
    }
  }

  toRightMax() {
    if (this.rightClickable) {
      this.page = this.nOfPages - 1;
      this.updateItemsPerPage();
    }
  }

  toLeft() {
    if (this.leftClickable) {
      this.page--;
      this.updateItemsPerPage();
    }
  }

  toLeftMax() {
    if (this.leftClickable) {
      this.page = 0;
      this.updateItemsPerPage();
    }
  }
}
