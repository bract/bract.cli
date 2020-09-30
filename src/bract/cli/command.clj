;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.command
  "CLI commands implementation."
  (:require
    [bract.core.keydef :as core-kdef]))


(defn command-run
  "Run all steps."
  [context]
  (assoc context
    (key core-kdef/ctx-launch?) true))


(defn command-dryrun
  "Run all steps except launch."
  [context]
  (assoc context
    (key core-kdef/ctx-launch?) false))


(defn command-config
  "Print the given config using the format determined from the supplied config file names."
  [context]
  (let [config (core-kdef/ctx-config context)
        config-filenames (core-kdef/ctx-config-files context)]
    (core-kdef/print-config config config-filenames))
  (core-kdef/induce-exit context))


(defn command-repl
  "Launch a REPL."
  [context]
  (clojure.main/main)
  (core-kdef/induce-exit context))
