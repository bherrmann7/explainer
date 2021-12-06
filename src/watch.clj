
(ns watch
  (:require [providers]
            [web-server]
            [chunk-provider]))

(def version (atom 0))

(defn watcher [context providers-orig]
  (println "Watching for changes...")

  (def providers providers-orig)

  (web-server/start (:output-dir context) version)
 
  (while true
    (Thread/sleep 1000)
    (.print System/out  ".")
    (.flush System/out)

    ;; did input.edn change? => rebuild providers and index.html
    (if (providers/is-edn-dirty context)
      (do
        (def providers (providers/build-providers context))
        (providers/write-page context providers)
        (swap! version inc)
        (println "\nUpdated all"))
      (let [dirty  (filter chunk-provider/is-dirty providers)]
        (doseq [dp dirty]
          (println " says dirty " (chunk-provider/summary dp)))
        (if (not (empty? dirty))
          (do
            (println "\nUpdated some data files. -- rebuilding entire document.")
            (providers/write-page context providers) ;; wack it all.
            (swap! version inc)))))))


