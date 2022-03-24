export const image2base64 = (file: any, callback: any) => {
  if (!/image\/(png|jpg|jpeg|bmp|gif|tiff|webp)/.test(file.type))
    throw 'Not Valid Image';

  const reader = new FileReader();
  reader.onload = (file) => {
    const image = new Image();
    image.onload = (cos: Event) => {
      const loadedImg = cos.target as HTMLImageElement;
      callback(reader.result, loadedImg.width, loadedImg.height);
    };

    image.src = file.target?.result as string;
  };
  reader.readAsDataURL(file);
};

export const b64toBlob = (dataURI: any, type: any) => {
  let byteString = atob(dataURI.split(',').pop());
  let ab = new ArrayBuffer(byteString.length);
  let ia = new Uint8Array(ab);
  for (let i = 0; i < byteString.length; i++) {
    ia[i] = byteString.charCodeAt(i);
  }
  return new Blob([ab], { type });
};
