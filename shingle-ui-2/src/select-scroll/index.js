import template from './index.html';

let createButtonSelectResourceCallBack;

let resources;
const setResourcesToScroll = received => {
  // valid resources

  // set resources
  resources = received.resources;

  // set SelectScroll to visible
  const scroller = document.getElementById("scroller");
  scroller.removeAttribute("hidden");
};

let selectResourceCallBack;

class SelectScroll extends HTMLElement {
  constructor() {
    super();
    const element = this.attachShadow({mode: 'open'});
    element.innerHTML = template;
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (name !== 'hidden' || !resources) {
      return;
    }

    resources.forEach(resource => {
      let element = createButtonSelectResourceCallBack(resource);
      this.shadowRoot.appendChild(element);
    });
  }

  static get observedAttributes() {
    return ['hidden'];
  }
}

export const defineSelectScroll = (selectResource, createButtonSelectResource) => {
  selectResourceCallBack = selectResource;
  createButtonSelectResourceCallBack = createButtonSelectResource;
  customElements.define('select-scroll', SelectScroll);
  return {
    setResourcesToScroll
  };
};