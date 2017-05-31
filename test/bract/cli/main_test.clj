;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.main-test
  (:require
    [clojure.test :refer :all]
    [bract.cli.main :as main]))


(defn inc-inducer
  [context]
  (update context :inc-target inc))


(deftest test-main
  (testing "no arg, one pre-configured inducer"
    (let [context (main/trigger {:bract.core/cli-args []
                                 :inc-target 0
                                 :bract.core/config {"bract.core.inducers" ['bract.cli.main-test/inc-inducer]}})]
      (is (= 1 (:inc-target context)))))
  (testing "no arg, one inducer in config file"
    (let [context (main/trigger {:bract.core/cli-args []
                                 :inc-target 0
                                 :bract.core/config-files "sample.edn"})]
      (is (= 1 (:inc-target context)))))
  (testing "no arg, one inducer in pre-specified config file"
    (let [context (main/trigger {:bract.core/cli-args ["-f" "sample.edn"]
                                 :inc-target 0})]
      (is (= 1 (:inc-target context)))))
  (testing "CLI arg, config file"
    (let [context (main/trigger {:bract.core/cli-args ["-f" "sample.edn"]
                                 :inc-target 0})]
      (is (= 1 (:inc-target context))))))
