# Brief Description

Without much of front-end experience, the author try to build up simplest
website.

# Architecture

There are 5 main blocks in system: HTML Pages, HTML common, CSS, JS,
Visualization Framework. Together, these blocks are connected into a directed
acyclic graph which help to avoid the dependency dead loop.

Fun fact, bootstrap framework is main decorator of site and is currently part of
Visualization Framework as well as DataTable. The list of decoration framework
could be expanded in future.

The front-end layers follows the DAG as below :

```
                                      ┌────────────┐
                                      │ HTML pages │
                                      │            │
                                      │  Page 1    │
                                      │  Page 2    │
                                      │  Page 3    │
                                      └─────┬──────┘
                                            │
                                            │
          ┌─────────────────────────┬───────┴─┬────────────────┐
          │                         │         │                │
          │                         │         │                │
┌─────────▼─────────┐        ┌──────▼─────┐   │   ┌────────────┼───────────────────────────┐
│ CSS (Feature base)│        │ HTML common│   │   │            │                           │
│                   │        │            │   │   │     ┌──────▼────────┐                  │
│  main.css         │        │  HTML 1    │   │   │     │JS (Page base) │                  │
│  feature_1.css    │        │  HTML 2    │   │   │     │               │                  │
│  feature_2.css    │        │  HTML 3    │   │   │     │  page_1.js    │                  │
│  feature_3.css    │        └──────┬─────┘   │   │     │  page_2.js    │                  │
└─────────┬─────────┘               │         │   │     │  page_3.js    │                  │
          │                         │         │   │     └───────┬───────┘                  │
          │                         │         │   │             │                          │
          │                         │         │   │             │                          │
          └─────────────┬───────────┴─────────┘   │             ├───────────────┐          │
                        │                         │             │               │          │
                        │                         │             │               │          │
                        │                         │             │           ┌───▼─────┐    │
                        │                         │             │       ┌───┤common JS│    │
                        │                         │    ┌────────▼───┐   │   └────┬────┘    │
        ┌───────────────▼─────────┐               │    │ Data Model ◄───┤        │         │
        │ Visualization Framework │               │    └────────────┘   │        │         │
        │                         │               │                     │   ┌────▼───┐     │
        │   Bootstrap             │               │                     └───┤ulake JS│     │
        │   DataTable             ◄───────────────┤                         └────┬───┘     │
        │   Etc.                  │               │                              │         │
        └─────────────────────────┘               │                              │         │
                                                  │                              │         │
                                                  │                         ┌────▼──┐      │
                                                  │                         │  API  │      │
                                                  │                         └───────┘      │
                                                  │                                        │
                                                  │                                        │
                                                  └────────────────────────────────────────┘
```

1. HTML pages: list of main page of site as Home, Query, etc.
2. HTML common: common html page which could be included in most of HTML pages.
3. CSS: since CSS is hardly change, the file is split following feature base.
4. JS: provide main action for page, the file is based on page view.
5. Visualization Framework: hold decorators framework of site.

The detail of JS block:

1. page js: main list of file providing function for each page.
2. common js: hold common used js function.
3. ulake js: hold function to communicate with lake api.
4. data model: models for complex data in system.

# Quote

The author does not expect to bring any complex operation to site side.
Respect it ! (but could be continue to change when needing)
