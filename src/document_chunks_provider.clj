

(ns document-chunks-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [utils]))
;;
;; A provides documenation as html of the providers
;; 


(deftype Provider [all-providers]
  ChunkProviderProtocol
  (as-html [_] "TBD: Documentation of each chunk type to appear here")
  (is-dirty [_] false)
  (summary [_] "documenation provider"))


