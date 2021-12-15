
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
        {:keys [say watch-flag]} ctx
        ]
       
    ;; ensure the output dir exists.
    (.mkdir (java.io.File. (:output-dir ctx)))

    (let [providers (providers/build-providers ctx)]
      (say "Page has" (count providers) "chunks")
      (doseq [provider providers]
        (say "   " (chunk-provider/is-dirty provider) " " (chunk-provider/summary provider)))

      (if (providers/is-edn-dirty ctx)
        (providers/write-page ctx providers) nil)

      (doseq [p providers]
        (if (chunk-provider/is-dirty p)
          (chunk-provider/as-html p) ;; forces rebuild currently.
          nil))

      (if watch-flag (watch/watcher ctx providers) nil))))

(comment
  (-main "-w" "-r" )
 )
