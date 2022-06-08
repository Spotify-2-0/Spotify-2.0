import { Component, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { Subscription } from 'rxjs';
import { AvatarService } from 'src/app/services/avatar.service';
import { ThemeService } from 'src/app/services/theme.service';
import { UserService } from 'src/app/services/user.service';
import { CollectionsComponent } from './collections/collections.component';
import { SingleCollectionComponent } from './collections/single-collection/single-collection.component';

@Component({
  selector: 'app-logged-page',
  templateUrl: './logged-page.component.html',
})
export class LoggedPageComponent implements OnInit, OnDestroy {
  avatarBlob!: string;
  userId!: number;
  firstName!: string;
  lastName!: string;
  addTrackPopOut = false;
  createCollectionPopOut = false;

  collectionPoput!: Subscription;
  clickListenFunction!: Function;

  constructor(
    private readonly themeService: ThemeService,
    private readonly avatarService: AvatarService,
    private readonly userService: UserService,
    private readonly renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.userService.me().subscribe((user) => {
      this.userId = user.id;
      this.avatarBlob = this.userService.getUserProfileUrl(this.userId);
      this.firstName = user.firstName;
      this.lastName = user.lastName;
    });
    this.avatarService.change.subscribe(() => {
      this.avatarBlob = this.userService.getUserProfileUrl(this.userId);
    });

    this.clickListenFunction = this.renderer.listen(
      'document',
      'click',
      (e: Event) => {
        if (this.createCollectionPopOut) {
          if (
            (e.composedPath()[0] as HTMLElement).className ==
            'collection-create'
          ) {
            this.createCollectionPopOut = false;
          }
        }

        if (this.addTrackPopOut) {
          if ((e.composedPath()[0] as HTMLElement).className == 'add-track') {
            this.addTrackPopOut = false;
          }
        }
      }
    );
  }

  public subscribeToCollectionPopouts(componentRef: any) {
    if(componentRef instanceof CollectionsComponent){
      const collectionChildElem: CollectionsComponent = componentRef;

        collectionChildElem.collectionPopout.subscribe((value) => {
          this.createCollectionPopOut = value;
        });
    } else if (componentRef instanceof SingleCollectionComponent){
        const trackAddChildElem: SingleCollectionComponent = componentRef;
        trackAddChildElem.addTrackPopOut.subscribe((value) => {
          this.addTrackPopOut = value;
        })
      }
  }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  ngOnDestroy(): void {
    if (this.collectionPoput) {
      this.collectionPoput.unsubscribe();
    }

    this.clickListenFunction();
  }
}
