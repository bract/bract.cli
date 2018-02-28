;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.inducer
  (:require
    [clojure.string     :as string]
    [clojure.pprint     :as pp]
    [clojure.tools.cli  :as cli]
    [keypin.util        :as kputil]
    [bract.core.keydef  :as core-kdef]
    [bract.core.inducer :as core-inducer]
    [bract.core.util    :as core-util]
    [bract.cli.keydef   :as clim-kdef]
    [bract.cli.internal :as internal]))


(defn merge-commands
  "Merge given CLI command map (key: command-string, value: {:doc string :handler (fn [context]) -> context}) to the
  existing one."
  [context new-commands]
  (let [old-commands (clim-kdef/ctx-app-commands context)]
    (assoc context
      (key clim-kdef/ctx-app-commands) (merge old-commands new-commands))))


(defn parse-args
  "Given context with key :bract.cli/cli-args, parse CLI args and return (potentially reduced) the context updated
  with config filename, CLI command and command-arguments."
  [context]
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
                        (reduced context))
      errors          (do
                        (core-util/err-println (string/join \newline errors))
                        (reduced context))
      :otherwise      (-> context
                        (internal/assoc-verbose     parse-result)
                        (internal/assoc-config-file parse-result)
                        (internal/assoc-command     parse-result)
                        (internal/assoc-cmd-args    parse-result)))))


(defn execute-command
  [context]
  (let [command   (clim-kdef/ctx-command context)
        arguments (clim-kdef/ctx-cmd-args context)
        app-commands (clim-kdef/ctx-app-commands context)]
    (if (contains? app-commands command)
      (let [{:keys [doc handler]} (get app-commands command)]
        (core-util/expected (some-fn ifn? kputil/fqvn?)
          "CLI-command handler function or fully qualified fn name" handler)
        (core-inducer/apply-inducer "CLI command-handler" context handler))
      (core-util/expected (format "a valid command %s" (->> (keys app-commands)
                                                         (concat (keys app-commands))
                                                         vec
                                                         pr-str))
        command))))
