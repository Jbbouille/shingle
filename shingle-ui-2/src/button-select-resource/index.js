import template from './index.html';

let onClickResource;

class ButtonSelectResource extends HTMLElement {
  constructor(resource) {
    super();
    const element = this.attachShadow({mode: 'open'});
    element.innerHTML = template.replace('{{verb}}', resource.verb).replace('{{path}}', resource.path);
    this.addEventListener('click', () => onClickResource(resource));
    this.setAttribute('path', resource.path);
    this.setAttribute('verb', resource.verb);
  }
}

const createButtonSelectResource = (resource) => {
  return new ButtonSelectResource(resource);
};

export const defineButtonSelectResource = onClick => {
  customElements.define('button-select-resource', ButtonSelectResource);
  onClickResource = onClick;
  return {
    createButtonSelectResource
  };
};