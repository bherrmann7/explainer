
(ns providers
  (:require
   [clojure.string :as str]
   [utils]
   [chunk-provider]
   [html-provider]
   [hiccup-provider]
   [plantuml-file-provider]
   [dot-file-provider]
   [js-file-provider]
   [swagger-provider]
   [resource-provider]
   [watch-reloader-provider]
   [unknown-provider]
   ))

(def document-all-providers [
    (html-provider/->Provider nil)
    (hiccup-provider/->Provider nil)
    (plantuml-file-provider/->Provider nil nil)
    (dot-file-provider/->Provider nil nil)
    (js-file-provider/->Provider nil nil)
    (swagger-provider/->Provider nil nil)
    (resource-provider/->Provider nil nil)
    (unknown-provider/->Provider nil nil)
                             ])

(defn get-chunk-providers
  "given a type and data, return the appropriate provider.  A bit like a factory."
  [context [chunk-type chunk-data]]
  (case chunk-type
    :html (html-provider/->Provider chunk-data)
    :html-hiccup (hiccup-provider/->Provider chunk-data)
    :plantuml-file (plantuml-file-provider/->Provider context chunk-data)
    :dot-file (dot-file-provider/->Provider context chunk-data)
    :js-file (js-file-provider/->Provider context chunk-data)
    :swagger-inline (swagger-provider/->Provider context chunk-data)
    :resource (resource-provider/->Provider context chunk-data)
    (unknown-provider/->Provider chunk-type chunk-data)))

(defn load-chunks
  "load edn data into memory"
  [input-filename]
  (let [chunks (partition 2 (read-string (slurp input-filename)))]
    chunks))

(defn prepend-watch-reload-provider [edn-file-providers]
  (conj edn-file-providers (watch-reloader-provider/->Provider)))

(defn build-providers
  "load inital edn file, and break into collection of providers.  Handle special watch provider also."
  [{:keys [input-dir input-filename watch-flag], :as context}]
  (let [
        input-edn-file (str input-dir "/" input-filename)
        _ (if (not (.exists (java.io.File. input-edn-file))) (utils/die "Input file does not exist: " input-edn-file) nil)
        edn-file-providers (map #(get-chunk-providers context %) (load-chunks input-edn-file))
        providers (if watch-flag (prepend-watch-reload-provider edn-file-providers) edn-file-providers)]
    providers))

(defn write-page
  "Loop through the providers and output the page by extracting each provider's content"
  [context providers]
  (let [{:keys [output-web-page verbose]} context
        web-page-content (str/join "\n\n" (map chunk-provider/as-html providers))]
    (spit output-web-page web-page-content)
    (verbose "Wrote " output-web-page)))

(defn is-edn-dirty [context]
  (let [{:keys [input-edn-file  output-web-page]} context]
    (utils/is-newer input-edn-file output-web-page)))

