;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.inducer
  "Inducers provided by the bract.cli module."
  (:require
    [clojure.string     :as string]
    [clojure.pprint     :as pp]
    [clojure.tools.cli  :as cli]
    [keypin.util        :as kputil]
    [bract.core.echo    :as echo]
    [bract.core.keydef  :as core-kdef]
    [bract.core.inducer :as core-inducer]
    [bract.core.util    :as core-util]
    [bract.cli.keydef   :as clim-kdef]
    [bract.cli.internal :as internal]))


(defn merge-commands
  "Merge given CLI command map `{\"command-name\" {:doc string :handler inducer}}` to the existing one. The inducer is
  `(fn [context]) -> context`."
  [context new-commands]
  (echo/echo "Adding CLI commands:" (keys new-commands))
  (->> new-commands
    (reduce-kv (fn [m k command]
                 (core-util/expected :handler
                   "command to have a :handler key with corresponding function or fully qualified name" command)
                 (assoc m k command))
      {})
    (merge (clim-kdef/ctx-app-commands context))  ; merge/assoc instead of update to enforce app-commands validation
    (assoc context (key clim-kdef/ctx-app-commands))))


(defn merge-launch-commands
  "Merge given CLI command map `{\"command-name\" {:doc string :handler launcher}}` to the existing one. The launcher is
  `(fn [context]) -> context` meant to replace the entry at key `:bract.core/launcher` in the context."
  [context new-commands]
  (echo/echo "Adding CLI commands for launchers:" (->> (vals new-commands)
                                                    (map :handler)
                                                    (zipmap (keys new-commands))))
  (->> new-commands
    (reduce-kv (fn [m k command]
                 (as-> (fn make-updater [launchers]
                         (core-util/expected coll?
                           "a collection of launcher functions or their fully qualified names at key :launchers"
                           launchers)
                         (fn update-launchers [context]
                           (assoc context
                             (key core-kdef/ctx-launch?) true
                             (key core-kdef/ctx-launchers) launchers)))
                   $
                   ($ (:launchers command))
                   (assoc command :handler $)
                   (assoc m k $)))
      {})
    (merge (clim-kdef/ctx-app-commands context))  ; merge/assoc instead of update to enforce app-commands validation
    (assoc context (key clim-kdef/ctx-app-commands))))


(defn parse-args
  "Given context with key `:bract.cli/cli-args`, parse CLI args and return (potentially reduced) the context updated
  with config filename, CLI command and command-arguments."
  [context]
  (core-inducer/when-context-has-key
    [context (key core-kdef/ctx-cli-args) "CLI-args parsing"]
    (let [cli-args (core-kdef/ctx-cli-args context)
          {:keys [options
                  arguments
                  summary
                  errors]
           :as parse-result} (cli/parse-opts cli-args internal/cli-options
                               :in-order true)]
      (cond
        (:help options) (do
                          (core-util/err-println summary)
                          (core-util/err-println (str "\nCommands:\n"
                                                   (->> (clim-kdef/ctx-app-commands context)
                                                     (reduce-kv (fn [a command {doc :doc}]
                                                                  (conj a {"Command" command "Description" doc}))
                                                       [])
                                                     pp/print-table
                                                     with-out-str)))
                          (core-kdef/induce-exit context))
        errors          (do
                          (core-util/err-println (string/join \newline errors))
                          (core-kdef/induce-exit context))
        :otherwise      (-> context
                          (internal/assoc-verbose     parse-result)
                          (internal/assoc-config-file parse-result)
                          (internal/assoc-command     parse-result)
                          (internal/assoc-cmd-args    parse-result))))))


(defn execute-command
  "Execute CLI command placed under key `:bract.cli/command` in the context using the CLI aruments placed under the key
  `:bract.cli/cmd-args`. Verify valid command by looking up the command map under the key `:bract.cli/app-commands` in
  the context."
  [context]
  (let [command   (clim-kdef/ctx-command context)
        arguments (clim-kdef/ctx-cmd-args context)
        app-commands (clim-kdef/ctx-app-commands context)]
    (if (contains? app-commands command)
      (let [{:keys [doc handler]} (get app-commands command)]
        (core-util/expected (some-fn ifn? kputil/fqvn?)
          "CLI-command handler function or fully qualified fn name" handler)
        (core-inducer/apply-inducer "CLI command-handler" context handler))
      (do
        (core-util/err-println (format "ERROR: Expected a valid command %s, but found '%s'."
                                 (->> (keys app-commands)
                                   vec
                                   pr-str)
                                 command))
        (core-kdef/induce-exit context 1)))))
