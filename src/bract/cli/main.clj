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
    [bract.core.config  :as core-config]
    [bract.core.inducer :as core-inducer]
    [bract.core.util    :as core-util])
  (:gen-class))


(defn trigger
  [context]
  (core-util/induce context
    [core-inducer/set-verbosity   ; set default verbosity
     clim-inducer/parse-args      ; parse CLI arguments and populate context
     core-inducer/set-verbosity   ; set user-preferred verbosity
     core-inducer/read-config     ; read config file(s) and populate context
     clim-inducer/execute-command ; execute the resolved command
     core-inducer/run-inducers    ; finally run the configured inducers
     ]))


(defn -main
  "This function becomes the Java main() method entry point."
  [& args]
  (trigger {(key core-config/ctx-cli-args) (vec args)}))
