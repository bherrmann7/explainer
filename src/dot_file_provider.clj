
(ns dot-file-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]))

(import net.sourceforge.plantuml.SourceStringReader)

(defn create-image-filename [dot-input-filename]
  (assert (.endsWith dot-input-filename ".dot") (str "Expected graphvis dot filename to end in .dot! filename: " dot-input-filename))
  (let [name-length (count dot-input-filename)]
    (str (subs dot-input-filename 0 (- name-length 3)) ".png")))


;; Does 2 things
;; - generates the dot diagram using the plantuml api
;; - returns html to display the diagram
(defn create-plantuml-image [filename]
  (let [input-contents (slurp filename)
        input-dot-wrapped (str "@startdot\n" input-contents "\n@enddot\n")
        plantuml-reader (SourceStringReader. input-dot-wrapped)
        png-filename (create-image-filename filename)
        png-output-stream (java.io.FileOutputStream. (str "docs/" png-filename))]
    (.outputImage plantuml-reader png-output-stream)
    (.close png-output-stream)
    (str "<img src=" png-filename " />")))


(deftype Provider [data]
  ChunkProviderProtocol
  (as-html [_] (create-plantuml-image data))
  (is-dirty [_] false))






