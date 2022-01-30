
(ns explainer
  (:gen-class)
  (:require
   [cli]
   [utils]
   [pages]
   [watch]))

(defn -main [& args]

  ;;    comand line argument influnces running context
  (let [context (cli/create-context args)
        {:keys [verbose watch-flag]} context

        ;; ensure the output dir exists.
        _ (.mkdir (java.io.File. (:output-dir context)))

        ;; file the edn files, and create chunk provider lists for each 
        page-to-providers (pages/scan-and-load-pages context)]

      ;; save all the pages to disk
    (pages/write-pages context page-to-providers)

      ;; if watch mode, then start up web server and watch files for changes and rebuild on demand.
    #_(if watch-flag (watch/watcher context page-to-providers) nil)))

(comment
  (-main)
  (-main "-v" "-d")
  (-main "-v" "-d" "-w" "-r"))
