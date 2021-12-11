
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

(defn parse-cli [args]
  (into {} (.parse (.withVersion (Docopt. usage) "alpha") (into [] args))))


