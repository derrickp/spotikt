# Spotikt
Collect some stats about your Spotify listen history.

## Running
```bash
./gradlew run --args="StreamingHistory0.json output.json output_areas.json listens"
```

Where:
  - `StreamingHistory0.json` is your Spotify history file
  - `output.json` is the output file for parsing, helps keep track of where the parsing is at
  - `output_areas.json` output file for the areas gathered so far. Keeps track of current parse index.
  - `listens` is what operation to perform. Options are `listens`, `areas`, `stats`
    - `listens` parses Spotify history and gets information from musicbrainz
    - `areas` gathers areas that artists are from
    - `stats` Produce stats of listens
