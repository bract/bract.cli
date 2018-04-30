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
    [bract.core.keydef :as core-kdef]
    [bract.cli.keydef  :as clim-kdef]
    [bract.cli.inducer :as clim-inducer])
  (:import
    [java.io ByteArrayInputStream StringReader]))


(deftest test-merge-commands
  (is (= {:bract.cli/app-commands (merge clim-kdef/default-commands
                                    {"foo" {:doc "test"
                                            :handler 'identity}})}
        (clim-inducer/merge-commands {} {"foo" {:doc "test"
                                                :handler 'identity}})))
  (is (= {:bract.cli/app-commands {"foo" {:doc "test foo"
                                          :handler 'identity}
                                   "bar" {:doc "test bar"
                                          :handler 'identity}}}
        (clim-inducer/merge-commands
          {:bract.cli/app-commands {"foo" {:doc "test foo"
                                           :handler 'identity}}}
          {"bar" {:doc "test bar"
                  :handler 'identity}}))))


(deftest test-merge-launch-commands
  (let [context (clim-inducer/merge-launch-commands {} {"foo" {:doc "test"
                                                               :handler 'identity}})]
    (is (contains? context :bract.cli/app-commands))
    (is (fn? (get-in context [:bract.cli/app-commands "foo" :handler]))))
  (let [context (clim-inducer/merge-launch-commands
                  {:bract.cli/app-commands {"foo" {:doc "test foo"
                                                   :handler 'identity}}}
                  {"bar" {:doc "test bar"
                          :handler 'identity}})]
    (is (fn? (get-in context [:bract.cli/app-commands "bar" :handler])))))


(deftest test-parse-args
  (is (thrown? IllegalArgumentException
        (clim-inducer/parse-args {})) "missing CLI args")
  (is (core-kdef/ctx-exit?
        (clim-inducer/parse-args {(key clim-kdef/ctx-config-required?) true
                                  (key core-kdef/ctx-cli-args) ["-c" "hey"]})) "config file required but not passed")
  (let [context {(key core-kdef/ctx-cli-args) ["-f" "foo.edn"]}]
    (is (= (assoc context
             (key clim-kdef/ctx-command) "run"
             (key clim-kdef/ctx-cmd-args) []
             (key core-kdef/ctx-verbose?) false
             (key core-kdef/ctx-config-files) ["foo.edn"])
          (clim-inducer/parse-args context)) "specified config file"))
  (let [context {(key core-kdef/ctx-cli-args) ["-f" "foo.edn,bar.edn"]}]
    (is (= (assoc context
             (key clim-kdef/ctx-command) "run"
             (key clim-kdef/ctx-cmd-args) []
             (key core-kdef/ctx-verbose?) false
             (key core-kdef/ctx-config-files) ["foo.edn" "bar.edn"])
          (clim-inducer/parse-args context)) "specified multiple config files"))
  (let [context {(key core-kdef/ctx-cli-args) ["-c" "hey"]}]
    (is (= (assoc context
             (key clim-kdef/ctx-command) "hey"
             (key clim-kdef/ctx-cmd-args) []
             (key core-kdef/ctx-verbose?) false)
          (clim-inducer/parse-args context)) "specified command"))
  (let [context {(key core-kdef/ctx-cli-args) ["-v"]}]
    (is (= (assoc context
             (key core-kdef/ctx-verbose?) true
             (key clim-kdef/ctx-command) "run"
             (key clim-kdef/ctx-cmd-args) [])
          (clim-inducer/parse-args context)) "specified verbosity flag"))
  (let [context {(key core-kdef/ctx-cli-args) ["-vc" "hey"]}]
    (is (= (assoc context
             (key clim-kdef/ctx-command) "hey"
             (key clim-kdef/ctx-cmd-args) []
             (key core-kdef/ctx-verbose?) true)
          (clim-inducer/parse-args context)) "specified command and verbose"))
  (let [context {(key core-kdef/ctx-cli-args) ["-vc" "hey" "foo" "bar"]}]
    (is (= (assoc context
             (key clim-kdef/ctx-command) "hey"
             (key clim-kdef/ctx-cmd-args) ["foo" "bar"]
             (key core-kdef/ctx-verbose?) true)
          (clim-inducer/parse-args context)) "specified verbose, command and arguments")))


(deftest test-execute-command
  (is (thrown? IllegalArgumentException
        (clim-inducer/execute-command {})) "missing command")
  (is (thrown? IllegalArgumentException
        (clim-inducer/execute-command {(key clim-kdef/ctx-command) "run"})) "missing command args")
  (is (not (reduced?
             (clim-inducer/execute-command {(key clim-kdef/ctx-command) "run"
                                            (key clim-kdef/ctx-cmd-args) []}))) "the `run` command")
  (is (not (reduced?
             (clim-inducer/execute-command {(key clim-kdef/ctx-command) "dryrun"
                                            (key clim-kdef/ctx-cmd-args) []}))) "the `dryrun` command")
  (is (core-kdef/ctx-exit?
        (clim-inducer/execute-command {(key core-kdef/ctx-config) {"foo" "bar"}
                                       (key clim-kdef/ctx-command) "config"
                                       (key clim-kdef/ctx-cmd-args) []})) "the `config` command")
  (comment  ; this particular test/assertion messes with test reports
    (is (reduced?
          (binding [*in* (StringReader. "")]
            (clim-inducer/execute-command {(key core-kdef/ctx-config) {"foo" "bar"}
                                           (key clim-kdef/ctx-command) "repl"
                                           (key clim-kdef/ctx-cmd-args) []}))) "the `repl` command")))
