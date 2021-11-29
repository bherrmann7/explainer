

(ns cli)

(import org.docopt.Docopt)

(def usage (str
            "Explainer\n"
            "\n"
            "Usage:\n"
            "  explainer [options] \n"
            "  explainer --help\n"
            "  explainer --version\n"
            "\n"
            "Options:\n"
            "  -o <output-dir>, --output-dir <output-dir>  Where to write the output files. [default: docs]\n"
            "  -i <input-dir>, --input-dir <input-dir>     Where to write the input files. [default: input]\n"
            "  -w --watch    Wait and watch for input file changes, and reflect them to the output file.\n"
            "  -v --verbose  Verbose output.\n"
            "  -d --debug    Debug output.\n"
            "  -h --help     Show this screen.\n"
            "  --version     Show version.\n"
            "\n"))

;;(def args [])
;;(def args ["--out" "docs"])

;;(defn handle-exception [docoptExitException]
;;  (println "Dee code" (.getExitCode docoptExitException))
;;  (println "Dee msg" (.getMessage docoptExitException)))

(defn parse-cli [args]
;;  (.parse (.withExit (Docopt. usage) false) args)
  (into {} (.parse (.withVersion (Docopt. usage) "alpha") (into [] args))))
;;  (catch org.docopt.DocoptExitException dee (handle-dee dee))))
;;  (println "Gots" xx))

