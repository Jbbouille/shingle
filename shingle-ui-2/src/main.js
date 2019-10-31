import {defineFileUploader} from './file-uploader/index';
import {defineSelectScroll} from './select-scroll/index';
import {defineResourceDetails} from './resource-details/index';
import {defineButtonSelectResource} from './button-select-resource/index';

const {selectResource} = defineResourceDetails();
const {createButtonSelectResource} = defineButtonSelectResource(selectResource);
const {setResourcesToScroll} = defineSelectScroll(selectResource, createButtonSelectResource);

const resourcesFileSetter = resources => setResourcesToScroll(resources);
defineFileUploader(resourcesFileSetter);
