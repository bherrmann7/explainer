
(ns plantuml-file-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]))

(import net.sourceforge.plantuml.SourceStringReader)

(defn create-image-filename [plantuml-input-filename]
  (assert (.endsWith plantuml-input-filename ".pu") (str "Expected plant uml filename to end in .pu! filename: " plantuml-input-filename))
  (let [name-length (count plantuml-input-filename)]
    (str (subs plantuml-input-filename 0 (- name-length 3)) ".png")))


;; Does 2 things
;; - generates the plantuml diagram using the plantuml api
;; - returns html to display the diagram
(defn create-plantuml-image [filename]
  (let [input-contents (slurp filename)
        plantuml-reader (SourceStringReader. input-contents)
        png-filename (create-image-filename filename)
        png-output-stream (java.io.FileOutputStream. (str "docs/" png-filename))]
    (.outputImage plantuml-reader png-output-stream)
    (.close png-output-stream)
    (str "<img src=" png-filename " />")))


(deftype Provider [data]
  ChunkProviderProtocol
  (as-html [_] (create-plantuml-image data))
  (is-dirty [_] false))






