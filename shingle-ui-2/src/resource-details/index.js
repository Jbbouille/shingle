import template from './index.html';

let selectedResource;
const selectResource = resource => {
  console.log(resource);
  selectedResource = resource
};

class ResourceDetails extends HTMLElement {
  constructor() {
    super();
    const element = this.attachShadow({mode: 'open'});
    element.innerHTML = template;
  }

}

export const defineResourceDetails = () => {
  customElements.define('resource-detail', ResourceDetails);
  return {
    selectResource
  };
};