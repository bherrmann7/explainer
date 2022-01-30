
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
            "  -x <output-filename>, --output-filename <output-filename>      Output filename [default: index.html]\n"
            "  -z <input-filename>, --input-filename <input-filename>        Input filename [default: index.edn]\n"
            "  -w --watch    Wait and watch for input file changes, and reflect them to the output file.\n"
            "  -r --repl     Use this when running from repl.  Invokes watcher as background thread.\n"
            "  -v --verbose  Verbose output.\n"
            "  -d --debug    Debug output.\n"
            "  -h --help     Show this screen.\n"
            "  --version     Show version.\n"
            "\n"))

(defn parse-cli [args]
  (into {} (.parse (.withVersion (Docopt. usage) "alpha") (into [] args))))

(defn create-context
  "Takes in the command lines and defaults and builds the progams running 'context' or configuration values."
  [args]
  (let [pargs (parse-cli args)
        verbose (fn [& args] (if (get pargs "--verbose") (apply println (conj args "verbose ")) nil))
        debug (fn [& args] (if (get pargs "--debug") (apply println (conj args "debug ")) nil))
        _ (verbose "raw args:" args)
        _ (verbose "parsed args:" pargs)
        context-raw {:verbose verbose
                     :debug debug
                     :watch-flag  (get pargs "--watch")
                     :output-dir (get pargs "--output-dir")
                     :input-dir  (get pargs "--input-dir")
                     :repl (get pargs "--repl")
                     :input-filename (get pargs "--input-filename")
                     :output-filename (get pargs "--output-filename")}
        {:keys [output-dir input-dir input-filename output-filename]} context-raw
        context (assoc context-raw
                       :input-edn-file  (str input-dir "/" input-filename)
                       :output-web-page (str output-dir "/" output-filename))]
    context))
