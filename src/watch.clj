
(ns watch
  (:require [providers]
            [chunk-provider]))

(defn watcher [context providers-orig]
  (println "Watching for changes...")

  (def providers providers-orig)

  (while true
    (Thread/sleep 1000)
    (.print System/out  ".")
    (.flush System/out)

    ;; did input.edn change? => rebuild providers and index.html
    (if (providers/is-edn-dirty context)
      (do
        (def providers (providers/build-providers context))
        (providers/write-page context providers)
        (println "\nUpdated all"))
      (if (some? (map chunk-provider/is-dirty providers))
        (do
          (println "\nUpdated some data files.")
          (providers/write-page context providers)) ;; wack it all.
        ))))



