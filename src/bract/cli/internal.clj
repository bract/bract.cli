;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.internal
  (:require
    [clojure.string    :as string]
    [bract.cli.keydef  :as clim-kdef]
    [bract.core.keydef :as core-kdef]
    [bract.core.util   :as core-util]))


(def cli-options
  [["-c" "--command COMMAND"         "Command to run" :default "run"]
   ["-f" "--config-file CONFIG-FILE" "Config file names (comma-separated)"]
   ["-h" "--help"        "Show usage"         :default false]
   ["-v" "--verbose"     "Verbose execution"  :default nil]])


(defn assoc-verbose
  [context parse-result]
  (if (or (reduced? context) (core-kdef/ctx-exit? context))
    context
    (let [verbose (get-in parse-result [:options :verbose])]
      (cond
        (true? verbose)  (assoc context (key core-kdef/ctx-verbose?) true)
        (false? verbose) (assoc context (key core-kdef/ctx-verbose?) false)
        (nil? verbose)   (if (contains? context (key core-kdef/ctx-verbose?))
                           context
                           (assoc context (key core-kdef/ctx-verbose?) false))
        :otherwise       context))))


(defn assoc-config-file
  [context parse-result]
  (if (or (reduced? context) (core-kdef/ctx-exit? context))
    context
    (if-let [config-filenames (get-in parse-result [:options :config-file])]
      (as-> config-filenames <>
        (string/split <> #",")
        (mapv string/trim <>)
        (assoc context (key core-kdef/ctx-config-files) <>))
      (if (clim-kdef/ctx-config-required? context)
        (do
          (core-util/err-println "ERROR: No config file specified as argument")
          (core-util/err-println (get-in parse-result [:summary]))
          (core-kdef/induce-exit context 1))
        context))))


(defn assoc-command
  [context parse-result]
  (if (or (reduced? context) (core-kdef/ctx-exit? context))
    context
    (let [command (get-in parse-result [:options :command] "run")]
      (assoc context (key clim-kdef/ctx-command) command))))


(defn assoc-cmd-args
  [context parse-result]
  (if (or (reduced? context) (core-kdef/ctx-exit? context))
    context
    (let [arguments (get-in parse-result [:arguments] [])]
      (assoc context (key clim-kdef/ctx-cmd-args) arguments))))
