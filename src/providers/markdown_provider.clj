
(ns providers.markdown-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [markdown.core]))

(deftype Provider [data]
  ChunkProviderProtocol
  (as-html [_] (markdown.core/md-to-html-string data))
  (is-dirty [_] false)
  (summary [_] (str "markdown " (count data) " bytes")))

