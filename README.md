## akka-http server with bundled json events generator

### Building
clone, `cd` and:
```sh
$ sbt stage
```
Above will generate following files:
```text
.
└── target
    └── universal
        ├── scripts
        │   └── bin
        │       ├── http-stream-exercise
        │       └── http-stream-exercise.bat
        └── stage
            ├── bin
            │   ├── http-stream-exercise
            │   └── http-stream-exercise.bat
            ├── lib
            │   ├── com.hochgi.http-stream-exercise-0.0.1.jar
            │   └── …
            └── resources
                └── generator-linux-amd64
```
(you can also use `dist` instead of stage to get a packaged zip)

### Running
Simply execute the script under generated bin dir:
```sh
$ bin/http-stream-exercise --help
  -m, --max-line-length  <arg>   Maximum length of lines coming out from
                                 generator (DEFAULT 8192)
  -p, --port  <arg>              Which port to use for the server (DEFAULT 1729)
  -h, --help                     Show help message
```

As seen in `--help`, you can pass some custom configs...
Defaults should be good enough. default port should be quite [unique](https://en.wikipedia.org/wiki/Taxicab_number), so you probably won't need to stop another server running on your machine just to test it.

### Querying
Simple GET to `localhost:$PORT/stats` with optional `?pretty` query parameter.
```sh
$ curl -s localhost:1729/stats
{"eventTypesCount":{"baz":3,"foo":4,"bar":6},"wordsCount":{"sit":1,"ipsum":3,"lorem":5,"amet":1,"dolor":3},"failCount":6}
```
or
```sh
$ curl -s localhost:1729/stats?pretty
{
  "eventTypesCount": {
    "baz": 5,
    "foo": 6,
    "bar": 10
  },
  "wordsCount": {
    "sit": 4,
    "ipsum": 3,
    "lorem": 6,
    "amet": 1,
    "dolor": 7
  },
  "failCount": 8
}
```  

### Possible improvements
- I usually add tests, and think every meaningful project should have tests. Future changes may break functionality unintentionaly, and good tests can save a lot of debugging time in the future.
- In this toy example, there isn't much that can go wrong. But in general, I prefer to avoid mutable shared state. the `@volatile private[this] var stats` is an example of what I prefer to avoid in larger systems. A more complex but reliable solution, would be to use a publisher that listeners can hook up into, and get latest updates properly.
- I only exposed a single API, and a more extensive solution can have multiple views. We can also think of a usefull API to expose the stream of events as a chunked response, and not just the accumulated value. We can also extend this to fetch events from multiple sources, and aggregate the stats.
