# Shingle UI

## Introduction
This is the UI part of the project.

## How to use it
Build it or download it. Put it in an http server like Nginx with the shingle.json file. Go to your url. Example go to `http://localhost:1234/?doc=docs/shingle.json&cust=cust/custoShingle.json`.
Find a example of [working shingle](http://shingle.github.io).

## Functionalities
Here are some of Shingle functionalities:
- Code organization
- Search bar
- Customization
- API link

## Customization
You are able to customize the shingle.json file with a custo.json file. Example:
```json
{
  "resourceExtensions": [
    {
      "extensionName": "Legacy",
      "name": "Legacy API"
    },
    {
      "extensionName": "Deprecated",
      "name": "Legacy API"
    }
  ]
}
```

## Test locally
### Installation
You must use yarn, go in the `shingle-ui` directory:
```
yarn install
```
### Running

In the shingle-ui directory, run:

```
yarn dev
```

Which will start a server on http://localhost:1234.

in order to load a API resource, add your file in the dist directory then go to: `http://localhost:1234/?doc=/shingle.json`