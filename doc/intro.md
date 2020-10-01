# Introduction to bract.cli

The bract.cli module provides the Command Line Interface (CLI) entry point features for Bract.


## Commands

The provided CLI commands are implemented in the `bract.cli.command` namespace.

| Command | Implemented by | Changes to the context                   |
|---------|----------------|------------------------------------------|
| `run`   |`command-run`   |sets `:bract.core/launch?` to `true`      |
| `dryrun`|`command-dryrun`|sets `:bract.core/launch?` to `false`     |
| `config`|`command-config`|finally sets `:bract.core/exit?` to `true`|
| `repl`  |`command-repl`  |finally sets `:bract.core/exit?` to `true`|


## Context keys

| Context key                 | Value type      | Description                     |
|-----------------------------|-----------------|---------------------------------|
|`:bract.cli/config-required?`|boolean          |is a config file required?       |
|`:bract.cli/command`         |string           |the CLI command to execute       |
|`:bract.cli/cmd-args`        |vector of string |arguments for the CLI command    |
|`:bract.cli/app-commands`    |map of string:map|commands to be handled by the app|
