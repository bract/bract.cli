# bract.cli Change Log


## [WIP] 0.3.1 / 2017-June-??

- [TODO] Use bract.core 0.3.1
- Extract default command map as `bract.cli.config/default-commands` for extensibility
- Add documentation for Github pages


## 0.3.0 / 2017-June-11
- Use bract.core 0.3.0


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
