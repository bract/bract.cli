;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns bract.cli.keydef-test
  (:require
    [clojure.test :refer :all]
    [bract.cli.keydef :as kdef]))


(deftest context-test
  (testing "happy cases"
    (let [good-context {:bract.cli/config-required? true
                        :bract.cli/command          "foo"
                        :bract.cli/cmd-args         ["bar" "baz"]
                        :bract.cli/app-commands     {"foo" {:doc "baz" :handler identity}}}]
      (is (true?           (kdef/ctx-config-required? good-context)))
      (is (= "foo"         (kdef/ctx-command          good-context)))
      (is (= ["bar" "baz"] (kdef/ctx-cmd-args         good-context)))
      (is (= {"foo" {:doc "baz"
                     :handler identity}} (kdef/ctx-app-commands good-context)))))
  (testing "default values"
    (is (false? (kdef/ctx-config-required? {})))
    (is (map?   (kdef/ctx-app-commands     {}))))
  (testing "missing values"
    (is (thrown? IllegalArgumentException (kdef/ctx-command  {})))
    (is (thrown? IllegalArgumentException (kdef/ctx-cmd-args {}))))
  (testing "bad values"
    (let [bad-context {:bract.cli/config-required? 10
                       :bract.cli/command          10
                       :bract.cli/cmd-args         10
                       :bract.cli/app-commands     10}]
      (is (thrown? IllegalArgumentException (kdef/ctx-config-required? bad-context)))
      (is (thrown? IllegalArgumentException (kdef/ctx-command          bad-context)))
      (is (thrown? IllegalArgumentException (kdef/ctx-cmd-args         bad-context)))
      (is (thrown? IllegalArgumentException (kdef/ctx-app-commands     bad-context))))))
