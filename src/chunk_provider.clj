
(ns chunk-provider)

;;
;; A provider of html for a web page
;; 

(defprotocol ChunkProviderProtocol
  "provides a chunk of html for a web page.  Responsible for html and generating dependant files - usually images."

  (as-html [this] "render page chunk as html.  Generates data if
  needed.")

  (is-dirty [this] "Did the underlying data (typically file)
  change. Can be called repeadtyly (ie like ever 250ms) to see if we
  need to regenerate the page."))
