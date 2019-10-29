# Shingle

## Introduction 
Shingle is a library design to build a documentation from annotation, it is similar to [Swagger](https://swagger.io/) (more lightweight). It can interact with some of the swagger Annotation. It is fast, clean and extensible (as usual with new libraries). 

This product was initially designed [@Libon](https://www.libon.com/).

## Shingle code organisation
The shingle repository is divided in 4 parts:
- Core: The entry point of the core of shingle is the class `fr.shingle.jaxrs.DocumentationBuilder`.
- Shingle-maven-plugin: This a plugin that can be use in java projects in order to build documentation with Maven and Shingle.
- Shingle-UI: The UI.
- Shingle annotations which contains java annotations that you can add tou your project.

## Contributors
Main contributors are:
- obourgain
- jmoussa
- jbpetit