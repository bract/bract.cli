;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.command
  (:require
    [clojure.string    :as string]
    [keypin.core       :as keypin]
    [bract.core.config :as core-config])
  (:import
    [java.io Writer]
    [java.util Map]
    [keypin ConfigIO PropertyConfigIO]))


(defn command-run
  "Run all steps."
  [context]
  context)


(defn command-dryrun
  "Run all steps except launch."
  [context]
  (assoc context
    (key core-config/ctx-launch?) false))


(defn command-config
  "Print the given config using the format determined from the supplied config file names. Indicate flow-termination
  by returning reduced context"
  [context]
  (let [config (core-config/ctx-config context)
        config-filenames (core-config/ctx-config-files context)
        ^ConfigIO configIO (if (some (comp #(.endsWith ^String % ".edn")
                                       string/trim
                                       string/lower-case)
                                 config-filenames)
                             keypin/edn-file-io
                             PropertyConfigIO/INSTANCE)]
    (.writeConfig configIO *out* ^Map config true))
  (reduced context))


(defn command-repl
  "Launch a REPL and indicate flow-termination by returning reduced context."
  [context]
  (clojure.main/main)
  (reduced context))
