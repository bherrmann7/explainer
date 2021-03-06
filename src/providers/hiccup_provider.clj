
(ns providers.hiccup-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [hiccup.core]))

(deftype Provider [data]
  ChunkProviderProtocol
  (as-html [_] (hiccup.core/html data))
  (is-dirty [_] false)
  (summary [_] (str "hiccup " (count data) " bytes")))



