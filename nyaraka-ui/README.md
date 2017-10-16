# Nyaraka UI

## Introduction
This is the UI part of the project.

## How to use it
Build it or download it. Put it in an http server like Nginx with the nyaraka.json file. Go to your url. Example go to `http://localhost:4567/?doc=docs/nyaraka.json&cust=cust/custoNyaraka.json`.
Find a example of [working nyaraka](http://nyaraka.github.io).

## Functionalities
Here are some of Nyaraka functionalities:
- Code organization
- Search bar
- Customization
- API link

## Customization
You are able to customize the nyaraka.json file with a custo.json file. Example:
```json
{
  "resourceExtensions": [
    {
      "extensionName": "Legacy"
      "name": "Legacy API",
    },
    {
      "extensionName": "Deprecated"
      "name": "Legacy API",
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

And then, in the nyaraka-ui directory:

```
npm install
```

### Running

In the nyaraka-ui directory, run:

```
gobble
```

Which will start a server on http://localhost:4567.

.