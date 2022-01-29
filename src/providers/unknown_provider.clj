
(ns providers.unknown-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]))

(deftype Provider [type data]
  ChunkProviderProtocol

  (as-html [_] (str "
<div class='alert alert-danger' role='alert'>
Unknown data in docs.edn; type: " type "
</div>"))
  (is-dirty [_] false)
  (summary [_] (str "Unknown chunk, An error has occured.  type:" type " data:" data)))


