import {
  AfterViewInit,
  Component,
  ComponentFactoryResolver,
  ComponentRef,
  Input,
  Type,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { Step } from './step';
import { isObservable } from "rxjs";

@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
})
export class StepperComponent implements AfterViewInit {
  @ViewChild('container', { read: ViewContainerRef })
  container!: ViewContainerRef;

  @Input() public finalView!: Type<any>;
  @Input() public steps: {
    name: string;
    component: Type<Step>;
  }[] = [];
  public stepIndex = 0;
  private currentRef!: ComponentRef<Step>;

  constructor(private readonly resolver: ComponentFactoryResolver) {}

  public ngAfterViewInit(): void {
    this.currentRef = this.getStepComponent(0);
  }

  public getStepComponent(index: number): ComponentRef<Step> {
    const factory = this.resolver.resolveComponentFactory<Step>(
      this.steps[index].component
    );
    return this.container.createComponent(factory);
  }

  public getStatus(i: number): string {
    if (i == this.stepIndex) return 'Current';
    if (i < this.stepIndex) return 'Done';
    return 'Pending';
  }

  public nextStep(): void {
    const can = this.currentRef.instance.canProceed();
    if (isObservable(can)) {
      can.subscribe(x => {
        if (x) this.step()
      })
    }
    else if (can) {
      this.step();
    }
  }

  private step() {
    this.stepIndex++;
    this.currentRef.destroy();
    if (this.stepIndex < this.steps.length)
      this.currentRef = this.getStepComponent(this.stepIndex);
    else {
      const factory = this.resolver.resolveComponentFactory(this.finalView);
      this.currentRef = this.container.createComponent(factory);
    }
  }

  public currentStepName(): string {
    return this.steps[this.stepIndex].name;
  }
}
