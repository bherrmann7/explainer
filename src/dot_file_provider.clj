
(ns dot-file-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [filename-util]))

(import net.sourceforge.plantuml.SourceStringReader)

;; Does 2 things
;; - generates the dot diagram using the plantuml api
;; - returns html to display the diagram
(defn create-plantuml-image [filename]
  (let [input-contents (slurp filename)
        input-dot-wrapped (str "@startdot\n" input-contents "\n@enddot\n")
        plantuml-reader (SourceStringReader. input-dot-wrapped)
        png-filename (filename-util/create-image-filename filename ".dot")
        png-output-stream (java.io.FileOutputStream. (str "docs/" png-filename))]
    (.outputImage plantuml-reader png-output-stream)
    (.close png-output-stream)
    (str "<img src=" png-filename " />")))

(deftype Provider [data]
  ChunkProviderProtocol
  (as-html [_] (create-plantuml-image data))
  (is-dirty [_] false))






