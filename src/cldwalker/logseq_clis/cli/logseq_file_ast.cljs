(ns cldwalker.logseq-clis.cli.logseq-file-ast
  "Print the logseq ast of a given markdown file"
  (:require ["fs" :as fs]
            ["path" :as path]
            [clojure.pprint :as pprint]
            [cldwalker.logseq-clis.util :as util]
            [logseq.graph-parser.mldoc :as gp-mldoc]))

(defn -main*
  [args]
  (let [[input] args
        config (gp-mldoc/default-config :markdown)]
    (if (.isDirectory (fs/lstatSync input))
      (map #(hash-map :file (path/join input %)
                      :ast (util/file-ast (path/join input %) config))
           (js->clj (fs/readdirSync input)))
      (util/file-ast input config))))

(defn -main
  [& args]
  (if (not= 1 (count args))
    (println "Usage: logseq-file-ast IN\n\nGiven a markdown"
             "IN file or directory, parses file(s) with mldoc and writes to stdout.")
    (pprint/pprint (-main* args))))

#js {:main -main}
