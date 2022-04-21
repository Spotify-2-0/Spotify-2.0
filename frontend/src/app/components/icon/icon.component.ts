import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { IconService } from '../../services/icon.service';

@Component({
  selector: 'app-icon',
  templateUrl: './icon.component.html',
})
export class IconComponent implements OnInit, OnChanges {
  @Input() public icon!: string;
  @Input() isBlack: boolean = false;
  public data!: SafeHtml;

  constructor(
    private readonly registry: IconService,
    private readonly sanitizer: DomSanitizer
  ) {}

  public ngOnInit(): void {
    this.requestIcon();
  }

  public ngOnChanges(changes: SimpleChanges) {
    this.requestIcon();
  }

  private requestIcon(): void {
    this.registry.requestIcon(this.icon).subscribe((iconData) => {
      if (iconData) {
        this.data = this.sanitizer.bypassSecurityTrustHtml(iconData);
      }
    });
  }
}
