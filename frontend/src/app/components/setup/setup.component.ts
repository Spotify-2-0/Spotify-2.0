import {AfterViewInit, ChangeDetectorRef, Component, TemplateRef, ViewChild} from '@angular/core';
import { ThemeService } from "../../services/theme.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import { displayNameValidator } from "../../sites/signup-page/validators";

@Component({
  selector: 'app-setup',
  templateUrl: './setup.component.html',
  styleUrls: ['./setup.component.scss']
})
export class SetupComponent implements AfterViewInit{
  @ViewChild('step_1st') private step_1st!: TemplateRef<HTMLElement>;
  @ViewChild('step_2nd') private step_2nd!: TemplateRef<HTMLElement>;
  @ViewChild('step_3rd') private step_3rd!: TemplateRef<HTMLElement>;

  form: FormGroup = this.fb.group({
    displayName: ['', [displayNameValidator()]],
  })

  public stepIndex = 0;
  public steps: {
    getTemplate: () => TemplateRef<HTMLElement>,
    title: string,
    isSkippable: boolean,
    canProceed: () => boolean,
  }[] = [
    {
      getTemplate: () => this.step_1st,
      title: 'Confirm your email',
      isSkippable: false,
      canProceed: () => true,
    },
    {
      getTemplate: () => this.step_2nd,
      title: 'How others see you',
      isSkippable: false,
      canProceed: () => true,
    },
    {
      getTemplate: () => this.step_3rd,
      title: 'Let us know you',
      isSkippable: true,
      canProceed: () => true,
    },
  ]

  constructor(
    private readonly fb: FormBuilder,
    private readonly themeService: ThemeService,
    private readonly cdr: ChangeDetectorRef
  ) { }

  ngAfterViewInit() {
    this.cdr.detectChanges();
  }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  public currentStep() {
    return this.steps[this.stepIndex]
  }

  public getStep(index: number) {
    return this.steps[index]
  }

  public nextStep() {
    this.stepIndex++;
  }

  public getStatus(i: number): string {
    if (i == this.stepIndex) return 'Current';
    if (i < this.stepIndex) return 'Done';
    return 'Pending';
  }

  public getUserInitials(): string {
    return 'LF';
  }

}
