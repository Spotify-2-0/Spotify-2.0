import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class TrackService {

  constructor(private readonly http: HttpClient) {}

  public addTrack(collectionId: number, formData: FormData): Observable<void> {
    return this.http.post<void>(`${environment.serverURL}/collection/${collectionId}/track`, formData);
  }
}
