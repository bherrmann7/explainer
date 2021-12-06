
(ns unknown-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]))

(deftype Provider [type data]
  ChunkProviderProtocol

  (as-html [data] (str "
<div class='alert alert-danger' role='alert'>
Unknown data in docs.edn; type: " type "
</div>"))
  (is-dirty [_] false)
  (summary [_] "Unknown chunk, An error has occured."))


