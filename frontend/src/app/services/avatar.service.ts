import { Injectable } from "@angular/core";
import { Subject } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AvatarService {
  delete: Subject<boolean> = new Subject();
  change: Subject<void> = new Subject();

  emitDeleteImage(value: boolean){
    this.delete.next(value);
  }

  emitChangeImage(){
    this.change.next();
  }
}
