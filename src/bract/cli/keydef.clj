;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.keydef
  "Context and configuration keys used by the bract.cli module."
  (:require
    [keypin.core :as keypin]
    [keypin.util :as kputil]
    [bract.cli.command :as command]))


(def default-commands {"run"    {:doc "Run all steps"               :handler #'command/command-run}
                       "dryrun" {:doc "Run all steps except launch" :handler #'command/command-dryrun}
                       "config" {:doc "Print configuration"         :handler #'command/command-config}
                       "repl"   {:doc "Start a REPL"                :handler #'command/command-repl}})


(keypin/defkey
  ctx-config-required? [:bract.cli/config-required? kputil/bool? "Config file required?" {:default false}]
  ctx-command          [:bract.cli/command          string?      "The CLI command to execute"]
  ctx-cmd-args         [:bract.cli/cmd-args         (kputil/vec?
                                                      string?)   "Arguments for the CLI command"]
  ctx-app-commands     [:bract.cli/app-commands     (every-pred
                                                      map? seq)  "Commands (non-empty) to be handled by the app"
                        {:default default-commands}])
