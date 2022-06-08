import { Component } from '@angular/core';
import packageJson from '../../../../package.json';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
})
export class FooterComponent {
  public version: string = packageJson.version;

  constructor() { }

}
