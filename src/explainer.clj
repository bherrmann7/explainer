
(ns explainer
  (:gen-class)
  (:require
   [cli]
   [utils]
   [providers]
   [watch]
   [chunk-provider]
   [html-provider]
   [js-file-provider]
   [hiccup-provider]
   [plantuml-file-provider]
   [dot-file-provider]
   [unknown-provider]))

;; input.edn
;; [
;;    chunk-type chunk-data
;;    chunk-type chunk-data
;;    ...
;; ]

(defn -main [& args]
  (let [pargs (cli/parse-cli args)
        verbose (get pargs "--verbose")
        debug (get pargs "--debug")
        say (fn [& args] (if verbose (apply println args) nil))
        say-debug (fn [& args] (if debug (apply println args) nil))
        _ (say "raw args:" args)
        _ (say "parsed args:" pargs)
        context-raw {:say say
                     :say-debug say-debug
                     :verbose (get pargs "--verbose")
                     :watch-flag  (get pargs "--watch")
                     :output-dir (get pargs "--output-dir")
                     :output-filename "index.html"
                     :input-dir  (get pargs "--input-dir")
                     :input-filename "doc.edn"
                     :repl (get pargs "--repl") }
        {:keys [watch-flag output-dir input-dir input-filename output-filename]} context-raw
        context (assoc context-raw
                       :input-edn-file  (str input-dir "/" input-filename)
                       :output-web-page (str output-dir "/" output-filename))]

;; ensure the output dir exists.
    (.mkdir (java.io.File. (:output-dir context)))

    (let [providers (providers/build-providers context)]
      (say "Page has" (count providers) "chunks")
      (doseq [provider providers]
        (say "   " (chunk-provider/is-dirty provider) " " (chunk-provider/summary provider)))

      (if (providers/is-edn-dirty context)
        (providers/write-page context providers) nil)

      (doseq [p providers]
        (if (chunk-provider/is-dirty p)
          (chunk-provider/as-html p) ;; forces rebuild currently.
          nil))

      (if watch-flag (watch/watcher context providers) nil))))

(comment
  (-main "-w")
 )
