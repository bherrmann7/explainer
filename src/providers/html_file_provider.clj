
(ns providers.html-file-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [selmer.parser]
            [clojure.java.io]
            [utils]))

(defn compute-input-filename [context data-map]
  (let [{:keys [input-dir]} context]
    (str input-dir "/" (:filename data-map))))

(defn error [ msg ]
   (println "Yikes: " msg)
   (str "<h1 style='background-color: red; color: white;'>" msg "</h1>"))

(defn create-html [context data-map]
  (cond
    (not (map? data-map)) (error "The data provided to the :html-file chunk must be a map.  It must have a key :filename")
    (not (contains? data-map :filename)) (error "The map provided to the :html-file chunk, must provide a :filename key")
    :else
    (let [filename (compute-input-filename context data-map)]
      (if (not (.exists (clojure.java.io/file filename)))
        (error (str "Missing file: " filename))
        (selmer.parser/render (slurp filename) data-map)))))

(defn is-dirty
  "If the html-file source file has changed, rebuild the output file"
  [context data-map output-filename]
  (utils/is-newer (compute-input-filename context data-map) output-filename))

(deftype Provider [context data-map output-filename]
  ChunkProviderProtocol
  (as-html [_]  (create-html context data-map))
  (is-dirty [_] (is-dirty context data-map output-filename))
  (summary [_]  (str "html " (count (create-html context data-map)) " bytes")))

