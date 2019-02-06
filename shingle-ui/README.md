# Shingle UI

## Introduction
This is the UI part of the project.

## How to use it
Build it or download it. Put it in an http server like Nginx with the shingle.json file. Go to your url. Example go to `http://localhost:4567/?doc=docs/shingle.json&cust=cust/custoShingle.json`.
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

You'll need to globally install `gobble-cli`:

```
npm install -g gobble-cli
```

And then, in the shingle-ui directory:

```
npm install
```

### Running

In the shingle-ui directory, run:

```
gobble
```

Which will start a server on http://localhost:4567.

.