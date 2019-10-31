import template from './index.html';

let resourcesFileSetter;

class FileUploader extends HTMLInputElement {
  constructor() {
    super();

    const element = this.attachShadow({mode: 'open'});

    element.innerHTML = template;
    element.firstElementChild.onchange = this.selectFile;
  }

  static onLoadEnd(event) {
    if (event.target.readyState !== FileReader.DONE) {
      throw "ERROR load should have end.";
    }
    resourcesFileSetter(JSON.parse(event.target.result));
  }

  selectFile(_) {
    const file = this.files[0];
    const fileReader = new FileReader();

    fileReader.onloadend = FileUploader.onLoadEnd;
    fileReader.readAsBinaryString(file);
  }
}

export const defineFileUploader = setter => {
  resourcesFileSetter = setter;
  customElements.define('file-uploader', FileUploader, {extends: "input"});
};