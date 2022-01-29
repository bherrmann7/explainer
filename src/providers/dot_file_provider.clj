
(ns providers.dot-file-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [utils]))

(import net.sourceforge.plantuml.SourceStringReader)

;; Does 2 things
;; - generates the dot diagram using the plantuml api
;; - returns html to display the diagram
(defn create-dot-image [context filename]
  (let [{:keys [input-dir output-dir]} context
        input-contents (slurp (str input-dir "/" filename))
        input-dot-wrapped (str "@startdot\n" input-contents "\n@enddot\n")
        plantuml-reader (SourceStringReader. input-dot-wrapped)
        png-filename (utils/create-image-filename filename ".dot")
        png-output-stream (java.io.FileOutputStream. (str output-dir "/" png-filename))]
    (.outputImage plantuml-reader png-output-stream)
    (.close png-output-stream)
    (str "<img src=" png-filename " />")))

(defn is-dirty
  "If the dot source file has changed, rebuild the output file"
  [context filename]
  (let
   [{:keys [input-dir output-dir]} context
    input-filename (str input-dir "/" filename)
    output-filename (str output-dir "/" (utils/create-image-filename filename ".dot"))]
    (utils/is-newer input-filename output-filename)))

(deftype Provider [context data]
  ChunkProviderProtocol
  (as-html [_] (create-dot-image context data))
  (is-dirty [_] (is-dirty context data))
  (summary [_] (str "dot file: " data)))





