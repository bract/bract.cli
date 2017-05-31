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
    [bract.core.config  :as core-config]
    [bract.core.util    :as core-util]
    [bract.cli.config   :as clim-config]
    [bract.cli.internal :as internal]))


(defn parse-args
  "Given context with key :bract.cli/cli-arguments, parse CLI args and return (potentially reduced) the context updated
  with config filename, CLI command and command-arguments."
  [context]
  (let [cli-args (core-config/ctx-cli-args context)
        {:keys [options
                arguments
                summary
                errors]
         :as parse-result} (cli/parse-opts cli-args internal/cli-options
                             :in-order true)]
    (cond
      (:help options) (do
                        (core-util/err-println summary)
                        (core-util/err-println (str "Commands:\n"
                                                 (->> (clim-config/ctx-app-commands context)
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
  (let [command   (clim-config/ctx-command context)
        arguments (clim-config/ctx-cmd-args context)
        app-commands (clim-config/ctx-app-commands context)]
    (if (contains? app-commands command)
      (let [{:keys [doc handler]} (get app-commands command)]
        (core-util/expected (some-fn ifn? kputil/fqvn?)
          "CLI-command handler function or fully qualified fn name" handler)
        (core-config/apply-inducer context "CLI command-handler" handler))
      (core-util/expected (format "a valid command %s" (->> (keys app-commands)
                                                         (concat (keys app-commands))
                                                         vec
                                                         pr-str))
        command))))
