import { Component, OnInit } from '@angular/core';
import { Genres } from "./genres";
import { Step } from "../../stepper/step";

@Component({
  selector: 'app-step-user-interests',
  templateUrl: './step-user-interests.component.html',
  styleUrls: ['./step-user-interests.component.scss']
})
export class StepUserInterestsComponent implements Step {
  public genres = Genres;
  private chosenGenres: string[] = [];

  constructor() { }

  public onGenreClick(genre: string[]): void {
    const index = this.chosenGenres.findIndex(x => x === genre[0]);
    if (index !== -1) {
      this.chosenGenres.splice(index, 1);
    } else {
      this.chosenGenres.push(genre[0])
    }
  }

  public isChosen(genre: string[]): boolean {
    const index = this.chosenGenres.findIndex(x => x === genre[0]);
    return index !== -1;
  }

  public canProceed(): boolean {
    return true;
  }
}
