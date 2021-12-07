
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
        (try
          (def providers (providers/build-providers context))
          (providers/write-page context providers)
          (println "\nUpdated all")
          (catch Throwable thr (let [{:keys [output-web-page]} context]
                                 (println "\nError... " str)
                                 (spit output-web-page (str thr)))))
        (swap! version inc)
        (let [dirty  (filter chunk-provider/is-dirty providers)]
          (doseq [dp dirty]
            (println " says dirty " (chunk-provider/summary dp)))
          (if (seq dirty)
            (do
              (println "\nUpdated some data files. -- rebuilding entire document.")
              (providers/write-page context providers) ;; wack it all.
              (swap! version inc)) nil))) nil)))



