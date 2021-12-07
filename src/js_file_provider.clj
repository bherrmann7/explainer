
(ns js-file-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [utils]))

(defn create-js-file [context filename]
  (let [{:keys [input-dir output-dir]} context
        input-contents (slurp (str input-dir "/" filename))
        output-path (str output-dir "/" filename)]
    (spit output-path input-contents)
    (str "<script src='" filename "' ></script>")))

(defn is-dirty
  "If the dot source file has changed, rebuild the output file"
  [context filename]
  (let
   [{:keys [input-dir output-dir say-debug]} context
    input-filename (str input-dir "/" filename)
    output-filename (str output-dir "/" filename)]
    (say-debug "                is " input-filename " newer than " output-filename "? " (utils/is-newer input-filename output-filename))
    (utils/is-newer input-filename output-filename)))

(deftype Provider [context data]
  ChunkProviderProtocol
  (as-html [_] (create-js-file context data))
  (is-dirty [_] (is-dirty context data))
  (summary [_]
    (let [{:keys [input-dir]} context]
      (str "js file: " input-dir "/" data))))


