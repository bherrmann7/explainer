

(ns pages
  (:require
   [providers.manager]
   [clojure.string]
   [providers.chunk-provider]))

(defn collect-page-files [{:keys [input-dir]}]
  ;; ignore emailcs .#foo.edn files
  (filter #(and (not (.startsWith % ".")) (.endsWith % ".edn")) (.list (java.io.File. input-dir))))

;; (collect-page-files {:input-dir "input"}) => ("kitchen-sink.edn" "index.edn" "roadmap.edn")

(defn compute-output-filename [output-dir page-input-filename]
  (str output-dir "/" (clojure.string/replace page-input-filename #"\.edn$" ".html")))

(defn build-providers [{:keys [output-dir input-dir verbose], :as  context} page-filename]
  (let [page-input-filename (str input-dir "/" page-filename)
        page-output-filename (compute-output-filename output-dir page-filename)
        providers (providers.manager/build-providers context page-input-filename page-output-filename)]
    (verbose "Page " page-filename "has" (count providers) "chunks")
    (doseq [provider providers]
      (verbose "   dirty:" (providers.chunk-provider/is-dirty provider) " chunk:" (providers.chunk-provider/summary provider)))
    providers))

(defn scan-and-load-pages [context]
  (let [page-files (collect-page-files context)]
    (reduce #(assoc %1 %2 (build-providers context %2)) {} page-files)))


(defn write-page [{:keys [output-dir verbose], :as  context} page-input-filename providers]
  (let [page-output-filename (compute-output-filename output-dir page-input-filename)]
    (verbose "writing" page-input-filename "->" page-output-filename)
    (providers.manager/write-page context page-output-filename providers)))

(defn write-pages
  "Write all pages to disk"
  [context page-to-provider]
  (doseq [[page-input-filename providers] page-to-provider]
    (write-page context page-input-filename providers)))

