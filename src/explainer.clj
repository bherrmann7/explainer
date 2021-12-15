
(ns explainer
  (:gen-class)
  (:require
   [context]
   [utils]
   [providers]
   [watch]
   [chunk-provider]))

;; input.edn
;; [
;;    chunk-type chunk-data
;;    chunk-type chunk-data
;;    ...
;; ]

(defn -main [& args]
  (let [ctx (context/create args)
        {:keys [verbose watch-flag]} ctx
        ]
       
    ;; ensure the output dir exists.
    (.mkdir (java.io.File. (:output-dir ctx)))

    (let [providers (providers/build-providers ctx)]
      (verbose "Page has" (count providers) "chunks")
      (doseq [provider providers]
        (verbose "   dirty:" (chunk-provider/is-dirty provider) " chunk:" (chunk-provider/summary provider)))

      ;; if you run the program, we regenerate all the things.
      (providers/write-page ctx providers)

      (if watch-flag (watch/watcher ctx providers) nil))))

(comment
  (-main "-v" "-d")
 )
