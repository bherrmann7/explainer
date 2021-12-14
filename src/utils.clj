
;; A junk drawer

(ns utils)

(defn die [& args]
  (.println System/err (str "ERROR: " (apply str args)))
  (System/exit 3))

(defn is-newer [first-filename second-filename]
  (let [ f1 (java.io.File. first-filename)
         f2 (java.io.File. second-filename) ]
    (if (.exists f2)
      (> (.lastModified f1) (.lastModified f2))
      true)))

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

