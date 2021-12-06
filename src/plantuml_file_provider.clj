
(ns plantuml-file-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [utils]))

(import net.sourceforge.plantuml.SourceStringReader)

;; Does 2 things
;; - generates the plantuml diagram using the plantuml api
;; - returns html to display the diagram
(defn create-plantuml-image [context filename]
  (let [{:keys [input-dir output-dir]} context
        input-contents (slurp (str input-dir "/" filename))
        plantuml-reader (SourceStringReader. input-contents)
        png-filename (utils/create-image-filename filename ".pu")
        png-output-stream (java.io.FileOutputStream. (str output-dir "/" png-filename))
        dd (.outputImage plantuml-reader png-output-stream)]
    (.close png-output-stream)
    (str "<img src=" png-filename " />")))

(defn is-dirty
  "If the dot source file has changed, rebuild the output file"
  [context filename]
  (let
   [{:keys [input-dir output-dir say-debug]} context
    input-filename (str input-dir "/" filename)
    output-filename (str output-dir "/" (utils/create-image-filename filename ".pu"))]
    (say-debug "                is " input-filename " newer than " output-filename "? " (utils/is-newer input-filename output-filename))
    (utils/is-newer input-filename output-filename)))

(deftype Provider [context data]
  ChunkProviderProtocol
  (as-html [_] (create-plantuml-image context data))
  (is-dirty [_] (is-dirty context data))
  (summary [_]
    (let [{:keys [input-dir]} context]
      (str "plantuml file: " input-dir "/" data))))


