
(ns explainer
  (:gen-class)
  (:require
   [clojure.string :as str]

   [chunk-provider]
   [html-provider]
   [hiccup-provider]
   [plantuml-file-provider]
   [unknown-provider]
   ))


;; input.edn
;; [
;;    chunk-type chunk-data
;;    chunk-type chunk-data
;;    ...
;; ]

(defn get-chunk-providers [[chunk-type chunk-data]]
  (case chunk-type
    :html (html-provider/->Provider chunk-data)
    :html-hiccup (hiccup-provider/->Provider chunk-data)
    :plantuml-file (plantuml-file-provider/->Provider chunk-data)
    (unknown-provider/->Provider chunk-type chunk-data)))

;; 
(defn is-newer [ first-filename second-filename ]
  (> (.lastModified first-filename) (.lastModified second-filename)))

(defn load-chunks [ input-filename output-dir ]
  (.mkdir (java.io.File. output-dir)) ;; This seeems odly placed
  (let [ chunks (partition 2 (read-string (slurp input-filename)))]
    chunks))

(defn build-page [input-filename output-dir output-filename ]
  (let [ providers (map get-chunk-providers (load-chunks input-filename output-dir))]
    (spit output-filename (str/join (map #(chunk-provider/as-html %1) providers))))
  )

(defn -main [ & _ ]
  (let [
        ;; perhaps coommand line parameters ?
        input-filename "input.edn"
        output-dir "docs"
        output-filename (str output-dir "/index.html")
        ]
    (.mkdir (java.io.File. output-dir))
    (build-page input-filename output-dir output-filename)
    ))
