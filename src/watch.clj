
(ns watch
  (:require [providers]
            [web-server]
            [chunk-provider]))

(use '[clojure.java.shell :only [sh]])

(def version (atom 0))

(defn watcher [context providers-orig]
  (let [
        {:keys [output-web-page input-dir]} context
        reloader-text (slurp (str input-dir "/reloader.js"))]
  (println "Watching for changes...")

  ;; use an atom      
  (def providers providers-orig)

  (web-server/start (:output-dir context) version)

  (sh "xdg-open" "http://localhost:3000/index.html")

  (while true
    (Thread/sleep 1000)
    (.print System/out  ".")
    (.flush System/out)

    ;; did input.edn change? => rebuild providers and index.html
    (if (providers/is-edn-dirty context)
      (do
        (try
          (def providers (providers/build-providers context))
          (providers/write-page context providers)
          (println "\nUpdated all")
          (catch Throwable thr (do
                                 (println "\nError... " str)
                                 (spit output-web-page (str "<script>"reloader-text "</script>" thr)))))
        (swap! version inc)
        (let [dirty  (filter chunk-provider/is-dirty providers)]
          (doseq [dp dirty]
            (println " says dirty " (chunk-provider/summary dp)))
          (if (seq dirty)
            (do
              (println "\nUpdated some data files. -- rebuilding entire document.")
              (providers/write-page context providers) ;; wack it all.
              (swap! version inc)) nil))) nil))))



