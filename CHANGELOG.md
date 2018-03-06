# bract.cli Change Log

## Todo

- None


## [WIP] 0.6.0 / 2018-March-??

- Use bract.core 0.6.0
  - [Todo] Use the GA version
- [Todo] Exclude config inducers from root
  - Bootstrap off the context inducers (which may only optionally include config inducers)
- [Todo] Drop `bract.cli.main/trigger`
- [Todo] Base `bract.cli.main` on `bract.core.main`


## 0.5.1 / 2018-March-05

- Use bract.core 0.5.1
- Fix commands visibility in CLI help message
  - In root CLI inducers move `read-context` ahead of `parse-args`
  - Run inducers `:bract.cli/pre-inducers` before parsing CLI args
  - Include default (minimal) CLI commands in `bract.cli.main/-main` root context
  - Add inducer `bract.cli.inducer/merge-commands` to merge old and new commands
- Make `bract.cli.main/-main` return exit code using `:bract.core/app-exit-code`


## 0.5.0 / 2018-February-18

- Use bract.core 0.5.0


## 0.4.1 / 2017-August-08

- Use bract.core 0.4.1


## 0.4.0 / 2017-August-05

- Use the GA version of bract.core 0.4.0


## 0.4.0-alpha2 / 2017-August-01

- Use bract.core 0.4.0-alpha2
- Merge context from `bract-context.edn` in CLI entry point when available
- Catch exception and print stack trace in the main entry point (due to uncaught handler)


## 0.4.0-alpha1 / 2017-July-31

- Use bract.core 0.4.0-alpha1
- [BREAKING CHANGE] Rename `bract.cli.config` namespace to `bract.cli.keydef`


## 0.3.1 / 2017-June-30

- Use bract.core 0.3.1
- Extract default command map as `bract.cli.config/default-commands` for extensibility


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
