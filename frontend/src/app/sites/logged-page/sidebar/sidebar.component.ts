import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public collections: string[] = [];

  constructor() { }

  ngOnInit(): void {
    this.collections = [
      "test collection 1",
      "test collection 2",
      "test collection 3",
    ];
  }

}
