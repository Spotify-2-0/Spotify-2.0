import { Component, ElementRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ModalService } from "../../services/modal.service";

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
})
export class ModalComponent implements OnInit, OnDestroy {
  @Input() id!: string;
  private readonly element: any;

  constructor(
    private readonly modalService: ModalService,
    private readonly el: ElementRef
  ) {
    this.element = el.nativeElement;
  }

  public ngOnInit(): void {
    if (this.id) {
      document.body.appendChild(this.element);

      this.modalService.add(this);
      this.close()
    }
  }

  public ngOnDestroy(): void {
    this.modalService.remove(this.id);
    this.element.remove();
  }

  public open(): void {
    this.element.style.display = 'block';
    document.body.classList.add('modal--open');
  }

  public close(): void {
    this.element.style.display = 'none';
    document.body.classList.remove('modal--open');
  }

}
