
(ns hiccup-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [hiccup.core]
            ))

(deftype Provider [data]
  ChunkProviderProtocol
  (as-html [_] (hiccup.core/html data))
  (is-dirty [_] false))


