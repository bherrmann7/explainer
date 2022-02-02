(ns watch
  (:require [providers.manager]
            [web-server]
            [pages]
            [utils]
            [providers.watch-reloader-provider]
            [providers.chunk-provider]))

(use '[clojure.java.shell :only [sh]])

(def version (atom 0))

;; reused used if the main document (doc.edn) didnt change.
(def last-page-to-providers (atom nil))

;; rebuild entire page and all providers
(defn rebuild-all [context]
    (println "Rebuild all content")
    (let [page-to-providers (pages/scan-and-load-pages context)]
      (pages/write-pages context page-to-providers)
    (reset! last-page-to-providers page-to-providers)
    (swap! version inc)))

(defn is-edn-dirty [context page-name]
   (let [page-input-filename (str (:input-dir context) "/" page-name)
         page-output-filename (pages/compute-output-filename (:output-dir context) page-name)]
     (utils/is-newer page-input-filename page-output-filename)))

(defn any-edn-dirty [context page-names]
  (let [ o (filter #(is-edn-dirty context %) page-names)]
  (not-empty o)))

(defn check-chunks-for-changes [context page-to-providers]
  (doseq [ [page-name providers] page-to-providers]
    (let [dirty  (filter providers.chunk-provider/is-dirty providers)]
      (doseq [dp dirty]
        (println page-name " says dirty " (providers.chunk-provider/summary dp)))
      (if (seq dirty)
        (do
          (println "\nUpdated some data files. -- rebuilding " page-name)
          (pages/write-page context page-name providers) 
          (swap! version inc))
        nil))))

(defn do-forever [context page-to-providers]
  (while true
    (Thread/sleep 1000)
    (.print System/out  ".")
    (.flush System/out)

    ;; did input.edn change? => rebuild providers and index.html
    (if (any-edn-dirty context (keys page-to-providers))
      (rebuild-all context)
      (check-chunks-for-changes context @last-page-to-providers))))
           

(defn watcher [context page-to-providers]
  (println "Watching for changes... ")
  (reset! last-page-to-providers page-to-providers)

  (web-server/start (:output-dir context) version)

  ;; Yikes this is not cross platform.
  (future (sh "xdg-open" "http://localhost:3000/index.html"))

  (do-forever context page-to-providers))




