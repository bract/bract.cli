(defproject bract/bract.cli "0.6.0-alpha1"
  :description "CLI module for Bract"
  :url "https://github.com/bract/bract.cli"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :global-vars {*warn-on-reflection* true
                *assert* true
                *unchecked-math* :warn-on-boxed}
  :dependencies [[bract/bract.core "0.6.0-alpha1"]
                 [org.clojure/tools.cli "0.3.5" :exclusions [org.clojure/clojure]]]
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :coverage {:plugins [[lein-cloverage "1.0.9"]]}
             :rel {:min-lein-version "2.7.1"
                   :pedantic? :abort}
             :c17 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :c18 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :c19 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :dln {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
