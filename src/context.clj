
(ns context)

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
            "  -r --repl     When used from repl use this to invoke watcher as background thread\n"
            "  -h --help     Show this screen.\n"
            "  --version     Show version.\n"
            "\n"))

(defn parse-cli [args]
  (into {} (.parse (.withVersion (Docopt. usage) "alpha") (into [] args))))


(defn create [ args ]
  (let [
  pargs (parse-cli args)
        verbose (get pargs "--verbose")
        debug (get pargs "--debug")
        say (fn [& args] (if verbose (apply println args) nil))
        say-debug (fn [& args] (if debug (apply println args) nil))
        _ (say "raw args:" args)
        _ (say "parsed args:" pargs)
        context-raw {:say say
                     :say-debug say-debug
                     :verbose (get pargs "--verbose")
                     :watch-flag  (get pargs "--watch")
                     :output-dir (get pargs "--output-dir")
                     :output-filename "index.html"
                     :input-dir  (get pargs "--input-dir")
                     :input-filename "doc.edn"
                     :repl (get pargs "--repl") }
        {:keys [watch-flag output-dir input-dir input-filename output-filename]} context-raw
        context (assoc context-raw
                       :input-edn-file  (str input-dir "/" input-filename)
                       :output-web-page (str output-dir "/" output-filename))]
  context))
