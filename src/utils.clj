
;; A junk drawer

(ns utils)

(defn die [& args]
  (.println System/err (str "ERROR: " (apply str args)))
  (System/exit 3))

(defn is-newer [first-filename second-filename]
  (> (.lastModified (java.io.File. first-filename)) (.lastModified (java.io.File. second-filename))))

(defn without-slashes [filename]
  (subs filename (inc (.lastIndexOf filename "/"))))

;; "(file-parts "dr.eam.txt") => ["dr.eam" ".txt"]"
(defn file-parts [filename]
  (let [dex (.lastIndexOf filename ".")]
    (if (= -1 dex)
      [filename ""]
      [(subs filename 0 dex) (subs filename dex)])))

;; given a datafile, create an output image file.
(defn create-image-filename [filename extension-expected]
  (let [filename-no-slashes (without-slashes filename)
        [base extension] (file-parts filename-no-slashes)]
    (if (not= extension extension-expected)
      (.println System/err (str "EXPLAINER WARNNIG: expected input filename '" filename "' to have extension " extension-expected))
      nil)
    (str base ".png")))

