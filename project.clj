(defproject editor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [seesaw "1.5.0"]]
  :main ^:skip-aot editor.core
  :target-path "target/%s"
  :java-source-paths ["src/java"]
  :test-paths ["src" "test"]
  ;; :javac-options     ["-target" "1.6" "-source" "1.6"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
