
(ns html-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]))

(deftype Provider [data]
  ChunkProviderProtocol
  (as-html [_] data)
  (is-dirty [_] false)
  (summary [_] (str "html " (count data) " bytes")))

