
(ns watch
  (:require [providers.manager]
            [web-server]
            [providers.watch-reloader-provider]
            [providers.chunk-provider]))

(use '[clojure.java.shell :only [sh]])

(def version (atom 0))

;; reused used if the main document (doc.edn) didnt change.
(def last-providers (atom nil))

;; rebuild entire page and all providers
(defn rebuild-all [{:keys [output-web-page input-dir], :as  context}]
  (try
    (let [providers (providers.manager/build-providers context)]
      (reset! last-providers providers)
      (providers.manager/write-page context providers)
      (println "\nUpdated all"))
    (catch Throwable thr (do
                           (println "\nError... " str)
                           (spit output-web-page (str providers.watch-reloader-provider/reloader-javascript  thr)))))
  (swap! version inc))

(defn check-chunks-for-changes [context providers]
  (let [dirty  (filter providers.chunk-provider/is-dirty providers)]
    (doseq [dp dirty]
      (println " says dirty " (providers.chunk-provider/summary dp)))
    (if (seq dirty)
      (do
        (println "\nUpdated some data files. -- rebuilding entire document.")
        (providers.manager/write-page context providers) ;; wack it all.
        (swap! version inc)) nil)))

(defn do-forever [context]
  (while true
    (Thread/sleep 1000)
    (.print System/out  ".")
    (.flush System/out)

    ;; did input.edn change? => rebuild providers and index.html
    (if (providers.manager/is-edn-dirty context)
      (rebuild-all context)
      (check-chunks-for-changes context @last-providers))))

(defn watcher [{:keys [repl output-web-page input-dir], :as context}  providers]
  (println "Watching for changes... ")
  (reset! last-providers providers)

  (web-server/start (:output-dir context) version)

  (future (sh "xdg-open" "http://localhost:3000/index.html"))

  (do-forever context))




