import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AvatarService } from 'src/app/services/avatar.service';
import { CollectionsService } from 'src/app/services/collections.service';
import { requiredValidator } from 'src/app/shared/validators';

@Component({
  selector: 'app-collection-create',
  templateUrl: './collection-create.component.html',
})
export class CollectionCreateComponent implements OnInit {
  collectionCreateForm: FormGroup = this.fb.group({
    collectionTitle: ['', requiredValidator()],
    collectionType: ['', requiredValidator()],
  });
  collectionTypes: string[] = [];
  imgBlob: Blob | null = null;
  @Output() closeCollectionCreateForm: EventEmitter<boolean> =
    new EventEmitter();

  constructor(
    private readonly fb: FormBuilder,
    private readonly collectionService: CollectionsService,
    private readonly avatarService: AvatarService
  ) {}

  ngOnInit(): void {
    this.collectionService.getCollectionTypes().subscribe((collections) => {
      this.collectionTypes = collections;

      this.collectionCreateForm = this.fb.group({
        collectionTitle: ['', requiredValidator()],
        collectionType: [
          this.collectionTypes[0] == null ? 'null' : this.collectionTypes[0],
          requiredValidator(),
        ],
      });
    });
  }

  onSubmit() {
    this.collectionCreateForm.markAllAsTouched();

    if (this.collectionCreateForm.valid) {
      this.collectionService.addCollection({
        name: this.collectionCreateForm.controls['collectionTitle'].value,
        image: this.imgBlob,
        type: this.collectionCreateForm.controls['collectionType'].value,
      });
      this.closeCollectionCreateForm.emit(false);
    }
  }

  onDelete() {
    this.imgBlob = null;
    this.avatarService.emitDeleteImage(true);
  }
}
