import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { IdAndNameDTO } from "../models/models";
import { environment } from "src/environments/environment";
import { Observable } from "rxjs";

@Injectable({providedIn: 'root'})
export class GenresService {

  constructor(private readonly http: HttpClient) {}

  public getGenres = (): Observable<IdAndNameDTO[]> => {
    return this.http.get<IdAndNameDTO[]>(`${environment.serverURL}/genre`);
  }

  public getAllGenresByNameStartingWith = (str: string): Observable<IdAndNameDTO[]> => {
    return this.http.get<IdAndNameDTO[]>(`${environment.serverURL}/genre/startingWith`, { params: {
      name: str
    } });
  }
}
