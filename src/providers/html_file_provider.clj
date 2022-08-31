
(ns providers.html-file-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [selmer.parser]
            [utils]))

(defn compute-input-filename [context data-map]
  (let [{:keys [input-dir]} context]
    (str input-dir "/" (:filename data-map))))

(defn create-html [context data-map]
  (selmer.parser/render (slurp (compute-input-filename context data-map)) data-map))

(defn is-dirty
  "If the html-file source file has changed, rebuild the output file"
  [context data-map output-filename]
  (let
   [{:keys [input-dir]} context
    input-filename (str input-dir "/" (:filename data-map))]
    (utils/is-newer input-filename output-filename)))

(deftype Provider [context data-map output-filename]
  ChunkProviderProtocol
  (as-html [_]  (create-html context data-map))
  (is-dirty [_] (is-dirty context data-map output-filename))
  (summary [_]  (str "html " (count (create-html context data-map)) " bytes")))

