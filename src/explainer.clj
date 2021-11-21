
(ns explainer
  (:gen-class)
  (:require [render-html]
            [render-hiccup]
            [clojure.string]
            [render-plantuml-file]))

;; Could use multimethods, but I know what I'm going to dispatch to,
(defn render-chunk [[type data]]
  (case type
    :html (render-html/render data)
    :html-hiccup (render-hiccup/render data)
    :plantuml-file (render-plantuml-file/render data)
    (str "<div>DO NOT KNOW HOW TO RENDER TYPE: " type "</div>")))

(defn -main [ & args ]
  (let [document (partition 2 (read-string (slurp "input.edn")))]
    (.mkdir (java.io.File. "docs"))
    (spit "docs/index.html" (clojure.string/join (map render-chunk document))))
  (println "wrote docs/index.html"))

