
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
  "If the dot source file has changed, rebuild the output file"
  [context data-map]
  (utils/is-newer (compute-input-filename context data-map) (:output-web-page context)))

(deftype Provider [context data-map]
  ChunkProviderProtocol
  (as-html [_]  (create-html context data-map))
  (is-dirty [_] (is-dirty context data-map))
  (summary [_]  (str "html " (count (create-html context data-map)) " bytes")))



