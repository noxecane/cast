(ns user
  (:require [cljs.build.api :as compiler]
            [cljs.repl :as repl]
            [cljs.repl.browser :as browser]
            [clojure.tools.nrepl.server :refer [default-handler start-server]]
            [cider.nrepl :refer [cider-middleware]]
            [refactor-nrepl.middleware :refer [wrap-refactor]]))

(def server
  (start-server :port 5000
                :handler (apply
                          default-handler
                          (conj
                           (map resolve cider-middleware)
                           #'wrap-refactor))))
(println "Started server on port 5000")


(defn cljs-prod-build []
  (compiler/build "src" {:output-to "cast.min.js"
                         :output-dir "out"
                         :optimizations :advanced}))

(defn cljs-build []
  (compiler/build "src" {:main 'cast.core
                         :output-to "cast.js"
                         :output-dir "out"
                         :browser-repl true
                         :verbose true}))

(defn cljs-repl []
  (cljs-build)
  (repl/repl (browser/repl-env)
             :main 'cast.core
             :output-dir "out"))

(defn cljs-watch []
  (compiler/watch "src" {:output-to "cast.js"
                         :output-dir "out"
                         :optimizations :none
                         :cache-analysis true
                         :source-map true
                         :main 'cast.core}))
