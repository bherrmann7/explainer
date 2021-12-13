
(ns providers
  (:require
   [clojure.string :as str]
   [cli]
   [utils]
   [chunk-provider]
   [html-provider]
   [hiccup-provider]
   [plantuml-file-provider]
   [dot-file-provider]
   [js-file-provider]
   [swagger-provider]
   [unknown-provider]))

(defn get-chunk-providers [context [chunk-type chunk-data]]
  (case chunk-type
    :html (html-provider/->Provider chunk-data)
    :html-hiccup (hiccup-provider/->Provider chunk-data)
    :plantuml-file (plantuml-file-provider/->Provider context chunk-data)
    :dot-file (dot-file-provider/->Provider context chunk-data)
    :js-file (js-file-provider/->Provider context chunk-data)
    :swagger-inline (swagger-provider/->Provider context chunk-data)
    (unknown-provider/->Provider chunk-type chunk-data)))

(defn load-chunks [input-filename]
  (let [chunks (partition 2 (read-string (slurp input-filename)))]
    chunks))

;; (let [{:keys [name location description]} client]
(defn build-providers [context]
  (let [{:keys [input-dir input-filename]} context
        input-edn-file (str input-dir "/" input-filename)
        _ (if (not (.exists (java.io.File. input-edn-file))) (utils/die "Input file does not exist: " input-edn-file) nil)
        providers (map #(get-chunk-providers context %) (load-chunks input-edn-file))]
    providers))

(defn write-page [context providers]
  (let [{:keys [output-web-page say]} context
        web-page-content (str/join (map chunk-provider/as-html providers))]
    (spit output-web-page web-page-content)
    (say "Wrote " output-web-page)))

(defn is-edn-dirty [context]
  (let [{:keys [input-edn-file  output-web-page]} context]
    (utils/is-newer input-edn-file output-web-page)))

