(ns cast.core
  (:require [clojure.browser.repl :as repl]
            [cljsjs.d3]))

(repl/connect "http://localhost:9000/repl")

(enable-console-print!)
(println "Hello world")
