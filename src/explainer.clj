
(ns explainer
  (:gen-class)
  (:require
   [cli]
   [utils]
   [providers.manager]
   [watch]
   [providers.chunk-provider]))

(defn -main [& args]
  (let [context (cli/create args)
        {:keys [verbose watch-flag]} context]

    ;; ensure the output dir exists.
    (.mkdir (java.io.File. (:output-dir context)))

    (let [providers (providers.manager/build-providers context)]
      (verbose "Page has" (count providers) "chunks")
      (doseq [provider providers]
        (verbose "   dirty:" (providers.chunk-provider/is-dirty provider) " chunk:" (providers.chunk-provider/summary provider)))

      ;; if you run the program, we regenerate all the things.
      (providers.manager/write-page context providers)

      (if watch-flag (watch/watcher context providers) nil))))

(comment
  (-main "-v" "-d")
  (-main "-v" "-d" "-w" "-r"))
