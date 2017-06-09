# bract.cli TODO and Change Log


## [WIP] 0.3.0 / 2017-June-??
- Use bract.core 0.3
  - [TODO] Remove this when using final GA version


## 0.2.0 / 2017-June-04
- Extract codebase into `bract.cli` namespace
- Drop `--launch` CLI flag in favour of `dryrun` command
- Enhanced `bract.cli.main` namespace (from erstwhile `bract.core.main`)
- Process CLI arguments via overridable inducers
- Support for custom CLI commands handled by the application
- Use `[bract/bract.core "0.2.0"]`


## 0.1.0 / 2017-April-25 (part of `[bract/bract.core "0.1.0"]` code base)
- CLI commands
  - Run
  - Dry run
  - Print config
  - Clojure REPL
