import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { fromEvent, Subscription } from 'rxjs';
import { b64toBlob } from '../../shared/functions';

export interface CropResult {
  blob: Blob;
  base64: string;
}

@Component({
  selector: 'app-avatar-cropper',
  templateUrl: './avatar-cropper.component.html',
})
export class AvatarCropperComponent implements OnChanges {
  isDragMouseDown: boolean = false;
  isResizeMouseDown: boolean = false;
  wrapper!: HTMLElement;
  parent!: HTMLElement;
  child!: HTMLElement;
  canvas!: HTMLCanvasElement;
  context!: CanvasRenderingContext2D;
  result: any = {};

  drag = {
    startX: 0,
    startY: 0,
  };
  scale = 1;

  bigger: string = '';
  resizer: any = {};
  subscriptions: Subscription[] = [];

  @Input() width!: number;
  @Input() height!: number;
  @Input() quality: number = 77;
  @Input() dataURI!: any;
  private format = 'jpeg';

  @Output() finished: EventEmitter<CropResult | null> = new EventEmitter();

  @ViewChild('parentSource', { static: true }) parentSource!: ElementRef;
  @ViewChild('imageSource', { static: true }) imageSource!: ElementRef;
  @ViewChild('childSource', { static: true }) childSource!: ElementRef;
  @ViewChild('wrapper', { static: true }) wrapperSource!: ElementRef;

  async ngOnChanges(changes: SimpleChanges) {
    if (changes.dataURI) {
      setTimeout(() => this.init(), 50);
    }
  }

  public changeScale($event: Event): void {
    this.scale = Number(($event.target as HTMLInputElement).value);
  }

  private getFormat() {
    return 'image/' + this.format;
  }

  init() {
    this.parent = this.parentSource.nativeElement;
    this.child = this.childSource.nativeElement;
    this.wrapper = this.wrapperSource.nativeElement;
    this.bigger = this.getBigger();

    this.subscriptions.push(
      fromEvent<MouseEvent>(window, 'mousedown').subscribe((event) =>
        this.onMouseDown(event)
      ),
      fromEvent<MouseEvent>(window, 'mousemove').subscribe((event) =>
        this.onMouseMove(event)
      ),
      fromEvent<MouseEvent>(window, 'mouseup').subscribe((event) =>
        this.onMouseUp(event)
      )
    );

    this.canvas = document.createElement('canvas');
    this.context = this.canvas.getContext('2d')!;
    const bb = this.parentSource?.nativeElement?.getBoundingClientRect();
    const a = bb.width > bb.height ? bb.height : bb.width;
    this.canvas.width = a;
    this.canvas.height = a;
  }

  /* Crop */

  crop() {
    let ch = this.child.getBoundingClientRect();
    let pa = this.parent.getBoundingClientRect();

    let image = new Image();
    image.src = this.dataURI;

    let ratX = image.width / pa.width;
    let ratY = image.height / pa.height;

    let startX = (ch.x - pa.x) * ratX;
    let startY = (ch.y - pa.y) * ratY;

    let scaleW = ch.width * ratX;
    let scaleH = ch.height * ratY;

    this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
    this.context.drawImage(
      image,
      startX,
      startY,
      scaleW,
      scaleH,
      0,
      0,
      this.canvas.width,
      this.canvas.height
    );

    let format = this.getFormat();

    let result = this.canvas.toDataURL(format, 100);

    return { blob: b64toBlob(result, format), base64: result };
  }

  private getBigger() {
    const bb = this.parentSource?.nativeElement?.getBoundingClientRect();
    if (!bb) return '';
    const a = bb.width > bb.height ? bb.height : bb.width;
    return a + 'px';
  }

  getCursorPos(e: any) {
    e = e.touches && e.touches[0] ? e.touches[0] : e;
    return { x: e.pageX, y: e.pageY };
  }

  private onMouseDown(event: MouseEvent) {
    const clickedOnImage = !!event
      .composedPath()
      .find((x) => this.wrapper === (x as HTMLElement));

    if (clickedOnImage) {
      event.preventDefault();
      this.isDragMouseDown = true;
      const pos = this.getCursorPos(event);
      const ch = this.parent.getBoundingClientRect();
      this.drag = {
        startX: pos.x - ch.x,
        startY: pos.y - ch.y,
      };
    }
  }

  private onMouseUp(event: MouseEvent) {
    if (!this.isDragMouseDown && !this.isResizeMouseDown) return;
    this.isDragMouseDown = false;
    this.isResizeMouseDown = false;
  }

  private onMouseMove(event: MouseEvent) {
    if (!this.isDragMouseDown) return;

    const pos = this.getCursorPos(event);
    const imgBB = this.parent.getBoundingClientRect();
    const snapBB = this.child.getBoundingClientRect();
    const relative = this.wrapper.getBoundingClientRect();

    let x = pos.x - this.drag.startX;
    let y = pos.y - this.drag.startY;
    x =
      x <= snapBB.x && x + imgBB.width >= snapBB.x + snapBB.width ? x : imgBB.x;
    y =
      y <= snapBB.y && y + imgBB.height >= snapBB.y + snapBB.height
        ? y
        : imgBB.y;

    this.parent.style.left = x - relative.x + 'px';
    this.parent.style.top = y - relative.y + 'px';
  }

  onApply() {
    this.finished.emit(this.crop());
  }

  onCancel() {
    this.finished.emit(null);
  }
}
