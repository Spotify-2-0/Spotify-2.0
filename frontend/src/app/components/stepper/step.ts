import { Observable } from "rxjs";

export interface Step {
  canProceed: () => boolean | Observable<boolean>;
}
