;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.inducer-test
  (:require
    [clojure.test :refer :all]
    [bract.core.config :as core-config]
    [bract.cli.config  :as clim-config]
    [bract.cli.inducer :as clim-inducer])
  (:import
    [java.io ByteArrayInputStream StringReader]))


(deftest test-parse-args
  (is (thrown? IllegalArgumentException
        (clim-inducer/parse-args {})) "missing CLI args")
  (is (reduced?
        (clim-inducer/parse-args {(key clim-config/ctx-config-required?) true
                                  (key core-config/ctx-cli-args) ["-c" "hey"]})) "config file required but not passed")
  (let [context {(key core-config/ctx-cli-args) ["-f" "foo.edn"]}]
    (is (= (assoc context
             (key clim-config/ctx-command) "run"
             (key clim-config/ctx-cmd-args) []
             (key core-config/ctx-verbose?) false
             (key core-config/ctx-config-files) ["foo.edn"])
          (clim-inducer/parse-args context)) "specified config file"))
  (let [context {(key core-config/ctx-cli-args) ["-f" "foo.edn,bar.edn"]}]
    (is (= (assoc context
             (key clim-config/ctx-command) "run"
             (key clim-config/ctx-cmd-args) []
             (key core-config/ctx-verbose?) false
             (key core-config/ctx-config-files) ["foo.edn" "bar.edn"])
          (clim-inducer/parse-args context)) "specified multiple config files"))
  (let [context {(key core-config/ctx-cli-args) ["-c" "hey"]}]
    (is (= (assoc context
             (key clim-config/ctx-command) "hey"
             (key clim-config/ctx-cmd-args) []
             (key core-config/ctx-verbose?) false)
          (clim-inducer/parse-args context)) "specified command"))
  (let [context {(key core-config/ctx-cli-args) ["-v"]}]
    (is (= (assoc context
             (key core-config/ctx-verbose?) true
             (key clim-config/ctx-command) "run"
             (key clim-config/ctx-cmd-args) [])
          (clim-inducer/parse-args context)) "specified verbosity flag"))
  (let [context {(key core-config/ctx-cli-args) ["-vc" "hey"]}]
    (is (= (assoc context
             (key clim-config/ctx-command) "hey"
             (key clim-config/ctx-cmd-args) []
             (key core-config/ctx-verbose?) true)
          (clim-inducer/parse-args context)) "specified command and verbose"))
  (let [context {(key core-config/ctx-cli-args) ["-vc" "hey" "foo" "bar"]}]
    (is (= (assoc context
             (key clim-config/ctx-command) "hey"
             (key clim-config/ctx-cmd-args) ["foo" "bar"]
             (key core-config/ctx-verbose?) true)
          (clim-inducer/parse-args context)) "specified verbose, command and arguments")))


(deftest test-execute-command
  (is (thrown? IllegalArgumentException
        (clim-inducer/execute-command {})) "missing command")
  (is (thrown? IllegalArgumentException
        (clim-inducer/execute-command {(key clim-config/ctx-command) "run"})) "missing command args")
  (is (not (reduced?
             (clim-inducer/execute-command {(key clim-config/ctx-command) "run"
                                            (key clim-config/ctx-cmd-args) []}))) "the `run` command")
  (is (not (reduced?
             (clim-inducer/execute-command {(key clim-config/ctx-command) "dryrun"
                                            (key clim-config/ctx-cmd-args) []}))) "the `dryrun` command")
  (is (reduced?
        (clim-inducer/execute-command {(key core-config/ctx-config) {"foo" "bar"}
                                       (key clim-config/ctx-command) "config"
                                       (key clim-config/ctx-cmd-args) []})) "the `config` command")
  (comment  ; this particular test/assertion messes with test reports
    (is (reduced?
          (binding [*in* (StringReader. "")]
            (clim-inducer/execute-command {(key core-config/ctx-config) {"foo" "bar"}
                                           (key clim-config/ctx-command) "repl"
                                           (key clim-config/ctx-cmd-args) []}))) "the `repl` command")))
