
(ns explainer
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

(defn run [opt]
  (let [document (partition 2 (read-string (slurp "input.edn")))]
    (spit "index.html" (clojure.string/join (map render-chunk document))))
  (println "wrote index.html"))

