;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.main
  "Provided entry-point for Bract CLI applications."
  (:require
    [bract.cli.inducer  :as clim-inducer]
    [bract.cli.keydef   :as clim-kdef]
    [bract.core.keydef  :as core-kdef]
    [bract.core.inducer :as core-inducer]
    [bract.core.util    :as core-util])
  (:gen-class))


(defn trigger
  "Implementation detail for the CLI main entry point. Trigger execution of the following inducers in a sequence on the
  given context:
  bract.core.inducer/set-verbosity        ; set default verbosity
  bract.core.inducer/read-context         ; read the pre-CLI, pre-config context
  [bract.core.inducer/run-context-inducers
   :bract.cli/pre-inducers]               ; run pre-CLI inducers
  bract.cli.inducer/parse-args            ; parse CLI arguments and populate context
  bract.core.inducer/set-verbosity        ; set user-preferred verbosity
  bract.core.inducer/run-context-inducers ; run pre-config inducers
  bract.core.inducer/read-config          ; read config file(s) and populate context
  bract.cli.inducer/execute-command       ; execute the resolved command
  bract.core.inducer/run-config-inducers  ; finally run the configured inducers"
  [context]
  (core-inducer/induce context
    [core-inducer/set-verbosity            ; set default verbosity
     core-inducer/read-context             ; read the pre-CLI, pre-config context
     (list core-inducer/run-context-inducers
       (key clim-kdef/ctx-pre-inducers))   ; run pre-CLI inducers
     clim-inducer/parse-args               ; parse CLI arguments and populate context
     core-inducer/set-verbosity            ; set user-preferred verbosity
     core-inducer/run-context-inducers     ; run pre-config inducers
     core-inducer/read-config              ; read config file(s) and populate context
     clim-inducer/execute-command          ; execute the resolved command
     core-inducer/run-config-inducers      ; finally run the configured inducers
     ]))


(defn -main
  "This function becomes the Java main() method entry point."
  [& args]
  (try
    (when-let [exit-code (-> {(key core-kdef/ctx-context-file) "bract-context.edn"
                              (key clim-kdef/ctx-app-commands) clim-kdef/default-commands
                              (key clim-kdef/ctx-pre-inducers) []
                              (key core-kdef/ctx-cli-args)     (vec args)}
                           trigger
                           core-kdef/ctx-app-exit-code)]
      (System/exit (int exit-code)))
    (catch Throwable e
      (core-util/pst-when-uncaught-handler e)
      (throw e))))
