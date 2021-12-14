
(ns resource-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [clojure.java.io :as io]
            [utils]))

(defn update-file [ {:keys [input-dir output-dir say say-debug]} file ]
  (io/copy (io/file (str input-dir "/" file)) (io/file (str output-dir "/" file))))

(defn is-filename-newer  [ {:keys [input-dir output-dir say say-debug]} file ]
  (utils/is-newer (str input-dir "/" file) (str output-dir "/" file)))

  
(deftype Provider [context filename]
  ChunkProviderProtocol
  (as-html [_] (do
                 (if (is-filename-newer context filename) (update-file context filename) nil)
                 ""))
  (is-dirty [_] (is-filename-newer context filename))
  (summary [_] (str "resource file: " filename)))

