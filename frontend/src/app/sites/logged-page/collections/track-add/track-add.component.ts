import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ValidationErrors,
} from '@angular/forms';
import { requiredValidator } from 'src/app/shared/validators';
import { CollectionsService } from 'src/app/services/collections.service';
import { IdAndNameDTO } from 'src/app/models/models';
import { GenresService } from 'src/app/services/genres.service';
import { UserService } from 'src/app/services/user.service';
import { TrackService } from 'src/app/services/track.service';

@Component({
  selector: 'app-track-add',
  templateUrl: './track.add.component.html',
})
export class TrackAddComponent implements OnInit, AfterViewInit {
  public trackForm: FormGroup = this.fb.group({
    title: ['', requiredValidator()],
    album: ['', requiredValidator()],
    genres: '',
    artists: '',
    file: ['', requiredValidator()],
  });
  public albums: IdAndNameDTO[] = [];
  public tagsToSave: IdAndNameDTO[] = [];
  public otherArtistsToSave: IdAndNameDTO[] = [];
  public isTypingTags: boolean = false;
  public isTypingOtherArtists: boolean = false;
  public executingTagsInterval!: NodeJS.Timeout;
  public executingOtherArtistsInterval!: NodeJS.Timeout;
  public searchedTags: IdAndNameDTO[] = [];
  public searchedTagsLoaded: boolean = false;
  public searchedOtherArtists: IdAndNameDTO[] = [];
  public searchedOtherArtistsLoaded: boolean = false;

  public TYPING_DELAY: number = 500;
  public MAX_NUMBER_OF_TAGS: number = 10;
  public MAX_NUMBER_OF_OTHER_ARTISTS: number = 10;

  @ViewChild('tags') tagsInputRef!: ElementRef;
  @ViewChild('otherArtists') otherArtistsInputRef!: ElementRef;
  @ViewChild('selectorLabel') selectorLabelRef!: ElementRef;
  @ViewChild('fileInput') fileInputRef!: ElementRef;

  @Output() openTrackAddForm: EventEmitter<boolean> = new EventEmitter();

  constructor(
    private readonly fb: FormBuilder,
    private readonly collectionsService: CollectionsService,
    private readonly genresService: GenresService,
    private readonly userService: UserService,
    private readonly trackService: TrackService
  ) {}

  ngOnInit(): void {
    this.collectionsService
      .getLoggedUserColections()
      .subscribe((albums: IdAndNameDTO[]) => {
        console.log(albums);
        this.albums = albums;
        this.trackForm.controls['album'].setValue(
          this.albums[0] == null ? 'null' : this.albums[0].name
        );
      });
  }

  ngAfterViewInit(): void {
    this.setTypingTagListenerEvent();
    this.setTypingOtherArtistsListenerEvent();
    this.setListenerForInputName();
  }

  public onSubmit() {
    this.trackForm.markAllAsTouched();

    if (this.trackForm.valid) {
      const formToSend = new FormData();
      const file: File = (<HTMLInputElement>(
        this.fileInputRef.nativeElement
      )).files!.item(0)!;
      const albumId = this.albums.find(
        (album) => album.name == this.trackForm.controls['album'].value
      )?.id;

      if (albumId) {
        formToSend.set('track', file);
        formToSend.set('name', this.trackForm.controls['title'].value);
        for(let tag of this.tagsToSave) {
          formToSend.append(
            'genres',
            tag.id.toString()
          );
        }
        for(let otherArtist of this.otherArtistsToSave){
          formToSend.append(
            'artists',
            otherArtist.id.toString()
          );
        }
        this.trackService.addTrack(albumId, formToSend).subscribe();
        this.openTrackAddForm.emit(false);
      }
    }
  }

  public getAlbumsNames(): string[] {
    return this.albums.map((album) => album.name);
  }

  public addTagToSave(tag: IdAndNameDTO) {
    this.tagsToSave.push(tag);
    this.isTypingTags = false;
    (<HTMLInputElement>this.tagsInputRef.nativeElement).value = '';
    this.searchedTagsLoaded = false;
  }

  public deleteTag(tag: IdAndNameDTO) {
    this.tagsToSave.splice(this.tagsToSave.indexOf(tag), 1);
    this.isTypingTags = false;
  }

  public deleteOtherArtist(artist: IdAndNameDTO) {
    this.otherArtistsToSave.splice(this.otherArtistsToSave.indexOf(artist), 1);
    this.isTypingOtherArtists = false;
  }

  public addOtherArtistToSave(otherArtist: IdAndNameDTO) {
    this.otherArtistsToSave.push(otherArtist);
    this.isTypingOtherArtists = false;
    (<HTMLInputElement>this.otherArtistsInputRef.nativeElement).value = '';
    this.searchedOtherArtistsLoaded = false;
  }

  public isInputFileError() {
    const control = this.trackForm.controls['file'];
    return control.touched && control.invalid;
  }

  public getInputFileError() {
    const control = this.trackForm.controls['file'];
    const errors: ValidationErrors = control.errors!;
    return errors[Object.keys(errors)[0]];
  }

  private setTypingTagListenerEvent() {
    const element = <HTMLInputElement>this.tagsInputRef.nativeElement;

    element.addEventListener('input', (event) => {
      if (element.value.length > 0) {
        this.isTypingTags = true;

        if (this.executingTagsInterval) {
          clearTimeout(this.executingTagsInterval);
        }

        this.executingTagsInterval = setTimeout(() => {
          this.setSearchedTagsByNameStartingWith(element.value);
        }, this.TYPING_DELAY);
      } else {
        this.isTypingTags = false;
      }
    });
  }

  private setTypingOtherArtistsListenerEvent() {
    const element = <HTMLInputElement>this.otherArtistsInputRef.nativeElement;

    element.addEventListener('input', (event) => {
      if (element.value.length > 0) {
        this.isTypingOtherArtists = true;

        if (this.executingOtherArtistsInterval) {
          clearTimeout(this.executingOtherArtistsInterval);
        }

        this.executingOtherArtistsInterval = setTimeout(() => {
          this.setSearchedOtherArtistsByNameStartingWith(element.value);
        }, this.TYPING_DELAY);
      } else {
        this.isTypingOtherArtists = false;
      }
    });
  }

  private setSearchedTagsByNameStartingWith(str: string) {
    if (this.tagsToSave.length < this.MAX_NUMBER_OF_TAGS) {
      this.searchedTagsLoaded = false;
      this.genresService
        .getAllGenresByNameStartingWith(str)
        .subscribe(
          (tags) =>
            (this.searchedTags = tags.filter(
              (tag) => !this.tagsToSave.find((e) => e.id === tag.id)
            ))
        );
      this.searchedTagsLoaded = true;
    }
  }

  private setSearchedOtherArtistsByNameStartingWith(str: string): void {
    if (this.otherArtistsToSave.length < this.MAX_NUMBER_OF_OTHER_ARTISTS) {
      this.searchedOtherArtistsLoaded = false;
      this.userService
        .getUsersByNameStartingWith(str)
        .subscribe(
          (users) =>
            (this.searchedOtherArtists = users.filter(
              (user) => !this.otherArtistsToSave.find((e) => e.id === user.id)
            ))
        );
      this.searchedOtherArtistsLoaded = true;
    }
  }

  private setListenerForInputName() {
    const fileInput = <HTMLInputElement>this.fileInputRef.nativeElement;
    const selectorLabel = <HTMLSpanElement>this.selectorLabelRef.nativeElement;

    fileInput.addEventListener('change', () => {
      selectorLabel.textContent = fileInput.files!.item(0)!.name;
    });
  }
}
