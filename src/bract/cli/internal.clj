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
    [bract.cli.config  :as clim-config]
    [bract.core.config :as core-config]
    [bract.core.util   :as core-util]))


(def cli-options
  [["-c" "--command COMMAND"         "Command to run" :default "run"]
   ["-f" "--config-file CONFIG-FILE" "Config file names (comma-separated)"]
   ["-h" "--help"        "Show usage"         :default false]
   ["-v" "--verbose"     "Verbose execution"  :default nil]])


(defn assoc-verbose
  [context parse-result]
  (if (reduced? context)
    context
    (let [verbose (get-in parse-result [:options :verbose])]
      (cond
        (true? verbose)  (assoc context (key core-config/ctx-verbose?) true)
        (false? verbose) (assoc context (key core-config/ctx-verbose?) false)
        (nil? verbose)   (if (contains? context (key core-config/ctx-verbose?))
                           context
                           (assoc context (key core-config/ctx-verbose?) false))
        :otherwise       context))))


(defn assoc-config-file
  [context parse-result]
  (if (reduced? context)
    context
    (if-let [config-filenames (get-in parse-result [:options :config-file])]
      (as-> config-filenames <>
        (string/split <> #",")
        (mapv string/trim <>)
        (assoc context (key core-config/ctx-config-files) <>))
      (if (clim-config/ctx-config-required? context)
        (do
          (core-util/err-println "No config file specified as argument")
          (core-util/err-println (get-in parse-result [:summary]))
          (reduced context))
        context))))


(defn assoc-command
  [context parse-result]
  (if (reduced? context)
    context
    (let [command (get-in parse-result [:options :command] "run")]
      (assoc context (key clim-config/ctx-command) command))))


(defn assoc-cmd-args
  [context parse-result]
  (if (reduced? context)
    context
    (let [arguments (get-in parse-result [:arguments] [])]
      (assoc context (key clim-config/ctx-cmd-args) arguments))))
