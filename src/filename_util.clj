
(ns filename-util)

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
      (.println System/err (str "WARNNIG: expected " filename " to have extension " extension-expected))
      nil)
    (str base ".png")))

